package org.ihtsdo.otf.transformationandtemplate.service.script;

import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.ihtsdo.otf.exception.TermServerScriptException;
import org.ihtsdo.otf.transformationandtemplate.service.PermissionService;
import org.ihtsdo.otf.transformationandtemplate.service.client.*;
import org.ihtsdo.otf.utils.ExceptionUtils;
import org.ihtsdo.sso.integration.SecurityUtil;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snomed.otf.scheduler.domain.Job;
import org.snomed.otf.scheduler.domain.JobRun;
import org.snomed.otf.scheduler.domain.JobStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import org.springframework.context.ApplicationContext;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class ScriptManager {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private SnowstormClientFactory snowstormClientFactory;

	@Autowired
	private AuthoringServicesClientFactory authoringServicesClientFactory;

	@Autowired
	private PermissionService permissionService;

	@Autowired
	private ApplicationContext applicationContext;
	
	@Value("${template-service.script.SEP.out-of-scope}")
	private String SEPOutOfScope;

	@Value("${batch-jobs.required.roles}")
	private Set<String> requiredRoles;
	
	public enum ConfigItem { SEP_OUT_OF_SCOPE }
	
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
		logger.info("Script Manager Initialising");
		knownJobMap = new HashMap<>();
		knownJobs = new HashSet<>();
		
		//Now what jobs do I know about?
		Reflections reflections = new Reflections(new ConfigurationBuilder().forPackages("org.ihtsdo.otf.transformationandtemplate.service.script"));
		Set<Class<? extends JobClass>> jobClasses = reflections.getSubTypesOf(JobClass.class);
		
		logger.info("Job Manager detected {} job classes", jobClasses.size());
		for (Class<? extends JobClass> jobClass : jobClasses) {
			if (!Modifier.isAbstract(jobClass.getModifiers())) {
				try {
						Job thisJob = instantiate(jobClass, null, null).getJob();
						logger.info("Registering known job: {}", thisJob.getName());
						knownJobMap.put(thisJob.getName(), jobClass);
						knownJobs.add(thisJob);
				} catch (Exception e) {
					logger.error("Failed to register job {}", jobClass, e);
				}
			}
		}
	}

	public JobRun runJob(JobRun jobRun) {
		checkPermissionOrThrow(jobRun);

		//Create a task before running the task in another thread and returning
		logger.info ("Received request to run {}", jobRun);
		try {
			if (StringUtils.isEmpty(jobRun.getJobName())) {
				throw new TermServerScriptException("Job run request did not specify job name");
			} else if (!knownJobMap.containsKey(jobRun.getJobName())) {
				throw new TermServerScriptException("Unable to run unknown job '" + jobRun.getJobName() + "'");
			}
			JobClass jobInstance = instantiate(knownJobMap.get(jobRun.getJobName()), jobRun, applicationContext);
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

	private void checkPermissionOrThrow(JobRun jobRun) {
		if (requiredRoles.isEmpty()) return;
		if (StringUtils.isEmpty(jobRun.getProject())) {
			throw new IllegalArgumentException("Project key must be provided");
		}
		AuthoringServicesClient asClient = getASClient();
		AuthoringProject project = asClient.retrieveProject(jobRun.getProject());
		boolean hasPermission = false;
		for (String role : requiredRoles) {
			if (permissionService.userHasRoleOnBranch(role, project.getBranchPath(), SecurityUtil.getAuthentication())) {
				hasPermission = true;
				break;
			}
		}
		if (!hasPermission) {
			throw new AccessDeniedException("You are not allowed to run this job");
		}
	}

	private JobClass instantiate(Class<? extends JobClass> jobClass, JobRun jobRun, ApplicationContext appContext) throws TermServerScriptException {
		try {
			Constructor<? extends JobClass> constructor = jobClass.getDeclaredConstructor(JobRun.class, this.getClass(), ApplicationContext.class);
            return constructor.newInstance(jobRun, this, appContext);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new TermServerScriptException("Failed to instantiate " + jobClass.getName(), e);
		}
	}

	public String getConfig(ConfigItem configItem) {
        return switch (configItem) {
            case SEP_OUT_OF_SCOPE -> SEPOutOfScope;
            default -> throw new IllegalArgumentException("Unrecognised config item " + configItem);
        };
	}


}
