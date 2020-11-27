package org.ihtsdo.otf.transformationandtemplate.service.componenttransform;

import com.google.common.collect.Iterables;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.*;
import org.ihtsdo.otf.rest.exception.BusinessServiceException;
import org.ihtsdo.otf.transformationandtemplate.domain.ComponentTransformationRequest;
import org.ihtsdo.otf.transformationandtemplate.service.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClientException;

import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.google.common.collect.Sets.difference;
import static java.lang.String.format;
import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.ihtsdo.otf.transformationandtemplate.service.client.ConceptValidationResult.Severity.ERROR;
import static org.ihtsdo.otf.rest.client.terminologyserver.pojo.DescriptionPojo.Acceptability.ACCEPTABLE;
import static org.ihtsdo.otf.rest.client.terminologyserver.pojo.DescriptionPojo.Acceptability.PREFERRED;

/**
 * Service which acts as the logged in user and provides high level authoring functionality.
 * For example creating a batch of descriptions involves: loading the concepts, joining the descriptions, validation then save.
 */
public class HighLevelAuthoringService {

	private final SnowstormClient snowstormClient;

	private final AuthoringServicesClient authoringServicesClient;
	private final boolean skipDroolsValidation;

	private int processingBatchMaxSize;

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private static final Comparator<DescriptionPojo> DESCRIPTION_WITHOUT_ID_COMPARATOR = Comparator.comparing(DescriptionPojo::getTerm).thenComparing(DescriptionPojo::getLang);
	private static final Comparator<DescriptionPojo> DESCRIPTION_WITH_CONCEPT_ID_COMPARATOR = Comparator.comparing(DescriptionPojo::getTerm).thenComparing(DescriptionPojo::getLang).thenComparing(DescriptionPojo::getConceptId);
	private static final Comparator<DescriptionReplacementPojo> CONCEPT_ID_COMPARATOR = Comparator.comparing(DescriptionReplacementPojo::getConceptId);
	private static final Comparator<ConceptValidationResult> CONCEPT_VALIDATION_RESULT_COMPARATOR = Comparator.comparing(ConceptValidationResult::getSeverity);

	public HighLevelAuthoringService(SnowstormClient snowstormClient, AuthoringServicesClient authoringServicesClient, int processingBatchMaxSize, boolean skipDroolsValidation) {
		this.snowstormClient = snowstormClient;
		this.authoringServicesClient = authoringServicesClient;
		this.processingBatchMaxSize = processingBatchMaxSize;
		this.skipDroolsValidation = skipDroolsValidation;
	}

	public List<ChangeResult<? extends SnomedComponent>> createDescriptions(
			ComponentTransformationRequest request, List<DescriptionPojo> descriptions, List<ChangeResult<DescriptionPojo>> changes) throws BusinessServiceException {

		if (descriptions.isEmpty()) {
			return new ArrayList<>(changes);
		}
		logger.info("Starting process to save {} descriptions.", descriptions.size());

		try {
			// Initial terminology server communication check
			snowstormClient.getBranch("MAIN");

			String defaultModuleId;
			try {
				defaultModuleId = snowstormClient.getDefaultModuleId(request.getBranchPath());
			} catch (WebClientException e) {
				logger.info("Failed to load branch {} from the terminology server.", request, e);
				return failAllRemaining(changes, format("Failed to load branch %s from the terminology server.", request));
			}

			// Give new descriptions a temporary UUID
			descriptions.forEach(description -> {
				if (description.getDescriptionId() == null) {
					description.setDescriptionId(UUID.randomUUID().toString());
				}
			});

			Map<String, Set<DescriptionPojo>> conceptIdToDescriptionMap = new HashMap<>();
			for (DescriptionPojo description : descriptions) {
				conceptIdToDescriptionMap.computeIfAbsent(description.getConceptId(), (key) -> new HashSet<>()).add(description);
			}

			// Split into batches of how many changes per branch / task
			int batchNumber = 0;
			for (List<String> conceptIdTaskBatch : Iterables.partition(conceptIdToDescriptionMap.keySet(), request.getBatchSize())) {

				batchNumber++;
				String branchPath = getBatchBranch(request, batchNumber);

				// Split into smaller batches if the number of per branch changes exceeds the number of concepts which should be processed at a time.
				for (List<String> conceptIdProcessingBatch : Iterables.partition(conceptIdTaskBatch, processingBatchMaxSize)) {
					Map<String, Set<DescriptionPojo>> batchMap = new HashMap<>();
					for (String conceptId : conceptIdProcessingBatch) {
						batchMap.put(conceptId, conceptIdToDescriptionMap.get(conceptId));
					}
					createDescriptionBatch(batchMap, defaultModuleId, changes, branchPath);
				}
			}

		} catch (WebClientException | TimeoutException e) {// This RuntimeException is thrown by WebClient
			logger.error("Failed to communicate with the terminology server.", e);
			failAllRemaining(changes, "Failed to communicate with the terminology server.");
		}

		return new ArrayList<>(changes);
	}

