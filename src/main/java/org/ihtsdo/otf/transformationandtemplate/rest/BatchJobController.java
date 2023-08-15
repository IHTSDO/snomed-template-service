package org.ihtsdo.otf.transformationandtemplate.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

	@Operation(summary="List jobs")
	@RequestMapping(value = "/batch-jobs", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Set<Job> listJobs() throws IOException {
		return scriptManager.listJobs();
	}

	@Operation(summary="Run job")
	@ApiResponses({
			@ApiResponse(responseCode = "200",  description = "OK")
	})
	@RequestMapping(value="/batch-jobs", method= RequestMethod.POST)
	public JobRun runJob(@RequestBody JobRun jobRun) throws BusinessServiceException {
		return scriptManager.runJob(jobRun);
	}
}
