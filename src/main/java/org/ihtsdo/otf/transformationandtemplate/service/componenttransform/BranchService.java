package org.ihtsdo.otf.transformationandtemplate.service.componenttransform;

import org.ihtsdo.otf.rest.client.RestClientException;
import org.ihtsdo.otf.rest.client.terminologyserver.SnowstormRestClientFactory;
import org.ihtsdo.otf.transformationandtemplate.service.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BranchService {
	private static final Logger LOGGER = LoggerFactory.getLogger(BranchService.class);

	@Autowired
	private SnowstormRestClientFactory terminologyClientFactory;

	/**
	 * Set flag on Snowstorm Branch.
	 *
	 * @param branchPath Path of Branch to update.
	 * @param key        Key of flag.
	 * @param value      Value of flag.
	 * @throws ServiceException If error occurs whilst communicating with Snowstorm.
	 */
	public void setAuthorFlag(String branchPath, String key, String value) throws ServiceException {
		if (branchPath == null || key == null || value == null) {
			throw new IllegalArgumentException("Cannot set authorFlag as arguments are invalid.");
		}

		LOGGER.debug("Setting flag {}={} on Branch {}.", key, value, branchPath);
		try {
			terminologyClientFactory.getClient().setAuthorFlag(branchPath, key, value);
		} catch (RestClientException e) {
			LOGGER.error("Failed to set flag on Branch {}. Message: {}", branchPath, e.getMessage());
			throw new ServiceException("Failed to set flag on branch " + branchPath, e);
		}
	}
}
