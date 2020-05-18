package org.ihtsdo.otf.transformationandtemplate.rest;

import io.kaicode.rest.util.branchpathrewrite.BranchPathUriUtil;
import io.swagger.annotations.ApiParam;
import org.ihtsdo.otf.rest.exception.BusinessServiceException;
import org.ihtsdo.otf.transformationandtemplate.domain.ComponentTransformationJob;
import org.ihtsdo.otf.transformationandtemplate.domain.ComponentTransformationRequest;
import org.ihtsdo.otf.transformationandtemplate.domain.TransformationRecipe;
import org.ihtsdo.otf.transformationandtemplate.service.componenttransform.ComponentTransformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Set;

@RestController
public class TransformationController {

	@Autowired
	private ComponentTransformService componentTransformService;

	@RequestMapping(value = "/{branchPath}/recipes", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Set<TransformationRecipe> listTransformationRecipes(@PathVariable String branchPath) throws IOException {
		return componentTransformService.listRecipes(BranchPathUriUtil.decodePath(branchPath));
	}

	@RequestMapping(value = "/{branchPath}/recipes/{recipe}/jobs", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntity<Void> createTransformationJob(
			@PathVariable String branchPath,
			@ApiParam("Recipe key")
			@PathVariable String recipe,
			@RequestParam("tsvFile") MultipartFile tsvFile,
			UriComponentsBuilder uriComponentsBuilder) throws BusinessServiceException, IOException {

		branchPath = BranchPathUriUtil.decodePath(branchPath);
		ComponentTransformationJob job = componentTransformService.queueBatchTransformation(new ComponentTransformationRequest(recipe, branchPath, tsvFile.getInputStream()));

		return ResponseEntity.created(uriComponentsBuilder.path("/{branchPath}/recipes/{recipe}/jobs/{jobId}")
				.buildAndExpand(branchPath, recipe, job.getId()).toUri()).build();
	}

	@RequestMapping(value = "/{branchPath}/recipes/{recipe}/jobs/{jobId}", method = RequestMethod.GET, produces = "application/json")
	public ComponentTransformationJob getTransformationJob(
			@PathVariable String branchPath,
			@ApiParam("Recipe key")
			@PathVariable String recipe,
			@PathVariable String jobId) throws BusinessServiceException {

		branchPath = BranchPathUriUtil.decodePath(branchPath);

		return componentTransformService.loadTransformationJob(branchPath, jobId);
	}

}
