package org.ihtsdo.otf.transformationandtemplate.service.client;

import org.ihtsdo.otf.rest.client.terminologyserver.pojo.*;
import org.ihtsdo.otf.rest.exception.BusinessServiceException;
import org.ihtsdo.otf.rest.exception.ProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.google.common.collect.Sets.difference;
import static java.lang.String.format;
import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.ihtsdo.otf.transformationandtemplate.service.client.ConceptValidationResult.Severity.ERROR;

public class SnowstormClient {

	private static final String DEFAULT_MODULE_ID_METADATA_KEY = "defaultModuleId";
	private static final ParameterizedTypeReference<List<ConceptPojo>> CONCEPT_LIST_TYPE_REF = new ParameterizedTypeReference<List<ConceptPojo>>() {};
	private static final ParameterizedTypeReference<List<ConceptValidationResult>> CONCEPT_VALIDATION_RESULT_TYPE_REF = new ParameterizedTypeReference<List<ConceptValidationResult>>() {};
	private static final Comparator<ConceptValidationResult> CONCEPT_VALIDATION_RESULT_COMPARATOR = Comparator.comparing(ConceptValidationResult::getSeverity);
	private static final Comparator<DescriptionPojo> DESCRIPTION_WITHOUT_ID_COMPARATOR = Comparator.comparing(DescriptionPojo::getTerm).thenComparing(DescriptionPojo::getLang);

	private final WebClient webClient;
	private final Logger logger = LoggerFactory.getLogger(SnowstormClient.class);

	public static SnowstormClient createClientForUser(String snowstormApiUrl, String authenticationCookie) {
		return new SnowstormClient(snowstormApiUrl, authenticationCookie);
	}

	private SnowstormClient(String snowstormApiUrl, String authenticationCookie) {
		WebClient.Builder builder = WebClient.builder()
				.baseUrl(snowstormApiUrl)
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		if (!isEmpty(authenticationCookie) && authenticationCookie.contains("=")) {
			String[] split = authenticationCookie.split("=");
			builder.defaultCookie(split[0], split[1]);
		}
		webClient = builder.build();
	}

	public List<ChangeResult<? extends SnomedComponent>> updateDescriptions(List<DescriptionPojo> descriptions,
			List<ChangeResult<DescriptionPojo>> changes, String branchPath) throws BusinessServiceException {

		if (descriptions.isEmpty()) {
			return new ArrayList<>(changes);
		}
		logger.info("Starting process to update {} descriptions.", descriptions.size());

		try {
			// Initial terminology server communication check
			getBranch("MAIN");

			// Batch load concepts by description id
			Set<String> descriptionIds = descriptions.stream().map(DescriptionPojo::getDescriptionId).collect(Collectors.toSet());
			List<ConceptPojo> concepts = webClient.post()
					.uri(uriBuilder -> uriBuilder
							.path("/browser/{branch}/concepts/bulk-load")
							.build(branchPath))
					.body(BodyInserters.fromObject(ConceptBulkLoadRequest.byDescriptionId(descriptionIds)))
					.retrieve()
					.bodyToMono(CONCEPT_LIST_TYPE_REF)
					.block();
			logger.info("Loaded {} concepts.", concepts.size());

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
		} catch (WebClientException e) {// This RuntimeException is thrown by WebClient
			logger.error("Failed to communicate with the terminology server.", e);
			failAllRemaining(changes, "Failed to communicate with the terminology server.");
		}

		return new ArrayList<>(changes);
	}

