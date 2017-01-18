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

public class SnowOwlTerminologyServerAdapter implements TerminologyServerAdapter {

	private final String terminologyServerUrl;

	@Autowired
	private RestTemplate restTemplate;

	private Logger logger = LoggerFactory.getLogger(getClass());

	public SnowOwlTerminologyServerAdapter(String terminologyServerUrl) {
		this.terminologyServerUrl = terminologyServerUrl;
	}

	@Override
	public boolean eclQueryHasAnyMatches(String branchPath, String ecl) {
		String authenticationToken = SecurityUtil.getAuthenticationToken();
		logger.debug("authenticationToken (masked) {}", obfuscate(authenticationToken));
		URI uri = UriComponentsBuilder.fromHttpUrl(terminologyServerUrl + "MAIN/concepts?offset=0&limit=1")
				.queryParam("ecl", ecl)
				.build().toUri();
		logger.debug("URI {}", uri);
		RequestEntity<Void> countRequest = RequestEntity.get(uri)
				.header("Cookie", authenticationToken)
				.build();
		ResponseEntity<EntityCountResponse> response = restTemplate.exchange(countRequest, EntityCountResponse.class);
		Long count = response.getBody().getTotal();
		return count > 0;
	}

	private String obfuscate(String authenticationToken) {
		if (authenticationToken != null) {
			int length = authenticationToken.length();
			int maskLength = length / 4;
			Strings.padEnd(authenticationToken.substring(0, length - maskLength), length, '*');
		}
		return null;
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
