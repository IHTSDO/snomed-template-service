package org.ihtsdo.otf.transformationandtemplate.service.componenttransform;

import org.ihtsdo.otf.rest.client.terminologyserver.pojo.SnomedComponent;
import org.ihtsdo.otf.rest.exception.BusinessServiceException;
import org.ihtsdo.otf.rest.exception.ProcessingException;
import org.ihtsdo.otf.rest.exception.ResourceNotFoundException;
import org.ihtsdo.otf.transformationandtemplate.domain.ComponentTransformationRequest;
import org.ihtsdo.otf.transformationandtemplate.domain.TransformationRecipe;
import org.ihtsdo.otf.transformationandtemplate.service.JsonStore;
import org.ihtsdo.otf.transformationandtemplate.service.client.ChangeResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Service
public class ComponentTransformService {

	@Autowired
	private DescriptionService descriptionService;

	@Autowired
	private JsonStore transformationRecipeStore;

	public Set<TransformationRecipe> listRecipes(String branchPath) throws IOException {
		return transformationRecipeStore.loadAll(TransformationRecipe.class);
	}

	public List<ChangeResult<? extends SnomedComponent>> startBatchTransformation(ComponentTransformationRequest request) throws BusinessServiceException {
		TransformationRecipe recipe;
		String recipeKey = request.getRecipe();
		try {
			recipe = transformationRecipeStore.load(recipeKey, TransformationRecipe.class);
		} catch (IOException e) {
			throw new BusinessServiceException(String.format("Failed to load recipe '%s'.", recipeKey));
		}
		if (recipe == null) {
			throw new ResourceNotFoundException(String.format("Recipe '%s' not found.", recipeKey));
		}

		switch (recipe.getComponent()) {
			case CONCEPT:
				break;
			case DESCRIPTION:
				return descriptionService.startBatchTransformation(recipe, request);
			default:
				throw new ProcessingException("Unable to transform component of type " + recipe.getComponent());
		}
		return null;
	}
}
