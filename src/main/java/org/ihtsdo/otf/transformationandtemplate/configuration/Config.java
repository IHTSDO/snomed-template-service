package org.ihtsdo.otf.transformationandtemplate.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.kaicode.rest.util.branchpathrewrite.BranchPathUriRewriteFilter;
import org.ihtsdo.otf.rest.client.terminologyserver.SnowstormRestClientFactory;
import org.ihtsdo.otf.transformationandtemplate.service.JsonStore;
import org.ihtsdo.sso.integration.RequestHeaderAuthenticationDecorator;
import org.snomed.authoringtemplate.service.LogicalTemplateParserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.io.File;
import java.text.SimpleDateFormat;

import static com.google.common.base.Predicates.not;
import static springfox.documentation.builders.PathSelectors.regex;

@Configuration
@EnableSwagger2
@EnableWebSecurity
public class Config extends WebSecurityConfigurerAdapter {
	
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
	public JsonStore getTransformationRecipeJsonStore(@Value("${transformationRecipeStorePath}") String transformationRecipeStorePath) {
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
	
	@SuppressWarnings("rawtypes")
	@Bean
	public FilterRegistrationBean getSingleSignOnFilter() {
		FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean<>(
				new RequestHeaderAuthenticationDecorator());
		filterRegistrationBean.setOrder(1);
		return filterRegistrationBean;
	}

	// Swagger Config
	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.any())
				.paths(not(regex("/error")))
				.build();
	}

	// Security
	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/swagger-ui.html",
						"/swagger-resources/**",
						"/v2/api-docs",
						"/webjars/springfox-swagger-ui/**").permitAll()
				.anyRequest().authenticated()
				.and().httpBasic();
		http.csrf().disable();
		http.addFilterAfter(new RequestHeaderAuthenticationDecorator(), BasicAuthenticationFilter.class);
	}
	
	@Override
	public void configure(WebSecurity web) {
		web.httpFirewall(allowUrlEncodedSlashHttpFirewall());
	}
	
	
	@Bean
	public HttpFirewall allowUrlEncodedSlashHttpFirewall() {
		DefaultHttpFirewall firewall = new DefaultHttpFirewall();
		firewall.setAllowUrlEncodedSlash(true);
		return firewall;
	}
}
