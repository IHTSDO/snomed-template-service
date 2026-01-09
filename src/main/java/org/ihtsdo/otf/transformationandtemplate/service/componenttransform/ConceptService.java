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
import org.ihtsdo.otf.transformationandtemplate.service.client.DialectVariations;
import org.semanticweb.elk.util.collections.ArraySet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snomed.otf.owltoolkit.constants.Concepts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;

import static org.ihtsdo.otf.rest.client.terminologyserver.pojo.DescriptionPojo.CaseSignificance;
import static org.ihtsdo.otf.rest.client.terminologyserver.pojo.DescriptionPojo.Type;
import static org.ihtsdo.otf.rest.client.terminologyserver.pojo.DescriptionPojo.Acceptability;
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
		if (ChangeType.EDIT.equals(recipe.getChangeType())) {
			return addConceptsToEdit(recipe, request);
		}
		throw new ProcessingException(format("Change type %s for component %s is not implemented.", recipe.getChangeType(), recipe.getChangeType()));
	}

	private List<ChangeResult<? extends SnomedComponent>> createConcepts(TransformationRecipe recipe, ComponentTransformationRequest request) throws BusinessServiceException {
		HighLevelAuthoringService authoringServiceForCurrentUser = authoringServiceFactory.createServiceForCurrentUser(request.isSkipDroolsValidation());
		List<ChangeResult<ConceptPojo>> changes = new ArrayList<>();
		List<ConceptPojo> concepts = new ArrayList<>();
		readNewConceptChanges(authoringServiceForCurrentUser, request, recipe, changes, concepts);
		return authoringServiceForCurrentUser.createConcepts(request, concepts, changes);
	}

	private List<ChangeResult<? extends SnomedComponent>> addConceptsToEdit(TransformationRecipe recipe, ComponentTransformationRequest request) throws BusinessServiceException {
		HighLevelAuthoringService authoringServiceForCurrentUser = authoringServiceFactory.createServiceForCurrentUser(request.isSkipDroolsValidation());
		List<ChangeResult<ConceptPojo>> changes = new ArrayList<>();
		List<ConceptPojo> concepts = new ArrayList<>();
		readConceptsToEdit(request, recipe, changes, concepts);
		return authoringServiceForCurrentUser.addConceptsToEdit(request, concepts, changes);
    }


	private void readConceptsToEdit(ComponentTransformationRequest request, TransformationRecipe recipe, List<ChangeResult<ConceptPojo>> changes, List<ConceptPojo> concepts) throws BusinessServiceException {
		try (TransformationStream transformationStream = transformationStreamFactory.createTransformationStream(recipe, request)) {
			ComponentTransformation componentTransformation;
			while ((componentTransformation = transformationStream.next()) != null) {
				String conceptId = componentTransformation.getValueString("conceptId");
				ConceptPojo concept = new ConceptPojo();
				concept.setConceptId(conceptId);
				concepts.add(concept);

				ChangeResult<ConceptPojo> changeResult = new ChangeResult<>(concept);
				changes.add(changeResult);
			}
		} catch (IOException e) {
			throw new BusinessServiceException("Failed to read transformation stream.", e);
		}
	}

	private void readNewConceptChanges(HighLevelAuthoringService highLevelAuthoringService, ComponentTransformationRequest request, TransformationRecipe recipe, List<ChangeResult<ConceptPojo>> changes, List<ConceptPojo> concepts) throws BusinessServiceException {
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

				String fsn = componentTransformation.getValueString("fsn");
				String semanticTag = componentTransformation.getValueString("semanticTag");
				String usPreferredTerm = componentTransformation.getValueString("usPreferredTerm");
				String gbPreferredTerm = componentTransformation.getValueString("gbPreferredTerm");
				String translatedPreferredTerm = componentTransformation.getValueString("translatedPreferredTerm");

				// FSN
				Map<String, Acceptability> acceptabilityMap = new HashMap<>();
				acceptabilityMap.put(RF2Constants.US_ENG_LANG_REFSET, Acceptability.PREFERRED);
				if (translatedPreferredTerm == null || !StringUtils.hasLength(translatedPreferredTerm.trim())) {
					acceptabilityMap.put(RF2Constants.GB_ENG_LANG_REFSET, Acceptability.PREFERRED);
				}
				CaseSignificance fsnCaseSignificance = CaseSignificance.fromConceptId(componentTransformation.getValueString("fsnCaseSignificanceId"));
				DescriptionPojo fsnDescription = getNewDescription(Type.FSN, fsn + (" (") + semanticTag + ")", "en", acceptabilityMap, fsnCaseSignificance);
				descriptions.add(fsnDescription);

				// US preferred term
				acceptabilityMap = new HashMap<>();
				acceptabilityMap.put(RF2Constants.US_ENG_LANG_REFSET, Acceptability.PREFERRED);
				CaseSignificance usCaseSignificance = CaseSignificance.fromConceptId(componentTransformation.getValueString("usCaseSignificanceId"));
				DescriptionPojo usDescription = getNewDescription(Type.SYNONYM, usPreferredTerm, "en", acceptabilityMap, usCaseSignificance);
				descriptions.add(usDescription);

				// GB preferred term
				if (translatedPreferredTerm == null || !StringUtils.hasLength(translatedPreferredTerm.trim())) {
					CaseSignificance gbCaseSignificance = CaseSignificance.fromConceptId(componentTransformation.getValueString("gbCaseSignificanceId"));
					gbCaseSignificance = gbCaseSignificance != null ? gbCaseSignificance : usCaseSignificance;
					Map<String, Object> gbTermMap = generateGBTerms(highLevelAuthoringService, usPreferredTerm, gbPreferredTerm);
					if (!CollectionUtils.isEmpty(gbTermMap)) {
						String generatedGBPreferredTerm = (String) gbTermMap.get("gbPreferredTerm");
						if (generatedGBPreferredTerm != null && StringUtils.hasLength(generatedGBPreferredTerm.trim())) {
							if (generatedGBPreferredTerm.equals(usDescription.getTerm())) {
								usDescription.getAcceptabilityMap().put(RF2Constants.GB_ENG_LANG_REFSET, Acceptability.PREFERRED);
							} else {
								acceptabilityMap = new HashMap<>();
								acceptabilityMap.put(RF2Constants.GB_ENG_LANG_REFSET, Acceptability.PREFERRED);
								DescriptionPojo gbDescription = getNewDescription(Type.SYNONYM, generatedGBPreferredTerm, "en", acceptabilityMap, gbCaseSignificance);
								descriptions.add(gbDescription);
							}
						}
						Set<String> gbAcceptableTerms = (Set<String>) gbTermMap.get("gbAcceptableTerms");
						if (!CollectionUtils.isEmpty(gbAcceptableTerms)) {
							for (String term : gbAcceptableTerms) {
								acceptabilityMap = new HashMap<>();
								acceptabilityMap.put(RF2Constants.GB_ENG_LANG_REFSET, Acceptability.ACCEPTABLE);
								DescriptionPojo acceptableGBDescription = getNewDescription(Type.SYNONYM, term, "en", acceptabilityMap,  gbCaseSignificance);
								descriptions.add(acceptableGBDescription);
							}
						}
					}
				}

				// Translated preferred term if any
				if (StringUtils.hasLength(translatedPreferredTerm)) {
					CaseSignificance translatedCaseSignificance = CaseSignificance.fromConceptId(componentTransformation.getValueString("caseSignificanceId"));
					acceptabilityMap = new HashMap<>();
					String langRefset = componentTransformation.getValueString("langRefset");
					if (langRefset != null) {
						acceptabilityMap.put(langRefset, Acceptability.PREFERRED);
					}
					DescriptionPojo translatedDescription = getNewDescription(Type.SYNONYM, translatedPreferredTerm, componentTransformation.getValueString("lang"), acceptabilityMap, translatedCaseSignificance);
					descriptions.add(translatedDescription);
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

	private DescriptionPojo getNewDescription(Type type, String term, String lang, Map<String, Acceptability> acceptabilityMap, CaseSignificance caseSignificance) {
		DescriptionPojo description = new DescriptionPojo();
		description.setType(type);
		description.setTerm(term);
		description.setLang(lang);
		if (!CollectionUtils.isEmpty(acceptabilityMap)) {
			description.setAcceptabilityMap(acceptabilityMap);
		}
		if (caseSignificance != null) {
			description.setCaseSignificance(caseSignificance);
		}
		return description;
	}

	private Map<String, Object> generateGBTerms(HighLevelAuthoringService highLevelAuthoringService, String usPreferredTerm, String inputGBPreferredTerm) {
		Map<String, Object> result = new HashMap<>();
		if (!StringUtils.hasLength(usPreferredTerm)) { return result; }

		String gbPreferredTerm = usPreferredTerm;
		Set<String> gbAcceptableTerms = new ArraySet<>();
		String[] tokenizedWords = Arrays.stream(usPreferredTerm.split(" "))
				.map(String::trim)
				.filter(StringUtils::hasLength)
				.toArray(String[]::new);
		DialectVariations dialectVariations = highLevelAuthoringService.getEnUsToEnGbSuggestions(new HashSet<>(Arrays.asList(tokenizedWords)));
		if (dialectVariations != null) {
			Map<String, String> dialectMatchingWords = dialectVariations.getMap();
			Map<String, Set<String>> synonymMatchingWords = dialectVariations.getSynonyms();

			// strip any synonym words that appears in dialect Map
			if (!CollectionUtils.isEmpty(synonymMatchingWords) && !CollectionUtils.isEmpty(dialectMatchingWords)) {
				for (Map.Entry<String, Set<String>> entry : synonymMatchingWords.entrySet()) {
					if (dialectMatchingWords.get(entry.getKey()) != null) {
						Set<String> original = synonymMatchingWords.get(entry.getKey());
						Set<String> words = synonymMatchingWords.get(entry.getKey());
						for (String word : original) {
							words.removeIf(item -> dialectMatchingWords.get(entry.getKey()).equals(word));
						}
						synonymMatchingWords.put(entry.getKey(), words);
					}
				}
				for (Map.Entry<String, Set<String>> entry : synonymMatchingWords.entrySet()) {
					if (synonymMatchingWords.get(entry.getKey()).isEmpty()) {
						synonymMatchingWords.remove(entry.getKey());
					}
				}
			}

			// replace original words with the suggested dialect spellings
			if (!CollectionUtils.isEmpty(dialectMatchingWords)) {
				for (Map.Entry<String, String> entry : dialectMatchingWords.entrySet()) {
					gbPreferredTerm = gbPreferredTerm.replace(entry.getKey(), dialectMatchingWords.get(entry.getKey()));
					if (StringUtils.hasLength(inputGBPreferredTerm) && !gbPreferredTerm.equals(inputGBPreferredTerm)) {
						gbAcceptableTerms.add(gbPreferredTerm);
					}
				}
			}

			// replace original words with the suggested synonym spellings
			if (!CollectionUtils.isEmpty(synonymMatchingWords)) {
				for (Map.Entry<String, Set<String>> entry : synonymMatchingWords.entrySet()) {
                    for (String word : synonymMatchingWords.get(entry.getKey())) {
                        String tempTermGb = usPreferredTerm.replace(entry.getKey(), word.trim()).trim();
                        if (!tempTermGb.equals(gbPreferredTerm) && (!StringUtils.hasLength(inputGBPreferredTerm) || !tempTermGb.equals(inputGBPreferredTerm))) {
                            gbAcceptableTerms.add(tempTermGb);
                        }
                    }
				}
			}
		}

		result.put("gbPreferredTerm", inputGBPreferredTerm != null && StringUtils.hasLength(inputGBPreferredTerm.trim()) ? inputGBPreferredTerm.trim() : gbPreferredTerm);
		result.put("gbAcceptableTerms", gbAcceptableTerms);
		return result;
	}

	// Some basic validation like identifier formats
	private boolean valid(ConceptPojo concept, ChangeResult<ConceptPojo> changeResult, ChangeType changeType) {
		List<Function<ConceptPojo, String>> validation = new ArrayList<>(List.of(
                conceptPojo -> {
                    if (changeType == ChangeType.CREATE) {
						boolean fsnFound = false;
						boolean usPreferredTermFound = false;
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
							if (Type.FSN.equals(descriptionPojo.getType())) {
								fsnFound = true;
							}
							if (Type.SYNONYM.equals(descriptionPojo.getType())
								&& descriptionPojo.getAcceptabilityMap().containsKey(RF2Constants.US_ENG_LANG_REFSET)
								&& Acceptability.PREFERRED.equals(descriptionPojo.getAcceptabilityMap().get(RF2Constants.US_ENG_LANG_REFSET))) {
								usPreferredTermFound = true;
							}
                        }
						if (!fsnFound) { return "FSN term is required"; }
						if (!usPreferredTermFound) { return "US preferred term is required"; }
                        if (conceptPojo.getClassAxioms().isEmpty()) { return "At least one axiom entry is required"; }
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

	public static Map<String, Acceptability> getAcceptabilityMapFromConceptIdStringMap(Map<String, String> acceptabilityStrings) {
		Map<String, Acceptability> acceptabilityMap = new HashMap<>();
		acceptabilityStrings.forEach((key, value) -> acceptabilityMap.put(key, Acceptability.fromConceptId(value)));
		return acceptabilityMap;
	}
}
