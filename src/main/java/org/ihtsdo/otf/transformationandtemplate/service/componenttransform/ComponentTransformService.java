package org.ihtsdo.otf.transformationandtemplate.service.componenttransform;

import org.ihtsdo.otf.transformationandtemplate.domain.ComponentTransformationRequest;
import org.ihtsdo.otf.transformationandtemplate.domain.TransformationRecipe;
import org.ihtsdo.otf.transformationandtemplate.service.JsonStore;
import org.ihtsdo.otf.transformationandtemplate.service.exception.OperationNotImplementedException;
import org.ihtsdo.otf.transformationandtemplate.service.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ComponentTransformService {

	@Autowired
	private DescriptionService descriptionService;

	@Autowired
	private JsonStore transformationRecipeStore;

	public void startBatchTransformation(ComponentTransformationRequest request) throws ServiceException {
		TransformationRecipe recipe;
		String recipeName = request.getRecipe();
		try {
			recipe = transformationRecipeStore.load(recipeName, TransformationRecipe.class);
		} catch (IOException e) {
			throw new ServiceException(String.format("Failed to load recipe '%s'.", recipeName));
		}
		if (recipe == null) {
			throw new ServiceException(String.format("Recipe '%s' not found.", recipeName));
		}

		switch (recipe.getComponent()) {
			case CONCEPT:
				break;
			case DESCRIPTION:
				descriptionService.startBatchTransformation(recipe, request);
				break;
			default:
				throw new OperationNotImplementedException();
		}
	}

}
