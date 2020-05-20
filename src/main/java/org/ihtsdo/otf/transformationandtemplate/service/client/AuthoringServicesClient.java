package org.ihtsdo.otf.transformationandtemplate.service.client;

import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import static org.ihtsdo.otf.transformationandtemplate.service.client.RestClientHelper.asMap;

public class AuthoringServicesClient {

	private final WebClient restClient;

	private AuthoringServicesClient(String apiUrl, String authenticationToken) {
		restClient = RestClientHelper.getRestClient(apiUrl, authenticationToken);
	}

	public static AuthoringServicesClient createClientForUser(String apiUrl, String authenticationToken) {
		return new AuthoringServicesClient(apiUrl, authenticationToken);
	}

	public AuthoringTask createTask(String projectKey, String title, String description) {
		// Create a new task using project key and return branch path
		return restClient.post()
				.uri(uriBuilder -> uriBuilder.path("/projects/{projectKey}/tasks").build(projectKey))
				.body(BodyInserters.fromObject(asMap("summary", title, "description", description)))
				.retrieve()
				.bodyToMono(AuthoringTask.class)
				.block();
	}

	public AuthoringTask putTaskInProgress(String projectKey, String taskKey) {
		return restClient.put()
				.uri(uriBuilder -> uriBuilder.path("/projects/{projectKey}/tasks/{taskKey}").build(projectKey, taskKey))
				.body(BodyInserters.fromObject(asMap("status", "IN_PROGRESS")))
				.retrieve()
				.bodyToMono(AuthoringTask.class)
				.block();
	}
}
