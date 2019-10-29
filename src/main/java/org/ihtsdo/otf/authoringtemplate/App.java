package org.ihtsdo.otf.authoringtemplate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class App {

	public static void main(String[] args) {
		System.setProperty("org.apache.tomcat.util.buf.UDecoder.ALLOW_ENCODED_SLASH", "true");
		System.setProperty("tomcat.util.http.parser.HttpParser.requestTargetAllow", "{}|[]()");
		SpringApplication.run(App.class, args);
	}
}
