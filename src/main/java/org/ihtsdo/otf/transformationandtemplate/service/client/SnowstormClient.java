package org.ihtsdo.otf.transformationandtemplate.service.client;

import org.ihtsdo.otf.rest.client.terminologyserver.pojo.*;
import org.ihtsdo.otf.rest.exception.BusinessServiceException;
import org.ihtsdo.otf.rest.exception.ProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static org.ihtsdo.otf.transformationandtemplate.service.client.ConceptValidationResult.Severity.ERROR;

@Service
public class SnowstormClient {

	private static final String DEFAULT_MODULE_ID_METADATA_KEY = "defaultModuleId";
	private static final ParameterizedTypeReference<List<ConceptPojo>> CONCEPT_LIST_TYPE_REF = new ParameterizedTypeReference<List<ConceptPojo>>() {};
	private static final ParameterizedTypeReference<List<ConceptValidationResult>> CONCEPT_VALIDATION_RESULT_TYPE_REF = new ParameterizedTypeReference<List<ConceptValidationResult>>() {};
	private static final Comparator<ConceptValidationResult> CONCEPT_VALIDATION_RESULT_COMPARATOR = Comparator.comparing(ConceptValidationResult::getSeverity);
	private static final Comparator<DescriptionPojo> DESCRIPTION_WITHOUT_ID_COMPARATOR = Comparator.comparing(DescriptionPojo::getTerm).thenComparing(DescriptionPojo::getLang);

	private final WebClient webClient;
	private final Logger logger = LoggerFactory.getLogger(SnowstormClient.class);

	public SnowstormClient(@Value("${terminologyserver.url}") String snowstormApiUrl) {
		webClient = WebClient.builder()
				.baseUrl(snowstormApiUrl)
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.build();
	}

	public List<ChangeResult<? extends SnomedComponent>> createDescriptions(List<DescriptionPojo> descriptions,
			List<ChangeResult<DescriptionPojo>> changes, String branchPath) throws BusinessServiceException {

		if (descriptions.isEmpty()) {
			return new ArrayList<>(changes);
		}

		logger.info("Starting process to save {} descriptions.", descriptions.size());

		// Initial terminology server communication check
		try {
			getBranch("MAIN");
		} catch (WebClientException e) {
			logger.error("Failed to communicate with the terminology server.", e);
			failAllRemaining(changes, "Failed to communicate with the terminology server.");
		}

		// Get branch metadata
		Branch branch;
		try {
			branch = getBranch(branchPath);
		} catch (WebClientException e) {
			logger.info("Failed to load branch {} from the terminology server.", branchPath, e);
			return failAllRemaining(changes, format("Failed to load branch %s from the terminology server.", branchPath));
		}
		String defaultModuleId = getMetadataString(branch, DEFAULT_MODULE_ID_METADATA_KEY);

		try {
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
					.body(BodyInserters.fromObject(new ConceptIdsRequest(conceptIds)))
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
				} else {
					getChangeResult(changes, description).fail(format("Concept %s not found.", description.getConceptId()));
					// Description not joined to any concept so no will not appear in the update request.
				}
			}

			// Run batch validation
			List<ConceptValidationResult> validationResults = runValidation(branchPath, concepts);

			Map<String, Set<ConceptValidationResult>> conceptValidationResultMap = new HashMap<>();
			for (ConceptValidationResult validationResult : validationResults) {
				conceptValidationResultMap.computeIfAbsent(validationResult.getConceptId(), (c) -> new TreeSet<>(CONCEPT_VALIDATION_RESULT_COMPARATOR));
			}

			// Remove concepts with validation errors
			// All component changes for this concept will not be saved
			Set<String> conceptsWithError = validationResults.stream()
					.filter(validationResult -> validationResult.getSeverity() == ERROR)
					.map(ConceptValidationResult::getConceptId)
					.collect(Collectors.toSet());
			logger.info("{} concepts had validation errors.", conceptsWithError.size());
			for (String conceptWithError : conceptsWithError) {
				for (DescriptionPojo description : conceptIdToDescriptionMap.get(conceptWithError)) {
					getChangeResult(changes, description).fail(format("Concept validation errors: %s", toString(conceptValidationResultMap.get(conceptWithError))));
				}
				conceptMap.remove(conceptWithError);
				// Whole concept removed from map so changes will not appear in the update request.
			}

			// Remove temp description UUIDs
			descriptions.forEach(description -> {
				if (description.getDescriptionId().contains("-")) {
					description.setDescriptionId(null);
				}
			});

			if (conceptMap.isEmpty()) {
				return new ArrayList<>(changes);
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
			ConceptChangeBatchStatus status = getBatchStatus(branchPath, locationHeader, maxWaitSeconds);
			if (ConceptChangeBatchStatus.Status.FAILED == status.getStatus()) {
				return failAllRemaining(changes, "Persisting concept batch failed with message: " + status.getMessage());
			}

			// Batch load concepts again to fetch identifiers of new components
			List<ConceptPojo> updatedConcepts = webClient.post()
					.uri(uriBuilder -> uriBuilder
							.path("/browser/{branch}/concepts/bulk-load")
							.build(branchPath))
					.body(BodyInserters.fromObject(new ConceptIdsRequest(conceptMap.keySet())))
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
			return failAllRemaining(changes, "Failed to communicate with the terminology server.");
		}

		return new ArrayList<>(changes);
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

	private ChangeResult<DescriptionPojo> getChangeResult(List<ChangeResult<DescriptionPojo>> changeResults, DescriptionPojo description) {
		for (ChangeResult<DescriptionPojo> changeResult : changeResults) {
			if (DESCRIPTION_WITHOUT_ID_COMPARATOR.compare(changeResult.getComponent(), description) == 0) {
				return changeResult;
			}
		}
		return null;
	}

	private List<ChangeResult<? extends SnomedComponent>> failAllRemaining(List<ChangeResult<DescriptionPojo>> changeResults, String message) {
		changeResults.stream().filter(r -> r.getSuccess() == null).forEach(r -> {
			r.fail(message);
		});

		return new ArrayList<>(changeResults);
	}

	private ConceptChangeBatchStatus getBatchStatus(String branchPath, String locationHeader, int maxWaitSeconds) throws ProcessingException {
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

	private List<ConceptValidationResult> runValidation(String branchPath, List<ConceptPojo> concepts) {
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

	private static final class ConceptIdsRequest {

		private final Set<String> conceptIds;

		public ConceptIdsRequest(Set<String> conceptIds) {
			this.conceptIds = conceptIds;
		}

		public Set<String> getConceptIds() {
			return conceptIds;
		}
	}
}
