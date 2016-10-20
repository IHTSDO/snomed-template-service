package org.ihtsdo.otf.authoringtemplate.service;

import org.ihtsdo.otf.authoringtemplate.domain.Attribute;
import org.ihtsdo.otf.authoringtemplate.domain.AttributeGroup;
import org.ihtsdo.otf.authoringtemplate.domain.Template;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;

public class TemplateParserServiceTest {

	private TemplateParserService service;

	@Before
	public void setUp() throws Exception {
		service = new TemplateParserService();
	}

	@Test
	public void testParseTemplate() throws Exception {
		final InputStream templateStream = getClass().getResourceAsStream("/templates/ct-of-x_template.txt");
		Assert.assertNotNull("Found template stream resource", templateStream);
		final Template template = service.parseTemplate(templateStream);
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
}