	public List<ChangeResult<? extends SnomedComponent>> createDescriptions(List<DescriptionPojo> descriptions,
			List<ChangeResult<DescriptionPojo>> changes, String branchPath) throws BusinessServiceException {

		if (descriptions.isEmpty()) {
			return new ArrayList<>(changes);
		}
		logger.info("Starting process to save {} descriptions.", descriptions.size());


		try {
			// Initial terminology server communication check
			getBranch("MAIN");

			String defaultModuleId;
			try {
				defaultModuleId = getDefaultModuleId(branchPath);
			} catch (WebClientException e) {
				logger.info("Failed to load branch {} from the terminology server.", branchPath, e);
				return failAllRemaining(changes, format("Failed to load branch %s from the terminology server.", branchPath));
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

			// Batch load concepts
			Set<String> conceptIds = descriptions.stream().map(DescriptionPojo::getConceptId).collect(Collectors.toSet());
			List<ConceptPojo> concepts = webClient.post()
					.uri(uriBuilder -> uriBuilder
							.path("/browser/{branch}/concepts/bulk-load")
							.build(branchPath))
					.body(BodyInserters.fromObject(ConceptBulkLoadRequest.byConceptId(conceptIds)))
					.retrieve()
					.bodyToMono(CONCEPT_LIST_TYPE_REF)
					.block();
			logger.info("Loaded {} concepts.", concepts.size());

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
			List<ConceptPojo> updatedConcepts = webClient.post()
					.uri(uriBuilder -> uriBuilder
							.path("/browser/{branch}/concepts/bulk-load")
							.build(branchPath))
					.body(BodyInserters.fromObject(ConceptBulkLoadRequest.byConceptId(conceptMap.keySet())))
					.retrieve()
					.bodyToMono(CONCEPT_LIST_TYPE_REF)
					.block();
			logger.info("Loaded {} concepts after update.", updatedConcepts.size());

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
		} catch (WebClientException e) {// This RuntimeException is thrown by WebClient
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
	 * @throws ProcessingException Thrown if terminology server update times out.
	 */
	private void bulkValidateThenUpdateConcepts(Map<String, ConceptPojo> conceptMap, String branchPath,
			List<ChangeResult<DescriptionPojo>> descriptionChanges) throws WebClientException, ProcessingException {

		// Run batch validation
		List<ConceptValidationResult> validationResults = runValidation(branchPath, conceptMap.values());

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
					.forEach(changeResult -> changeResult.fail(format("Concept validation errors: %s", toString(conceptValidationResultMap.get(conceptWithError)))));
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
		logger.info("Saving {} concepts.", conceptMap.size());
		ClientResponse bulkUpdateResponse = webClient.post()
				.uri(uriBuilder -> uriBuilder
						.path("/browser/{branch}/concepts/bulk")
						.build(branchPath))
				.body(BodyInserters.fromObject(conceptMap.values()))
				.exchange()
				.block();
		String locationHeader = bulkUpdateResponse.headers().header("Location").get(0);
		logger.info("Bulk update job url: {}", locationHeader);

		int maxWaitSeconds = conceptMap.size() * 10_000;
		ConceptChangeBatchStatus status = getBatchStatus(locationHeader, maxWaitSeconds);
		if (ConceptChangeBatchStatus.Status.FAILED == status.getStatus()) {
			failAllRemaining(descriptionChanges, "Persisting concept batch failed with message: " + status.getMessage());
			conceptMap.clear();
		}
	}

	private String getDefaultModuleId(String branchPath) {
		String defaultModuleId;// Get branch metadata
		Branch branch = getBranch(branchPath);
		defaultModuleId = getMetadataString(branch, DEFAULT_MODULE_ID_METADATA_KEY);
		return defaultModuleId;
	}

	private String getMetadataString(Branch branch, String key) {
		return branch != null && branch.getMetadata() != null && branch.getMetadata().containsKey(key) ? (String) branch.getMetadata().get(key) : null;
	}

	private Branch getBranch(String branchPath) {
		return webClient.get()
				.uri(uriBuilder -> uriBuilder
						.path("/branches/{branch}")
						.queryParam("includeInheritedMetadata", true)
						.build(branchPath))
				.retrieve()
				.bodyToMono(Branch.class)
				.block();
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

	private ConceptChangeBatchStatus getBatchStatus(String locationHeader, int maxWaitSeconds) throws ProcessingException {
		int waitSeconds = 0;
		while (waitSeconds < maxWaitSeconds) {
			ConceptChangeBatchStatus latestBatchStatus = webClient.get()
					.uri(locationHeader)
					.retrieve()
					.bodyToMono(ConceptChangeBatchStatus.class)
					.block();
			ConceptChangeBatchStatus.Status status = latestBatchStatus.getStatus();
			if (status != ConceptChangeBatchStatus.Status.RUNNING) {
				return latestBatchStatus;
			}
			try {
				waitSeconds++;
				Thread.sleep(1_000);
			} catch (InterruptedException e) {
				logger.warn("Interrupted while polling batch status.", e);
			}
		}
		throw new ProcessingException("Batch change exceeded maximum duration.");
	}

	private String toString(Set<ConceptValidationResult> conceptValidationResults) {
		return conceptValidationResults.toString();
	}

	private List<ConceptValidationResult> runValidation(String branchPath, Collection<ConceptPojo> concepts) {
		logger.info("Validating {} concepts.", concepts.size());
		return webClient.post()
					.uri(uriBuilder -> uriBuilder
							.path("/browser/{branch}/validate/concepts")
							.build(branchPath))
					.body(BodyInserters.fromObject(concepts))
					.retrieve()
					.bodyToMono(CONCEPT_VALIDATION_RESULT_TYPE_REF)
					.block();
	}

	private static final class ConceptBulkLoadRequest {

		private final Set<String> conceptIds;
		private final Set<String> descriptionIds;

		private ConceptBulkLoadRequest(Set<String> conceptIds, Set<String> descriptionIds) {
			this.conceptIds = conceptIds;
			this.descriptionIds = descriptionIds;
		}

		public static ConceptBulkLoadRequest byConceptId(Set<String> conceptIds) {
			return new ConceptBulkLoadRequest(conceptIds, Collections.emptySet());
		}

		public static ConceptBulkLoadRequest byDescriptionId(Set<String> descriptionIds) {
			return new ConceptBulkLoadRequest(Collections.emptySet(), descriptionIds);
		}

		public Set<String> getConceptIds() {
			return conceptIds;
		}

		public Set<String> getDescriptionIds() {
			return descriptionIds;
		}
	}
}
