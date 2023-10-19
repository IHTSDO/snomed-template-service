package org.ihtsdo.otf.transformationandtemplate.service.client;


import reactor.netty.http.client.HttpClient;

import org.ihtsdo.otf.utils.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.util.unit.DataSize;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

public class RestClientHelper {

	public static WebClient getRestClient(String apiUrl, String authenticationCookie, String codecMaxInMemorySize) {
		WebClient.Builder builder = WebClient.builder()
				.clientConnector(new ReactorClientHttpConnector(HttpClient.create().followRedirect(true)))
				.baseUrl(apiUrl)
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
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
}
