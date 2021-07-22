package org.ihtsdo.otf.transformationandtemplate.rest;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import org.ihtsdo.otf.rest.exception.BusinessServiceException;
import org.ihtsdo.otf.transformationandtemplate.service.script.ScriptManager;
import org.snomed.otf.scheduler.domain.Job;
import org.snomed.otf.scheduler.domain.JobRun;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Set;

@RestController
public class BatchJobController {

	public static final String TAB = "\t";
	@Autowired
	private ScriptManager scriptManager;

	@ApiOperation(value="List jobs")
	@RequestMapping(value = "/batch-jobs", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Set<Job> listJobs() throws IOException {
		return scriptManager.listJobs();
	}

	@ApiOperation(value="Run job")
	@ApiResponses({
			@ApiResponse(code = 200, message = "OK")
	})
	@RequestMapping(value="/batch-jobs", method= RequestMethod.POST)
	public JobRun runJob(@RequestBody JobRun jobRun) throws BusinessServiceException {
		return scriptManager.runJob(jobRun);
	}
}
