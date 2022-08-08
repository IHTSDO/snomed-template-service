package org.ihtsdo.otf.transformationandtemplate.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "Version", description = "Build Version")
public class VersionController {

	@Autowired
	BuildProperties buildProperties;

	@Value("${spring.security.user.name}")
	private String username;

	@Value("${spring.security.user.password}")
	private String password;

	private final static Logger LOGGER = LoggerFactory.getLogger(VersionController.class);

	@ApiOperation("Software build version and timestamp.")
	@RequestMapping(value = "/version", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public BuildVersion getBuildInformation() {
		LOGGER.info("Configured 'spring.security.user.name': {}", username);
		LOGGER.info("Configured 'spring.security.user.password': {}", password);
		return new BuildVersion(buildProperties.getVersion(), buildProperties.getTime().toString());
	}

	public static final class BuildVersion {

		private String version;
		private String time;

		BuildVersion(String version, String time) {
			this.version = version;
			this.time = time;
		}

		public String getVersion() {
			return version;
		}

		public String getTime() {
			return time;
		}
	}

}
