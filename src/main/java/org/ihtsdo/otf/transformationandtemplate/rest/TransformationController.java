package org.ihtsdo.otf.transformationandtemplate.rest;

import io.kaicode.rest.util.branchpathrewrite.BranchPathUriUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.DescriptionPojo;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.SnomedComponent;
import org.ihtsdo.otf.rest.exception.BusinessServiceException;
import org.ihtsdo.otf.transformationandtemplate.domain.ComponentTransformationJob;
import org.ihtsdo.otf.transformationandtemplate.domain.ComponentTransformationRequest;
import org.ihtsdo.otf.transformationandtemplate.domain.ComponentType;
import org.ihtsdo.otf.transformationandtemplate.domain.TransformationRecipe;
import org.ihtsdo.otf.transformationandtemplate.service.client.ChangeResult;
import org.ihtsdo.otf.transformationandtemplate.service.componenttransform.ComponentTransformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

import static java.lang.String.format;

@RestController
public class TransformationController {

	public static final String TAB = "\t";
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

	@RequestMapping(value = "/{branchPath}/recipes/{recipe}/jobs/{jobId}/result-tsv", method = RequestMethod.GET, produces = "text/csv")
	@ResponseBody
	public void getTransformationJobResultAsTsv(
			@PathVariable String branchPath,
			@ApiParam("Recipe key")
			@PathVariable String recipe,
			@PathVariable String jobId,
			HttpServletResponse servletResponse) throws BusinessServiceException, IOException {

		branchPath = BranchPathUriUtil.decodePath(branchPath);

		TransformationRecipe transformationRecipe = componentTransformService.loadRecipeOrThrow(branchPath, recipe);
		ComponentType componentType = transformationRecipe.getComponent();

		if (componentType == ComponentType.DESCRIPTION) {
			List<ChangeResult<DescriptionPojo>> changeResults = componentTransformService.loadDescriptionTransformationJobResults(branchPath, jobId);
			servletResponse.setContentType("text/tsv");
			servletResponse.setHeader("Content-Disposition", format("inline; filename=\"batch-transformation-results-%s.txt\"", jobId));
			writeDescriptionResults(changeResults, servletResponse);
		} else {
			throw new BusinessServiceException(format("Writing TSV for type %s is not yet implemented.", componentType));
		}
	}

	private void writeDescriptionResults(List<ChangeResult<DescriptionPojo>> descriptionChangeResults, HttpServletResponse servletResponse) throws IOException {
		try (PrintWriter writer = servletResponse.getWriter()) {
			writer.println(String.join(TAB,
					"description_id",
					"concept_id",
					"term",
					"success",
					"message"));
			for (ChangeResult<DescriptionPojo> changeResult : descriptionChangeResults) {
				DescriptionPojo description = changeResult.getComponent();
				writer.println(String.join(TAB,
						description.getId(),
						description.getConceptId(),
						description.getTerm(),
						changeResult.getSuccess().toString(),
						changeResult.getMessageOrEmpty()
				));

			}
		}

	}

}
