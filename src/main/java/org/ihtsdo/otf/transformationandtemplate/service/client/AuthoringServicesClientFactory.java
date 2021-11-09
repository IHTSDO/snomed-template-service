package org.ihtsdo.otf.transformationandtemplate.service.client;

import org.ihtsdo.otf.utils.StringUtils;
import org.ihtsdo.sso.integration.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AuthoringServicesClientFactory {

	private final String authoringServicesApiUrl;
	private final String codecMaxInMemorySize;
	private final Logger logger = LoggerFactory.getLogger(getClass());

	public AuthoringServicesClientFactory(@Value("${authoring-services.url}") String authoringServicesApiUrl, @Value("${spring.codec.max-in-memory-size}") String codecMaxInMemorySize) {
		this.authoringServicesApiUrl = authoringServicesApiUrl;
		this.codecMaxInMemorySize = codecMaxInMemorySize;
	}

	public AuthoringServicesClient getClientForCurrentUser() {
		String authenticationToken = SecurityUtil.getAuthenticationToken();
		if (StringUtils.isEmpty(authenticationToken)) {
			logger.warn("Authentication token is not set.");
		} else {
			logger.debug("Authentication token starts: " + authenticationToken.substring(0, 5));
		}
		return AuthoringServicesClient.createClientForUser(authoringServicesApiUrl, authenticationToken, codecMaxInMemorySize);
	}
}
