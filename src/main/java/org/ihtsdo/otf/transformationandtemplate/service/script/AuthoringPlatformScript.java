package org.ihtsdo.otf.transformationandtemplate.service.script;

import org.apache.commons.lang3.StringUtils;
import org.ihtsdo.otf.exception.TermServerScriptException;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.IConcept;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.Project;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.RefsetMemberPojo;
import org.ihtsdo.otf.transformationandtemplate.domain.Concept;
import org.ihtsdo.otf.transformationandtemplate.service.ConstantStrings;
import org.ihtsdo.otf.transformationandtemplate.service.client.*;
import org.ihtsdo.otf.utils.ExceptionUtils;
import org.ihtsdo.otf.utils.SnomedUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snomed.otf.scheduler.domain.JobRun;
import org.snomed.otf.scheduler.domain.JobStatus;
import org.snomed.otf.script.Script;
import org.snomed.otf.script.dao.*;

public abstract class AuthoringPlatformScript extends Script implements JobClass {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

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
	public String detectReleaseBranch() {
		return null;  //AP Scripts don't run against release branch
	}

	@Override
	public String getEnv() {
		String apiURL = SnowstormClientFactory.instance().getApiUrl();
		if (apiURL.contains("dev-")) {
			return "DEV";
		} else if (apiURL.contains("uat-")) {
			return "UAT";
		}
		return "PROD";
	}
	
	@Override
	public String getReportName() {
		return this.getClass().getSimpleName();
	}
	
	public void initialise() throws TermServerScriptException {
		String taskTitle = jobRun.getJobName() + " - Initialising";
		logger.info(taskTitle);
		
		if (StringUtils.isEmpty(jobRun.getProject())) {
			throw new TermServerScriptException("'project' parameter missing from jobRun request");
		}
		//Clients are user specific so we have to create new ones for each job
		asClient = mgr.getASClient();
		tsClient = mgr.getTSClient();
		initialiseReportConfiguration(jobRun);
		ReportSheetManager.targetFolderId = "1fMHFzq5rP1WGmq3AXA2C2RwYXADzGLxR";
		try {
			task = asClient.createTask(jobRun.getProject(), taskTitle, "Report in progress");
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
		String branchPath = task.getBranchPath();
		tsClient.createBranch(branchPath);
		tsClient.setAuthorFlag(branchPath, ConstantStrings.AUTHOR_FLAG_BATCH_CHANGE, "true");
		try {
			String url = createGoogleSheet();
			updateTaskDescription(getLink(url), false);
			info ("Updated task with result sheet url");
			runJob();
			info (this.getClass().getName() + " processing complete.  Updating task " + task.getKey());
			updateTaskTitleState("Complete");
			info (task.getKey() + " " + this.getClass().getSimpleName() + " batch job completed successfully");
		} catch (Exception e) {
			String msg = ExceptionUtils.getExceptionCause("Batch job " + task.getKey() + " failed", e);
			info (msg);
			try {
				updateTaskTitleState("Failed");
				updateTaskDescription(msg, true);
			} catch (Exception e2) {
				error("Failure during processing (see above) coupled with failure to update task to indicate failure", e2);
			} finally {
				try {
					reportManager.flushFiles(true, false);
				} catch (Exception e3) {
					error("Failure during cleanup", e3);
				}
			}
		}
	}
	
	protected void percentageComplete(int i) {
		info (i + "% complete");
		updateTaskTitleState("Running - " + i + "%");
	}

	private String getLink(String url) {
		return "View <a target=\"_blank\" href=\"" + url + "\">report</a>";
	}

	private String createGoogleSheet() throws TermServerScriptException {
		reportManager = ReportManager.create(this, reportConfiguration);
		reportManager.setTabNames(new String[] {"Process", "Summary"});
		String[] columnHeadings = new String[] {"Task,SCTID,FSN,Semtag,Severity,Action,Info,Detail1, Detail2, ",
												"Issue, Count"};
		reportManager.initialiseReportFiles(columnHeadings);
		return reportManager.getUrl();
	}

	abstract public void runJob() throws TermServerScriptException;
	
	public void updateTaskTitleState(String newState) {
		String taskTitle = jobRun.getJobName() + " - " + newState;
		AuthoringTask taskChanges = task.clone();
		taskChanges.setSummary(taskTitle);
		asClient.updateAuthoringTaskNotNullFieldsAreSet(taskChanges);
	}
	
	public void updateTaskDescription(String desc, boolean append) {
		AuthoringTask taskChanges = task.clone();
		String newText = desc;
		if (append) {
			newText = task.getDescription() + "<br/>" + desc;
		}
		taskChanges.setDescription(newText);
		taskChanges.setStatus("IN_PROGRESS");
		asClient.updateAuthoringTaskNotNullFieldsAreSet(taskChanges);
	}
	
	@Override
	public DataUploader getReportDataUploader() {
		//Currently no requirement for AP scripts to upload to S3
		return null;
	}
	
	protected void removeRefsetMember(IConcept c, RefsetMemberPojo rm) throws TermServerScriptException {
		//Has this rm been published?
		if (StringUtils.isEmpty(rm.getReleasedEffectiveTime())) {
			info("Deleting " + rm);
			tsClient.deleteRefsetMember(task.getBranchPath(), rm);
			report(c, Severity.LOW, ReportActionType.REFSET_MEMBER_DELETED, "", rm);
		} else {
			info("Inactivating " + rm);
			rm.setActive(false);
			tsClient.updateRefsetMember(task.getBranchPath(), rm);
			report(c, Severity.LOW, ReportActionType.REFSET_MEMBER_INACTIVATED, "", rm);
		}
	}
	

	protected boolean report(IConcept c, Severity severity, ReportActionType action, Object... details) throws TermServerScriptException {
		String semTag = SnomedUtils.deconstructFSN(c.getFsnTerm(), true)[1];
		return report(TAB_0, task, c.getConceptId(), c.getFsnTerm(), semTag, severity, action, details);
	}


}
