package org.ihtsdo.otf.transformationandtemplate.service.script;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.ihtsdo.otf.exception.TermServerScriptException;
import org.ihtsdo.otf.transformationandtemplate.service.client.AuthoringServicesClient;
import org.ihtsdo.otf.transformationandtemplate.service.client.AuthoringServicesClientFactory;
import org.ihtsdo.otf.transformationandtemplate.service.client.AuthoringTask;
import org.ihtsdo.otf.transformationandtemplate.service.client.SnowstormClient;
import org.ihtsdo.otf.transformationandtemplate.service.client.SnowstormClientFactory;
import org.ihtsdo.otf.utils.ExceptionUtils;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snomed.otf.scheduler.domain.Job;
import org.snomed.otf.scheduler.domain.JobRun;
import org.snomed.otf.scheduler.domain.JobStatus;
import org.snomed.otf.script.Script;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.snomed.otf.script.Script.info;

@Service
public class ScriptManager {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private SnowstormClientFactory snowstormClientFactory;

	@Autowired
	private AuthoringServicesClientFactory authoringServicesClientFactory;
	
	Map<String, Class<? extends JobClass>> knownJobMap;
	Set<Job> knownJobs;
	ExecutorService executor = Executors.newFixedThreadPool(3);

	@PostConstruct
	private void init() {
		populateKnownJobs();
	}
	
	SnowstormClient getTSClient() { 
		return snowstormClientFactory.getClientForCurrentUser();
	}
	
	AuthoringServicesClient getASClient() {
		return authoringServicesClientFactory.getClientForCurrentUser();
	}

	public Set<Job> listJobs() {
		if (knownJobs == null) {
			populateKnownJobs();
		}
		return knownJobs;
	}

	private void populateKnownJobs() {
		Script.info("Script Manager Initialising");
		knownJobMap = new HashMap<>();
		knownJobs = new HashSet<>();
		
		//Now what jobs do I know about?
		Reflections reflections = new Reflections("org.ihtsdo.otf.transformationandtemplate.service.script");
		Set<Class<? extends JobClass>> jobClasses = reflections.getSubTypesOf(JobClass.class);
		
		logger.info("Job Manager detected {} job classes", jobClasses.size());
		for (Class<? extends JobClass> jobClass : jobClasses) {
			if (!Modifier.isAbstract(jobClass.getModifiers())) {
				try {
						Job thisJob = instantiate(jobClass, null).getJob();
						info("Registering known job: " + thisJob.getName());
						knownJobMap.put(thisJob.getName(), jobClass);
						knownJobs.add(thisJob);
				} catch (Exception e) {
					logger.error("Failed to register job {}", jobClass, e);
				}
			}
		}
		
	}

	public JobRun runJob(JobRun jobRun) {
		//Create a task before running the task in another thread and returning
		info ("Received request to run " + jobRun);
		try {
			if (StringUtils.isEmpty(jobRun.getJobName())) {
				throw new TermServerScriptException("Job run request did not specify job name");
			} else if (!knownJobMap.containsKey(jobRun.getJobName())) {
				throw new TermServerScriptException("Unable to run unknown job '" + jobRun.getJobName() + "'");
			}
			JobClass jobInstance = instantiate(knownJobMap.get(jobRun.getJobName()), jobRun);
			jobInstance.initialise();
			executor.execute(jobInstance);
		} catch (Exception e) {
			jobRun.setStatus(JobStatus.Failed);
			String msg = "Failed to start " + jobRun.getJobName();
			logger.error(msg, e);
			jobRun.setDebugInfo(ExceptionUtils.getExceptionCause(msg, e));
		}
		return jobRun;
	}

	private JobClass instantiate(Class<? extends JobClass> jobClass, JobRun jobRun) throws TermServerScriptException {
		try {
			Constructor<? extends JobClass> constructor = jobClass.getDeclaredConstructor(JobRun.class, this.getClass());
			if (constructor == null) {
				throw new TermServerScriptException(jobClass.getName() + " does not provide a (jobRun, ScriptManager) constructor");
			}
			return constructor.newInstance(jobRun, this);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new TermServerScriptException("Failed to instantiate " + jobClass.getName(), e);
		}
	}


}
