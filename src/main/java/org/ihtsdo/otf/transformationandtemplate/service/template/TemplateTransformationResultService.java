package org.ihtsdo.otf.transformationandtemplate.service.template;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.ihtsdo.otf.transformationandtemplate.TransformationResourceConfiguration;
import org.ihtsdo.otf.transformationandtemplate.service.exception.ServiceException;
import org.ihtsdo.otf.transformationandtemplate.service.ResourcePathHelper;
import org.ihtsdo.otf.resourcemanager.ResourceManager;
import org.ihtsdo.otf.rest.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;

import static org.ihtsdo.otf.transformationandtemplate.service.template.TransformationStatus.COMPLETED;
import static org.ihtsdo.otf.transformationandtemplate.service.template.TransformationStatus.COMPLETED_WITH_FAILURE;

@Service
public class TemplateTransformationResultService {

	private final ResourceManager transformationResourceManager;

	private Gson prettyJson;

	private Logger logger = LoggerFactory.getLogger(getClass());

	public TemplateTransformationResultService(
			@Autowired TransformationResourceConfiguration transformationResourceConfiguration,
			@Autowired ResourceLoader cloudResourceLoader) {
		
		prettyJson = new GsonBuilder().setPrettyPrinting().create();
		this.transformationResourceManager = new ResourceManager(transformationResourceConfiguration, cloudResourceLoader);
	}

	public TransformationResult getResult(String transformationId) throws ServiceException {
		TemplateTransformation transformation = getTemplateTransformation(transformationId);
		TransformationStatus status = transformation.getStatus();
		if (COMPLETED != status && COMPLETED_WITH_FAILURE != status) {
			throw new IllegalStateException("No results are available for transformation id " + transformationId + " due to the status is " + status);
		}
		
		TransformationResult result;
		try (InputStream input = transformationResourceManager.readResourceStream(ResourcePathHelper.getResultPath(transformationId));
				Reader reader = new InputStreamReader(input, StandardCharsets.UTF_8)) {

			result = prettyJson.fromJson(reader, TransformationResult.class);
		} catch (Exception e) {
			String msg = "Failed to get resutls for transformation " + transformationId;
			logger.error(msg, e);
			throw new ServiceException(msg, e);
		}
		return result;
	}
	
	public void writeResultsToFile(TemplateTransformation transformation, TransformationResult result) throws ServiceException {
		if (result != null) {
			String resourcePath = ResourcePathHelper.getResultPath(transformation.getTransformationId());
			try ( OutputStream output = transformationResourceManager.writeResourceStream(resourcePath);Writer writer = new OutputStreamWriter(output)) {
				prettyJson.toJson(result, writer);
			} catch (Exception e) {
				throw new ServiceException("Failed to write results to disk for transformation id " + transformation.getTransformationId(), e);
			}
		}
	}

	public void update(TemplateTransformation transformation) throws ServiceException {
		String statusPath = ResourcePathHelper.getStatusPath(transformation.getTransformationId());
		transformation.setLastUpdatedDate(Calendar.getInstance().getTime());
		logger.info("Template transformation id {} for branch {} is {}", transformation.getTransformationId(), transformation.getBranchPath(),
				transformation.getStatus().toString());
		try (OutputStream output = transformationResourceManager.writeResourceStream(statusPath);
			Writer writer = new OutputStreamWriter(output)) {
			writer.write(prettyJson.toJson(transformation));
		} catch (IOException e) {
			String errorMsg = "Failed to update transformation status " + transformation;
			logger.error(errorMsg, e);
			throw new ServiceException(errorMsg, e);
		}
	}
	
	public TemplateTransformation getTemplateTransformation(String transformationId) {
		String statusPath = ResourcePathHelper.getStatusPath(transformationId);
		try (InputStream input = transformationResourceManager.readResourceStream(statusPath);
			Reader reader = new InputStreamReader(input, StandardCharsets.UTF_8)) {
			return prettyJson.fromJson(reader, TemplateTransformation.class);
		} catch (IOException e) {
			throw new ResourceNotFoundException("Can't find any template transformation with id " + transformationId, e);
		}
	}
}