	private void createDescriptionBatch(Map<String, Set<DescriptionPojo>> conceptIdToDescriptionMap, String defaultModuleId,
			List<ChangeResult<DescriptionPojo>> changes, String branchPath) throws BusinessServiceException, TimeoutException {

		// Batch load concepts
		List<ConceptPojo> concepts = snowstormClient.getFullConcepts(SnowstormClient.ConceptBulkLoadRequest.byConceptId(conceptIdToDescriptionMap.keySet()), branchPath);

		// Join new descriptions to concepts
		Map<String, ConceptPojo> conceptMap = concepts.stream().collect(Collectors.toMap(ConceptPojo::getConceptId, Function.identity()));
		for (String conceptId : conceptIdToDescriptionMap.keySet()) {
			ConceptPojo conceptPojo = conceptMap.get(conceptId);
			List<String> preferredLanguageRefsets = new ArrayList<>();
			for (DescriptionPojo description : conceptIdToDescriptionMap.get(conceptId)) {
				if (conceptPojo != null) {
					conceptPojo.add(description);

					// Assign description module
					if (description.getModuleId() == null) {
						if (defaultModuleId != null) {
							description.setModuleId(defaultModuleId);
						} else {
							description.setModuleId(conceptPojo.getModuleId());
						}
					}

					// Get preferred language refset within new description
					if (description.getAcceptabilityMap() != null) {
						Map<String, DescriptionPojo.Acceptability> acceptabilityMap = description.getAcceptabilityMap();
						for (String languageRefset : acceptabilityMap.keySet()) {
							if (PREFERRED.equals(acceptabilityMap.get(languageRefset))) {
								preferredLanguageRefsets.add(languageRefset);
							}
						}
					}

					if (!conceptPojo.isActive()) {
						getChangeResult(changes, description, DESCRIPTION_WITHOUT_ID_COMPARATOR).addWarning("Adding description to inactive concept");
					}

				} else {
					getChangeResult(changes, description, DESCRIPTION_WITHOUT_ID_COMPARATOR).fail(format("Concept %s not found.", description.getConceptId()));
					// Description not joined to any concept so no will not appear in the update request.
				}
			}

			// Set the the existing PT automatically to acceptable if any
			if (conceptPojo != null && !preferredLanguageRefsets.isEmpty()) {
				for (DescriptionPojo description : conceptPojo.getDescriptions()) {
					if (description.isActive() &&
						defaultModuleId.equals(description.getModuleId()) &&
						!description.getDescriptionId().contains("-")) {
						Map<String, DescriptionPojo.Acceptability> acceptabilityMap = description.getAcceptabilityMap();
						for (String languageRefset : acceptabilityMap.keySet()) {
							if (PREFERRED.equals(acceptabilityMap.get(languageRefset)) && preferredLanguageRefsets.contains(languageRefset)) {
								acceptabilityMap.put(languageRefset, ACCEPTABLE);
							}
						}
						description.setAcceptabilityMap(acceptabilityMap);
					}
				}
			}
		}

		bulkValidateThenUpdateConcepts(conceptMap, branchPath, new ArrayList<>(changes));
		if (!conceptMap.isEmpty()) {
			// Batch load concepts again to fetch identifiers of new components
			List<ConceptPojo> updatedConcepts = snowstormClient.getFullConcepts(SnowstormClient.ConceptBulkLoadRequest.byConceptId(conceptMap.keySet()), branchPath);

			for (ConceptPojo updatedConcept : updatedConcepts) {
				final Set<DescriptionPojo> savedDescriptions = updatedConcept.getDescriptions();
				Set<DescriptionPojo> descriptionPojos = conceptIdToDescriptionMap.get(updatedConcept.getConceptId());
				for (DescriptionPojo descriptionPojo : descriptionPojos) {
					if (descriptionPojo.getDescriptionId() == null) {
						// Set description id from updated concept so it's in the final output
						savedDescriptions.stream()
								.filter(d -> DESCRIPTION_WITHOUT_ID_COMPARATOR.compare(descriptionPojo, d) == 0)
								.findFirst()
								.ifPresent(pojo -> descriptionPojo.setDescriptionId(pojo.getDescriptionId()));
					}
					getChangeResult(changes, descriptionPojo, DESCRIPTION_WITH_CONCEPT_ID_COMPARATOR).success();
				}
			}
		}
	}

