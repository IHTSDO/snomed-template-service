package org.ihtsdo.otf.transformationandtemplate.service.componenttransform;

import com.google.common.collect.Iterables;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.ConceptChangeBatchStatus;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.ConceptPojo;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.DescriptionPojo;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.SnomedComponent;
import org.ihtsdo.otf.rest.exception.BusinessServiceException;
import org.ihtsdo.otf.transformationandtemplate.domain.ComponentTransformationRequest;
import org.ihtsdo.otf.transformationandtemplate.service.client.AuthoringServicesClient;
import org.ihtsdo.otf.transformationandtemplate.service.client.ChangeResult;
import org.ihtsdo.otf.transformationandtemplate.service.client.ConceptValidationResult;
import org.ihtsdo.otf.transformationandtemplate.service.client.SnowstormClient;
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

/**
 * Service which acts as the logged in user and provides high level authoring functionality.
 * For example creating a batch of descriptions involves: loading the concepts, joining the descriptions, validation then save.
 */
public class HighLevelAuthoringService {

	private final SnowstormClient snowstormClient;

	private final AuthoringServicesClient authoringServicesClient;

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private static final Comparator<DescriptionPojo> DESCRIPTION_WITHOUT_ID_COMPARATOR = Comparator.comparing(DescriptionPojo::getTerm).thenComparing(DescriptionPojo::getLang);
	private static final Comparator<ConceptValidationResult> CONCEPT_VALIDATION_RESULT_COMPARATOR = Comparator.comparing(ConceptValidationResult::getSeverity);

	public HighLevelAuthoringService(SnowstormClient snowstormClient, AuthoringServicesClient authoringServicesClient) {
		this.snowstormClient = snowstormClient;
		this.authoringServicesClient = authoringServicesClient;
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

			// Split into batches
			int batchNumber = 0;
			for (List<String> conceptIdBatch : Iterables.partition(conceptIdToDescriptionMap.keySet(), request.getBatchSize())) {

				batchNumber++;
				String branchPath = request.getBranchPath();
//				String projectKey = request.getProjectKey();
//				if (!isEmpty(projectKey)) {
//					branchPath = authoringServicesClient.getNextBranch(projectKey);
//				}

				// Batch load concepts
				List<ConceptPojo> concepts = snowstormClient.getFullConcepts(SnowstormClient.ConceptBulkLoadRequest.byConceptId(conceptIdBatch), request.getBranchPath());

				// Join new descriptions to concepts
				Map<String, ConceptPojo> conceptMap = concepts.stream().collect(Collectors.toMap(ConceptPojo::getConceptId, Function.identity()));
				for (DescriptionPojo description : descriptions) {
					ConceptPojo conceptPojo = conceptMap.get(description.getConceptId());
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

						if (!conceptPojo.isActive()) {
							getChangeResult(changes, description).addWarning("Adding description to inactive concept");
						}

					} else {
						getChangeResult(changes, description).fail(format("Concept %s not found.", description.getConceptId()));
						// Description not joined to any concept so no will not appear in the update request.
					}
				}

				bulkValidateThenUpdateConcepts(conceptMap, branchPath, changes);
				if (conceptMap.isEmpty()) {
					return new ArrayList<>(changes);
				}

				// Batch load concepts again to fetch identifiers of new components
				List<ConceptPojo> updatedConcepts = snowstormClient.getFullConcepts(SnowstormClient.ConceptBulkLoadRequest.byConceptId(conceptMap.keySet()), request.getBranchPath());

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
						getChangeResult(changes, descriptionPojo).success();
					}
				}
			}

		} catch (WebClientException | TimeoutException e) {// This RuntimeException is thrown by WebClient
			logger.error("Failed to communicate with the terminology server.", e);
			failAllRemaining(changes, "Failed to communicate with the terminology server.");
		}

		return new ArrayList<>(changes);
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

			String branchPath = request.getBranchPath();

			// Batch load concepts by description id
			Set<String> descriptionIds = descriptions.stream().map(DescriptionPojo::getDescriptionId).collect(Collectors.toSet());
			List<ConceptPojo> concepts = snowstormClient.getFullConcepts(SnowstormClient.ConceptBulkLoadRequest.byDescriptionId(descriptionIds), branchPath);

			// Update existing descriptions
			Map<String, DescriptionPojo> descriptionIdMap = descriptions.stream().collect(Collectors.toMap(DescriptionPojo::getDescriptionId, Function.identity()));
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
						}
					}
				}
			}
			// Fail all descriptions which were not found to update
			for (String notFoundDescriptionId : difference(descriptionIdMap.keySet(), descriptionsFound)) {
				getChangeResult(changes, descriptionIdMap.get(notFoundDescriptionId)).fail("Description not found on the specified branch.");
			}

			if (!descriptionsFound.isEmpty()) {
				Map<String, ConceptPojo> conceptMap = concepts.stream().collect(Collectors.toMap(ConceptPojo::getConceptId, Function.identity()));
				bulkValidateThenUpdateConcepts(conceptMap, branchPath, changes);
				// Mark all changes which have not failed as successful
				changes.stream().filter(change -> change.getSuccess() == null).forEach(ChangeResult::success);
			}
		} catch (WebClientException | TimeoutException e) {// This RuntimeException is thrown by WebClient
			logger.error("Failed to communicate with the terminology server.", e);
			failAllRemaining(changes, "Failed to communicate with the terminology server.");
		}

		return new ArrayList<>(changes);
	}

	/**
	 * Any concepts which fail validation or update will be removed from the conceptMap.
	 * @param conceptMap Map of concepts to be updated.
	 * @param branchPath Branch path to validation and update against.
	 * @param descriptionChanges Set of changes contained in the concepts.
	 * @throws WebClientException Thrown if terminology server communication returns non 2xx status code.
	 * @throws TimeoutException Thrown if terminology server update times out.
	 */
	public void bulkValidateThenUpdateConcepts(Map<String, ConceptPojo> conceptMap, String branchPath,
			List<ChangeResult<DescriptionPojo>> descriptionChanges) throws WebClientException, TimeoutException {

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
			descriptionChanges.stream()
					.filter(change -> change.getSuccess() == null && change.getComponent().getConceptId().equals(conceptWithError))
					.forEach(changeResult -> changeResult.fail(format("Concept validation errors: %s", conceptValidationResultMap.get(conceptWithError).toString())));
			conceptMap.remove(conceptWithError);
			// Whole concept removed from map so changes will not appear in the update request.
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
			failAllRemaining(descriptionChanges, "Persisting concept batch failed with message: " + status.getMessage());
			conceptMap.clear();
		}
	}


	private ChangeResult<DescriptionPojo> getChangeResult(List<ChangeResult<DescriptionPojo>> changeResults, DescriptionPojo description) throws BusinessServiceException {
		for (ChangeResult<DescriptionPojo> changeResult : changeResults) {
			if (DESCRIPTION_WITHOUT_ID_COMPARATOR.compare(changeResult.getComponent(), description) == 0) {
				return changeResult;
			}
		}
		String message = format("Change result not found for description %s", description.toString());
		logger.error(message);
		throw new BusinessServiceException(message);
	}

	private List<ChangeResult<? extends SnomedComponent>> failAllRemaining(List<ChangeResult<DescriptionPojo>> changeResults, String message) {
		changeResults.stream().filter(r -> r.getSuccess() == null).forEach(r -> r.fail(message));
		return new ArrayList<>(changeResults);
	}

}
