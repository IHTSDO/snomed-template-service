package org.ihtsdo.otf.transformationandtemplate.service.client;

import org.ihtsdo.sso.integration.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static org.apache.commons.lang.StringUtils.isEmpty;

@Service
public class SnowstormClientFactory {

	private final String snowstormApiUrl;
	private final String codecMaxInMemorySize;
	private final Logger logger = LoggerFactory.getLogger(getClass());

	public SnowstormClientFactory(@Value("${terminologyserver.url}") String snowstormApiUrl, @Value("${spring.codec.max-in-memory-size}") String codecMaxInMemorySize) {
		this.snowstormApiUrl = snowstormApiUrl;
		this.codecMaxInMemorySize = codecMaxInMemorySize;
	}

	public SnowstormClient getClientForCurrentUser() {
		String authenticationToken = SecurityUtil.getAuthenticationToken();
		if (isEmpty(authenticationToken)) {
			logger.warn("Authentication token is not set.");
		}
		return SnowstormClient.createClientForUser(snowstormApiUrl, authenticationToken, codecMaxInMemorySize);
	}
}