	public List<ChangeResult<? extends SnomedComponent>> updateDescriptions(
			ComponentTransformationRequest request, List<DescriptionPojo> descriptions, List<ChangeResult<DescriptionPojo>> changes) throws BusinessServiceException {

		if (descriptions.isEmpty()) {
			return new ArrayList<>(changes);
		}
		logger.info("Starting process to update {} descriptions.", descriptions.size());

		try {
			// Initial terminology server communication check
			snowstormClient.getBranch("MAIN");

			// Split into batches
			int batchNumber = 0;
			for (List<DescriptionPojo> descriptionTaskBatch : Iterables.partition(descriptions, request.getBatchSize())) {
				batchNumber++;
				String branchPath = getBatchBranch(request, batchNumber);

				// Split into smaller batches if the number of per branch changes exceeds the number of concepts which should be processed at a time.
				for (List<DescriptionPojo> descriptionProcessingBatch : Iterables.partition(descriptionTaskBatch, processingBatchMaxSize)) {
					List<ChangeResult<DescriptionPojo>> changesBatch = changes.stream()
							.filter(descriptionPojoChangeResult -> descriptionProcessingBatch.contains(descriptionPojoChangeResult.getComponent()))
							.collect(Collectors.toList());
					updateDescriptionBatch(descriptionProcessingBatch, changesBatch, branchPath);
				}

			}
		} catch (WebClientException | TimeoutException e) {// This RuntimeException is thrown by WebClient
			logger.error("Failed to communicate with the terminology server.", e);
			failAllRemaining(changes, "Failed to communicate with the terminology server.");
		}

		return new ArrayList<>(changes);
	}

