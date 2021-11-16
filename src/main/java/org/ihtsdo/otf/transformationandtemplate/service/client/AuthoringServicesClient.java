package org.ihtsdo.otf.transformationandtemplate.service.client;

import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

import static org.ihtsdo.otf.transformationandtemplate.service.client.RestClientHelper.asMap;

import org.ihtsdo.otf.exception.TermServerScriptException;

public class AuthoringServicesClient {

	private final WebClient restClient;

	private AuthoringServicesClient(String apiUrl, String authenticationToken, String codecMaxInMemorySize) {
		restClient = RestClientHelper.getRestClient(apiUrl, authenticationToken, codecMaxInMemorySize);
	}

	public static AuthoringServicesClient createClientForUser(String apiUrl, String authenticationToken, String codecMaxInMemorySize) {
		return new AuthoringServicesClient(apiUrl, authenticationToken, codecMaxInMemorySize);
	}

	public AuthoringTask createTask(String projectKey, String title, String description) {
		// Create a new task using project key and return branch path
		return restClient.post()
				.uri(uriBuilder -> uriBuilder.path("/projects/{projectKey}/tasks").build(projectKey))
				.body(BodyInserters.fromValue(asMap("summary", title, "description", description)))
				.retrieve()
				.onStatus(HttpStatus::isError, response -> response.bodyToMono(String.class) 
						.flatMap(error -> Mono.error(new TermServerScriptException("Failed to create task: " + error)))
				)
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
				.onStatus(HttpStatus::isError, response -> response.bodyToMono(String.class) 
						.flatMap(error -> Mono.error(new TermServerScriptException("Failed to delete member: " + error)))
				)
				.bodyToMono(AuthoringProject.class)
				.block();
	}

	public AuthoringTask updateAuthoringTaskNotNullFieldsAreSet(AuthoringTask task) {
		return restClient.put()
				.uri(uriBuilder -> uriBuilder.path("/projects/{projectKey}/tasks/{taskKey}").build(task.getProjectKey(), task.getKey()))
				.body(BodyInserters.fromValue(task))
				.retrieve()
				.onStatus(HttpStatus::isError, response -> response.bodyToMono(String.class) 
						.flatMap(error -> Mono.error(new TermServerScriptException("Failed to delete member: " + error)))
				)
				.bodyToMono(AuthoringTask.class)
				.block();
	}
}
