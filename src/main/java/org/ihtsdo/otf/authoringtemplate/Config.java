package org.ihtsdo.otf.authoringtemplate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ihtsdo.otf.authoringtemplate.service.JsonStore;
import org.ihtsdo.sso.integration.RequestHeaderAuthenticationDecorator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.io.File;

import static com.google.common.base.Predicates.not;
import static springfox.documentation.builders.PathSelectors.regex;

@SpringBootApplication
public class Config {

	@Bean
	public ObjectMapper getGeneralMapper() {
		return Jackson2ObjectMapperBuilder
				.json()
				.serializationInclusion(JsonInclude.Include.NON_NULL)
				.build();
	}

	@Bean
	public JsonStore getTemplateJsonStore(@Value("${templateStorePath}") String templateStorePath) {
		return new JsonStore(new File(templateStorePath), getGeneralMapper());
	}

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.any())
				.paths(not(regex("/error")))
				.build();
	}

	// Security
	@Configuration
	@EnableWebSecurity
	@Order(1)
	public static class ApiWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {
		protected void configure(HttpSecurity http) throws Exception {
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

	}

}