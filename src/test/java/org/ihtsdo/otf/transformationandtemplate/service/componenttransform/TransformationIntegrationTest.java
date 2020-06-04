package org.ihtsdo.otf.transformationandtemplate.service.componenttransform;

import org.ihtsdo.otf.rest.client.terminologyserver.pojo.Branch;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.ConceptChangeBatchStatus;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.ConceptPojo;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.DescriptionPojo;
import org.ihtsdo.otf.rest.exception.BusinessServiceException;
import org.ihtsdo.otf.transformationandtemplate.domain.ComponentTransformationJob;
import org.ihtsdo.otf.transformationandtemplate.domain.ComponentTransformationRequest;
import org.ihtsdo.otf.transformationandtemplate.service.client.ChangeResult;
import org.ihtsdo.otf.transformationandtemplate.service.client.SnowstormClient;
import org.ihtsdo.otf.transformationandtemplate.service.client.SnowstormClientFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;
import java.util.concurrent.TimeoutException;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.ihtsdo.otf.rest.client.terminologyserver.pojo.DescriptionPojo.Acceptability.ACCEPTABLE;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class TransformationIntegrationTest {

	@Autowired
	private ComponentTransformService componentTransformService;

	@MockBean
	private SnowstormClientFactory snowstormClientFactory;

	@MockBean
	private SnowstormClient snowstormClientMock;

	@Before
	public void before() {
		Mockito.when(snowstormClientFactory.getClientForCurrentUser()).thenReturn(snowstormClientMock);
	}

	@Test
	public void test() throws BusinessServiceException, InterruptedException, TimeoutException {
		String branchPath = "MAIN/KAITEST/KAITEST-100";

		Mockito.when(snowstormClientMock.getBranch(any())).thenReturn(new Branch());
		Mockito.when(snowstormClientMock.getFullConcepts(any(), any())).thenReturn(Arrays.asList(
				new ConceptPojo("272379006").add(new DescriptionPojo("Event (event)").setDescriptionId("123")),
				new ConceptPojo("242605002").add(new DescriptionPojo("Bite (event)").setDescriptionId("456"))
		));
		Mockito.when(snowstormClientMock.runValidation(any(), any())).thenReturn(new ArrayList<>());
		Mockito.when(snowstormClientMock.saveUpdateConceptsNoValidation(any(), any())).thenReturn(new ConceptChangeBatchStatus(ConceptChangeBatchStatus.Status.COMPLETED));

		ComponentTransformationJob job = componentTransformService.queueBatchTransformation(new ComponentTransformationRequest(
				"description-translate-tsv", branchPath, null, null, null, null, 100, getClass().getResourceAsStream("description-create-tsv-test.tsv")));

		int maxWait = 10;// seconds
		int wait = 0;
		while (!job.getStatus().getStatus().isEndState() && wait++ < maxWait) {
			Thread.sleep(1_000);
			job = componentTransformService.loadTransformationJob(branchPath, job.getId());
		}

		@SuppressWarnings("unchecked")
		ArgumentCaptor<Collection<ConceptPojo>> conceptsSavedCaptor = ArgumentCaptor.forClass(Collection.class);
		ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);

		Mockito.verify(snowstormClientMock).saveUpdateConceptsNoValidation(conceptsSavedCaptor.capture(), stringArgumentCaptor.capture());

		assertEquals(branchPath, stringArgumentCaptor.getValue());

		DescriptionPojo descriptionEvent = null;
		DescriptionPojo descriptionBite = null;
		for (ConceptPojo conceptPojo : conceptsSavedCaptor.getValue()) {
			for (DescriptionPojo description : conceptPojo.getDescriptions()) {
				if ("The event".equals(description.getTerm())) {
					descriptionEvent = description;
				}
				if ("A human bite".equals(description.getTerm())) {
					descriptionBite = description;
				}
			}
		}
		assertNotNull(descriptionEvent);
		assertNotNull(descriptionBite);

		assertEquals("272379006", descriptionEvent.getConceptId());
		assertEquals("The event", descriptionEvent.getTerm());
		assertEquals("en", descriptionEvent.getLang());
		assertEquals("900000000000448009", descriptionEvent.getCaseSignificance().getConceptId());
		assertEquals("900000000000013009", descriptionEvent.getType().getConceptId());
		assertEquals("900000000000013009", descriptionEvent.getType().getConceptId());
		Map<String, DescriptionPojo.Acceptability> acceptabilityMap = descriptionEvent.getAcceptabilityMap();
		assertEquals(1, acceptabilityMap.size());
		assertTrue(acceptabilityMap.containsKey("900000000000508004"));
		assertEquals(ACCEPTABLE, acceptabilityMap.get("900000000000508004"));

		assertEquals("242605002", descriptionBite.getConceptId());
		assertEquals("A human bite", descriptionBite.getTerm());
		assertEquals("en", descriptionBite.getLang());
		assertEquals("900000000000448009", descriptionBite.getCaseSignificance().getConceptId());
		assertEquals("900000000000013009", descriptionBite.getType().getConceptId());
		assertEquals("900000000000013009", descriptionBite.getType().getConceptId());
		acceptabilityMap = descriptionBite.getAcceptabilityMap();
		assertEquals(1, acceptabilityMap.size());
		assertTrue(acceptabilityMap.containsKey("900000000000509007"));
		assertEquals(ACCEPTABLE, acceptabilityMap.get("900000000000509007"));

		List<ChangeResult<DescriptionPojo>> changeResults = componentTransformService.loadDescriptionTransformationJobResults(branchPath, job.getId());
		assertEquals(3, changeResults.size());
		assertEquals(TRUE, changeResults.get(0).getSuccess());
		assertEquals(TRUE, changeResults.get(1).getSuccess());
		assertEquals(FALSE, changeResults.get(2).getSuccess());
		assertEquals("Simple validation failed: At least one valid acceptability entry is required.", changeResults.get(2).getMessage());
	}

}
