package org.ihtsdo.otf.transformationandtemplate.service.componenttransform;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ihtsdo.otf.resourcemanager.ResourceManager;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.DescriptionPojo;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.SnomedComponent;
import org.ihtsdo.otf.rest.exception.BusinessServiceException;
import org.ihtsdo.otf.rest.exception.ProcessingException;
import org.ihtsdo.otf.rest.exception.ResourceNotFoundException;
import org.ihtsdo.otf.transformationandtemplate.configuration.TransformationJobResourceConfiguration;
import org.ihtsdo.otf.transformationandtemplate.domain.ComponentTransformationJob;
import org.ihtsdo.otf.transformationandtemplate.domain.ComponentTransformationRequest;
import org.ihtsdo.otf.transformationandtemplate.domain.StatusAndMessage;
import org.ihtsdo.otf.transformationandtemplate.domain.TransformationRecipe;
import org.ihtsdo.otf.transformationandtemplate.service.JsonStore;
import org.ihtsdo.otf.transformationandtemplate.service.client.ChangeResult;
import org.ihtsdo.otf.transformationandtemplate.service.template.TransformationStatus;
import org.ihtsdo.sso.integration.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Boolean.FALSE;
import static java.lang.String.format;

@Service
public class ComponentTransformService {

	private static final String CONFIGURATION_FILE = "job-configuration.json";
	private static final String STATUS_FILE = "status.json";
	public static final String RESULTS_FILE = "results.json";
	public static final String INPUT_TSV = "input.tsv";

	@Autowired
	private DescriptionService descriptionService;

	@Autowired
	private JsonStore transformationRecipeStore;

	@Autowired
	private ObjectMapper objectMapper;

	private final ResourceManager transformationJobResourceManager;

	private final ExecutorService jobExecutorService;

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public ComponentTransformService(
			@Autowired TransformationJobResourceConfiguration transformationJobResourceConfiguration,
			@Autowired ResourceLoader cloudResourceLoader,
			@Value("${transformation.job.concurrency.max}") int concurrentTransformationJobs) {

		transformationJobResourceManager = new ResourceManager(transformationJobResourceConfiguration, cloudResourceLoader);
		jobExecutorService = Executors.newFixedThreadPool(concurrentTransformationJobs);
	}

	public Set<TransformationRecipe> listRecipes(String branchPath) throws IOException {
		TreeSet<TransformationRecipe> transformationRecipes = new TreeSet<>(Comparator.comparing(TransformationRecipe::getTitleOrEmpty));
		transformationRecipes.addAll(transformationRecipeStore.loadAll(TransformationRecipe.class));
		return transformationRecipes;
	}

	public TransformationRecipe loadRecipeOrThrow(String branchPath, String recipe) throws IOException {
		TransformationRecipe transformationRecipe = transformationRecipeStore.load(recipe, TransformationRecipe.class);
		if (transformationRecipe == null) {
			throw new ResourceNotFoundException("Recipe", recipe);
		}
		return transformationRecipe;
	}

	public ComponentTransformationJob queueBatchTransformation(ComponentTransformationRequest request) throws BusinessServiceException {
		TransformationRecipe recipe;
		String recipeKey = request.getRecipe();
		try {
			recipe = transformationRecipeStore.load(recipeKey, TransformationRecipe.class);
		} catch (IOException e) {
			throw new BusinessServiceException(format("Failed to load recipe '%s'.", recipeKey));
		}
		if (recipe == null) {
			throw new ResourceNotFoundException(format("Recipe '%s' not found.", recipeKey));
		}

		ComponentTransformationJob job = new ComponentTransformationJob(request, SecurityUtil.getUsername());
		// Write configuration and status to separate files
		persistJobResource(job, CONFIGURATION_FILE, job);
		job.updateStatus(TransformationStatus.QUEUED);
		persistJobResource(job, STATUS_FILE, job.getStatus());
		persistJobResource(job, INPUT_TSV, job.getRequest().getTsvValues());

		// Job may pause here if all executor threads are in use.
		jobExecutorService.submit(() -> {
			try {
				logger.info("Running {} transformation for user {} on branch {} with id {}.", recipeKey, job.getUser(), job.getRequest().getBranchPath(), job.getId());
				job.updateStatus(TransformationStatus.RUNNING, null);
				persistJobResource(job, STATUS_FILE, job.getStatus());

				// Read input from storage
				InputStream tsvInputStream = readJobResource(job, INPUT_TSV, InputStream.class);
				request.setTsvValues(tsvInputStream);

				List<ChangeResult<? extends SnomedComponent>> changeResults = doRunTransform(job, recipe, request);
				persistJobResource(job, RESULTS_FILE, changeResults);

				if (changeResults.stream().anyMatch(changeResult -> FALSE == changeResult.getSuccess())) {
					job.updateStatus(TransformationStatus.COMPLETED_WITH_FAILURE, "Not all changes were successful. See results file for details.");
				} else {
					job.updateStatus(TransformationStatus.COMPLETED, null);
				}
				persistJobResource(job, STATUS_FILE, job.getStatus());
				logger.info("Transformation {} {}", job.getId(), job.getStatus());
			} catch (Exception e) {
				logger.error("Exception during component transformation job id {}, branch {}.", job.getId(), request.getBranchPath(), e);
				try {
					job.updateStatus(TransformationStatus.FAILED, "Unhanded exception during component transformation. " + e.getMessage());
					persistJobResource(job, STATUS_FILE, job.getStatus());
				} catch (BusinessServiceException be) {
					logger.error("Also failed to update persistent status of job {}.", job.getId());
				}
			}
			return null;
		});

		return job;
	}

