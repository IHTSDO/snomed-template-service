package org.ihtsdo.otf.authoringtemplate.service;

import java.io.InputStream;
import java.util.List;

import org.ihtsdo.otf.authoringtemplate.domain.logical.Attribute;
import org.ihtsdo.otf.authoringtemplate.domain.logical.AttributeGroup;
import org.ihtsdo.otf.authoringtemplate.domain.logical.LogicalTemplate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class LogicalLogicalTemplateParserServiceTest {

	private LogicalTemplateParserService service;

	@Before
	public void setUp() throws Exception {
		service = new LogicalTemplateParserService();
	}

	@Test
	public void testParseTemplateCtOfX() throws Exception {
		final InputStream templateStream = getClass().getResourceAsStream("/templates/ct-of-x_template.txt");
		Assert.assertNotNull("Found template stream resource", templateStream);
		final LogicalTemplate template = service.parseTemplate(templateStream);
		Assert.assertNotNull(template);

		Assert.assertEquals(1, template.getFocusConcepts().size());
		Assert.assertEquals("71388002", template.getFocusConcepts().get(0));

		Assert.assertEquals(1, template.getAttributeGroups().size());
		final AttributeGroup attributeGroup = template.getAttributeGroups().get(0);
		Assert.assertEquals("1", attributeGroup.getCardinalityMin());
		Assert.assertEquals("1", attributeGroup.getCardinalityMax());

		final List<Attribute> attributes = attributeGroup.getAttributes();
		Assert.assertEquals(2, attributes.size());

		Assert.assertEquals("260686004", attributes.get(0).getType());
		Assert.assertEquals("312251004", attributes.get(0).getValue());
		Assert.assertEquals(null, attributes.get(0).getCardinalityMin());
		Assert.assertEquals(null, attributes.get(0).getCardinalityMax());

		Assert.assertEquals("405813007", attributes.get(1).getType());
		Assert.assertEquals("<< 442083009 |Anatomical or acquired body structure|", attributes.get(1).getAllowableRangeECL());
		Assert.assertEquals("slotX", attributes.get(1).getSlotName());
		Assert.assertEquals("1", attributes.get(1).getCardinalityMin());
		Assert.assertEquals("1", attributes.get(1).getCardinalityMax());
	}

	@Test
	public void testParseTemplateGuidedCtOfX() throws Exception {
		final InputStream templateStream = getClass().getResourceAsStream("/templates/guided-ct-of-x_template.txt");
		Assert.assertNotNull("Found template stream resource", templateStream);
		final LogicalTemplate template = service.parseTemplate(templateStream);
		Assert.assertNotNull(template);

		Assert.assertEquals(1, template.getFocusConcepts().size());
		Assert.assertEquals("71388002", template.getFocusConcepts().get(0));

		Assert.assertEquals(2, template.getAttributeGroups().size());

		AttributeGroup attributeGroup = template.getAttributeGroups().get(0);
		Assert.assertEquals("1", attributeGroup.getCardinalityMin());
		Assert.assertEquals("1", attributeGroup.getCardinalityMax());

		List<Attribute> attributes = attributeGroup.getAttributes();
		Assert.assertEquals(3, attributes.size());

		Assert.assertEquals("260686004", attributes.get(0).getType());
		Assert.assertEquals("312251004", attributes.get(0).getValue());
		Assert.assertEquals(null, attributes.get(0).getCardinalityMin());
		Assert.assertEquals(null, attributes.get(0).getCardinalityMax());

		Assert.assertEquals("405813007", attributes.get(1).getType());
		Assert.assertEquals("<< 442083009 |Anatomical or acquired body structure|", attributes.get(1).getAllowableRangeECL());
		Assert.assertEquals("procSite", attributes.get(1).getSlotName());
		Assert.assertEquals("1", attributes.get(1).getCardinalityMin());
		Assert.assertEquals("1", attributes.get(1).getCardinalityMax());

		Assert.assertEquals("363703001", attributes.get(2).getType());
		Assert.assertEquals("429892002", attributes.get(2).getValue());
		Assert.assertEquals(null, attributes.get(2).getCardinalityMin());
		Assert.assertEquals(null, attributes.get(2).getCardinalityMax());

		attributeGroup = template.getAttributeGroups().get(1);
		Assert.assertEquals(null, attributeGroup.getCardinalityMin());
		Assert.assertEquals(null, attributeGroup.getCardinalityMax());

		attributes = attributeGroup.getAttributes();
		Assert.assertEquals(2, attributes.size());

		Assert.assertEquals("260686004", attributes.get(0).getType());
		Assert.assertEquals("312251004", attributes.get(0).getValue());
		Assert.assertEquals(null, attributes.get(0).getCardinalityMin());
		Assert.assertEquals(null, attributes.get(0).getCardinalityMax());

		Assert.assertEquals("405813007", attributes.get(1).getType());
		Assert.assertEquals(null, attributes.get(1).getAllowableRangeECL());
		Assert.assertEquals(null, attributes.get(1).getSlotName());
		Assert.assertEquals("procSite", attributes.get(1).getSlotReference());
		Assert.assertEquals("1", attributes.get(1).getCardinalityMin());
		Assert.assertEquals("1", attributes.get(1).getCardinalityMax());

	}
	
	@Test
	public void testParseTemplateWithMultipleFocusConcepts() throws Exception {
		final InputStream templateStream = getClass().getResourceAsStream("/templates/multiple-focus-concepts-template.txt");
		Assert.assertNotNull("Found template stream resource", templateStream);
		final LogicalTemplate template = service.parseTemplate(templateStream);
		Assert.assertNotNull(template);

		Assert.assertEquals(2, template.getFocusConcepts().size());
		Assert.assertEquals("420134006", template.getFocusConcepts().get(0));
		Assert.assertEquals("473011001", template.getFocusConcepts().get(1));
	}
}
