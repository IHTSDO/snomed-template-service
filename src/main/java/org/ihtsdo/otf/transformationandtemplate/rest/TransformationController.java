package org.ihtsdo.otf.transformationandtemplate.rest;

import io.kaicode.rest.util.branchpathrewrite.BranchPathUriUtil;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.SnomedComponent;
import org.ihtsdo.otf.rest.exception.BusinessServiceException;
import org.ihtsdo.otf.transformationandtemplate.domain.ComponentTransformationRequest;
import org.ihtsdo.otf.transformationandtemplate.service.client.ChangeResult;
import org.ihtsdo.otf.transformationandtemplate.service.componenttransform.ComponentTransformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
public class TransformationController {

	@Autowired
	private ComponentTransformService componentTransformService;

	@RequestMapping(value = "/{branchPath}/recipes/{recipeName}/run", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public List<ChangeResult<? extends SnomedComponent>> runTransformation(@PathVariable String branchPath, @PathVariable String recipeName,
			@RequestParam("tsvFile") MultipartFile tsvFile) throws BusinessServiceException, IOException {

		return componentTransformService.startBatchTransformation(new ComponentTransformationRequest(recipeName, BranchPathUriUtil.decodePath(branchPath), tsvFile.getInputStream()));
	}

}
