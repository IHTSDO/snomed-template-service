package org.ihtsdo.otf.transformationandtemplate.service.client;

import com.amazonaws.util.StringMapBuilder;

import org.ihtsdo.otf.utils.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.unit.DataSize;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

public class RestClientHelper {

	public static WebClient getRestClient(String apiUrl, String authenticationCookie, String codecMaxInMemorySize) {
		WebClient.Builder builder = WebClient.builder()
				.baseUrl(apiUrl)
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		if (codecMaxInMemorySize != null) {
			int codecMaxInMemorySizeBytes = (int) DataSize.parse(codecMaxInMemorySize).toBytes();
			builder.exchangeStrategies(
					ExchangeStrategies.builder()
							.codecs(configurer -> configurer
									.defaultCodecs()
									.maxInMemorySize(codecMaxInMemorySizeBytes))
							.build());

		}
		if (!StringUtils.isEmpty(authenticationCookie) && authenticationCookie.contains("=")) {
			String[] split = authenticationCookie.split("=");
			builder.defaultCookie(split[0], split[1]);
		}
		return builder.build();
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
