package org.ihtsdo.otf.transformationandtemplate.service.client;

import org.apache.commons.lang3.StringUtils;
import org.ihtsdo.otf.exception.TermServerScriptException;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.Branch;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.ConceptChangeBatchStatus;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.ConceptPojo;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.RefsetMemberPojo;
import org.ihtsdo.otf.transformationandtemplate.domain.Concept;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class SnowstormClient {
	
	public static final long DEFAULT_TIMEOUT = 60; //seconds
	public static final long DEFAULT_PAGESIZE = 500;

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
	
	public ConceptPojo getFullConcept(String branchPath, String conceptId) {
		return webClient.get()
				.uri(uriBuilder -> uriBuilder
						.path("browser/{branch}/concepts/{conceptId}")
						.build(branchPath, conceptId))
				.retrieve()
				.bodyToMono(ConceptPojo.class)
				.block(Duration.of(DEFAULT_TIMEOUT, ChronoUnit.SECONDS));
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
				.block(Duration.of(DEFAULT_TIMEOUT, ChronoUnit.SECONDS));
	}
	

	public List<Concept> getParents(String branchPath, String conceptId) {
		return Arrays.asList(webClient.get()
				.uri(uriBuilder -> uriBuilder
						.path("/browser/{branch}/concepts/{sctId}/parents")
						.queryParam("form", "inferred")
						.build(branchPath, conceptId))
				.retrieve()
				.bodyToMono(Concept[].class)
				.block(Duration.of(DEFAULT_TIMEOUT, ChronoUnit.SECONDS)));
	}
	
	public List<Concept> getChildren(String branchPath, String conceptId) {
		return Arrays.asList(webClient.get()
				.uri(uriBuilder -> uriBuilder
						.path("/browser/{branch}/concepts/{sctId}/children")
						.queryParam("form", "inferred")
						.build(branchPath, conceptId))
				.retrieve()
				.bodyToMono(Concept[].class)
				.block(Duration.of(DEFAULT_TIMEOUT, ChronoUnit.SECONDS)));
	}

	public List<Concept> getAncestors(String branchPath, String conceptId) {
		return Arrays.asList(webClient.get()
				.uri(uriBuilder -> uriBuilder
						.path("/browser/{branch}/concepts/{sctId}/ancestors")
						.queryParam("inferred", "inferred")
						.build(branchPath, conceptId))
				.retrieve()
				.bodyToMono(Concept[].class)
				.block(Duration.of(DEFAULT_TIMEOUT, ChronoUnit.SECONDS)));
	}
	

	public RefsetMemberPojo createRefsetMember(String branchPath, RefsetMemberPojo rm) {
		return webClient.post()
			.uri(uriBuilder -> uriBuilder
					.path("{branch}/members")
					.build(branchPath))
			.body(Mono.just(rm), RefsetMemberPojo.class)
			.retrieve()
			.onStatus(HttpStatus::isError, response -> response.bodyToMono(String.class) 
					.flatMap(error -> Mono.error(new TermServerScriptException("Failed to create member: " + rm + " due to "+ error)))
			)
			.bodyToMono(RefsetMemberPojo.class)
			.block(Duration.of(DEFAULT_TIMEOUT, ChronoUnit.SECONDS));
	}
	

	public RefsetMemberPojo updateRefsetMember(String branchPath, RefsetMemberPojo rm) throws TermServerScriptException {
		if (StringUtils.isEmpty(rm.getId())) {
			throw new TermServerScriptException("Request to update Refset Member without a uuid! " + rm);
		}
		return webClient.put()
		.uri(uriBuilder -> uriBuilder
				.path("{branch}/members/{uuid}")
				.build(branchPath, rm.getId()))
		.body(Mono.just(rm), RefsetMemberPojo.class)
		.retrieve()
		.onStatus(HttpStatus::isError, response -> response.bodyToMono(String.class) 
				.flatMap(error -> Mono.error(new TermServerScriptException("Failed to updated member: " + rm + " due to "+ error)))
		)
		.bodyToMono(RefsetMemberPojo.class)
		.block(Duration.of(DEFAULT_TIMEOUT, ChronoUnit.SECONDS));
	}
	
	public void deleteRefsetMember(String branchPath, RefsetMemberPojo rm) throws TermServerScriptException {
		if (StringUtils.isEmpty(rm.getId())) {
			throw new TermServerScriptException("Request to update Refset Member without a uuid! " + rm);
		}
		webClient.delete()
		.uri(uriBuilder -> uriBuilder
				.path("{branch}/members/{uuid}")
				.build(branchPath, rm.getId()))
		.retrieve()
		.onStatus(HttpStatus::isError, response -> response.bodyToMono(String.class) 
				.flatMap(error -> Mono.error(new TermServerScriptException("Failed to delete member: " + error)))
		)
		.bodyToMono(Void.class)
		.block(Duration.of(DEFAULT_TIMEOUT, ChronoUnit.SECONDS));
	}

	/*public List<RefsetMemberPojo> getRefsetMembers(String branchPath, String refsetId, List<ConceptPojo> ref) {
		MultiValueMap<String, String> queryParamMap = new LinkedMultiValueMap<>();
		queryParamMap.add("refsetId", refsetId);
		return getRefsetMembers(branchPath, queryParamMap);
	}*/

	public List<RefsetMemberPojo> getRefsetMembers(String branchPath, MultiValueMap<String, String> queryParamMap, boolean isPOST) {
		long currentOffset = 0;
		return fetchRefsetMemberPage(branchPath, queryParamMap, currentOffset)
				.expand(response -> {
					long expected = response.getTotal();
					long totalReceived = response.getOffset() + response.getItems().size();
					if (totalReceived >= expected) {
						return Mono.empty();
					}
					return fetchRefsetMemberPage(branchPath, queryParamMap, totalReceived);
				}).flatMap(response -> Flux.fromIterable(response.getItems())).collectList()
				.block(Duration.of(DEFAULT_TIMEOUT, ChronoUnit.SECONDS));
	}
	
	public List<RefsetMemberPojo> getRefsetMembers(String branchPath, MemberSearchRequest memberSearchRequest, boolean isPOST) {
		long currentOffset = 0;
		return fetchRefsetMemberPage(branchPath, memberSearchRequest, currentOffset)
				.expand(response -> {
					long expected = response.getTotal();
					long totalReceived = response.getOffset() + response.getItems().size();
					if (totalReceived >= expected) {
						return Mono.empty();
					}
					return fetchRefsetMemberPage(branchPath, memberSearchRequest, totalReceived);
				}).flatMap(response -> Flux.fromIterable(response.getItems())).collectList()
				.block(Duration.of(DEFAULT_TIMEOUT, ChronoUnit.SECONDS));
	}

	private Mono<RefsetMemberPage> fetchRefsetMemberPage(String branchPath, MultiValueMap<String, String> queryParams, long currentOffset) {
		logger.info("Requesting members from " + branchPath + " with parameters " + queryParams + " and offset " + currentOffset);
		return webClient.get()
				.uri(uriBuilder -> uriBuilder
						.path("{branch}/members")
						.queryParams(queryParams)
						.queryParam("offset", currentOffset)
						.queryParam("limit", DEFAULT_PAGESIZE)
						.build(branchPath))
				.retrieve()
				.bodyToMono(RefsetMemberPage.class);
	}
	
	private Mono<RefsetMemberPage> fetchRefsetMemberPage(String branchPath, MemberSearchRequest memberSearchRequest, long currentOffset) {
		logger.info("Requesting members from " + branchPath + " with memberSearchRequest " + memberSearchRequest + " and offset " + currentOffset);
		return webClient.post()
				.uri(uriBuilder -> uriBuilder
						.path("{branch}/members/search")
						.queryParam("offset", currentOffset)
						.queryParam("limit", DEFAULT_PAGESIZE)
						.build(branchPath))
				.bodyValue(memberSearchRequest)
				.retrieve()
				.bodyToMono(RefsetMemberPage.class);
	}

	public List<RefsetMemberPojo> findRefsetMemberByReferencedComponentId(String branchPath, String refsetId, String referencedComponentId, Boolean activeFlag) {
		MultiValueMap<String, String> queryParamMap = new LinkedMultiValueMap<>();
		queryParamMap.add("referenceSet", refsetId);
		queryParamMap.add("referencedComponentId", referencedComponentId);
		if (activeFlag != null) {
			queryParamMap.add("active", activeFlag.toString());
		}
		return getRefsetMembers(branchPath, queryParamMap, false);
	}

	public List<RefsetMemberPojo> findRefsetMemberByTargetComponentId(String branchPath, String refsetId, String targetComponentId, Boolean active) {
		MultiValueMap<String, String> queryParamMap = new LinkedMultiValueMap<>();
		queryParamMap.add("referenceSet", refsetId);
		queryParamMap.add("targetComponent", targetComponentId);
		if (active != null) {
			queryParamMap.add("active", active?"true":"false");
		}
		return getRefsetMembers(branchPath, queryParamMap, false);
	}
	
	public List<RefsetMemberPojo> findRefsetMemberByTargetComponentIds(String branchPath, String refsetId, Set<String> targetComponentIds, Boolean active) {
		MemberSearchRequest memberSearchRequest = new MemberSearchRequest()
				.withRefsetId(refsetId)
				.withActiveFlag(active)
				.withAdditionalFieldSet("targetComponentId", targetComponentIds);
		return getRefsetMembers(branchPath, memberSearchRequest, true);
	}
	
	public List<RefsetMemberPojo> findRefsetMemberByReferencedComponentIds(String branchPath, String refsetId, Set<String> referencedComponentIds, Boolean active) {
		MemberSearchRequest memberSearchRequest = new MemberSearchRequest()
				.withRefsetId(refsetId)
				.withActiveFlag(active)
				.withReferencedComponentIds("referencedComponentId", referencedComponentIds);
		return getRefsetMembers(branchPath, memberSearchRequest, true);
	}

	public List<Concept> conceptsByECL(String branchPath, String ecl) {
		return fetchNewConceptPage(branchPath, null, ecl, null, 0)
				.expand(response -> {
					long expected = response.getTotal();
					long totalReceived = response.getOffset() + response.getItems().size();
					if (totalReceived >= expected) {
						return Mono.empty();
					}
					return fetchNewConceptPage(branchPath, null, ecl, null, 0);
				}).flatMap(response -> Flux.fromIterable(response.getItems())).collectList()
				.block(Duration.of(DEFAULT_TIMEOUT, ChronoUnit.SECONDS));
	}

	public static final class RefsetMemberPage {
		List<RefsetMemberPojo> items;
		Long total;
		Long limit;
		Long offset;
		
		public List<RefsetMemberPojo> getItems() {
			return items;
		}
		public void setItems(List<RefsetMemberPojo> items) {
			this.items = items;
		}
		public Long getTotal() {
			return total;
		}
		public void setTotal(Long total) {
			this.total = total;
		}
		public Long getLimit() {
			return limit;
		}
		public void setLimit(Long limit) {
			this.limit = limit;
		}
		public Long getOffset() {
			return offset;
		}
		public void setOffset(Long offset) {
			this.offset = offset;
		}
	}
	
	public List<Concept> findNewConcepts(String branchPath, String ecl, String termFilter) {
		long currentOffset = 0;
		return fetchNewConceptPage(branchPath, false, ecl, termFilter, currentOffset)
				.expand(response -> {
					long expected = response.getTotal();
					long totalReceived = response.getOffset() + response.getItems().size();
					if (totalReceived >= expected) {
						return Mono.empty();
					}
					return fetchNewConceptPage(branchPath, false, ecl, termFilter, totalReceived);
				}).flatMap(response -> Flux.fromIterable(response.getItems())).collectList()
				.block(Duration.of(DEFAULT_TIMEOUT, ChronoUnit.SECONDS));
	}

	private Mono<ConceptPage> fetchNewConceptPage(String branchPath, Boolean isPublished, String ecl, String termFilter, long currentOffset) {
		if (termFilter == null) {
			logger.info("Requesting new concepts from {} with offset {}.", branchPath, currentOffset);
		} else {
			logger.info("Requesting new {} concepts from {} with offset {}.", termFilter, branchPath, currentOffset);
		}

		if (isPublished == null) {
			return webClient.get()
					.uri(uriBuilder -> uriBuilder
							.path("/{branch}/concepts")
							.queryParam("isPublished", isPublished)
							.queryParam("activeFilter", true)
							.queryParam("ecl", ecl)
							.queryParam("term", termFilter)
							.queryParam("offset", currentOffset)
							.queryParam("limit", DEFAULT_PAGESIZE)
							.build(branchPath))
					.retrieve()
					.onStatus(HttpStatus::isError, response -> response.bodyToMono(String.class)
							.flatMap(error -> Mono.error(new TermServerScriptException("Failed to delete member: " + error)))
					)
					.bodyToMono(ConceptPage.class);
		}
		
		return webClient.get()
				.uri(uriBuilder -> uriBuilder
					.path("/{branch}/concepts")
					.queryParam("isPublished", isPublished)
					.queryParam("activeFilter", true)
					.queryParam("ecl", ecl)
					.queryParam("term", termFilter)
					.queryParam("offset", currentOffset)
					.queryParam("limit", DEFAULT_PAGESIZE)
					.build(branchPath))
				.retrieve()
				.onStatus(HttpStatus::isError, response -> response.bodyToMono(String.class) 
						.flatMap(error -> Mono.error(new TermServerScriptException("Failed to delete member: " + error)))
				)
				.bodyToMono(ConceptPage.class);
	}
	
	public List<Concept> findUpdatedConcepts(String branchPath, boolean activeFilter, String termFilter, String ecl) {
		long currentOffset = 0;
		return fetchUpdatedConceptPage(branchPath, activeFilter, termFilter, ecl, currentOffset)
				.expand(response -> {
					long expected = response.getTotal();
					long totalReceived = response.getOffset() + response.getItems().size();
					if (totalReceived >= expected) {
						return Mono.empty();
					}
					return fetchUpdatedConceptPage(branchPath, activeFilter, termFilter, ecl, totalReceived);
				}).flatMap(response -> Flux.fromIterable(response.getItems())).collectList()
				.block(Duration.of(DEFAULT_TIMEOUT, ChronoUnit.SECONDS));
	}

	private Mono<ConceptPage> fetchUpdatedConceptPage(String branchPath, boolean activeFilter, String termFilter, String ecl, long currentOffset) {
		if (termFilter == null) {
			logger.info("Requesting {} concepts from {} with offset {}.", activeFilter ? "active" : "inactive", branchPath, currentOffset);
		} else {
			logger.info("Requesting {} '{}' concepts from {} with offset {}.", activeFilter ? "active " : "inactive ", termFilter, branchPath, currentOffset);
		}

		if (ecl == null) {
			return webClient.get()
					.uri(uriBuilder -> uriBuilder
							.path("/{branch}/concepts")
							.queryParam("isNullEffectiveTime", true)
							.queryParam("activeFilter", activeFilter)
							.queryParam("term", termFilter)
							.queryParam("offset", currentOffset)
							.queryParam("limit", DEFAULT_PAGESIZE)
							.build(branchPath))
					.retrieve()
					.onStatus(HttpStatus::isError, response -> response.bodyToMono(String.class)
							.flatMap(error -> Mono.error(new TermServerScriptException("Failed to delete member: " + error)))
					)
					.bodyToMono(ConceptPage.class);
		}

		return webClient.get()
				.uri(uriBuilder -> uriBuilder
					.path("/{branch}/concepts")
					.queryParam("isNullEffectiveTime", true)
					.queryParam("activeFilter", activeFilter)
					.queryParam("term", termFilter)
					.queryParam("ecl", ecl)
					.queryParam("offset", currentOffset)
					.queryParam("limit", DEFAULT_PAGESIZE)
					.build(branchPath))
				.retrieve()
				.onStatus(HttpStatus::isError, response -> response.bodyToMono(String.class) 
						.flatMap(error -> Mono.error(new TermServerScriptException("Failed to delete member: " + error)))
				)
				.bodyToMono(ConceptPage.class);
	}
	
	public ConceptPage fetchConceptPage(String branchPath, Boolean isPublished, String ecl, String termFilter, String searchAfter) {
		if (termFilter == null) {
			logger.info("Requesting concepts matching {} from {} with searchAfter {}.", ecl, branchPath, searchAfter);
		} else {
			logger.info("Requesting '{}' concepts matching {} from {} with searchAfter {}.", termFilter, ecl, branchPath, searchAfter);
		}

		return webClient.get()
				.uri(uriBuilder -> uriBuilder
						.path("/{branch}/concepts")
						.queryParam("isPublished", isPublished)
						.queryParam("activeFilter", true)
						.queryParam("ecl", ecl)
						.queryParam("term", termFilter)
						.queryParam("searchAfter", searchAfter)
						.queryParam("limit", DEFAULT_PAGESIZE)
						.build(branchPath))
				.retrieve()
				.onStatus(HttpStatus::isError, response -> response.bodyToMono(String.class)
						.flatMap(error -> Mono.error(new TermServerScriptException("Failed to delete member: " + error)))
				)
				.bodyToMono(ConceptPage.class).block();
	}
	
	//I tried doing this with generics, but couldn't then say DataPage<ConceptPojo>.class
	public static final class ConceptPage {
		List<Concept> items;
		Long total;
		Long limit;
		Long offset;
		String searchAfter;
		
		public List<Concept> getItems() {
			return items;
		}
		public void setItems(List<Concept> items) {
			this.items = items;
		}
		public Long getTotal() {
			return total;
		}
		public void setTotal(Long total) {
			this.total = total;
		}
		public Long getLimit() {
			return limit;
		}
		public void setLimit(Long limit) {
			this.limit = limit;
		}
		public Long getOffset() {
			return offset;
		}
		public void setOffset(Long offset) {
			this.offset = offset;
		}
		public String getSearchAfter() {
			return searchAfter;
		}
		public void setSearchAfter(String searchAfter) {
			this.searchAfter = searchAfter;
		}
	}
	
	public static final class MemberSearchRequest {
		Boolean active;
		String referenceSet;
		Map<String, Set<String>> additionalFieldSets;
		Set<String> referencedComponentIds;
		
		public Boolean getActive() {
			return active;
		}
		public void setActive(Boolean active) {
			this.active = active;
		}
		public MemberSearchRequest withActiveFlag(Boolean active) {
			this.active = active;
			return this;
		}
		public String getReferenceSet() {
			return referenceSet;
		}
		public void setReferenceSet(String referenceSet) {
			this.referenceSet = referenceSet;
		}
		public Map<String, Set<String>> getAdditionalFieldSets() {
			if (additionalFieldSets == null) {
				additionalFieldSets = new HashMap<>();
			}
			return additionalFieldSets;
		}
		public void setAdditionalFieldSets(Map<String, Set<String>> additionalFieldSets) {
			this.additionalFieldSets = additionalFieldSets;
		}
		
		public void addAdditionalFieldSet(String key, Set<String> values) {
			if (additionalFieldSets == null) {
				additionalFieldSets = new HashMap<>();
			}
			additionalFieldSets.put(key, values);
		}
		public MemberSearchRequest withAdditionalFieldSet(String key, Set<String> values) {
			addAdditionalFieldSet(key, values);
			return this;
		}
		public MemberSearchRequest withRefsetId(String refsetId) {
			referenceSet = refsetId;
			return this;
		}
		public Set<String> getReferencedComponentIds() {
			return referencedComponentIds;
		}
		public void setReferencedComponentIds(Set<String> referencedComponentIds) {
			this.referencedComponentIds = referencedComponentIds;
		}
		public MemberSearchRequest withReferencedComponentIds(String string, Set<String> referencedComponentIds) {
			this.referencedComponentIds = referencedComponentIds;
			return this;
		}
		public String toString() {
			return "[ referenceSet = " + referenceSet + ", "
					+ (referencedComponentIds == null ? "" : ( "referencedComponentIds = " + String.join(", ", referencedComponentIds) + (getAdditionalFieldSets().isEmpty()?"":", "))) 
					+ (getAdditionalFieldSets().isEmpty() ? "" : ( additionalFieldSets.entrySet().stream()
					.map(entry -> entry.getKey() + ": " + entry.getValue().size() + " items")
					.collect(Collectors.joining(", ")))) 
					+ "]";
		}
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

	public void setAuthorFlag(String branchPath, String key, String value) {
		logger.info("Setting author flag {} = {} on Branch {}.", key, value, branchPath);

		webClient.post()
				.uri(uriBuilder -> uriBuilder
						.path("/branches/{branch}/actions/set-author-flag")
						.build(branchPath))
				.body(BodyInserters.fromValue(Map.of("name", key, "value", value)))
				.retrieve()
				.bodyToMono(String.class)
				.block();
	}

	public List<Concept> getConcepts(String branchPath, List<String> sctIds) {
		return webClient.get()
				.uri(uriBuilder -> uriBuilder
					.path("/{branch}/concepts")
					.queryParam("conceptIds", sctIds)
					.build(branchPath))
				.retrieve()
				.onStatus(HttpStatus::isError, response -> response.bodyToMono(String.class) 
						.flatMap(error -> Mono.error(new TermServerScriptException("Failed to recover concepts: " + error))))
				.bodyToMono(ConceptPage.class)
				.block(Duration.of(DEFAULT_TIMEOUT, ChronoUnit.SECONDS))
				.getItems();
	}

	public List<Concept> getParents(String branchPath, Collection<Concept> concepts) {
		if (concepts == null || concepts.size() == 0) {
			return new ArrayList<>();
		}
		
		String ecl = ">!" + concepts.stream()
						.map(c -> c.getId())
						.collect(Collectors.joining(" OR >! "));
		
		ConceptSearchRequest request = new ConceptSearchRequest().withEclFilter(ecl);
		long currentOffset = 0;
		return fetchConceptPage(branchPath, request, currentOffset)
				.expand(response -> {
					long expected = response.getTotal();
					long totalReceived = response.getOffset() + response.getItems().size();
					if (totalReceived >= expected) {
						return Mono.empty();
					}
					return fetchConceptPage(branchPath, request, totalReceived);
				}).flatMap(response -> Flux.fromIterable(response.getItems())).collectList()
				.block(Duration.of(DEFAULT_TIMEOUT, ChronoUnit.SECONDS));
	}
	
	private Mono<ConceptPage> fetchConceptPage(String branchPath, ConceptSearchRequest request,
			long currentOffset) {
			logger.info("Requesting concepts from " + branchPath + " with ConceptSearchRequest " + request + " and offset " + currentOffset);
			return webClient.post()
					.uri(uriBuilder -> uriBuilder
							.path("{branch}/concepts/search")
							.queryParam("offset", currentOffset)
							.queryParam("limit", DEFAULT_PAGESIZE)
							.build(branchPath))
					.bodyValue(request)
					.retrieve()
					.onStatus(HttpStatus::isError, response -> response.bodyToMono(String.class) 
							.flatMap(error -> Mono.error(new TermServerScriptException("Failed to recover concepts: " + error))))
					.bodyToMono(ConceptPage.class);
		}

	class ConceptSearchRequest {
		String eclFilter;

		public String getEclFilter() {
			return eclFilter;
		}

		public void setEclFilter(String eclFilter) {
			this.eclFilter = eclFilter;
		}
		
		public ConceptSearchRequest withEclFilter(String eclFilter) {
			this.eclFilter = eclFilter;
			return this;
		}
		
		@Override
		public String toString() {
			return "[ eclFilter='" + eclFilter + "']";
		}
	}

}
