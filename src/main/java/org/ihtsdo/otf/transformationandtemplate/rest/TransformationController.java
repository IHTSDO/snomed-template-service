package org.ihtsdo.otf.transformationandtemplate.rest;

import io.kaicode.rest.util.branchpathrewrite.BranchPathUriUtil;
import io.swagger.v3.oas.annotations.Parameter;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.AxiomPojo;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.DescriptionPojo;
import org.ihtsdo.otf.rest.exception.BusinessServiceException;
import org.ihtsdo.otf.transformationandtemplate.domain.*;
import org.ihtsdo.otf.transformationandtemplate.service.client.ChangeResult;
import org.ihtsdo.otf.transformationandtemplate.service.client.DescriptionReplacementPojo;
import org.ihtsdo.otf.transformationandtemplate.service.componenttransform.ComponentTransformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpServletResponse;
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

			@Parameter( description = "Recipe key")
			@PathVariable String recipe,

			@Parameter( description = "Batch size")
			@RequestParam(defaultValue = "100") int batchSize,
			@Parameter( description = "Project key (optional - batches split between tasks)")
			@RequestParam(required = false) String projectKey,

			@Parameter( description = "Task title (optional)")
			@RequestParam(required = false) String taskTitle,

			@Parameter( description = "Task assignee (optional)")
			@RequestParam(required = false) String taskAssignee,

			@Parameter( description = "Task reviewer (optional)")
			@RequestParam(required = false) String taskReviewer,

			@Parameter(description = "tsvFile") MultipartFile tsvFile,
			UriComponentsBuilder uriComponentsBuilder,

			@Parameter( description = "Skip SNOMED Drools validation (optional)")
			@RequestParam(required = false, defaultValue = "false") boolean skipDroolsValidation) throws BusinessServiceException, IOException {

		branchPath = BranchPathUriUtil.decodePath(branchPath);

		ComponentTransformationJob job = componentTransformService.queueBatchTransformation
				(new ComponentTransformationRequest(recipe, branchPath, projectKey, taskTitle, taskAssignee, taskReviewer, batchSize, tsvFile.getInputStream(), skipDroolsValidation));

		return ResponseEntity.created(uriComponentsBuilder.path("/{branchPath}/recipes/{recipe}/jobs/{jobId}")
				.buildAndExpand(branchPath, recipe, job.getId()).toUri()).build();
	}

	@RequestMapping(value = "/{branchPath}/recipes/{recipe}/jobs/{jobId}", method = RequestMethod.GET, produces = "application/json")
	public ComponentTransformationJob getTransformationJob(
			@PathVariable String branchPath,
			@Parameter( description = "Recipe key")
			@PathVariable String recipe,
			@PathVariable String jobId) throws BusinessServiceException {

		branchPath = BranchPathUriUtil.decodePath(branchPath);

		return componentTransformService.loadTransformationJob(branchPath, jobId);
	}

	@RequestMapping(value = "/{branchPath}/recipes/{recipe}/jobs/{jobId}/result-tsv", method = RequestMethod.GET, produces = "text/csv")
	@ResponseBody
	public void getTransformationJobResultAsTsv(
			@PathVariable String branchPath,
			@Parameter( description = "Recipe key")
			@PathVariable String recipe,
			@PathVariable String jobId,
			HttpServletResponse servletResponse) throws BusinessServiceException, IOException {

		branchPath = BranchPathUriUtil.decodePath(branchPath);

		TransformationRecipe transformationRecipe = componentTransformService.loadRecipeOrThrow(branchPath, recipe);
		ComponentType componentType = transformationRecipe.getComponent();
		ChangeType chanageType = transformationRecipe.getChangeType();

		if (componentType == ComponentType.DESCRIPTION) {
			setTSVHeaders(jobId, servletResponse);
			if (ChangeType.REPLACE == chanageType) {
				List<ChangeResult<DescriptionReplacementPojo>> changeResults = componentTransformService.loadDescriptionReplacementTransformationJobResults(branchPath, jobId);
				writeDescriptionReplacementResults(changeResults, servletResponse);
			} else {
				List<ChangeResult<DescriptionPojo>> changeResults = componentTransformService.loadDescriptionTransformationJobResults(branchPath, jobId);
				writeDescriptionResults(changeResults, servletResponse);
			}

		} else if (componentType == ComponentType.AXIOM) {
			List<ChangeResult<AxiomPojo>> changeResults = componentTransformService.loadAxiomTransformationJobResults(branchPath, jobId);
			setTSVHeaders(jobId, servletResponse);
			writeAxiomResults(changeResults, servletResponse);
		} else {
			throw new BusinessServiceException(format("Writing TSV for type %s is not yet implemented.", componentType));
		}
	}

	private void setTSVHeaders(@PathVariable String jobId, HttpServletResponse servletResponse) {
		servletResponse.setContentType("text/tsv; charset=UTF-8");
		servletResponse.setCharacterEncoding("UTF-8");
		servletResponse.setHeader("Content-Disposition", format("inline; filename=\"batch-transformation-results-%s.txt\"", jobId));
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

	private void writeDescriptionReplacementResults(List<ChangeResult<DescriptionReplacementPojo>> descriptionChangeResults, HttpServletResponse servletResponse) throws IOException {
		try (PrintWriter writer = servletResponse.getWriter()) {
			writer.println(String.join(TAB,
					"description_id",
					"concept_id",
					"success",
					"message"));
			for (ChangeResult<DescriptionReplacementPojo> changeResult : descriptionChangeResults) {
				DescriptionReplacementPojo descriptionReplacement = changeResult.getComponent();
				writer.println(String.join(TAB,
						descriptionReplacement.getId(),
						descriptionReplacement.getConceptId(),
						changeResult.getSuccess().toString(),
						changeResult.getMessageOrEmpty()
				));
			}
		}
	}

	private void writeAxiomResults(List<ChangeResult<AxiomPojo>> changeResults, HttpServletResponse servletResponse) throws IOException {
		try (PrintWriter writer = servletResponse.getWriter()) {
			writer.println(String.join(TAB,
					"concept_id",
					"axiom_id",
					"owlExpression",
					"success",
					"message"));
			for (ChangeResult<AxiomPojo> changeResult : changeResults) {
				AxiomPojo axiom = changeResult.getComponent();
				writer.println(String.join(TAB,
						axiom.getConceptId(),
						axiom.getId(),
						axiom.getOwlExpression(),
						changeResult.getSuccess().toString(),
						changeResult.getMessageOrEmpty()
				));
			}
		}
	}

}
