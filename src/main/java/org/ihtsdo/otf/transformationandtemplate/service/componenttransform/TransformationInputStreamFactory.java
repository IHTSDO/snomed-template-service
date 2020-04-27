package org.ihtsdo.otf.transformationandtemplate.service.componenttransform;

import org.ihtsdo.otf.transformationandtemplate.domain.ComponentTransformationRequest;
import org.ihtsdo.otf.transformationandtemplate.domain.TransformationRecipe;
import org.ihtsdo.otf.transformationandtemplate.service.exception.OperationNotImplementedException;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Service
public class TransformationInputStreamFactory {

	public TransformationStream createTransformationStream(TransformationRecipe recipe, ComponentTransformationRequest request) throws OperationNotImplementedException {
		if("tsv".equals(recipe.getSource())) {
			return new TSVTransformationStream(recipe, new BufferedReader(new InputStreamReader(request.getTsvValues())));
		}
		throw new OperationNotImplementedException(String.format("Not able to process tranformation recipe with source type %s", recipe.getSource()));
	}

}