	public List<ChangeResult<? extends SnomedComponent>> replaceDescriptions(
			ComponentTransformationRequest request, List<DescriptionPojo> descriptions, List<ChangeResult<DescriptionReplacementPojo>> changes) throws BusinessServiceException {
		if (descriptions.isEmpty()) {
			return new ArrayList<>(changes);
		}
		logger.info("Starting process to replace {} descriptions.", descriptions.size());

		try {
			// Initial terminology server communication check
			snowstormClient.getBranch("MAIN");

			String defaultModuleId;
			try {
				defaultModuleId = snowstormClient.getDefaultModuleId(request.getBranchPath());
			} catch (WebClientException e) {
				logger.info("Failed to load branch {} from the terminology server.", request, e);
				return failAllRemaining(changes, format("Failed to load branch %s from the terminology server.", request));
			}

			// Give new descriptions a temporary UUID
			descriptions.forEach(description -> {
				if (description.getDescriptionId() == null) {
					description.setDescriptionId(UUID.randomUUID().toString());
				}
			});

			Map<String, Set<DescriptionPojo>> conceptIdToDescriptionMap = new HashMap<>();
			for (DescriptionPojo description : descriptions) {
				conceptIdToDescriptionMap.computeIfAbsent(description.getConceptId(), (key) -> new HashSet<>()).add(description);
			}

			Map<String, DescriptionReplacementPojo> conceptIdToDescriptionReplacementMap = new HashMap<>();
			for (ChangeResult<DescriptionReplacementPojo> changeResultDescriptionReplacement : changes) {
				conceptIdToDescriptionReplacementMap.computeIfAbsent(changeResultDescriptionReplacement.getComponent().getInactivatedDescription().getConceptId(), (key) -> changeResultDescriptionReplacement.getComponent());
			}

			// Split into batches of how many changes per branch / task
			int batchNumber = 0;
			for (List<String> conceptIdTaskBatch : Iterables.partition(conceptIdToDescriptionMap.keySet(), request.getBatchSize())) {

				batchNumber++;
				String branchPath = getBatchBranch(request, batchNumber);

				// Split into smaller batches if the number of per branch changes exceeds the number of concepts which should be processed at a time.
				for (List<String> conceptIdProcessingBatch : Iterables.partition(conceptIdTaskBatch, processingBatchMaxSize)) {
					Map<String, Set<DescriptionPojo>> batchMap = new HashMap<>();
					for (String conceptId : conceptIdProcessingBatch) {
						batchMap.put(conceptId, conceptIdToDescriptionMap.get(conceptId));
					}
					replaceDescriptionBatch(batchMap, defaultModuleId, changes, conceptIdToDescriptionReplacementMap, branchPath);
				}
			}

		} catch (WebClientException | TimeoutException e) {// This RuntimeException is thrown by WebClient
			logger.error("Failed to communicate with the terminology server.", e);
			failAllRemaining(changes, "Failed to communicate with the terminology server.");
		}

		return new ArrayList<>(changes);
	}
	private void replaceDescriptionBatch(Map <String, Set <DescriptionPojo>> conceptIdToDescriptionMap, String defaultModuleId,
										 List <ChangeResult <DescriptionReplacementPojo>> changes, Map <String, DescriptionReplacementPojo> conceptIdToDescriptionReplacementMap, String branchPath) throws BusinessServiceException, TimeoutException {

		// Batch load concepts
		List<ConceptPojo> concepts = snowstormClient.getFullConcepts(SnowstormClient.ConceptBulkLoadRequest.byConceptId(conceptIdToDescriptionMap.keySet()), branchPath);

		Map<String, ConceptPojo> conceptMap = concepts.stream().collect(Collectors.toMap(ConceptPojo::getConceptId, Function.identity()));
		Map<String, ConceptPojo> updatedConceptMap = new HashMap <>();
		for (String conceptId : conceptIdToDescriptionMap.keySet()) {
			ConceptPojo conceptPojo = conceptMap.get(conceptId);
			if (conceptPojo == null) {
				getChangeResult(changes, conceptIdToDescriptionReplacementMap.get(conceptId), CONCEPT_ID_COMPARATOR).fail(format("Concept %s not found.", conceptId));
			} else {
				boolean newDescriptionFound = false;
				boolean inactiveDescriptionFound = false;
				boolean updatedDescriptionFound = false;
				boolean skipUpdatingConcept = false;
				Map<String, DescriptionPojo>  descriptionMap = conceptPojo.getDescriptions().stream().collect(Collectors.toMap(DescriptionPojo::getDescriptionId, Function.identity()));
				for (DescriptionPojo description : conceptIdToDescriptionMap.get(conceptId)) {
					// Join new description
					if (description.getDescriptionId().contains("-")) {
						newDescriptionFound = true;
						conceptPojo.add(description);

						// Assign description module
						if (description.getModuleId() == null) {
							if (defaultModuleId != null) {
								description.setModuleId(defaultModuleId);
							} else {
								description.setModuleId(conceptPojo.getModuleId());
							}
						}
					} else {
						// Update the replacement description if specified and inactivate the provided description
						for (DescriptionPojo loadedDescription : conceptPojo.getDescriptions()) {
							if (loadedDescription.getDescriptionId().equals(description.getDescriptionId()) && !skipUpdatingConcept) {
								if (loadedDescription.getModuleId().equals(defaultModuleId)) {
									if (!description.isActive()) {
										if (!loadedDescription.isActive()) {
											getChangeResult(changes, conceptIdToDescriptionReplacementMap.get(conceptId), CONCEPT_ID_COMPARATOR).fail(format("Could not inactivate the inactive description with Id {}.", loadedDescription.getDescriptionId()));
											skipUpdatingConcept = true;
										} else if (!loadedDescription.isReleased()) {
											getChangeResult(changes, conceptIdToDescriptionReplacementMap.get(conceptId), CONCEPT_ID_COMPARATOR).fail(format("Could not inactivate the un-published description with Id {}.", loadedDescription.getDescriptionId()));
											skipUpdatingConcept = true;
										} else {
											loadedDescription.setInactivationIndicator(description.getInactivationIndicator());
											loadedDescription.setAssociationTargets(description.getAssociationTargets());
											loadedDescription.setActive(false);
											inactiveDescriptionFound = true;
										}
									} else {
										DescriptionPojo inactivatedDescription = descriptionMap.get(conceptIdToDescriptionReplacementMap.get(conceptId).getInactivatedDescription().getDescriptionId());
										if (inactivatedDescription != null) {
											// Get acceptability from the inactivated description
											Map<String, DescriptionPojo.Acceptability> acceptabilityMap = inactivatedDescription.getAcceptabilityMap();
											List<String> udpatedAcceptabilities = new ArrayList <>();
											for (String key : acceptabilityMap.keySet()) {
												if (PREFERRED.equals(acceptabilityMap.get(key))) {
													udpatedAcceptabilities.add(key);
												}
											}

											// update new acceptability for replaced description
											if (!udpatedAcceptabilities.isEmpty()) {
												acceptabilityMap = loadedDescription.getAcceptabilityMap();
												for (String languageRefset : udpatedAcceptabilities) {
													acceptabilityMap.put(languageRefset, PREFERRED);
												}
												loadedDescription.setAcceptabilityMap(acceptabilityMap);
												loadedDescription.setActive(true);
												updatedDescriptionFound = true;
											} else {
												getChangeResult(changes, conceptIdToDescriptionReplacementMap.get(conceptId), CONCEPT_ID_COMPARATOR).fail(format("No Preferred Acceptability in description {}", inactivatedDescription.getDescriptionId()));
												skipUpdatingConcept = true;
											}
										}
									}
								} else {
									getChangeResult(changes, conceptIdToDescriptionReplacementMap.get(conceptId), CONCEPT_ID_COMPARATOR).fail(format("Concept update description with Id {} against module {}.", loadedDescription.getDescriptionId(), defaultModuleId));
								}
							}
						}
					}

					if (!conceptPojo.isActive()) {
						getChangeResult(changes, conceptIdToDescriptionReplacementMap.get(conceptId), CONCEPT_ID_COMPARATOR).addWarning("Adding or replacing description to inactive concept");
					}
				}
				if (!skipUpdatingConcept) {
					if (inactiveDescriptionFound && (newDescriptionFound || updatedDescriptionFound)) {
						updatedConceptMap.put(conceptId, conceptPojo);
					} else {
						String error = "";
						if (!inactiveDescriptionFound) {
							error = "Inactivated description not found";
						} else {
							if (conceptIdToDescriptionReplacementMap.get(conceptId).getUpdatedDescription() != null) {
								error = "Replaced description not found";
							}
						}
						getChangeResult(changes, conceptIdToDescriptionReplacementMap.get(conceptId), CONCEPT_ID_COMPARATOR).fail(error);
					}
				}
			}
		}

		if (!updatedConceptMap.isEmpty()) {
			bulkValidateThenUpdateConcepts(updatedConceptMap, branchPath, changes);

			// Mark all changes which have not failed as successful
			changes.stream().filter(change -> change.getSuccess() == null).forEach(ChangeResult::success);
		}
	}

