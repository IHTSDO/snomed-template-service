package org.ihtsdo.otf.transformationandtemplate.rest;

import io.kaicode.rest.util.branchpathrewrite.BranchPathUriUtil;
import org.ihtsdo.otf.transformationandtemplate.domain.ComponentTransformationRequest;
import org.ihtsdo.otf.transformationandtemplate.service.componenttransform.ComponentTransformService;
import org.ihtsdo.otf.transformationandtemplate.service.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class TransformationController {

	@Autowired
	private ComponentTransformService componentTransformService;

	@RequestMapping(value = "/{branchPath}/recipe/{recipeName}/run", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public void runTransformation(@RequestParam String branchPath, @RequestParam String recipeName, @RequestParam("tsvFile") MultipartFile tsvFile) throws IOException, ServiceException {
		componentTransformService.startBatchTransformation(new ComponentTransformationRequest(recipeName, BranchPathUriUtil.decodePath(branchPath), tsvFile.getInputStream()));
	}

}
