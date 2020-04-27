package org.ihtsdo.otf.transformationandtemplate.service.componenttransform;

import org.ihtsdo.otf.rest.client.terminologyserver.pojo.DescriptionPojo;
import org.ihtsdo.otf.transformationandtemplate.domain.ComponentTransformationRequest;
import org.ihtsdo.otf.transformationandtemplate.service.client.SnowstormClient;
import org.ihtsdo.otf.transformationandtemplate.service.exception.ServiceException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class TransformationIntegrationTest {

	@Autowired
	private ComponentTransformService componentTransformService;

	@MockBean
	private SnowstormClient snowstormClient;

	@Test
	public void test() throws ServiceException {
		componentTransformService.startBatchTransformation(new ComponentTransformationRequest(
				"description-create-tsv", "MAIN/KAITEST/KAITEST-100", getClass().getResourceAsStream("description-creation-test-batch.tsv")));

		@SuppressWarnings("unchecked")
		ArgumentCaptor<List<DescriptionPojo>> listArgumentCaptor = ArgumentCaptor.forClass(List.class);
		ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
		verify(snowstormClient).createDescriptions(listArgumentCaptor.capture(), stringArgumentCaptor.capture());

		assertEquals("MAIN/KAITEST/KAITEST-100", stringArgumentCaptor.getValue());

		List<DescriptionPojo> descriptionsCreated = listArgumentCaptor.getValue();
		assertEquals(2, descriptionsCreated.size());

		DescriptionPojo description = descriptionsCreated.get(0);
		assertEquals("272379006", description.getConceptId());
		assertEquals("The event", description.getTerm());
		assertEquals("en", description.getLang());
		assertEquals("900000000000448009", description.getCaseSignificance());
		assertEquals("900000000000013009", description.getType());
		assertEquals("900000000000013009", description.getType());
		Map<String, String> acceptabilityMap = description.getAcceptabilityMap();
		assertEquals(1, acceptabilityMap.size());
		assertTrue(acceptabilityMap.containsKey("900000000000508004"));
		assertEquals("900000000000549004", acceptabilityMap.get("900000000000508004"));

		description = descriptionsCreated.get(1);
		assertEquals("242605002", description.getConceptId());
		assertEquals("A human bite", description.getTerm());
		assertEquals("en", description.getLang());
		assertEquals("900000000000448009", description.getCaseSignificance());
		assertEquals("900000000000013009", description.getType());
		assertEquals("900000000000013009", description.getType());
		acceptabilityMap = description.getAcceptabilityMap();
		assertEquals(1, acceptabilityMap.size());
		assertTrue(acceptabilityMap.containsKey("900000000000509007"));
		assertEquals("900000000000549004", acceptabilityMap.get("900000000000509007"));
	}

}