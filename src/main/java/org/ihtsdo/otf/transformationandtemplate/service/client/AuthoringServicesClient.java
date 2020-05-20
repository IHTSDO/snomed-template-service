package org.ihtsdo.otf.transformationandtemplate.service.client;

import org.springframework.web.reactive.function.client.WebClient;

public class AuthoringServicesClient {

	private final WebClient restClient;

	private AuthoringServicesClient(String apiUrl, String authenticationToken) {
		restClient = RestClientHelper.getRestClient(apiUrl, authenticationToken);
	}

	public static AuthoringServicesClient createClientForUser(String apiUrl, String authenticationToken) {
		return new AuthoringServicesClient(apiUrl, authenticationToken);
	}

	public String getNextBranch(String path) {
		// Create a new task using project key and return branch path

		return null;
	}
}
