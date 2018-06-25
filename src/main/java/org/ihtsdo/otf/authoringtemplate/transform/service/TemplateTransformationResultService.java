package org.ihtsdo.otf.authoringtemplate.transform.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.stream.Stream;

import org.ihtsdo.otf.authoringtemplate.domain.TemplateTransformation;
import org.ihtsdo.otf.authoringtemplate.service.Constants;
import org.ihtsdo.otf.authoringtemplate.service.exception.ServiceException;
import org.ihtsdo.otf.authoringtemplate.transform.TransformationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Service
public class TemplateTransformationResultService {

	private static final String TRANSFORMATIONS = "transformations";

	private File storeRootDirectory;

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private static final String RESULTS_JSON = "results.json";

	private static final String STATUS_JSON = "status.json";

	private Gson prettyJson;
	
	public TemplateTransformationResultService(@Value("${store.root-directory}") String storeRootDirectoryPath) {
		this.storeRootDirectory = new File(storeRootDirectoryPath);
		prettyJson = new GsonBuilder().setPrettyPrinting().create();
	}

	public TransformationResult getResult(TemplateTransformation transformation) throws ServiceException {
		TransformationResult result = null;
		File resultFile = getOrCreateTransformation(transformation, RESULTS_JSON);
		 try (Reader reader = new InputStreamReader(new FileInputStream(resultFile), "UTF-8")){
	            result = prettyJson.fromJson(reader, TransformationResult.class);
	      } catch (Exception e) {
			String msg = "Failed to get resutls for transformation " + transformation;
			logger.error(msg, e);
			throw new ServiceException(msg, e);
		} 
		return result;
	}
	
	private File getTransformationDirById(String transformationId) throws ServiceException {
		File file = new File(storeRootDirectory, TRANSFORMATIONS);
		if (!file.exists()) {
			throw new ServiceException("No transformation found with id " + transformationId);
		}
		 try (Stream<Path> paths = Files.find(
	                file.toPath(), 2,
	                (path,attrs) -> attrs.isDirectory()
	                        && path.toString().contains(transformationId))) {
	         return paths.findFirst().get().toFile();
	        } catch (Exception e) {
				throw new ServiceException("Failed to find any status file for transformation id " + transformationId);
			}
	}
	
	private File getFile(File rootDir, String relativePath) {
		return new File(rootDir.getPath().toString() + "/" + relativePath);
	}
	
	public void writeResultsToFile(TemplateTransformation transformation, TransformationResult result) throws ServiceException {
		if (result != null) {
			File resultFile = getOrCreateTransformation(transformation, RESULTS_JSON);
			
			try (Writer writer = new FileWriter(resultFile)) {
				prettyJson.toJson(result, writer);
			} catch (Exception e) {
				throw new ServiceException("Failed to write results to disk for transformation id " + transformation.getTransformationId(), e);
			}
		}
	}

	public void update(TemplateTransformation transformation) throws ServiceException {
		File statusFile = getOrCreateTransformation(transformation, STATUS_JSON);
		transformation.setLastUpdatedDate(Calendar.getInstance().getTime());
		try (Writer writer = new FileWriter(statusFile)) {
			writer.write(prettyJson.toJson(transformation));
		} catch (Exception e) {
			String errorMsg =  "Failed to update transformation status " + transformation;
			logger.error(errorMsg, e);
			throw new ServiceException(errorMsg, e);
		}
	}
	
	public TemplateTransformation getTemplateTransformation(String transformationId) throws ServiceException {
		File rootDir = getTransformationDirById(transformationId);
		File statusFile = getFile(rootDir, STATUS_JSON);
		try (Reader reader = new InputStreamReader(new FileInputStream(statusFile), Constants.UTF_8)) {
			 TemplateTransformation pojo = prettyJson.fromJson(reader, TemplateTransformation.class);
			 return pojo;
		} catch (Exception e) {
			throw new ServiceException("Can't find any status file for transaction id " + transformationId, e);
		}
	}

	public TransformationResult getTransformationResults(String transformationId) throws ServiceException {
		TemplateTransformation transformation = getTemplateTransformation(transformationId);
		logger.info("Found transformtion " + transformation);
		return getResult(transformation);
	}
	
	
	private File getOrCreateTransformation(TemplateTransformation transformation, String relativePath) throws ServiceException {
		File file = new File(storeRootDirectory, TRANSFORMATIONS  + "/" + DATE_FORMAT.format(transformation.getCreatedDate()) + "/" + transformation.getTransformationId() + "/" + relativePath);
		if (!file.exists()) {
			// Attempt to make directories
			File parentDirectory = file.getParentFile();
			if (parentDirectory.mkdirs()) {
				logger.info("created directory {}", parentDirectory.getAbsolutePath());
			}
			
			try {
				if (file.createNewFile()) {
					logger.debug("created file {}", file.getAbsolutePath());
				}
			} catch (IOException e) {
				throw new ServiceException("Failed to create file " + file.getAbsolutePath(), e);
			}
		}
		return file;
	}
}
