package org.ihtsdo.otf.transformationandtemplate.service.componenttransform;

import com.google.common.base.Strings;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.DescriptionPojo;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.SnomedComponent;
import org.ihtsdo.otf.rest.exception.BusinessServiceException;
import org.ihtsdo.otf.rest.exception.ProcessingException;
import org.ihtsdo.otf.transformationandtemplate.domain.ChangeType;
import org.ihtsdo.otf.transformationandtemplate.domain.ComponentTransformationRequest;
import org.ihtsdo.otf.transformationandtemplate.domain.TransformationRecipe;
import org.ihtsdo.otf.transformationandtemplate.service.client.ChangeResult;
import org.ihtsdo.otf.transformationandtemplate.service.client.SnowstormClient;
import org.ihtsdo.otf.utils.SnomedIdentifierUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;

import static java.lang.String.format;
import static org.ihtsdo.otf.utils.SnomedIdentifierUtils.isValidConceptIdFormat;

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
				return createDescriptions(recipe, request);
			case UPDATE:
				return updateDescriptions(recipe, request);
			default:
				throw new ProcessingException(format("Change type %s for component %s is not implemented.", recipe.getChangeType(), recipe.getChangeType()));
		}
	}

	private List<ChangeResult<? extends SnomedComponent>> createDescriptions(TransformationRecipe recipe, ComponentTransformationRequest request) throws BusinessServiceException {
		List<ChangeResult<DescriptionPojo>> changes = new ArrayList<>();
		List<DescriptionPojo> descriptions = new ArrayList<>();
		readDescriptions(request, recipe, changes, descriptions);
		return snowstormClient.createDescriptions(descriptions, changes, request.getBranchPath());
	}

	private List<ChangeResult<? extends SnomedComponent>> updateDescriptions(TransformationRecipe recipe, ComponentTransformationRequest request) throws BusinessServiceException {
		List<ChangeResult<DescriptionPojo>> changes = new ArrayList<>();
		List<DescriptionPojo> descriptions = new ArrayList<>();
		readDescriptions(request, recipe, changes, descriptions);
		return snowstormClient.updateDescriptions(descriptions, changes, request.getBranchPath());
	}

	private void readDescriptions(ComponentTransformationRequest request, TransformationRecipe recipe, List<ChangeResult<DescriptionPojo>> changes, List<DescriptionPojo> descriptions) throws BusinessServiceException {
		try (TransformationStream transformationStream = transformationStreamFactory.createTransformationStream(recipe, request)) {
			ComponentTransformation componentTransformation;
			while ((componentTransformation = transformationStream.next()) != null) {
				DescriptionPojo description = new DescriptionPojo();
				ChangeResult<DescriptionPojo> changeResult = new ChangeResult<>(description);
				changes.add(changeResult);

				description.setDescriptionId(componentTransformation.getValueString("descriptionId"));
				description.setConceptId(componentTransformation.getValueString("conceptId"));
				description.setTerm(componentTransformation.getValueString("term"));
				description.setLang(componentTransformation.getValueString("lang"));
				description.setCaseSignificance(DescriptionPojo.CaseSignificance.fromConceptId(componentTransformation.getValueString("caseSignificanceId")));
				description.setType(DescriptionPojo.Type.fromConceptId(componentTransformation.getValueString("typeId")));
				Map<String, String> acceptabilityStrings = componentTransformation.getValueMap("acceptability");
				Map<String, DescriptionPojo.Acceptability> acceptabilityMap = getAcceptabilityMapFromConceptIdStringMap(acceptabilityStrings);
				description.setAcceptabilityMap(acceptabilityMap);
				if (valid(description, changeResult, recipe.getChangeType())) {
					descriptions.add(description);
				}
			}
		} catch (IOException e) {
			throw new BusinessServiceException("Failed to read transformation stream.", e);
		}
		logger.info("{} of {} descriptions passed simple internal checks.", descriptions.size(), changes.size());
	}

	// Some basic validation like identifier formats
	private boolean valid(DescriptionPojo description, ChangeResult<DescriptionPojo> changeResult, ChangeType changeType) {
		List<Function<DescriptionPojo, String>> validation = new ArrayList<>(Arrays.asList(
				descriptionPojo -> descriptionPojo.getDescriptionId() == null ||
						SnomedIdentifierUtils.isValidDescriptionIdFormat(descriptionPojo.getDescriptionId()) ? null : "Description id format",
				descriptionPojo -> descriptionPojo.getCaseSignificance() == null ? "Case significance" : null,
				descriptionPojo -> descriptionPojo.getType() == null ? "Type" : null,
				descriptionPojo -> descriptionPojo.getAcceptabilityMap().isEmpty() ||
						descriptionPojo.getAcceptabilityMap().entrySet().stream()
								.anyMatch(entry -> !isValidConceptIdFormat(entry.getKey()) || entry.getValue() == null) ? "At least one valid acceptability entry" : null
		));
		if (changeType == ChangeType.UPDATE) {
			validation.add(descriptionPojo -> descriptionPojo.getDescriptionId() != null ? null : "Description id is required");
		}
		if (changeType == ChangeType.CREATE) {
			validation.addAll(Arrays.asList(
					descriptionPojo -> isValidConceptIdFormat(descriptionPojo.getConceptId()) ? null : "Concept id format",
					descriptionPojo -> Strings.isNullOrEmpty(descriptionPojo.getTerm()) ? "Term not empty" : null,
					descriptionPojo -> Strings.isNullOrEmpty(descriptionPojo.getLang()) ? "Lang not empty" : null
			));
		}
		for (Function<DescriptionPojo, String> validationFunction : validation) {
			String message = validationFunction.apply(description);
			if (message != null) {
				changeResult.fail(format("Simple validation failed: %s.", message));
				return false;
			}
		}
		return true;
	}

	public static Map<String, DescriptionPojo.Acceptability> getAcceptabilityMapFromConceptIdStringMap(Map<String, String> acceptabilityStrings) {
		Map<String, DescriptionPojo.Acceptability> acceptabilityMap = new HashMap<>();
		acceptabilityStrings.forEach((key, value) -> {
			acceptabilityMap.put(key, DescriptionPojo.Acceptability.fromConceptId(value));
		});
		return acceptabilityMap;
	}

}
