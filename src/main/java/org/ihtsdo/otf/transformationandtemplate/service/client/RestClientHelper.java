package org.ihtsdo.otf.transformationandtemplate.service.client;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import static org.apache.commons.lang.StringUtils.isEmpty;

public class RestClientHelper {

	public static WebClient getRestClient(String apiUrl, String authenticationCookie) {
		WebClient webClient;
		WebClient.Builder builder = WebClient.builder()
				.baseUrl(apiUrl)
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		if (!isEmpty(authenticationCookie) && authenticationCookie.contains("=")) {
			String[] split = authenticationCookie.split("=");
			builder.defaultCookie(split[0], split[1]);
		}
		webClient = builder.build();
		return webClient;
	}

}
