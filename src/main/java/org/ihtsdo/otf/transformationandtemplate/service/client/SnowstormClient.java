package org.ihtsdo.otf.transformationandtemplate.service.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.ihtsdo.otf.exception.TermServerScriptException;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.Branch;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.ConceptChangeBatchStatus;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.ConceptPojo;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.RefsetMemberPojo;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.CodeSystemVersion;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.ItemsPage;
import org.ihtsdo.otf.transformationandtemplate.domain.Concept;
import org.ihtsdo.otf.utils.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class SnowstormClient {
	
	public static final long DEFAULT_TIMEOUT = 180; //seconds
	public static final long DEFAULT_PAGESIZE = 500;

	private static final String DEFAULT_MODULE_ID_METADATA_KEY = "defaultModuleId";
	private static final String OPTIONAL_LANGUAGE_REFSET_METADATA_KEY = "optionalLanguageRefsets";
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

	public List<String> getOptionalLanguageRefsets(String branchPath) {
		Branch branch = getBranch(branchPath);
		if (branch != null && branch.getMetadata() != null && branch.getMetadata().containsKey(OPTIONAL_LANGUAGE_REFSET_METADATA_KEY)) {
			Collection<OptionalLanguageRefset> optionalLanguageRefsets = new ObjectMapper().convertValue(branch.getMetadata().get(OPTIONAL_LANGUAGE_REFSET_METADATA_KEY), new TypeReference<>(){});
			return optionalLanguageRefsets.stream().map(OptionalLanguageRefset::getRefsetId).collect(Collectors.toList());
		}
		return Collections.emptyList();
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

	/**
	 * Retrieves the latest version of a code system based on the given parameters.
	 *
	 * @param shortName           the short name of the code system
	 * @param futureVersions      indicates whether to include future versions of the code system
	 * @param internalReleases    indicates whether to include internal releases of the code system
	 *
	 * @return the latest version of the code system, or null if no versions are found.
	 */
	public CodeSystemVersion getLatestVersion(String shortName, boolean futureVersions, boolean internalReleases) {
		ItemsPage<CodeSystemVersion> versions =
			webClient.get()
				.uri(uriBuilder -> uriBuilder
						.path("/codesystems/{shortName}/versions")
						.queryParam("futureVersions", futureVersions)
						.queryParam("internalReleases", internalReleases)
						.build(shortName)
				)
				.retrieve()
				.onStatus(HttpStatusCode::isError, response -> response.bodyToMono(new ParameterizedTypeReference<ItemsPage<CodeSystemVersion>>() {})
						.flatMap(error -> Mono.error(new TermServerScriptException("Failed to retrieve versions for '" + shortName + "' : " + error)))
				)
				.bodyToMono(new ParameterizedTypeReference<ItemsPage<CodeSystemVersion>>() {})
				.block();

		if (versions == null || versions.getTotal() == 0 || versions.getItems().isEmpty()) {
			return null;
		}

		return versions.getItems().get(versions.getItems().size() - 1);
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
		int attempts = 0;
		while (true) {
			try {
				return webClient.post()
					.uri(uriBuilder -> uriBuilder
							.path("{branch}/members")
							.build(branchPath))
					.body(Mono.just(rm), RefsetMemberPojo.class)
					.retrieve()
					.onStatus(HttpStatusCode::isError, response -> response.bodyToMono(String.class)
							.flatMap(error -> Mono.error(new TermServerScriptException("Failed to create member: " + rm + " due to "+ error)))
					)
					.bodyToMono(RefsetMemberPojo.class)
					.block(Duration.of(DEFAULT_TIMEOUT, ChronoUnit.SECONDS));
			} catch (Exception e) {
				//TODO differentiate handling for errors which should be retried (eg 429)
				//vs those that won't work no matter how many times we try (eg 400)
				attempts++;
				if (attempts > 3) {
					throw new IllegalStateException("Failed to create refset member", e);
				}
				logger.warn(ExceptionUtils.getExceptionCause("Failed to create " + rm, e));
				logger.warn("Sleeping 30 seconds and trying again.");
				try {
					Thread.sleep(1000*30);
				} catch (Exception e2) {}
			}
		}
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
		.onStatus(HttpStatusCode::isError, response -> response.bodyToMono(String.class)
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
		.onStatus(HttpStatusCode::isError, response -> response.bodyToMono(String.class)
				.flatMap(error -> Mono.error(new TermServerScriptException("Failed to delete member: " + error)))
		)
		.bodyToMono(Void.class)
		.block(Duration.of(DEFAULT_TIMEOUT, ChronoUnit.SECONDS));
	}

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
		String searchAfter = null;
		return fetchConceptPage(branchPath, null, null, null, ecl, null, searchAfter)
				.expand(response -> {
					//Once we see the same searchAfter twice, we've reached the end
					//Or if we got enough in a single page
					if (response.getTotal() == response.getItems().size() ||
							response.searchAfter == searchAfter) {
						return Mono.empty();
					}
					return fetchConceptPage(branchPath, null, null, null, ecl, null, response.getSearchAfter());
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
		String searchAfter = null;
		return fetchConceptPage(branchPath, true, null, false, ecl, termFilter, searchAfter)
				.expand(response -> {
					long expected = response.getTotal();
					long totalReceived = response.getOffset() + response.getItems().size();
					if (totalReceived >= expected) {
						return Mono.empty();
					}
					return fetchConceptPage(branchPath, true, null, false, ecl, termFilter, null);
				}).flatMap(response -> Flux.fromIterable(response.getItems())).collectList()
				.block(Duration.of(DEFAULT_TIMEOUT, ChronoUnit.SECONDS));
	}

	public List<Concept> findUpdatedConcepts(String branchPath, boolean activeFilter, Boolean isPublished, String termFilter, String ecl) {
		String searchAfter = null;
		AtomicInteger totalReceived = new AtomicInteger(0);
		return fetchConceptPage(branchPath, activeFilter, true, null, ecl, termFilter, searchAfter)
				.expand(response -> {
					totalReceived.addAndGet(response.getItems().size());
					long expected = response.getTotal();
					if (totalReceived.get() >= expected) {
						return Mono.empty();
					}
					return fetchConceptPage(branchPath, activeFilter, true, isPublished, ecl, termFilter, response.getSearchAfter());
				}).flatMap(response -> Flux.fromIterable(response.getItems())).collectList()
				.block(Duration.of(DEFAULT_TIMEOUT, ChronoUnit.SECONDS));
	}

	public ConceptPage fetchConceptPageBlocking(String branchPath, Boolean isPublished, String ecl, String termFilter, String searchAfter) {
		return fetchConceptPage(branchPath, null, null, isPublished, ecl, termFilter, searchAfter).block();
	}
	
	public Mono<ConceptPage> fetchConceptPage(String branchPath, Boolean isActive, Boolean isUpdated, Boolean isPublished, String ecl, String termFilter, String searchAfter) {
		String activeStr = isActive == null ? "" : (isActive?"active ":"inactive ");
		String updatedStr = isUpdated == null ? "" : "updated ";
		String publishedStr = isPublished == null ? "" : (isPublished ? "existing " : "new ");
		String termFilterStr = termFilter == null ? "" : ("'" + termFilter + "' ");
		String eclStr = ecl == null ? "" : ("matching " + ecl + " ");
		logger.info("Requesting {}{}{}{}concepts {}from {} with searchAfter {}.", activeStr, updatedStr , publishedStr, termFilterStr, eclStr, branchPath, searchAfter);

		return webClient.get()
				.uri(uriBuilder -> uriBuilder
						.path("/{branch}/concepts")
						.queryParam("isNullEffectiveTime", isUpdated)
						.queryParam("isPublished", isPublished)
						.queryParam("activeFilter", isActive)
						.queryParam((ecl == null ? "void": "ecl"), ecl)
						.queryParam("term", termFilter)
						.queryParam("searchAfter", searchAfter)
						.queryParam("limit", DEFAULT_PAGESIZE)
						.build(branchPath))
				.retrieve()
				.onStatus(HttpStatusCode::isError, response -> response.bodyToMono(String.class)
						.flatMap(error -> Mono.error(new TermServerScriptException("Failed to recover concepts: " + error)))
				)
				.bodyToMono(ConceptPage.class);
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
				.onStatus(HttpStatusCode::isError, response -> response.bodyToMono(String.class)
						.flatMap(error -> Mono.error(new TermServerScriptException("Failed to recover concepts: " + error))))
				.bodyToMono(ConceptPage.class)
				.block(Duration.of(DEFAULT_TIMEOUT, ChronoUnit.SECONDS))
				.getItems();
	}

	public List<Concept> getParents(String branchPath, Collection<Concept> concepts) {
		if (concepts == null || concepts.isEmpty()) {
			return new ArrayList<>();
		}
		
		String ecl = ">!" + concepts.stream()
						.map(Concept::getId)
						.collect(Collectors.joining(" OR >! "));

		String searchAfter = null;
		return fetchConceptPage(branchPath, ecl, searchAfter)
				.expand(response -> {
					if (response.getTotal() == response.getItems().size() || response.getSearchAfter() == searchAfter) {
						return Mono.empty();
					}
					return fetchConceptPage(branchPath, ecl, response.getSearchAfter());
				}).flatMap(response -> Flux.fromIterable(response.getItems())).collectList()
				.block(Duration.of(DEFAULT_TIMEOUT, ChronoUnit.SECONDS));
	}

	private Mono<ConceptPage> fetchConceptPage(String branchPath, String ecl, String searchAfter) {
		ConceptSearchRequest request = new ConceptSearchRequest().withEclFilter(ecl).withSearchAfter(searchAfter);
		logger.info("Requesting concepts from " + branchPath + " with request " + request);
		return webClient.post()
				.uri(uriBuilder -> uriBuilder
						.path("{branch}/concepts/search")
						.build(branchPath))
				.bodyValue(request)
				.retrieve()
				.onStatus(HttpStatusCode::isError, response -> response.bodyToMono(String.class)
						.flatMap(error -> Mono.error(new TermServerScriptException("Failed to recover concepts: " + error))))
				.bodyToMono(ConceptPage.class);
	}

	class ConceptSearchRequest {
		String eclFilter;

		String searchAfter;

		public ConceptSearchRequest withEclFilter(String eclFilter) {
			this.eclFilter = eclFilter;
			return this;
		}

		public ConceptSearchRequest withSearchAfter(String searchAfter) {
			this.searchAfter = searchAfter;
			return this;
		}

		public String getEclFilter() {
			return eclFilter;
		}

		public void setEclFilter(String eclFilter) {
			this.eclFilter = eclFilter;
		}

		public String getSearchAfter() {
			return searchAfter;
		}

		public void setSearchAfter(String searchAfter) {
			this.searchAfter = searchAfter;
		}

		@Override
		public String toString() {
			return "[eclFilter='" + eclFilter + "', searchAfter='" + searchAfter + "']";
		}
	}

	static class OptionalLanguageRefset {
		private String refsetId;
		private String key;
		private String label;
		private String language;

		OptionalLanguageRefset() {}

		public String getRefsetId() {
			return refsetId;
		}

		public void setRefsetId(String refsetId) {
			this.refsetId = refsetId;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public String getLanguage() {
			return language;
		}

		public void setLanguage(String language) {
			this.language = language;
		}
	}

}