	public List<ChangeResult<? extends SnomedComponent>> updateAxioms(
			ComponentTransformationRequest request, List<AxiomPojo> axioms, List<ChangeResult<AxiomPojo>> changes) throws BusinessServiceException {

		if (axioms.isEmpty()) {
			return new ArrayList<>(changes);
		}
		logger.info("Starting process to update {} axioms.", axioms.size());

		try {
			// Initial terminology server communication check
			snowstormClient.getBranch("MAIN");

			// Split into batches, changes per task
			int batchNumber = 0;
			for (List<AxiomPojo> axiomsTaskBatch : Iterables.partition(axioms, request.getBatchSize())) {
				batchNumber++;
				String branchPath = getBatchBranch(request, batchNumber);

				// Split into smaller batches if the number of per branch changes exceeds the number of concepts which should be processed at a time.
				for (List<AxiomPojo> axiomsProcessingBatch : Iterables.partition(axiomsTaskBatch, processingBatchMaxSize)) {
					List<ChangeResult<AxiomPojo>> changesBatch = changes.stream()
							.filter(axiomPojoChangeResult -> axiomsProcessingBatch.contains(axiomPojoChangeResult.getComponent()))
							.collect(Collectors.toList());
					updateAxiomBatch(axiomsProcessingBatch, changesBatch, branchPath);
				}
			}
		} catch (WebClientException | TimeoutException e) {// This RuntimeException is thrown by WebClient
			logger.error("Failed to communicate with the terminology server.", e);
			failAllRemaining(changes, "Failed to communicate with the terminology server.");
		}

		return new ArrayList<>(changes);
	}

