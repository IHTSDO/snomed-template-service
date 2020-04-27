package org.ihtsdo.otf.transformationandtemplate.service.client;

import org.ihtsdo.otf.rest.client.terminologyserver.pojo.DescriptionPojo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SnowstormClient {

	public void createDescriptions(List<DescriptionPojo> descriptions, String branchPath) {
		// TODO: Try Spring 5 WebClient if available in latest version of Spring Boot.
		// TODO: Join descriptions to concepts, validate, update.
	}
}
