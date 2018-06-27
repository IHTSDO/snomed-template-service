package org.ihtsdo.otf.authoringtemplate;

import org.ihtsdo.otf.dao.resources.ResourceConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("transformation.job.storage")
public class TransformationResourceConfiguration extends ResourceConfiguration{

}
