package org.ihtsdo.otf.transformationandtemplate.service.componenttransform;

import org.ihtsdo.otf.transformationandtemplate.service.client.AuthoringServicesClient;
import org.ihtsdo.otf.transformationandtemplate.service.client.AuthoringServicesClientFactory;
import org.ihtsdo.otf.transformationandtemplate.service.client.SnowstormClient;
import org.ihtsdo.otf.transformationandtemplate.service.client.SnowstormClientFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HighLevelAuthoringServiceFactory {

	@Autowired
	private SnowstormClientFactory snowstormClientFactory;

	@Autowired
	private AuthoringServicesClientFactory authoringServicesClientFactory;

	public HighLevelAuthoringService createServiceForCurrentUser() {
		// Create clients using the current user's security context
		SnowstormClient snowstormClient = snowstormClientFactory.getClientForCurrentUser();
		AuthoringServicesClient authoringServicesClient = authoringServicesClientFactory.getClientForCurrentUser();
		return new HighLevelAuthoringService(snowstormClient, authoringServicesClient);
	}

}
