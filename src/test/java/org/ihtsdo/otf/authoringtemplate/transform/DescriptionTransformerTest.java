package org.ihtsdo.otf.authoringtemplate.transform;

import java.util.Map;

import org.snomed.authoringtemplate.domain.*;
import org.ihtsdo.otf.rest.client.snowowl.pojo.ConceptPojo;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
public class DescriptionTransformerTest {

	private DescriptionTransformer transformer;
	
	@Before
	public void setUp() {
		
	}
	
	@Test
	@Ignore
	public void testTransform() {
		//TODO
		ConceptPojo conceptToTransform = null;
		ConceptOutline conceptOutline = null;
		Map<String, String> slotValueMap = null;
		String inactivationReason = "Out_Of_Dated";
		transformer = new DescriptionTransformer(conceptToTransform, conceptOutline, slotValueMap, inactivationReason);
		transformer.transform();
	}
}
