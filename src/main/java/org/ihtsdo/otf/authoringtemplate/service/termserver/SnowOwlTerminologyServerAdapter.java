package org.ihtsdo.otf.authoringtemplate.service.termserver;

import com.google.common.base.Strings;
import org.ihtsdo.sso.integration.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Set;
import java.util.stream.Collectors;

public class SnowOwlTerminologyServerAdapter implements TerminologyServerAdapter {

	private final String terminologyServerUrl;

	@Autowired
	private RestTemplate restTemplate;

	private Logger logger = LoggerFactory.getLogger(getClass());

	public SnowOwlTerminologyServerAdapter(String terminologyServerUrl) {
		this.terminologyServerUrl = terminologyServerUrl;
	}

	@Override
	public Set<String> eclQuery(String branchPath, String ecl, int limit) {
		RequestEntity<Void> countRequest = createEclRequest(branchPath, ecl, limit);
		ResponseEntity<ConceptIdsResponse> response = restTemplate.exchange(countRequest, ConceptIdsResponse.class);
		return response.getBody().getConceptIds();
	}

	@Override
	public boolean eclQueryHasAnyMatches(String branchPath, String ecl) {
		RequestEntity<Void> countRequest = createEclRequest(branchPath, ecl, 1);
		ResponseEntity<EntityCountResponse> response = restTemplate.exchange(countRequest, EntityCountResponse.class);
		Long count = response.getBody().getTotal();
		return count > 0;
	}

	private RequestEntity<Void> createEclRequest(final String branchPath, String ecl, int limit) {
		String authenticationToken = SecurityUtil.getAuthenticationToken();
		logger.debug("authenticationToken (masked) {}", obfuscate(authenticationToken));
		URI uri = UriComponentsBuilder.fromHttpUrl(terminologyServerUrl + branchPath + "/concepts")
				.queryParam("ecl", ecl)
				.queryParam("active", true)
				.queryParam("offset", 0)
				.queryParam("limit", limit)
				.build().toUri();
		logger.debug("URI {}", uri);
		return RequestEntity.get(uri)
				.header("Cookie", authenticationToken)
				.build();
	}

	private String obfuscate(String authenticationToken) {
		if (authenticationToken != null) {
			int length = authenticationToken.length();
			int maskLength = length / 4;
			return Strings.padEnd(authenticationToken.substring(0, length - maskLength), length, '*');
		}
		return null;
	}

	private static final class ConceptIdsResponse {

		private ConceptIdsResponse() {
		}

		private long total;

		private Set<ConceptResponse> items;

		public Set<String> getConceptIds() {
			return items.stream().map(ConceptResponse::getId).collect(Collectors.toSet());
		}

		public long getTotal() {
			return total;
		}

		public void setTotal(long total) {
			this.total = total;
		}

		public Set<ConceptResponse> getItems() {
			return items;
		}

		public void setItems(Set<ConceptResponse> items) {
			this.items = items;
		}

		private static final class ConceptResponse {
			private String id;

			public String getId() {
				return id;
			}

			public void setId(String id) {
				this.id = id;
			}
		}

	}

	private static final class EntityCountResponse {

		private long total;

		public long getTotal() {
			return total;
		}

		public void setTotal(long total) {
			this.total = total;
		}
	}

}
