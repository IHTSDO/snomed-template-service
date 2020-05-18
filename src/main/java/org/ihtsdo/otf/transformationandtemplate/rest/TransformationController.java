package org.ihtsdo.otf.transformationandtemplate.rest;

import io.kaicode.rest.util.branchpathrewrite.BranchPathUriUtil;
import io.swagger.annotations.ApiParam;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.SnomedComponent;
import org.ihtsdo.otf.rest.exception.BusinessServiceException;
import org.ihtsdo.otf.transformationandtemplate.domain.ComponentTransformationRequest;
import org.ihtsdo.otf.transformationandtemplate.domain.TransformationRecipe;
import org.ihtsdo.otf.transformationandtemplate.service.client.ChangeResult;
import org.ihtsdo.otf.transformationandtemplate.service.componenttransform.ComponentTransformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
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

	@RequestMapping(value = "/{branchPath}/recipes/{recipe}/run", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public List<ChangeResult<? extends SnomedComponent>> runTransformationRecipe(
			@PathVariable String branchPath,
			@ApiParam("Recipe key")
			@PathVariable String recipe,
			@RequestParam("tsvFile") MultipartFile tsvFile) throws BusinessServiceException, IOException {

		return componentTransformService.startBatchTransformation(new ComponentTransformationRequest(recipe, BranchPathUriUtil.decodePath(branchPath), tsvFile.getInputStream()));
	}

}
