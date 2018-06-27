package org.ihtsdo.otf.authoringtemplate.transform;

import java.util.Map;

import org.ihtsdo.otf.authoringtemplate.domain.ConceptOutline;
import org.ihtsdo.otf.authoringtemplate.service.exception.ServiceException;
import org.ihtsdo.otf.rest.client.snowowl.pojo.ConceptMiniPojo;
import org.ihtsdo.otf.rest.client.snowowl.pojo.ConceptPojo;
import org.ihtsdo.otf.rest.client.snowowl.pojo.SimpleConceptPojo;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
public class RelationshipTransformerTest {
	
private RelationshipTransformer transformer;
	
	@Before
	public void setUp() {
		
	}
	
	@Test
	@Ignore
	public void testTransform() throws ServiceException {
		//TODO
		ConceptPojo conceptToTransform = null;
		ConceptOutline conceptOutline = null;
		Map<String, ConceptMiniPojo> attributeSlotMap = null;
		Map<String, SimpleConceptPojo> conceptIdMap = null;
		transformer = new RelationshipTransformer(conceptToTransform, conceptOutline, attributeSlotMap, conceptIdMap);
		transformer.transform();
	}

}
