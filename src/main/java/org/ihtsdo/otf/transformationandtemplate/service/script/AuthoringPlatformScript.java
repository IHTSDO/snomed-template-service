package org.ihtsdo.otf.transformationandtemplate.service.script;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.ihtsdo.otf.exception.TermServerScriptException;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.Project;
import org.ihtsdo.otf.transformationandtemplate.service.client.AuthoringServicesClient;
import org.ihtsdo.otf.transformationandtemplate.service.client.AuthoringTask;
import org.ihtsdo.otf.transformationandtemplate.service.client.SnowstormClient;
import org.ihtsdo.otf.utils.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snomed.otf.scheduler.domain.JobRun;
import org.snomed.otf.scheduler.domain.JobStatus;
import org.snomed.otf.script.Script;

public abstract class AuthoringPlatformScript extends Script implements JobClass {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());

	protected JobRun jobRun;
	protected Project project;
	protected AuthoringTask task;
	protected ScriptManager mgr;
	
	protected SnowstormClient tsClient;
	protected AuthoringServicesClient asClient;
	
	AuthoringPlatformScript (JobRun jobRun, ScriptManager mgr) {
		this.jobRun = jobRun;
		this.mgr = mgr;
	}
	
	@Override
	public boolean isOffline() {
		return false;
	}

	@Override
	public JobRun getJobRun() {
		return jobRun;
	}

	@Override
	public Project getProject() {
		return project;
	}

	@Override
	public String detectReleaseBranch() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getEnv() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public String getReportName() {
		return this.getClass().getSimpleName();
	}
	
	public void initialise() throws TermServerScriptException {
		String taskTitle = jobRun.getJobName() + " - Initialising";
		logger.info(taskTitle);
		//Clients are user specific so we have to create new ones for each job
		asClient = mgr.getASClient();
		tsClient = mgr.getTSClient();
		try {
			task = asClient.createTask(jobRun.getProject(), taskTitle, "Processing Report TBC");
			jobRun.setStatus(JobStatus.Scheduled);
			jobRun.setResultUrl(task.getKey());
			logger.info("Created task " + task.getKey());
		} catch (Exception e) {
			throw new TermServerScriptException("Unable to create task in " + jobRun.getProject(), e);
		}
	}
	
	public void run() {
		info ("Running " + this.getClass().getSimpleName());
		updateTaskTitleState("Running");
		tsClient.createBranch(task.getBranchPath());
		try {
			runJob();
			updateTaskTitleState("Complete");
			info (task.getKey() + " batch job completed successfully");
		} catch (Exception e) {
			String msg = ExceptionUtils.getExceptionCause("Batch job " + task.getKey() + " failed", e);
			info (msg);
			updateTaskTitleState("Failed");
			updateTaskDescription(msg);
		}
	}

	abstract public void runJob();
	
	public void updateTaskTitleState(String newState) {
		String taskTitle = jobRun.getJobName() + " - " + newState;
		AuthoringTask taskChanges = task.clone();
		taskChanges.setSummary(taskTitle);
		asClient.updateAuthoringTaskNotNullFieldsAreSet(taskChanges);
	}
	
	public void updateTaskDescription(String desc) {
		AuthoringTask taskChanges = task.clone();
		taskChanges.setDescription(desc);
		asClient.updateAuthoringTaskNotNullFieldsAreSet(taskChanges);
	}

	public void report(Object... details) {
		//TODO Output to report
		logger.info(Arrays.stream(details)
				.map(obj -> obj.toString())
				.collect(Collectors.joining(" ")));
	}
}
