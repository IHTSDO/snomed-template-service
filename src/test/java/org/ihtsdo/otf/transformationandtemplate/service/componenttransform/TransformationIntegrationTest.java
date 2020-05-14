package org.ihtsdo.otf.transformationandtemplate.service.componenttransform;

import org.ihtsdo.otf.rest.client.terminologyserver.pojo.DescriptionPojo;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.SnomedComponent;
import org.ihtsdo.otf.rest.exception.BusinessServiceException;
import org.ihtsdo.otf.transformationandtemplate.domain.ComponentTransformationRequest;
import org.ihtsdo.otf.transformationandtemplate.service.client.ChangeResult;
import org.ihtsdo.otf.transformationandtemplate.service.client.SnowstormClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

import static java.lang.Boolean.FALSE;
import static org.ihtsdo.otf.rest.client.terminologyserver.pojo.DescriptionPojo.Acceptability.ACCEPTABLE;
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
	public void test() throws BusinessServiceException {
		componentTransformService.startBatchTransformation(new ComponentTransformationRequest(
				"description-create-tsv", "MAIN/KAITEST/KAITEST-100", getClass().getResourceAsStream("description-create-tsv-test.tsv")));

		@SuppressWarnings("unchecked")
		ArgumentCaptor<List<DescriptionPojo>> listArgumentCaptor = ArgumentCaptor.forClass(List.class);
		@SuppressWarnings("unchecked")
		ArgumentCaptor<List<ChangeResult<DescriptionPojo>>> changeResultsArgumentCaptor = ArgumentCaptor.forClass(List.class);
		ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
		verify(snowstormClient).createDescriptions(listArgumentCaptor.capture(), changeResultsArgumentCaptor.capture(), stringArgumentCaptor.capture());

		assertEquals("MAIN/KAITEST/KAITEST-100", stringArgumentCaptor.getValue());

		List<DescriptionPojo> descriptionsCreated = listArgumentCaptor.getValue();
		assertEquals(2, descriptionsCreated.size());

		DescriptionPojo description = descriptionsCreated.get(0);
		assertEquals("272379006", description.getConceptId());
		assertEquals("The event", description.getTerm());
		assertEquals("en", description.getLang());
		assertEquals("900000000000448009", description.getCaseSignificance().getConceptId());
		assertEquals("900000000000013009", description.getType().getConceptId());
		assertEquals("900000000000013009", description.getType().getConceptId());
		Map<String, DescriptionPojo.Acceptability> acceptabilityMap = description.getAcceptabilityMap();
		assertEquals(1, acceptabilityMap.size());
		assertTrue(acceptabilityMap.containsKey("900000000000508004"));
		assertEquals(ACCEPTABLE, acceptabilityMap.get("900000000000508004"));

		description = descriptionsCreated.get(1);
		assertEquals("242605002", description.getConceptId());
		assertEquals("A human bite", description.getTerm());
		assertEquals("en", description.getLang());
		assertEquals("900000000000448009", description.getCaseSignificance().getConceptId());
		assertEquals("900000000000013009", description.getType().getConceptId());
		assertEquals("900000000000013009", description.getType().getConceptId());
		acceptabilityMap = description.getAcceptabilityMap();
		assertEquals(1, acceptabilityMap.size());
		assertTrue(acceptabilityMap.containsKey("900000000000509007"));
		assertEquals(ACCEPTABLE, acceptabilityMap.get("900000000000509007"));

		List<ChangeResult<DescriptionPojo>> resultList = changeResultsArgumentCaptor.getValue();
		assertEquals(3, resultList.size());
		assertEquals(FALSE, resultList.get(2).getSuccess());
		assertEquals("At least one valid acceptability entry validation failed.", resultList.get(2).getMessage());
	}

}