	public ComponentTransformationJob loadTransformationJob(String branchPath, String jobId) throws BusinessServiceException {
		StatusAndMessage status = readJobResource(branchPath, jobId, STATUS_FILE, StatusAndMessage.class);
		ComponentTransformationJob job = readJobResource(branchPath, jobId, CONFIGURATION_FILE, ComponentTransformationJob.class);
		if (status == null || job == null) {
			throw new ResourceNotFoundException(format("Job '%s' not found.", jobId));
		}
		job.setStatus(status);
		return job;
	}

	public List<ChangeResult<DescriptionPojo>> loadDescriptionTransformationJobResults(String branchPath, String jobId) throws BusinessServiceException {
		return readJobResource(branchPath, jobId, RESULTS_FILE, new TypeReference<List<ChangeResult<DescriptionPojo>>>() {});
	}

	private List<ChangeResult<? extends SnomedComponent>> doRunTransform(ComponentTransformationJob job, TransformationRecipe recipe, ComponentTransformationRequest request) throws BusinessServiceException {
		switch (recipe.getComponent()) {
			case DESCRIPTION:
				return descriptionService.startBatchTransformation(recipe, request);
			default:
				throw new ProcessingException("Unable to transform component of type " + recipe.getComponent());
		}
	}

	private <T> T readJobResource(ComponentTransformationJob job, String resourceName, Class<T> resourceClass) throws BusinessServiceException {
		String branchPath = job.getRequest().getBranchPath();
		String id = job.getId();
		return readJobResource(branchPath, id, resourceName, resourceClass);
	}

	private <T> T readJobResource(String branchPath, String id, String resourceName, TypeReference<T> typeReference) throws BusinessServiceException {
		return doReadJobResource(branchPath, id, resourceName, null, typeReference);
	}

	private <T> T readJobResource(String branchPath, String id, String resourceName, Class<T> resourceClass) throws BusinessServiceException {
		return doReadJobResource(branchPath, id, resourceName, resourceClass, null);
	}
	private <T> T doReadJobResource(String branchPath, String id, String resourceName, Class<T> resourceClass, TypeReference<T> typeReference) throws BusinessServiceException {
		try {
			InputStream inputStream = transformationJobResourceManager.readResourceStreamOrNullIfNotExists(getResourcePath(branchPath, id, resourceName));
			if (inputStream == null) {
				throw new ResourceNotFoundException(format("Resource %s not found for job id %s, branch %s.", resourceName, id, branchPath));
			}
			if (typeReference != null) {
				return objectMapper.readValue(inputStream, typeReference);
			}
			if (resourceClass.equals(InputStream.class)) {
				return (T) inputStream;
			} else {
				return objectMapper.readValue(inputStream, resourceClass);
			}
		} catch (IOException e) {
			throw new BusinessServiceException(format("Failed to read resource %s for job id %s, branch %s.", resourceName, id, branchPath), e);
		}
	}

	private void persistJobResource(ComponentTransformationJob job, String resourceName, Object resource) throws BusinessServiceException {
		try {
			byte[] bytes = objectMapper.writeValueAsBytes(resource);
			InputStream inputStream = new ByteArrayInputStream(bytes);
			persistJobResource(job, resourceName, inputStream);
		} catch (JsonProcessingException e) {
			throw new BusinessServiceException(format("Failed to persist resource %s for job id %s, branch %s.", resourceName, job.getId(), job.getRequest().getBranchPath()), e);
		}

	}

	private void persistJobResource(ComponentTransformationJob job, String resourceName, InputStream inputStream) throws BusinessServiceException {
		try {
			transformationJobResourceManager.writeResource(getResourcePath(job.getRequest().getBranchPath(), job.getId(), resourceName), inputStream);
		} catch (IOException e) {
			throw new BusinessServiceException(format("Failed to persist resource %s for job id %s, branch %s.", resourceName, job.getId(), job.getRequest().getBranchPath()), e);
		}
	}

	private String getResourcePath(String branchPath, String jobId, String resourceName) {
		return format("/%s/%s/%s", branchPath, jobId, resourceName);
	}
}
