package org.ihtsdo.otf.authoringtemplate.transform.service;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Calendar;

import org.ihtsdo.otf.authoringtemplate.TransformationResourceConfiguration;
import org.ihtsdo.otf.authoringtemplate.service.Constants;
import org.ihtsdo.otf.authoringtemplate.service.exception.ServiceException;
import org.ihtsdo.otf.authoringtemplate.transform.ResourcePathHelper;
import org.ihtsdo.otf.authoringtemplate.transform.TemplateTransformation;
import org.ihtsdo.otf.authoringtemplate.transform.TransformationResult;
import org.ihtsdo.otf.dao.resources.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Service
public class TemplateTransformationResultService {

	private final ResourceManager transformationResourceManager;

	private Logger logger = LoggerFactory.getLogger(getClass());

	private Gson prettyJson;

	
	public TemplateTransformationResultService( 
			@Autowired TransformationResourceConfiguration transformationResourceConfiguration,
			@Autowired ResourceLoader cloudResourceLoader) {
		
		prettyJson = new GsonBuilder().setPrettyPrinting().create();
		this.transformationResourceManager = new ResourceManager(transformationResourceConfiguration, cloudResourceLoader);
	}

	public TransformationResult getResult(TemplateTransformation transformation) throws ServiceException {
		return getResult(transformation.getTransformationId());
	}
	
	public TransformationResult getResult(String transformationId) throws ServiceException {
		TransformationResult result = null;
		 try (InputStream input = transformationResourceManager.readResourceStream(ResourcePathHelper.getResultPath(transformationId));
			  Reader reader = new InputStreamReader(input, "UTF-8")){
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
			try ( OutputStream output = transformationResourceManager.writeResourceStream(resourcePath);
				 Writer writer = new OutputStreamWriter(output)) {
				 prettyJson.toJson(result, writer);
			} catch (Exception e) {
				throw new ServiceException("Failed to write results to disk for transformation id " + transformation.getTransformationId(), e);
			}
		}
	}

	public void update(TemplateTransformation transformation) throws ServiceException {
		String statusPath = ResourcePathHelper.getStatusPath(transformation.getTransformationId());
		transformation.setLastUpdatedDate(Calendar.getInstance().getTime());
		
		try (OutputStream output = transformationResourceManager.writeResourceStream(statusPath);
				 Writer writer = new OutputStreamWriter(output)) {
				 writer.write(prettyJson.toJson(transformation));
		} catch (Exception e) {
			String errorMsg =  "Failed to update transformation status " + transformation;
			logger.error(errorMsg, e);
			throw new ServiceException(errorMsg, e);
		}
	}
	
	public TemplateTransformation getTemplateTransformation(String transformationId) throws ServiceException {
		String statusPath = ResourcePathHelper.getStatusPath(transformationId);
		try (InputStream input = transformationResourceManager.readResourceStream(statusPath);
			 Reader reader = new InputStreamReader(input, Constants.UTF_8)) {
			 TemplateTransformation pojo = prettyJson.fromJson(reader, TemplateTransformation.class);
			 return pojo;
		} catch (Exception e) {
			throw new ServiceException("Can't find any status file for transaction id " + transformationId, e);
		}
	}
}
