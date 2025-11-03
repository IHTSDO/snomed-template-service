package org.ihtsdo.otf.transformationandtemplate.rest;

import io.kaicode.rest.util.branchpathrewrite.BranchPathUriUtil;
import io.swagger.v3.oas.annotations.Parameter;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.AxiomPojo;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.ConceptPojo;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.DescriptionPojo;
import org.ihtsdo.otf.rest.exception.BusinessServiceException;
import org.ihtsdo.otf.transformationandtemplate.domain.*;
import org.ihtsdo.otf.transformationandtemplate.service.client.ChangeResult;
import org.ihtsdo.otf.transformationandtemplate.service.client.DescriptionReplacementPojo;
import org.ihtsdo.otf.transformationandtemplate.service.componenttransform.ComponentTransformService;
import org.ihtsdo.otf.rest.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.lang.String.format;

@RestController
public class TransformationController {

	public static final String TAB = "\t";
	@Autowired
	private ComponentTransformService componentTransformService;

	@Value("${transfromationTemplateStorePath}")
	private String transformationTemplateStorePath;

	private volatile String cachedLatestVersion = null;

	@GetMapping(value = "/transformation/template/download", produces = "application/octet-stream")
	public void downloadTransformationTemplate(HttpServletResponse response) throws IOException, ResourceNotFoundException {
		String latestVersion = getLatestTransformationTemplateVersion();
		if (latestVersion == null || latestVersion.isEmpty()) {
			throw new ResourceNotFoundException("Transformation template", "No version found");
		}

		File templateDir = new File(transformationTemplateStorePath, latestVersion);
		if (!templateDir.exists() || !templateDir.isDirectory()) {
			throw new ResourceNotFoundException("Transformation template", "Version directory: " + latestVersion);
		}

		// Find the Excel file in the version directory
		File[] files = templateDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".xlsx") || name.toLowerCase().endsWith(".xls"));
		if (files == null || files.length == 0) {
			throw new ResourceNotFoundException("Transformation template", "Excel file not found in version: " + latestVersion);
		}

		// Use the first Excel file found (assuming there's only one)
		File templateFile = files[0];

		// Set response headers
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		response.setHeader("Content-Disposition", format("attachment; filename=\"%s\"", templateFile.getName()));
		response.setContentLengthLong(templateFile.length());

		// Stream the file to the response
		try (InputStream inputStream = new FileInputStream(templateFile);
			 OutputStream outputStream = response.getOutputStream()) {
			byte[] buffer = new byte[8192];
			int bytesRead;
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}
			outputStream.flush();
		}
	}

	@GetMapping(value = "/transformation/template/version")
	public String getLatestTransformationTemplateVersion() {
		// Return cached version if available
		String cached = cachedLatestVersion;
		if (cached != null) {
			return cached;
		}

		// Compute and cache the latest version (thread-safe)
		synchronized (this) {
			// Double-check after acquiring lock
			if (cachedLatestVersion != null) {
				return cachedLatestVersion;
			}

			File templateStoreDir = new File(transformationTemplateStorePath);
			if (!templateStoreDir.exists() || !templateStoreDir.isDirectory()) {
				return null;
			}

			File[] versionDirs = templateStoreDir.listFiles(File::isDirectory);
			if (versionDirs == null || versionDirs.length == 0) {
				return null;
			}

			// Find the latest version by comparing version numbers
			Optional<String> latestVersion = Arrays.stream(versionDirs)
					.map(File::getName)
					.filter(this::isValidVersionFormat)
					.max(this::compareVersions);

			cachedLatestVersion = latestVersion.orElse(null);
			return cachedLatestVersion;
		}
	}
	

	@RequestMapping(value = "/{branchPath}/recipes", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Set<TransformationRecipe> listTransformationRecipes(@PathVariable String branchPath) throws IOException {
		return componentTransformService.listRecipes(BranchPathUriUtil.decodePath(branchPath));
	}

	@RequestMapping(value = "/{branchPath}/recipes/{recipe}/jobs", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntity<Void> createTransformationJob(
			@PathVariable String branchPath,

			@Parameter( description = "Recipe key")
			@PathVariable String recipe,

			@Parameter( description = "Batch size")
			@RequestParam(defaultValue = "100") int batchSize,
			@Parameter( description = "Project key (optional - batches split between tasks)")
			@RequestParam(required = false) String projectKey,

			@Parameter( description = "Task title (optional)")
			@RequestParam(required = false) String taskTitle,

			@Parameter( description = "Task assignee (optional)")
			@RequestParam(required = false) String taskAssignee,

			@Parameter( description = "Task reviewer (optional)")
			@RequestParam(required = false) String taskReviewer,

			@Parameter(description = "tsvFile") MultipartFile tsvFile,
			UriComponentsBuilder uriComponentsBuilder,

			@Parameter( description = "Skip SNOMED Drools validation (optional)")
			@RequestParam(required = false, defaultValue = "false") boolean skipDroolsValidation) throws BusinessServiceException, IOException {

		branchPath = BranchPathUriUtil.decodePath(branchPath);

		ComponentTransformationJob job = componentTransformService.queueBatchTransformation
				(new ComponentTransformationRequest(recipe, branchPath, projectKey, taskTitle, taskAssignee, taskReviewer, batchSize, tsvFile.getInputStream(), skipDroolsValidation));

		return ResponseEntity.created(uriComponentsBuilder.path("/{branchPath}/recipes/{recipe}/jobs/{jobId}")
				.buildAndExpand(branchPath, recipe, job.getId()).toUri()).build();
	}

	@RequestMapping(value = "/{branchPath}/recipes/{recipe}/jobs/{jobId}", method = RequestMethod.GET, produces = "application/json")
	public ComponentTransformationJob getTransformationJob(
			@PathVariable String branchPath,
			@Parameter( description = "Recipe key")
			@PathVariable String recipe,
			@PathVariable String jobId) throws BusinessServiceException {

		branchPath = BranchPathUriUtil.decodePath(branchPath);

		return componentTransformService.loadTransformationJob(branchPath, jobId);
	}

	@RequestMapping(value = "/{branchPath}/recipes/{recipe}/jobs/{jobId}/result-tsv", method = RequestMethod.GET, produces = "text/csv")
	@ResponseBody
	public void getTransformationJobResultAsTsv(
			@PathVariable String branchPath,
			@Parameter( description = "Recipe key")
			@PathVariable String recipe,
			@PathVariable String jobId,
			HttpServletResponse servletResponse) throws BusinessServiceException, IOException {

		branchPath = BranchPathUriUtil.decodePath(branchPath);

		TransformationRecipe transformationRecipe = componentTransformService.loadRecipeOrThrow(branchPath, recipe);
		ComponentType componentType = transformationRecipe.getComponent();
		ChangeType chanageType = transformationRecipe.getChangeType();

		if (componentType == ComponentType.DESCRIPTION) {
			setTSVHeaders(jobId, servletResponse);
			if (ChangeType.REPLACE == chanageType) {
				List<ChangeResult<DescriptionReplacementPojo>> changeResults = componentTransformService.loadDescriptionReplacementTransformationJobResults(branchPath, jobId);
				writeDescriptionReplacementResults(changeResults, servletResponse);
			} else {
				List<ChangeResult<DescriptionPojo>> changeResults = componentTransformService.loadDescriptionTransformationJobResults(branchPath, jobId);
				writeDescriptionResults(changeResults, servletResponse);
			}

		} else if (componentType == ComponentType.AXIOM) {
			List<ChangeResult<AxiomPojo>> changeResults = componentTransformService.loadAxiomTransformationJobResults(branchPath, jobId);
			setTSVHeaders(jobId, servletResponse);
			writeAxiomResults(changeResults, servletResponse);
		} else if (componentType == ComponentType.CONCEPT) {
			List<ChangeResult<ConceptPojo>> changeResults = componentTransformService.loadConceptTransformationJobResults(branchPath, jobId);
			setTSVHeaders(jobId, servletResponse);
			writeConceptResults(changeResults, servletResponse);
		} else {
			throw new BusinessServiceException(format("Writing TSV for type %s is not yet implemented.", componentType));
		}
	}

	private void setTSVHeaders(@PathVariable String jobId, HttpServletResponse servletResponse) {
		servletResponse.setContentType("text/tsv; charset=UTF-8");
		servletResponse.setCharacterEncoding("UTF-8");
		servletResponse.setHeader("Content-Disposition", format("inline; filename=\"batch-transformation-results-%s.txt\"", jobId));
	}

	private void writeConceptResults(List<ChangeResult<ConceptPojo>> changeResults, HttpServletResponse servletResponse) throws IOException {
		try (PrintWriter writer = servletResponse.getWriter()) {
			writer.println(String.join(TAB,
					"concept_id",
					"fsn",
					"success",
					"message"));
			for (ChangeResult<ConceptPojo> changeResult : changeResults) {
				ConceptPojo conceptPojo = changeResult.getComponent();
				writer.println(String.join(TAB,
						conceptPojo.getConceptId(),
						conceptPojo.getFsn(),
						changeResult.getSuccess() != null ? changeResult.getSuccess().toString() : "The success status is unknown",
						changeResult.getMessageOrEmpty()
				));
			}
		}
	}

	private void writeDescriptionResults(List<ChangeResult<DescriptionPojo>> descriptionChangeResults, HttpServletResponse servletResponse) throws IOException {
		try (PrintWriter writer = servletResponse.getWriter()) {
			writer.println(String.join(TAB,
					"description_id",
					"concept_id",
					"term",
					"success",
					"message"));
			for (ChangeResult<DescriptionPojo> changeResult : descriptionChangeResults) {
				DescriptionPojo description = changeResult.getComponent();
				writer.println(String.join(TAB,
						description.getId(),
						description.getConceptId(),
						description.getTerm(),
						changeResult.getSuccess() != null ? changeResult.getSuccess().toString() : "The success status is unknown",
						changeResult.getMessageOrEmpty()
				));
			}
		}
	}

	private void writeDescriptionReplacementResults(List<ChangeResult<DescriptionReplacementPojo>> descriptionChangeResults, HttpServletResponse servletResponse) throws IOException {
		try (PrintWriter writer = servletResponse.getWriter()) {
			writer.println(String.join(TAB,
					"description_id",
					"concept_id",
					"success",
					"message"));
			for (ChangeResult<DescriptionReplacementPojo> changeResult : descriptionChangeResults) {
				DescriptionReplacementPojo descriptionReplacement = changeResult.getComponent();
				writer.println(String.join(TAB,
						descriptionReplacement.getId(),
						descriptionReplacement.getConceptId(),
						changeResult.getSuccess() != null ? changeResult.getSuccess().toString() : "The success status is unknown",
						changeResult.getMessageOrEmpty()
				));
			}
		}
	}

	private void writeAxiomResults(List<ChangeResult<AxiomPojo>> changeResults, HttpServletResponse servletResponse) throws IOException {
		try (PrintWriter writer = servletResponse.getWriter()) {
			writer.println(String.join(TAB,
					"concept_id",
					"axiom_id",
					"owlExpression",
					"success",
					"message"));
			for (ChangeResult<AxiomPojo> changeResult : changeResults) {
				AxiomPojo axiom = changeResult.getComponent();
				writer.println(String.join(TAB,
						axiom.getConceptId(),
						axiom.getId(),
						axiom.getOwlExpression(),
						changeResult.getSuccess() != null ? changeResult.getSuccess().toString() : "The success status is unknown",
						changeResult.getMessageOrEmpty()
				));
			}
		}
	}

	private boolean isValidVersionFormat(String version) {
		// Version format should be like "2.20", "1.0", etc. (numeric with dots)
		return version.matches("^\\d+(\\.\\d+)*$");
	}

	private int compareVersions(String v1, String v2) {
		String[] parts1 = v1.split("\\.");
		String[] parts2 = v2.split("\\.");
		int maxLength = Math.max(parts1.length, parts2.length);

		for (int i = 0; i < maxLength; i++) {
			int part1 = i < parts1.length ? Integer.parseInt(parts1[i]) : 0;
			int part2 = i < parts2.length ? Integer.parseInt(parts2[i]) : 0;
			if (part1 != part2) {
				return Integer.compare(part1, part2);
			}
		}
		return 0;
	}

}