	private void updateDescriptionBatch(List<DescriptionPojo> descriptionBatch, List<ChangeResult<DescriptionPojo>> changesBatch, String branchPath) throws BusinessServiceException, TimeoutException {
		// Batch load concepts by description id
		Set<String> descriptionIds = descriptionBatch.stream().map(DescriptionPojo::getDescriptionId).collect(Collectors.toSet());
		List<ConceptPojo> concepts = snowstormClient.getFullConcepts(SnowstormClient.ConceptBulkLoadRequest.byDescriptionId(descriptionIds), branchPath);

		// Update existing descriptions
		Map<String, DescriptionPojo> descriptionIdMap = descriptionBatch.stream().collect(Collectors.toMap(DescriptionPojo::getDescriptionId, Function.identity()));
		Set<String> descriptionsFound = new HashSet<>();
		for (ConceptPojo loadedConcept : concepts) {
			for (DescriptionPojo loadedDescription : loadedConcept.getDescriptions()) {
				DescriptionPojo descriptionUpdate = descriptionIdMap.get(loadedDescription.getDescriptionId());
				if (descriptionUpdate != null) {
					descriptionUpdate.setConceptId(loadedConcept.getConceptId());
					descriptionsFound.add(descriptionUpdate.getDescriptionId());
					if (descriptionUpdate.getCaseSignificance() != null) {
						loadedDescription.setCaseSignificance(descriptionUpdate.getCaseSignificance());
					}
					if (!isEmpty(descriptionUpdate.getModuleId())) {
						loadedDescription.setModuleId(descriptionUpdate.getModuleId());
					}
					if (descriptionUpdate.getAcceptabilityMap() != null) {
						loadedDescription.setAcceptabilityMap(descriptionUpdate.getAcceptabilityMap());
					}
					if (!descriptionUpdate.isActive()) {
						loadedDescription.setInactivationIndicator(descriptionUpdate.getInactivationIndicator());
						loadedDescription.setAssociationTargets(descriptionUpdate.getAssociationTargets());
						loadedDescription.setActive(false);
					}
				}
			}
		}
		// Fail all descriptions which were not found to update
		for (String notFoundDescriptionId : difference(descriptionIdMap.keySet(), descriptionsFound)) {
			getChangeResult(changesBatch, descriptionIdMap.get(notFoundDescriptionId), DESCRIPTION_WITHOUT_ID_COMPARATOR).fail("Description not found on the specified branch.");
		}

		if (!descriptionsFound.isEmpty()) {
			Map<String, ConceptPojo> conceptMap = concepts.stream().collect(Collectors.toMap(ConceptPojo::getConceptId, Function.identity()));
			bulkValidateThenUpdateConcepts(conceptMap, branchPath, changesBatch);
			// Mark all changes which have not failed as successful
			changesBatch.stream().filter(change -> change.getSuccess() == null).forEach(ChangeResult::success);
		}
	}

