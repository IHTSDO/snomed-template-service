package org.ihtsdo.otf.transformationandtemplate.service.componenttransform;

import com.google.common.base.Strings;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.ConceptPojo;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.DescriptionPojo;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.SnomedComponent;
import org.ihtsdo.otf.rest.exception.BusinessServiceException;
import org.ihtsdo.otf.rest.exception.ProcessingException;
import org.ihtsdo.otf.transformationandtemplate.domain.ChangeType;
import org.ihtsdo.otf.transformationandtemplate.domain.ComponentTransformationRequest;
import org.ihtsdo.otf.transformationandtemplate.domain.TransformationRecipe;
import org.ihtsdo.otf.transformationandtemplate.service.client.ChangeResult;
import org.ihtsdo.otf.transformationandtemplate.service.client.SnowstormClient;
import org.ihtsdo.otf.transformationandtemplate.service.client.SnowstormClientFactory;
import org.ihtsdo.otf.utils.SnomedIdentifierUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;

import static java.lang.String.format;
import static org.ihtsdo.otf.rest.client.terminologyserver.pojo.ConceptPojo.InactivationIndicator.NOT_SEMANTICALLY_EQUIVALENT;
import static org.ihtsdo.otf.utils.SnomedIdentifierUtils.isValidConceptIdFormat;
import static org.ihtsdo.otf.utils.SnomedIdentifierUtils.isValidDescriptionIdFormat;
import static org.springframework.util.CollectionUtils.isEmpty;

@Service
public class DescriptionService {

	@Autowired
	private TransformationInputStreamFactory transformationStreamFactory;

	@Autowired
	private SnowstormClientFactory snowstormClientFactory;

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public List<ChangeResult<? extends SnomedComponent>> startBatchTransformation(TransformationRecipe recipe, ComponentTransformationRequest request) throws BusinessServiceException {
		SnowstormClient snowstormClient = snowstormClientFactory.getClientForCurrentUser();
		switch (recipe.getChangeType()) {
			case CREATE:
				return createDescriptions(recipe, request, snowstormClient);
			case UPDATE:
			case INACTIVATE:
				// This code for update or inactivate
				return updateDescriptions(recipe, request, snowstormClient);
			default:
				throw new ProcessingException(format("Change type %s for component %s is not implemented.", recipe.getChangeType(), recipe.getChangeType()));
		}
	}

	private List<ChangeResult<? extends SnomedComponent>> createDescriptions(TransformationRecipe recipe, ComponentTransformationRequest request, SnowstormClient snowstormClient) throws BusinessServiceException {
		List<ChangeResult<DescriptionPojo>> changes = new ArrayList<>();
		List<DescriptionPojo> descriptions = new ArrayList<>();
		readDescriptionChanges(request, recipe, changes, descriptions);
		return snowstormClient.createDescriptions(descriptions, changes, request.getBranchPath());
	}

	private List<ChangeResult<? extends SnomedComponent>> updateDescriptions(TransformationRecipe recipe, ComponentTransformationRequest request, SnowstormClient snowstormClient) throws BusinessServiceException {
		List<ChangeResult<DescriptionPojo>> changes = new ArrayList<>();
		List<DescriptionPojo> descriptions = new ArrayList<>();
		readDescriptionChanges(request, recipe, changes, descriptions);
		return snowstormClient.updateDescriptions(descriptions, changes, request.getBranchPath());
	}

	private void readDescriptionChanges(ComponentTransformationRequest request, TransformationRecipe recipe, List<ChangeResult<DescriptionPojo>> changes, List<DescriptionPojo> descriptions) throws BusinessServiceException {
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
				if (acceptabilityStrings != null) {
					Map<String, DescriptionPojo.Acceptability> acceptabilityMap = getAcceptabilityMapFromConceptIdStringMap(acceptabilityStrings);
					description.setAcceptabilityMap(acceptabilityMap);
				}

				if (recipe.getChangeType() == ChangeType.INACTIVATE) {
					description.setActive(false);
				}
				String inactivationIndicatorId = componentTransformation.getValueString("inactivationIndicator");
				if (inactivationIndicatorId != null) {
					description.setInactivationIndicator(ConceptPojo.InactivationIndicator.fromConceptId(inactivationIndicatorId));
				}
				List<String> associationTargetIds = componentTransformation.getValueList("associationTargets");
				HashMap<ConceptPojo.HistoricalAssociation, Set<String>> associationTargets = new HashMap<>();
				if (!isEmpty(associationTargetIds)) {
					// Refers To is the only applicable description association type in the authoring platform today.
					associationTargets.put(ConceptPojo.HistoricalAssociation.REFERS_TO, new HashSet<>(associationTargetIds));
				}
				description.setAssociationTargets(associationTargets);

				// Simple validation
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
						isValidDescriptionIdFormat(descriptionPojo.getDescriptionId()) ? null : "Description id format",
				descriptionPojo -> {
					ConceptPojo.InactivationIndicator inactivationIndicator = description.getInactivationIndicator();
					Map<ConceptPojo.HistoricalAssociation, Set<String>> associationTargets = descriptionPojo.getAssociationTargets();
					if (NOT_SEMANTICALLY_EQUIVALENT != inactivationIndicator && !isEmpty(associationTargets)) {
						return "Unable to process descriptions with association targets unless the inactivation indicator is Not semantically equivalent";
					}
					for (Set<String> values : associationTargets.values()) {
						for (String value : values) {
							if (isValidConceptIdFormat(value)) {
								return format("Association target value '%s' is not a valid concept id", value);
							}
						}
					}
					if (inactivationIndicator == null && !isEmpty(associationTargets)) {
						return "Valid inactivation indicator must be given if association targets are set";
					}
					return null;
				}
				));
		if (changeType == ChangeType.CREATE || changeType == ChangeType.UPDATE) {
			validation.addAll(Arrays.asList(
			descriptionPojo -> descriptionPojo.getCaseSignificance() == null ? "Case significance is required" : null,
					descriptionPojo -> descriptionPojo.getType() == null ? "Type is required" : null,
					descriptionPojo -> descriptionPojo.getAcceptabilityMap().isEmpty() ||
							descriptionPojo.getAcceptabilityMap().entrySet().stream()
									.anyMatch(entry -> !isValidConceptIdFormat(entry.getKey()) || entry.getValue() == null) ? "At least one valid acceptability entry is required" : null
			));
		}
		if (changeType == ChangeType.UPDATE) {
			validation.add(descriptionPojo -> descriptionPojo.getDescriptionId() != null ? null : "Description id is required");
		}
		if (changeType == ChangeType.CREATE) {
			validation.addAll(Arrays.asList(
					descriptionPojo -> isValidConceptIdFormat(descriptionPojo.getConceptId()) ? null : "Concept id format",
					descriptionPojo -> Strings.isNullOrEmpty(descriptionPojo.getTerm()) ? "Term is required" : null,
					descriptionPojo -> Strings.isNullOrEmpty(descriptionPojo.getLang()) ? "Lang is required" : null
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
		acceptabilityStrings.forEach((key, value) -> acceptabilityMap.put(key, DescriptionPojo.Acceptability.fromConceptId(value)));
		return acceptabilityMap;
	}

}
