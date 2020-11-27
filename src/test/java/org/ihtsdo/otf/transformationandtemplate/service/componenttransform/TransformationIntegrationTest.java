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
import static org.ihtsdo.otf.rest.client.terminologyserver.pojo.DescriptionPojo.Acceptability.PREFERRED;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(properties = "application.properties")
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

		DescriptionPojo svDescription = new DescriptionPojo("följdtillstånd efter fraktur på handleds- och handnivå").setDescriptionId("789");

		Map<String, DescriptionPojo.Acceptability> svAcceptabilityMap = new HashMap();
		svDescription.setLang("sv");
		svDescription.setType(DescriptionPojo.Type.SYNONYM);
		svAcceptabilityMap.put("46011000052107", PREFERRED);
		svDescription.setAcceptabilityMap(svAcceptabilityMap);
		svDescription.setModuleId("45991000052106");

		Mockito.when(snowstormClientMock.getBranch(any())).thenReturn(new Branch());
		Mockito.when(snowstormClientMock.getDefaultModuleId(branchPath)).thenReturn("45991000052106");
		Mockito.when(snowstormClientMock.getFullConcepts(any(), any())).thenReturn(Arrays.asList(
				new ConceptPojo("272379006").add(new DescriptionPojo("Event (event)").setDescriptionId("123")),
				new ConceptPojo("242605002").add(new DescriptionPojo("Bite (event)").setDescriptionId("456")),
				new ConceptPojo("774007").add(new DescriptionPojo("Bite (event)").setDescriptionId("456")),
				new ConceptPojo("210958007").add(new DescriptionPojo("Bite (event)").setDescriptionId("456")).add(svDescription)
		));
		Mockito.when(snowstormClientMock.runValidation(any(), any())).thenReturn(new ArrayList<>());
		Mockito.when(snowstormClientMock.saveUpdateConceptsNoValidation(any(), any())).thenReturn(new ConceptChangeBatchStatus(ConceptChangeBatchStatus.Status.COMPLETED));

		ComponentTransformationJob job = componentTransformService.queueBatchTransformation(new ComponentTransformationRequest(
				"description-create-tsv", branchPath, null, null, null, null, 100, getClass().getResourceAsStream("description-create-tsv-test.tsv"), false));

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
		DescriptionPojo descriptionFracture = null;
		DescriptionPojo descriptionFractureSV = null;
		for (ConceptPojo conceptPojo : conceptsSavedCaptor.getValue()) {
			for (DescriptionPojo description : conceptPojo.getDescriptions()) {
				if ("The event".equals(description.getTerm())) {
					descriptionEvent = description;
				}
				if ("A human bite".equals(description.getTerm())) {
					descriptionBite = description;
				}
				if ("följdtillstånd efter fraktur på handleds- och/eller handnivå".equals(description.getTerm())) {
					descriptionFracture = description;
				}
				if ("följdtillstånd efter fraktur på handleds- och handnivå".equals(description.getTerm())) {
					descriptionFractureSV = description;
				}
			}
		}
		assertNotNull(descriptionEvent);
		assertNotNull(descriptionBite);
		assertNotNull(descriptionFracture);
		assertNotNull(descriptionFractureSV);

		assertEquals("272379006", descriptionEvent.getConceptId());
		assertEquals("The event", descriptionEvent.getTerm());
		assertEquals("en", descriptionEvent.getLang());
		assertEquals("900000000000448009", descriptionEvent.getCaseSignificance().getConceptId());
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
		acceptabilityMap = descriptionBite.getAcceptabilityMap();
		assertEquals(1, acceptabilityMap.size());
		assertTrue(acceptabilityMap.containsKey("900000000000509007"));
		assertEquals(ACCEPTABLE, acceptabilityMap.get("900000000000509007"));

		// 210958007	Disorder due to and following fracture at wrist and/or hand level (disorder)	följdtillstånd efter fraktur på handleds- och/eller handnivå	sv	ci	SYNONYM	Swedish	PREFERRED
		assertEquals("210958007", descriptionFracture.getConceptId());
		assertEquals("följdtillstånd efter fraktur på handleds- och/eller handnivå", descriptionFracture.getTerm());
		assertEquals("sv", descriptionFracture.getLang());
		assertEquals("900000000000448009", descriptionFracture.getCaseSignificance().getConceptId());
		assertEquals("900000000000013009", descriptionFracture.getType().getConceptId());
		acceptabilityMap = descriptionFracture.getAcceptabilityMap();
		assertEquals(1, acceptabilityMap.size());
		assertTrue(acceptabilityMap.containsKey("46011000052107"));
		assertEquals(PREFERRED, acceptabilityMap.get("46011000052107"));

		assertEquals("följdtillstånd efter fraktur på handleds- och handnivå", descriptionFractureSV.getTerm());
		assertEquals("sv", descriptionFractureSV.getLang());
		acceptabilityMap = descriptionFractureSV.getAcceptabilityMap();
		assertEquals(1, acceptabilityMap.size());
		assertTrue(acceptabilityMap.containsKey("46011000052107"));
		assertEquals(ACCEPTABLE, acceptabilityMap.get("46011000052107"));

		List<ChangeResult<DescriptionPojo>> changeResults = componentTransformService.loadDescriptionTransformationJobResults(branchPath, job.getId());
		assertEquals(5, changeResults.size());
		assertEquals(TRUE, changeResults.get(0).getSuccess());
		assertEquals(TRUE, changeResults.get(1).getSuccess());
		assertEquals(FALSE, changeResults.get(2).getSuccess());
		assertEquals(TRUE, changeResults.get(3).getSuccess());
		assertEquals(TRUE, changeResults.get(4).getSuccess());
		assertEquals("Simple validation failed: At least one valid acceptability entry is required.", changeResults.get(2).getMessage());
	}

	@Test
	public void testDescriptionReplacements() throws BusinessServiceException, InterruptedException, TimeoutException {
		String branchPath = "MAIN/KAITEST/KAITEST-100";

		DescriptionPojo svDescription = new DescriptionPojo("följdtillstånd efter fraktur på handleds- och handnivå");
		Map<String, DescriptionPojo.Acceptability> svAcceptabilityMap = new HashMap();
		svDescription.setLang("sv");
		svDescription.setType(DescriptionPojo.Type.SYNONYM);
		svAcceptabilityMap.put("46011000052107", PREFERRED);
		svDescription.setAcceptabilityMap(svAcceptabilityMap);
		svDescription.setModuleId("45991000052106");
		svDescription.setReleased(true);
		svDescription.setDescriptionId("3112261000052114");

		DescriptionPojo svDescription1 = new DescriptionPojo("följdtillstånd efter fraktur på handleds- och handnivå 1");
		svDescription1.setLang("sv");
		svDescription1.setType(DescriptionPojo.Type.SYNONYM);
		svAcceptabilityMap = new HashMap();
		svAcceptabilityMap.put("46011000052107", PREFERRED);
		svDescription1.setAcceptabilityMap(svAcceptabilityMap);
		svDescription1.setModuleId("45991000052106");
		svDescription1.setReleased(true);
		svDescription1.setDescriptionId("2579921000052110");

		DescriptionPojo svDescription2 = new DescriptionPojo("följdtillstånd efter fraktur på handleds- och handnivå 2");
		svDescription2.setLang("sv");
		svDescription2.setType(DescriptionPojo.Type.SYNONYM);
		svAcceptabilityMap = new HashMap();
		svAcceptabilityMap.put("46011000052107", ACCEPTABLE);
		svDescription2.setAcceptabilityMap(svAcceptabilityMap);
		svDescription2.setModuleId("45991000052106");
		svDescription2.setDescriptionId("846011000052110");

		Mockito.when(snowstormClientMock.getBranch(any())).thenReturn(new Branch());
		Mockito.when(snowstormClientMock.getDefaultModuleId(branchPath)).thenReturn("45991000052106");
		Mockito.when(snowstormClientMock.getFullConcepts(any(), any())).thenReturn(Arrays.asList(
				new ConceptPojo("410058007").add(new DescriptionPojo("Bite (event)").setDescriptionId("111")).add(svDescription),
				new ConceptPojo("54352009").add(new DescriptionPojo("Bite 1 (event)").setDescriptionId("222")).add(svDescription1).add(svDescription2)
		));
		Mockito.when(snowstormClientMock.runValidation(any(), any())).thenReturn(new ArrayList<>());
		Mockito.when(snowstormClientMock.saveUpdateConceptsNoValidation(any(), any())).thenReturn(new ConceptChangeBatchStatus(ConceptChangeBatchStatus.Status.COMPLETED));

		ComponentTransformationJob job = componentTransformService.queueBatchTransformation(new ComponentTransformationRequest(
				"description-replacement-tsv", branchPath, null, null, null, null, 100, getClass().getResourceAsStream("description-replacement-tsv-test.tsv"), false));

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

		DescriptionPojo inactiveDescription1 = null;
		DescriptionPojo inactiveDescription2 = null;
		DescriptionPojo createdDescription = null;
		DescriptionPojo updatedDescription = null;
		for (ConceptPojo conceptPojo : conceptsSavedCaptor.getValue()) {
			for (DescriptionPojo description : conceptPojo.getDescriptions()) {
				if ("3112261000052114".equals(description.getDescriptionId())) {
					inactiveDescription1 = description;
				}
				if ("2579921000052110".equals(description.getDescriptionId())) {
					inactiveDescription2 = description;
				}
				if ("846011000052110".equals(description.getDescriptionId())) {
					updatedDescription = description;
				}
				if ("New replacement term".equals(description.getTerm())) {
					createdDescription = description;
				}
			}
		}
		assertNotNull(inactiveDescription1);
		assertNotNull(inactiveDescription2);
		assertNotNull(updatedDescription);
		assertNotNull(createdDescription);

		assertFalse(inactiveDescription1.isActive());
		assertFalse(inactiveDescription2.isActive());

		Map<String, DescriptionPojo.Acceptability> acceptabilityMap = updatedDescription.getAcceptabilityMap();
		assertEquals(1, acceptabilityMap.size());
		assertTrue(acceptabilityMap.containsKey("46011000052107"));
		assertEquals(PREFERRED, acceptabilityMap.get("46011000052107"));

		assertEquals("410058007", createdDescription.getConceptId());
		assertEquals("New replacement term", createdDescription.getTerm());
		acceptabilityMap = createdDescription.getAcceptabilityMap();
		assertEquals(1, acceptabilityMap.size());
		assertTrue(acceptabilityMap.containsKey("46011000052107"));
		assertEquals(PREFERRED, acceptabilityMap.get("46011000052107"));


		List<ChangeResult<DescriptionPojo>> changeResults = componentTransformService.loadDescriptionTransformationJobResults(branchPath, job.getId());
		assertEquals(5, changeResults.size());
		assertEquals(TRUE, changeResults.get(0).getSuccess());
		assertEquals(TRUE, changeResults.get(1).getSuccess());
	}
}