	private void updateAxiomBatch(List<AxiomPojo> axiomBatch, List<ChangeResult<AxiomPojo>> changesBatch, String branchPath) throws BusinessServiceException, TimeoutException {
		// Batch load concepts by conceptId
		Set<String> conceptIds = axiomBatch.stream().map(AxiomPojo::getConceptId).collect(Collectors.toSet());
		List<ConceptPojo> concepts = snowstormClient.getFullConcepts(SnowstormClient.ConceptBulkLoadRequest.byConceptId(conceptIds), branchPath);

		// Update existing axioms
		Map<String, Set<AxiomPojo>> conceptIdAxiomSetMap = new HashMap<>();
		axiomBatch.forEach(axiomPojo -> conceptIdAxiomSetMap.computeIfAbsent(axiomPojo.getConceptId(), key -> new HashSet<>()).add(axiomPojo));
		Set<AxiomPojo> axiomsFound = new HashSet<>();
		for (ConceptPojo loadedConcept : concepts) {
			for (AxiomPojo axiomUpdate : conceptIdAxiomSetMap.get(loadedConcept.getConceptId())) {
				Boolean gci = axiomUpdate.getGci();
				if (gci != null && !gci) {
					// Class axiom
					Set<AxiomPojo> activeClassAxioms = loadedConcept.getClassAxioms().stream().filter(AxiomPojo::isActive).collect(Collectors.toSet());
					for (AxiomPojo loadedAxiom : activeClassAxioms) {
						if (loadedAxiom.getAxiomId().equals(axiomUpdate.getAxiomId()) || activeClassAxioms.size() == 1) {
							axiomUpdate.setAxiomId(loadedAxiom.getAxiomId());
							axiomsFound.add(axiomUpdate);

							loadedAxiom.setRelationships(axiomUpdate.getRelationships());
						}
					}
				} else if (gci != null) {
					// GCI axiom
					Set<AxiomPojo> activeGCIAxioms = loadedConcept.getGciAxioms().stream().filter(AxiomPojo::isActive).collect(Collectors.toSet());
					for (AxiomPojo loadedAxiom : activeGCIAxioms) {
						if (loadedAxiom.getAxiomId().equals(axiomUpdate.getAxiomId()) || activeGCIAxioms.size() == 1) {
							axiomUpdate.setAxiomId(loadedAxiom.getAxiomId());
							axiomsFound.add(axiomUpdate);

							loadedAxiom.setRelationships(axiomUpdate.getRelationships());
						}
					}
				}
			}
		}
		// Fail all axioms which were not found to update
		for (AxiomPojo notFoundAxiom : difference(new HashSet<>(axiomBatch), axiomsFound)) {
			getChangeResult(changesBatch, notFoundAxiom).fail("Axiom not found on the specified branch.");
		}

		if (!axiomsFound.isEmpty()) {
			Map<String, ConceptPojo> conceptMap = concepts.stream().collect(Collectors.toMap(ConceptPojo::getConceptId, Function.identity()));
			bulkValidateThenUpdateConcepts(conceptMap, branchPath, changesBatch);
			// Mark all changes which have not failed as successful
			changesBatch.stream().filter(change -> change.getSuccess() == null).forEach(ChangeResult::success);
		}
	}

	private String getBatchBranch(ComponentTransformationRequest request, int batchNumber) {
		String branchPath = request.getBranchPath();

		String projectKey = request.getProjectKey();
		if (!isEmpty(projectKey)) {
			AuthoringTask task = authoringServicesClient.createTask(projectKey,
					format("%s - Batch #%s", request.getTaskTitle(), batchNumber), "Created automatically by the batch processing function.");
			branchPath = task.getBranchPath();
			snowstormClient.createBranch(branchPath);
			task.setStatus("IN_PROGRESS");
			if (request.getTaskAssignee() != null) {
				task.setAssignee(new TaskUser(request.getTaskAssignee()));
			}
			if (request.getTaskReviewer() != null) {
				task.setReviewers(Collections.singleton(new TaskUser(request.getTaskReviewer())));
			}
			authoringServicesClient.updateAuthoringTaskNotNullFieldsAreSet(task);
		}
		return branchPath;
	}

