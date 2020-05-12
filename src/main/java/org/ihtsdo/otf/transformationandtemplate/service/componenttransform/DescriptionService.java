package org.ihtsdo.otf.transformationandtemplate.service.componenttransform;

import org.ihtsdo.otf.rest.client.terminologyserver.pojo.DescriptionPojo;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.SnomedComponent;
import org.ihtsdo.otf.rest.exception.BusinessServiceException;
import org.ihtsdo.otf.rest.exception.ProcessingException;
import org.ihtsdo.otf.transformationandtemplate.domain.ComponentTransformationRequest;
import org.ihtsdo.otf.transformationandtemplate.domain.TransformationRecipe;
import org.ihtsdo.otf.transformationandtemplate.service.client.ChangeResult;
import org.ihtsdo.otf.transformationandtemplate.service.client.SnowstormClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

@Service
public class DescriptionService {

	@Autowired
	private TransformationInputStreamFactory transformationStreamFactory;

	@Autowired
	private SnowstormClient snowstormClient;

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public List<ChangeResult<? extends SnomedComponent>> startBatchTransformation(TransformationRecipe recipe, ComponentTransformationRequest request) throws BusinessServiceException {
		switch (recipe.getChangeType()) {
			case CREATE:
				List<DescriptionPojo> descriptions = createDescriptions(recipe, request);
				logger.info("{} descriptions.", descriptions.size());
				return snowstormClient.createDescriptions(descriptions, request.getBranchPath());
			default:
				throw new ProcessingException(format("Change type %s for component %s is not implemented.", recipe.getChangeType(), recipe.getChangeType()));
		}
	}

	private List<DescriptionPojo> createDescriptions(TransformationRecipe recipe, ComponentTransformationRequest request) throws BusinessServiceException {
		List<DescriptionPojo> descriptions = new ArrayList<>();
		try (TransformationStream transformationStream = transformationStreamFactory.createTransformationStream(recipe, request)) {
			ComponentTransformation componentTransformation;
			while ((componentTransformation = transformationStream.next()) != null) {
				// TODO: error reporting for converting values from rows
				DescriptionPojo description = new DescriptionPojo();
				description.setConceptId(componentTransformation.getValueString("conceptId"));
				description.setTerm(componentTransformation.getValueString("term"));
				description.setLang(componentTransformation.getValueString("lang"));
				description.setCaseSignificance(DescriptionPojo.CaseSignificance.fromConceptId(componentTransformation.getValueString("caseSignificanceId")));
				description.setType(DescriptionPojo.Type.fromConceptId(componentTransformation.getValueString("typeId")));
				Map<String, String> acceptabilityStrings = componentTransformation.getValueMap("acceptability");
				Map<String, DescriptionPojo.Acceptability> acceptabilityMap = getAcceptabilityMapFromConceptIdStringMap(acceptabilityStrings);
				description.setAcceptabilityMap(acceptabilityMap);
				descriptions.add(description);
			}
		} catch (IOException e) {
			throw new BusinessServiceException("Failed to read transformation stream.", e);
		}
		return descriptions;
	}

	public static Map<String, DescriptionPojo.Acceptability> getAcceptabilityMapFromConceptIdStringMap(Map<String, String> acceptabilityStrings) {
		Map<String, DescriptionPojo.Acceptability> acceptabilityMap = new HashMap<>();
		acceptabilityStrings.forEach((key, value) -> {
			acceptabilityMap.put(key, DescriptionPojo.Acceptability.fromConceptId(value));
		});
		return acceptabilityMap;
	}

}
