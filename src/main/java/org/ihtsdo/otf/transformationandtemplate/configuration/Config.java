package org.ihtsdo.otf.transformationandtemplate.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.kaicode.rest.util.branchpathrewrite.BranchPathUriRewriteFilter;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.ihtsdo.otf.rest.client.terminologyserver.SnowstormRestClientFactory;
import org.ihtsdo.otf.transformationandtemplate.service.JsonStore;
import org.ihtsdo.otf.transformationandtemplate.service.componenttransform.valueprovider.ValueProviderFactory;
import org.ihtsdo.sso.integration.RequestHeaderAuthenticationDecorator;
import org.snomed.authoringtemplate.service.LogicalTemplateParserService;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

@EnableWebSecurity
@Configuration
public class Config {

	@Autowired(required = false)
	private BuildProperties buildProperties;
	
	@Bean
	public ObjectMapper getGeneralMapper() {
		return Jackson2ObjectMapperBuilder
				.json()
				.serializationInclusion(JsonInclude.Include.NON_NULL)
				.dateFormat((new SimpleDateFormat("dd-MM-yyyy hh:mm:ss")))
				.build();
	}

	@Bean("templateJsonStore")
	public JsonStore getTemplateJsonStore(@Value("${templateStorePath}") String templateStorePath) {
		return new JsonStore(new File(templateStorePath), getGeneralMapper());
	}

	@Bean("transformationRecipeStore")
	public JsonStore getTransformationRecipeJsonStore(@Value("${transformationRecipeStorePath}") String transformationRecipeStorePath) throws IOException {
		ValueProviderFactory.loadConstantMap(transformationRecipeStorePath);
		return new JsonStore(new File(transformationRecipeStorePath), getGeneralMapper());
	}

	@Bean
	public SnowstormRestClientFactory snowOwlRestClientFactory(@Value("${terminologyserver.url}") String snowstormUrl,
			@Value("${terminologyserver.reasonerId}") String reasonerId) {

		return new SnowstormRestClientFactory(snowstormUrl, reasonerId);
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}
	
	@Bean
	public LogicalTemplateParserService logicalTemplateParserService() {
		return new LogicalTemplateParserService();
	}
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	@Bean
	public FilterRegistrationBean getUrlRewriteFilter() {
		// Encode branch paths in uri to allow request mapping to work
		return new FilterRegistrationBean(new BranchPathUriRewriteFilter(
				"/(.*)/templates",
				"/(.*)/templates/.*",
				"/(.*)/recipes/.*"
		));
	}

	// Swagger Config
	public OpenAPI apiInfo() {
		final String version = buildProperties != null ? buildProperties.getVersion() : "DEV";
		return new OpenAPI()
				.info(new Info().title("Snomed Template Service")
						.description("SNOMED CT Template Service REST API.")
						.version(version)
						.contact(new Contact().name("SNOMED International").url("https://www.snomed.org"))
						.license(new License().name("Apache 2.0").url("http://www.apache.org/licenses/LICENSE-2.0")))
				.externalDocs(new ExternalDocumentation().description("See more about Snomed Template Service in GitHub").url("https://github.com/IHTSDO/snomed-template-service"));
	}

	@Bean
	public GroupedOpenApi apiDocs() {
		GroupedOpenApi.Builder apiBuilder = GroupedOpenApi.builder()
				.group("snomed-template-service")
				.packagesToScan("org.ihtsdo.otf.transformationandtemplate.rest");
		// Don't show the error or root endpoints in swagger
		apiBuilder.pathsToExclude("/error", "/");
		return apiBuilder.build();
	}

	@Bean
	public GroupedOpenApi springActuatorApi() {
		return GroupedOpenApi.builder().group("actuator")
				.packagesToScan("org.springframework.boot.actuate")
				.pathsToMatch("/actuator/**")
				.build();
	}

	// Security
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		http.authorizeRequests()
				.antMatchers("/swagger-ui.html",
						"/version",
						"/swagger-ui/**",
						"/v3/api-docs/**").permitAll()
				.anyRequest().authenticated()
				.and().httpBasic();
		http.csrf().disable();
		http.addFilterBefore(new RequestHeaderAuthenticationDecorator(), FilterSecurityInterceptor.class);
		return http.build();
	}

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return (web) -> web.httpFirewall(allowUrlEncodedSlashHttpFirewall());
	}
	
	@Bean
	public HttpFirewall allowUrlEncodedSlashHttpFirewall() {
		DefaultHttpFirewall firewall = new DefaultHttpFirewall();
		firewall.setAllowUrlEncodedSlash(true);
		return firewall;
	}
}
