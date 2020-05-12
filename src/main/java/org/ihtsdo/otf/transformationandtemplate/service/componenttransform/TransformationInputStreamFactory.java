package org.ihtsdo.otf.transformationandtemplate.service.componenttransform;

import org.ihtsdo.otf.rest.exception.BadRequestException;
import org.ihtsdo.otf.rest.exception.ProcessingException;
import org.ihtsdo.otf.transformationandtemplate.domain.ComponentTransformationRequest;
import org.ihtsdo.otf.transformationandtemplate.domain.TransformationRecipe;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
public class TransformationInputStreamFactory {

	public TransformationStream createTransformationStream(TransformationRecipe recipe, ComponentTransformationRequest request) throws BadRequestException, IOException, ProcessingException {
		if("tsv".equals(recipe.getSource())) {
			return new TSVTransformationStream(recipe, new BufferedReader(new InputStreamReader(request.getTsvValues())));
		}
		throw new ProcessingException(String.format("Not able to process tranformation recipe with source type %s", recipe.getSource()));
	}

}
