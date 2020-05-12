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

	public List<ChangeResult<? extends SnomedComponent>> createDescriptions(List<DescriptionPojo> descriptions, String branchPath) throws BusinessServiceException {
		List<ChangeResult<DescriptionPojo>> changeResults = descriptions.stream().map(ChangeResult::new).collect(Collectors.toList());

		// Initial terminology server communication check
		try {
			getBranch("MAIN");
		} catch (WebClientException e) {
			logger.error("Failed to communicate with the terminology server.", e);
			failAllRemaining(changeResults, "Failed to communicate with the terminology server.");
		}

		// Get branch metadata
		Branch branch;
		try {
			branch = getBranch(branchPath);
		} catch (WebClientException e) {
			logger.info("Failed to load branch {} from the terminology server.", branchPath, e);
			return failAllRemaining(changeResults, format("Failed to load branch %s from the terminology server.", branchPath));
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

			// Join new descriptions to concepts
			Map<String, ConceptPojo> conceptMap = concepts.stream().collect(Collectors.toMap(ConceptPojo::getConceptId, Function.identity()));
			for (ChangeResult<DescriptionPojo> changeResult : changeResults) {
				DescriptionPojo description = changeResult.getComponent();
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
					changeResult.fail(format("Concept %s not found.", description.getConceptId()));
					// Description not joined to any concept so no will not appear in the update request.
				}
			}

			// Run batch validation
			List<ConceptValidationResult> validationResults = runValidation(branchPath, concepts);

			Map<String, Set<ConceptValidationResult>> conceptValidationResultMap = new HashMap<>();
			for (ConceptValidationResult validationResult : validationResults) {
				conceptValidationResultMap.computeIfAbsent(validationResult.getConceptId(), (c) -> new TreeSet<>(CONCEPT_VALIDATION_RESULT_COMPARATOR));
			}
		} catch (WebClientException e) {// This RuntimeException is thrown by WebClient
			logger.error("Failed to communicate with the terminology server.", e);
			return failAllRemaining(changeResults, "Failed to communicate with the terminology server.");
		}

		return new ArrayList<>(changeResults);
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

	private String toString(Set<ConceptValidationResult> conceptValidationResults) {
		return conceptValidationResults.toString();
	}

	private List<ConceptValidationResult> runValidation(String branchPath, List<ConceptPojo> concepts) {
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
