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
		AuthoringTask task = new AuthoringTask(taskKey, projectKey)
				.setStatus("IN_PROGRESS");
		return updateAuthoringTaskNotNullFieldsAreSet(task);
	}

	public AuthoringProject retrieveProject(String projectKey) {
		return restClient.get()
				.uri(uriBuilder -> uriBuilder.path("/projects/{projectKey}").build(projectKey))
				.retrieve()
				.bodyToMono(AuthoringProject.class)
				.block();
	}

	public AuthoringTask updateAuthoringTaskNotNullFieldsAreSet(AuthoringTask task) {
		return restClient.put()
				.uri(uriBuilder -> uriBuilder.path("/projects/{projectKey}/tasks/{taskKey}").build(task.getProjectKey(), task.getKey()))
				.body(BodyInserters.fromObject(task))
				.retrieve()
				.bodyToMono(AuthoringTask.class)
				.block();
	}
}
