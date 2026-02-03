package org.ihtsdo.otf.transformationandtemplate.service.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

import org.ihtsdo.otf.exception.TermServerScriptException;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;

public class AuthoringServicesClient {

	public static final String TASK_ENDPOINT = "/projects/{projectKey}/tasks/{taskKey}";
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
				.body(BodyInserters.fromValue(Map.of("summary", title, "description", description)))
				.retrieve()
				.onStatus(HttpStatusCode::isError, response -> response.bodyToMono(String.class)
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
				.onStatus(HttpStatusCode::isError, response -> response.bodyToMono(String.class)
						.flatMap(error -> Mono.error(new TermServerScriptException("Failed to retrieve project '" + projectKey + "' : " + error)))
				)
				.bodyToMono(AuthoringProject.class)
				.block();
	}
	
	public AuthoringTask getTask(String projectKey, String taskKey) {
		return restClient.get()
				.uri(uriBuilder -> uriBuilder.path(TASK_ENDPOINT).build(projectKey, taskKey))
				.retrieve()
				.onStatus(HttpStatusCode::isError, response -> response.bodyToMono(String.class)
						.flatMap(error -> Mono.error(new TermServerScriptException("Failed to retrieve task '" + taskKey + "' : " + error)))
				)
				.bodyToMono(AuthoringTask.class)
				.block();
	}

	public AuthoringTask updateAuthoringTaskNotNullFieldsAreSet(AuthoringTask task) {
		return restClient.put()
				.uri(uriBuilder -> uriBuilder.path(TASK_ENDPOINT).build(task.getProjectKey(), task.getKey()))
				.body(BodyInserters.fromValue(task))
				.retrieve()
				.onStatus(HttpStatusCode::isError, response -> response.bodyToMono(String.class)
						.flatMap(error -> Mono.error(new TermServerScriptException("Failed to delete member: " + error)))
				)
				.bodyToMono(AuthoringTask.class)
				.block();
	}

	public void addConceptsToSavedList(AuthoringTask task, JsonObject request) {
		// Convert Gson JsonObject to JSON string to avoid Jackson serialization issues
		Gson gson = new Gson();
		String jsonString = gson.toJson(request);
		byte[] jsonBytes = jsonString.getBytes(StandardCharsets.UTF_8);
		DataBufferFactory bufferFactory = new DefaultDataBufferFactory();
		DataBuffer buffer = bufferFactory.wrap(jsonBytes);
		
		restClient.post()
				.uri(uriBuilder -> uriBuilder.path(TASK_ENDPOINT + "/ui-state/saved-list").build(task.getProjectKey(), task.getKey()))
				.contentType(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromDataBuffers(Mono.just(buffer)))
				.retrieve()
				.onStatus(HttpStatusCode::isError, response -> response.bodyToMono(String.class)
						.flatMap(error -> Mono.error(new TermServerScriptException("Failed to add concepts to the saved list: " + error)))
				)
				.bodyToMono(Void.class)
				.block();
	}
	public DialectVariations getEnUsToEnGbSuggestions(Set<String> words) {
		return restClient.get()
				.uri(uriBuilder -> uriBuilder.path("/dialect/en-us/suggestions/en-gb").queryParam("words", String.join(",", words)).build())
				.retrieve()
				.onStatus(HttpStatusCode::isError, response -> response.bodyToMono(String.class)
						.flatMap(error -> Mono.error(new TermServerScriptException("Failed to retrieve the mapping terms and acceptable synonym variations: " + error)))
				)
				.bodyToMono(DialectVariations.class)
				.block();
	}
}
