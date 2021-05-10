package org.ihtsdo.otf.transformationandtemplate.service.client;

import org.ihtsdo.otf.rest.client.terminologyserver.pojo.Branch;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.ConceptChangeBatchStatus;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.ConceptPojo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class SnowstormClient {

	private static final String DEFAULT_MODULE_ID_METADATA_KEY = "defaultModuleId";
	private static final ParameterizedTypeReference<List<ConceptPojo>> CONCEPT_LIST_TYPE_REF = new ParameterizedTypeReference<>() {};
	private static final ParameterizedTypeReference<List<ConceptValidationResult>> CONCEPT_VALIDATION_RESULT_TYPE_REF = new ParameterizedTypeReference<>() {};

	private final WebClient webClient;
	private final Logger logger = LoggerFactory.getLogger(SnowstormClient.class);

	public static SnowstormClient createClientForUser(String snowstormApiUrl, String authenticationCookie, String codecMaxInMemorySize) {
		return new SnowstormClient(snowstormApiUrl, authenticationCookie, codecMaxInMemorySize);
	}

	private SnowstormClient(String snowstormApiUrl, String authenticationCookie, String codecMaxInMemorySize) {
		webClient = RestClientHelper.getRestClient(snowstormApiUrl, authenticationCookie, codecMaxInMemorySize);
	}

	public List<ConceptPojo> getFullConcepts(ConceptBulkLoadRequest conceptBulkLoadRequest, String branchPath) {
		List<ConceptPojo> concepts = webClient.post()
				.uri(uriBuilder -> uriBuilder
						.path("/browser/{branch}/concepts/bulk-load")
						.build(branchPath))
				.body(BodyInserters.fromValue(conceptBulkLoadRequest))
				.retrieve()
				.bodyToMono(CONCEPT_LIST_TYPE_REF)
				.block();
		logger.info("Loaded {} concepts.", concepts != null ? concepts.size() : 0);
		return concepts;
	}

	public ConceptChangeBatchStatus saveUpdateConceptsNoValidation(Collection<ConceptPojo> conceptPojos, String branchPath) throws TimeoutException {
		logger.info("Saving {} concepts.", conceptPojos.size());
		ClientResponse bulkUpdateResponse = webClient.post()
				.uri(uriBuilder -> uriBuilder
						.path("/browser/{branch}/concepts/bulk")
						.build(branchPath))
				.body(BodyInserters.fromValue(conceptPojos))
				.exchange()
				.block();
		String locationHeader = bulkUpdateResponse.headers().header("Location").get(0);
		logger.info("Bulk update job url: {}", locationHeader);

		int maxWaitSeconds = conceptPojos.size() * 10_000;
		return getBatchStatus(locationHeader, maxWaitSeconds);
	}

	public String getDefaultModuleId(String branchPath) {
		String defaultModuleId;// Get branch metadata
		Branch branch = getBranch(branchPath);
		defaultModuleId = getMetadataString(branch, DEFAULT_MODULE_ID_METADATA_KEY);
		return defaultModuleId;
	}

	private String getMetadataString(Branch branch, String key) {
		return branch != null && branch.getMetadata() != null && branch.getMetadata().containsKey(key) ? (String) branch.getMetadata().get(key) : null;
	}

	public Branch getBranch(String branchPath) {
		return webClient.get()
				.uri(uriBuilder -> uriBuilder
						.path("/branches/{branch}")
						.queryParam("includeInheritedMetadata", true)
						.build(branchPath))
				.retrieve()
				.bodyToMono(Branch.class)
				.block();
	}

	public ConceptChangeBatchStatus getBatchStatus(String locationHeader, int maxWaitSeconds) throws TimeoutException {
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
		throw new TimeoutException("Batch change exceeded maximum duration.");
	}

	public List<ConceptValidationResult> runValidation(String branchPath, Collection<ConceptPojo> concepts) {
		logger.info("Validating {} concepts.", concepts.size());
		return webClient.post()
					.uri(uriBuilder -> uriBuilder
							.path("/browser/{branch}/validate/concepts")
							.build(branchPath))
					.body(BodyInserters.fromValue(concepts))
					.retrieve()
					.bodyToMono(CONCEPT_VALIDATION_RESULT_TYPE_REF)
					.block();
	}

	public void createBranch(String branchPath) {
		logger.info("Creating branch {}.", branchPath);
		int i = branchPath.lastIndexOf("/");
		String name = branchPath.substring(i + 1);
		String parent = branchPath.substring(0, i);
		webClient.post()
				.uri(uriBuilder -> uriBuilder
						.path("/branches")
						.build(branchPath))
				.body(BodyInserters.fromValue(RestClientHelper.asMap("name", name, "parent", parent)))
				.retrieve()
				.bodyToMono(Map.class)
				.block();
	}

	public static final class ConceptBulkLoadRequest {

		private final Collection<String> conceptIds;
		private final Collection<String> descriptionIds;

		private ConceptBulkLoadRequest(Collection<String> conceptIds, Collection<String> descriptionIds) {
			this.conceptIds = conceptIds;
			this.descriptionIds = descriptionIds;
		}

		public static ConceptBulkLoadRequest byConceptId(Collection<String> conceptIds) {
			return new ConceptBulkLoadRequest(conceptIds, Collections.emptySet());
		}

		public static ConceptBulkLoadRequest byDescriptionId(Collection<String> descriptionIds) {
			return new ConceptBulkLoadRequest(Collections.emptySet(), descriptionIds);
		}

		public Collection<String> getConceptIds() {
			return conceptIds;
		}

		public Collection<String> getDescriptionIds() {
			return descriptionIds;
		}
	}
}
