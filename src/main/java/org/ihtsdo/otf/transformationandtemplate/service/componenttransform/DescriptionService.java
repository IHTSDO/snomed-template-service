package org.ihtsdo.otf.transformationandtemplate.service.componenttransform;

import org.ihtsdo.otf.rest.client.terminologyserver.pojo.DescriptionPojo;
import org.ihtsdo.otf.transformationandtemplate.domain.ComponentTransformationRequest;
import org.ihtsdo.otf.transformationandtemplate.domain.TransformationRecipe;
import org.ihtsdo.otf.transformationandtemplate.service.client.SnowstormClient;
import org.ihtsdo.otf.transformationandtemplate.service.exception.OperationNotImplementedException;
import org.ihtsdo.otf.transformationandtemplate.service.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class DescriptionService {

	@Autowired
	private TransformationInputStreamFactory transformationStreamFactory;

	@Autowired
	private SnowstormClient snowstormClient;

	private Logger logger = LoggerFactory.getLogger(getClass());

	public void startBatchTransformation(TransformationRecipe recipe, ComponentTransformationRequest request) throws ServiceException {
		switch (recipe.getChangeType()) {
			case CREATE:
				List<DescriptionPojo> descriptions = createDescriptions(recipe, request);
				logger.info("Created {} descriptions.", descriptions.size());
				snowstormClient.createDescriptions(descriptions, request.getBranchPath());
				break;
			default:
				throw new OperationNotImplementedException();
		}
	}

	private List<DescriptionPojo> createDescriptions(TransformationRecipe recipe, ComponentTransformationRequest request) throws ServiceException {
		List<DescriptionPojo> descriptions = new ArrayList<>();
		try (TransformationStream transformationStream = transformationStreamFactory.createTransformationStream(recipe, request)) {
			ComponentTransformation componentTransformation;
			while ((componentTransformation = transformationStream.next()) != null) {
				DescriptionPojo description = new DescriptionPojo();
				description.setConceptId(componentTransformation.getValueString("conceptId"));
				description.setTerm(componentTransformation.getValueString("term"));
				description.setLang(componentTransformation.getValueString("lang"));
				description.setCaseSignificance(componentTransformation.getValueString("caseSignificance"));
				description.setType(componentTransformation.getValueString("type"));
				description.setAcceptabilityMap(componentTransformation.getValueMap("acceptability"));
				descriptions.add(description);
			}
		} catch (IOException e) {
			throw new ServiceException("Failed to read transformation stream.", e);
		}
		return descriptions;
	}

}
