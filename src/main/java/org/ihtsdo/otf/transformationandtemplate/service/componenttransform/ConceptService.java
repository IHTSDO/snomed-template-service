package org.ihtsdo.otf.transformationandtemplate.service.componenttransform;

import com.google.common.base.Strings;
import org.ihtsdo.otf.RF2Constants;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.*;
import org.ihtsdo.otf.rest.exception.BusinessServiceException;
import org.ihtsdo.otf.rest.exception.ProcessingException;
import org.ihtsdo.otf.transformationandtemplate.domain.ChangeType;
import org.ihtsdo.otf.transformationandtemplate.domain.ComponentTransformationRequest;
import org.ihtsdo.otf.transformationandtemplate.domain.TransformationRecipe;
import org.ihtsdo.otf.transformationandtemplate.service.client.ChangeResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snomed.otf.owltoolkit.constants.Concepts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;

import static java.lang.String.format;
import static org.ihtsdo.otf.utils.SnomedIdentifierUtils.isValidConceptIdFormat;

@Service
public class ConceptService {

	@Autowired
	private TransformationInputStreamFactory transformationStreamFactory;

	@Autowired
	private HighLevelAuthoringServiceFactory authoringServiceFactory;

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public List<ChangeResult<? extends SnomedComponent>> startBatchTransformation(TransformationRecipe recipe, ComponentTransformationRequest request) throws BusinessServiceException {
        if (ChangeType.CREATE.equals(recipe.getChangeType())) {
			return createConcepts(recipe, request);
		}
		throw new ProcessingException(format("Change type %s for component %s is not implemented.", recipe.getChangeType(), recipe.getChangeType()));
	}

	private List<ChangeResult<? extends SnomedComponent>> createConcepts(TransformationRecipe recipe, ComponentTransformationRequest request) throws BusinessServiceException {
		HighLevelAuthoringService authoringServiceForCurrentUser = authoringServiceFactory.createServiceForCurrentUser(request.isSkipDroolsValidation());
		List<ChangeResult<ConceptPojo>> changes = new ArrayList<>();
		List<ConceptPojo> concepts = new ArrayList<>();
		readNewConceptChanges(request, recipe, changes, concepts);
		return authoringServiceForCurrentUser.createConcepts(request, concepts, changes);
	}


