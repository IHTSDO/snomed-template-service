package org.ihtsdo.otf.authoringtemplate.service;

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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Stream;

import org.ihtsdo.otf.authoringtemplate.domain.TemplateTransformation;
import org.ihtsdo.otf.authoringtemplate.service.exception.ServiceException;
import org.ihtsdo.otf.rest.client.snowowl.pojo.ConceptPojo;
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
	
	public TemplateTransformationResultService(@Value("${store.root-directory}") String storeRootDirectoryPath) {
		this.storeRootDirectory = new File(storeRootDirectoryPath);
	}

	public List<ConceptPojo> getResults(TemplateTransformation transformation) throws ServiceException {
		List<ConceptPojo> results = new ArrayList<>();
		File resultFile = getOrCreateTransformation(transformation, RESULTS_JSON);
		 try (Reader reader = new InputStreamReader(new FileInputStream(resultFile), "UTF-8")){
	            Gson gson = new GsonBuilder().create();
	            ConceptPojo pojo = gson.fromJson(reader, ConceptPojo.class);
	            results.add(pojo);
	      } catch (Exception e) {
			String msg = "Failed to get resutls for transformation " + transformation;
			logger.error(msg, e);
			throw new ServiceException(msg, e);
		} 
		return results;
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
		File file = new File(rootDir.getPath().toString() + "/" + relativePath);
		if (!file.exists()) {
			// Attempt to make directories
			File parentDirectory = file.getParentFile();
			if (!parentDirectory.mkdirs()) {
				logger.warn("Failed to create directory {}", parentDirectory.getAbsolutePath());
			}
		}
		return file;
	}
	
	public void writeResultsToFile(TemplateTransformation transformation, List<ConceptPojo> results) throws ServiceException {
		Gson gson = new Gson();
		File resultFile = getOrCreateTransformation(transformation, RESULTS_JSON);
		try (Writer writer = new FileWriter(resultFile)) {
			for (ConceptPojo pojo : results) {
				writer.append(gson.toJson(pojo));
			}
		} catch (Exception e) {
			throw new ServiceException("Failed to write results to disk for transformation id " + transformation.getTransformationId(), e);
		}
	}

	public void writeResultsToFile(File rootDir, List<ConceptPojo> results) throws IOException {
		Gson gson = new Gson();
		File resultFile = getFile(rootDir, RESULTS_JSON);
		try (Writer writer = new FileWriter(resultFile)) {
			for (ConceptPojo pojo : results) {
				writer.append(gson.toJson(pojo));
			}
		}
	}

	public void writeErrorMsgToFile(TemplateTransformation transformation, String errorMsg) {
		
	}

	public void update(TemplateTransformation transformation) throws ServiceException {
		File statusFile = getOrCreateTransformation(transformation, STATUS_JSON);
		transformation.setLastUpdatedDate(Calendar.getInstance().getTime());
		try (Writer writer = new FileWriter(statusFile)) {
			Gson gson = new Gson();
			writer.write(gson.toJson(transformation));
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
			 Gson gson = new GsonBuilder().create();
			 TemplateTransformation pojo = gson.fromJson(reader, TemplateTransformation.class);
			 return pojo;
		} catch (Exception e) {
			throw new ServiceException("Can't find any status file for transaction id " + transformationId, e);
		}
	}

	public List<ConceptPojo> getTransformationResults(String transformationId) throws ServiceException {
		File transformationFile = getTransformationDirById(transformationId);
		logger.info("Transformation dir " + transformationFile.getAbsolutePath());
		List<ConceptPojo> results = new ArrayList<>();
		File resultFile = getFile(transformationFile, RESULTS_JSON);
		 try (Reader reader = new InputStreamReader(new FileInputStream(resultFile), Constants.UTF_8)){
	            Gson gson = new GsonBuilder().create();
	            ConceptPojo pojo = gson.fromJson(reader, ConceptPojo.class);
	            results.add(pojo);
	      } catch (Exception e) {
			String msg = "Failed to get resutls for transformation id " + transformationId;
			logger.error(msg, e);
			throw new ServiceException(msg, e);
		} 
		return results;
	}
	
	
	private File getOrCreateTransformation(TemplateTransformation transformation, String relativePath) {
		File file = new File(storeRootDirectory, TRANSFORMATIONS  + "/" + DATE_FORMAT.format(transformation.getCreatedDate()) + "/" + transformation.getTransformationId() + "/" + relativePath);
		if (!file.exists()) {
			// Attempt to make directories
			File parentDirectory = file.getParentFile();
			if (!parentDirectory.mkdirs()) {
				logger.warn("Failed to create directory {}", parentDirectory.getAbsolutePath());
			}
		}
		return file;
	}
	
}
