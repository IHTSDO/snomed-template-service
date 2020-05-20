package org.ihtsdo.otf.transformationandtemplate.service.client;

import com.amazonaws.util.StringMapBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

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

	public static Map<String, String> asMap(String... keyThenValueThenRepeat) {
		StringMapBuilder stringMapBuilder = new StringMapBuilder();
		String key = null;
		for (String s : keyThenValueThenRepeat) {
			if (key == null) {
				key = s;
			} else {
				stringMapBuilder.put(key, s);
				key = null;
			}
		}
		return stringMapBuilder.build();
	}
}