	/**
	 * Any concepts which fail validation or update will be removed from the conceptMap.
	 * @param conceptMap Map of concepts to be updated.
	 * @param branchPath Branch path to validation and update against.
	 * @param changes Set of changes contained in the concepts.
	 * @throws WebClientException Thrown if terminology server communication returns non 2xx status code.
	 * @throws TimeoutException Thrown if terminology server update times out.
	 */
	public <T extends SnomedComponent> void bulkValidateThenUpdateConcepts(Map<String, ConceptPojo> conceptMap, String branchPath,
			List<ChangeResult<T>> changes) throws WebClientException, TimeoutException {

		if (!skipDroolsValidation) {
			// Run batch validation
			List<ConceptValidationResult> validationResults = snowstormClient.runValidation(branchPath, conceptMap.values());

			Map<String, Set<ConceptValidationResult>> conceptValidationResultMap = new HashMap<>();
			for (ConceptValidationResult validationResult : validationResults) {
				conceptValidationResultMap.computeIfAbsent(validationResult.getConceptId(), (c) -> new TreeSet<>(CONCEPT_VALIDATION_RESULT_COMPARATOR)).add(validationResult);
			}

			// Remove concepts with validation errors
			// All component changes for this concept will not be saved
			Set<String> conceptsWithError = validationResults.stream()
					.filter(validationResult -> validationResult.getSeverity() == ERROR)
					.map(ConceptValidationResult::getConceptId)
					.collect(Collectors.toSet());
			logger.info("{} concepts had validation errors.", conceptsWithError.size());
			for (String conceptWithError : conceptsWithError) {
				changes.stream()
						.filter(change -> change.getSuccess() == null && change.getComponent().getConceptId().equals(conceptWithError))
						.forEach(changeResult -> changeResult.fail(format("Concept validation errors: %s", conceptValidationResultMap.get(conceptWithError).toString())));
				conceptMap.remove(conceptWithError);
				// Whole concept removed from map so changes will not appear in the update request.
			}
		}

		// Remove temp description UUIDs
		for (ConceptPojo concept : conceptMap.values()) {
			for (DescriptionPojo description : concept.getDescriptions()) {
				if (description.getDescriptionId().contains("-")) {
					description.setDescriptionId(null);
				}
			}
		}

		if (conceptMap.isEmpty()) {
			return;
		}

		// Bulk update concepts
		Collection<ConceptPojo> conceptPojos = conceptMap.values();
		ConceptChangeBatchStatus status = snowstormClient.saveUpdateConceptsNoValidation(conceptPojos, branchPath);
		if (ConceptChangeBatchStatus.Status.FAILED == status.getStatus()) {
			failAllRemaining(changes, "Persisting concept batch failed with message: " + status.getMessage());
			conceptMap.clear();
		}
	}


	private ChangeResult<DescriptionPojo> getChangeResult(List<ChangeResult<DescriptionPojo>> changeResults, DescriptionPojo description, Comparator<DescriptionPojo> comparator) throws BusinessServiceException {
		for (ChangeResult<DescriptionPojo> changeResult : changeResults) {
			if (comparator.compare(changeResult.getComponent(), description) == 0) {
				return changeResult;
			}
		}
		String message = format("Change result not found for description %s", description.toString());
		logger.error(message);
		throw new BusinessServiceException(message);
	}

	private ChangeResult<DescriptionReplacementPojo> getChangeResult(List<ChangeResult<DescriptionReplacementPojo>> changeResults, DescriptionReplacementPojo descriptionReplacement, Comparator<DescriptionReplacementPojo> comparator) throws BusinessServiceException {
		for (ChangeResult<DescriptionReplacementPojo> changeResult : changeResults) {
			if (comparator.compare(changeResult.getComponent(), descriptionReplacement) == 0) {
				return changeResult;
			}
		}
		String message = format("Change result not found for description %s", descriptionReplacement.toString());
		logger.error(message);
		throw new BusinessServiceException(message);
	}

	private ChangeResult<AxiomPojo> getChangeResult(List<ChangeResult<AxiomPojo>> changeResults, AxiomPojo axiom) throws BusinessServiceException {
		for (ChangeResult<AxiomPojo> changeResult : changeResults) {
			if (changeResult.getComponent().equals(axiom)) {
				return changeResult;
			}
		}
		String message = format("Change result not found for axiom %s", axiom.toString());
		logger.error(message);
		throw new BusinessServiceException(message);
	}

	private <T extends SnomedComponent> List<ChangeResult<? extends SnomedComponent>> failAllRemaining(List<ChangeResult<T>> changeResults, String message) {
		changeResults.stream().filter(r -> r.getSuccess() == null).forEach(r -> r.fail(message));
		return new ArrayList<>(changeResults);
	}

}