	private void readNewConceptChanges(ComponentTransformationRequest request, TransformationRecipe recipe, List<ChangeResult<ConceptPojo>> changes, List<ConceptPojo> concepts) throws BusinessServiceException {
		try (TransformationStream transformationStream = transformationStreamFactory.createTransformationStream(recipe, request)) {
			ComponentTransformation componentTransformation;
			while ((componentTransformation = transformationStream.next()) != null) {
				ConceptPojo concept = new ConceptPojo();
				concept.setRelationships(Collections.emptySet());
				concept.setGciAxioms(Collections.emptySet());
				ChangeResult<ConceptPojo> changeResult = new ChangeResult<>(concept);
				changes.add(changeResult);

				concept.setDefinitionStatus(DefinitionStatus.PRIMITIVE);
				Set<DescriptionPojo> descriptions = new HashSet<>();
				DescriptionPojo description = new DescriptionPojo();

				String gbTerm = componentTransformation.getValueString("gbPreferredTerm");
				// FSN
				description.setType(DescriptionPojo.Type.FSN);
				description.setTerm(componentTransformation.getValueString("fsnTerm") + (" (")
							+ componentTransformation.getValueString("semanticTag") + ")");
				description.setLang("en");
				Map<String, DescriptionPojo.Acceptability> acceptabilityMap = new HashMap<>();
				acceptabilityMap.put(RF2Constants.US_ENG_LANG_REFSET, DescriptionPojo.Acceptability.PREFERRED);
				if (StringUtils.hasLength(gbTerm)) {
					acceptabilityMap.put(RF2Constants.GB_ENG_LANG_REFSET, DescriptionPojo.Acceptability.PREFERRED);
				}
				description.setAcceptabilityMap(acceptabilityMap);
				description.setCaseSignificance(DescriptionPojo.CaseSignificance.CASE_INSENSITIVE);
				descriptions.add(description);

				// US preferred term
				description = new DescriptionPojo();
				description.setType(DescriptionPojo.Type.SYNONYM);
				description.setTerm(componentTransformation.getValueString("usPreferredTerm"));
				description.setLang("en");
				acceptabilityMap = new HashMap<>();
				acceptabilityMap.put(RF2Constants.US_ENG_LANG_REFSET, DescriptionPojo.Acceptability.PREFERRED);
				description.setAcceptabilityMap(acceptabilityMap);
				description.setCaseSignificance(DescriptionPojo.CaseSignificance.CASE_INSENSITIVE);
				descriptions.add(description);

				// GB preferred term
				if (StringUtils.hasLength(gbTerm)) {
					if (gbTerm.equals(description.getTerm())) {
						description.getAcceptabilityMap().put(RF2Constants.GB_ENG_LANG_REFSET, DescriptionPojo.Acceptability.PREFERRED);
					} else {
						description = new DescriptionPojo();
						description.setType(DescriptionPojo.Type.SYNONYM);
						description.setTerm(gbTerm);
						description.setLang("en");
						acceptabilityMap = new HashMap<>();
						acceptabilityMap.put(RF2Constants.GB_ENG_LANG_REFSET, DescriptionPojo.Acceptability.PREFERRED);
						description.setAcceptabilityMap(acceptabilityMap);
						description.setCaseSignificance(DescriptionPojo.CaseSignificance.CASE_INSENSITIVE);
						descriptions.add(description);
					}
				}

				// Extension FSN (optional)
				String additionalFsn = componentTransformation.getValueString("additionalFsn");
				if (StringUtils.hasLength(additionalFsn)) {
					description = new DescriptionPojo();
					description.setType(DescriptionPojo.Type.FSN);
					description.setTerm(additionalFsn);
					description.setLang(componentTransformation.getValueString("additionalLang"));
					description.setCaseSignificance(DescriptionPojo.CaseSignificance.fromConceptId(componentTransformation.getValueString("additionalCaseSignificanceId")));
					Map<String, String> acceptabilityStrings = componentTransformation.getValueMap("additionalAcceptability");
					if (acceptabilityStrings != null) {
						acceptabilityMap = getAcceptabilityMapFromConceptIdStringMap(acceptabilityStrings);
						description.setAcceptabilityMap(acceptabilityMap);
					}

					descriptions.add(description);
				}

				// Extension preferred term if any
				String extensionPreferredTerm = componentTransformation.getValueString("term");
				if (StringUtils.hasLength(extensionPreferredTerm)) {
					description = new DescriptionPojo();
					description.setType(DescriptionPojo.Type.SYNONYM);
					description.setTerm(extensionPreferredTerm);
					description.setLang(componentTransformation.getValueString("lang"));
					description.setCaseSignificance(DescriptionPojo.CaseSignificance.fromConceptId(componentTransformation.getValueString("caseSignificanceId")));
					Map<String, String> acceptabilityStrings = componentTransformation.getValueMap("acceptability");
					if (acceptabilityStrings != null) {
						acceptabilityMap = getAcceptabilityMapFromConceptIdStringMap(acceptabilityStrings);
						description.setAcceptabilityMap(acceptabilityMap);
					}
					descriptions.add(description);
				}


				// Axioms
				AxiomPojo axiomPojo = new AxiomPojo();
				axiomPojo.setActive(true);
				axiomPojo.setDefinitionStatusId(DefinitionStatus.PRIMITIVE.getConceptId());

				RelationshipPojo relationship = new RelationshipPojo();
				ConceptMiniPojo type = new ConceptMiniPojo(Concepts.IS_A);
				type.setFsn(new ConceptMiniPojo.DescriptionMiniPojo());
				ConceptMiniPojo target = new ConceptMiniPojo(componentTransformation.getValueString("parentConceptId"));
				target.setFsn(new ConceptMiniPojo.DescriptionMiniPojo());
				relationship.setType(type);
				relationship.setTarget(target);
				axiomPojo.setRelationships(Collections.singleton(relationship));

				concept.setDescriptions(descriptions);
				concept.setClassAxioms(Collections.singleton(axiomPojo));

				// Simple validation
				if (valid(concept, changeResult, recipe.getChangeType())) {
					concepts.add(concept);
				}
			}
		} catch (IOException e) {
			throw new BusinessServiceException("Failed to read transformation stream.", e);
		}
		logger.info("{} of {} concepts passed simple internal checks.", concepts.size(), changes.size());
	}

	// Some basic validation like identifier formats
	private boolean valid(ConceptPojo concept, ChangeResult<ConceptPojo> changeResult, ChangeType changeType) {
		List<Function<ConceptPojo, String>> validation = new ArrayList<>(List.of(
                conceptPojo -> {
                    if (changeType == ChangeType.CREATE) {
                        for (DescriptionPojo descriptionPojo : conceptPojo.getDescriptions()) {
                            if (descriptionPojo.getCaseSignificance() == null) return "Case significance is required";
                            if (descriptionPojo.getType() == null) return "Type is required";
                            if (Strings.isNullOrEmpty(descriptionPojo.getTerm())) return "Term is required";
                            if (Strings.isNullOrEmpty(descriptionPojo.getLang())) return "Lang is required";
                            if (descriptionPojo.getAcceptabilityMap().isEmpty() ||
                                    descriptionPojo.getAcceptabilityMap().entrySet().stream()
                                            .anyMatch(entry -> !isValidConceptIdFormat(entry.getKey()) || entry.getValue() == null)) {
                                return "At least one valid acceptability entry is required";
                            }
                        }
                        if (conceptPojo.getClassAxioms().isEmpty()) return "At least one axiom entry is required";
                        for (AxiomPojo axiomPojo : conceptPojo.getClassAxioms()) {
                            if (axiomPojo.getRelationships().isEmpty()) {
                                return "At least one relationship entry is required";
                            }
                            for (RelationshipPojo relationshipPojo : axiomPojo.getRelationships()) {
								if (relationshipPojo.getType() == null || Strings.isNullOrEmpty(relationshipPojo.getType().getConceptId())) {
									return "Relationship type is required";
								}
                                if (relationshipPojo.getTarget() == null || Strings.isNullOrEmpty(relationshipPojo.getTarget().getConceptId())) {
                                    return "Relationship target is required";
                                }
                            }
                        }
                    }
                    return null;
                }
        ));
		for (Function<ConceptPojo, String> validationFunction : validation) {
			String message = validationFunction.apply(concept);
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
