package org.ihtsdo.otf.transformationandtemplate.service.componenttransform;

import org.awaitility.Awaitility;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.Branch;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.ConceptChangeBatchStatus;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.ConceptPojo;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.DescriptionPojo;
import org.ihtsdo.otf.rest.exception.BusinessServiceException;
import org.ihtsdo.otf.transformationandtemplate.domain.ComponentTransformationJob;
import org.ihtsdo.otf.transformationandtemplate.domain.ComponentTransformationRequest;
import org.ihtsdo.otf.transformationandtemplate.service.client.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.ihtsdo.otf.rest.client.terminologyserver.pojo.DescriptionPojo.Acceptability.ACCEPTABLE;
import static org.ihtsdo.otf.rest.client.terminologyserver.pojo.DescriptionPojo.Acceptability.PREFERRED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest(properties = "application.properties")
@ComponentScan(basePackages = "org.ihtsdo.otf.transformationandtemplate")
@Import(ComponentTransformService.class)
class TransformationIntegrationTest {

	@Autowired
	private ComponentTransformService componentTransformService;

	@MockitoBean
	private SnowstormClientFactory snowstormClientFactory;

	@MockitoBean
	private SnowstormClient snowstormClientMock;

	@MockitoBean
	private AuthoringServicesClient authoringServicesClientMock;

	@MockitoBean
	private AuthoringServicesClientFactory authoringServicesClientFactory;

	@MockitoBean
	private BranchService branchService;


	@BeforeEach
	void before() {
		Mockito.when(snowstormClientFactory.getClientForCurrentUser()).thenReturn(snowstormClientMock);
		Mockito.when(authoringServicesClientFactory.getClientForCurrentUser()).thenReturn(authoringServicesClientMock);
	}

	private ComponentTransformationJob awaitJobCompletion(String branchPath, ComponentTransformationJob job) throws BusinessServiceException {
		AtomicReference<ComponentTransformationJob> currentJob = new AtomicReference<>(job);
		Awaitility.await()
				.atMost(10, TimeUnit.SECONDS)
				.pollInterval(200, TimeUnit.MILLISECONDS)
				.until(() -> {
					ComponentTransformationJob updated = componentTransformService.loadTransformationJob(branchPath, currentJob.get().getId());
					currentJob.set(updated);
					return updated.getStatus().getStatus().isEndState();
				});
		return currentJob.get();
	}

	@Test
	void test() throws BusinessServiceException, TimeoutException {
		String branchPath = "MAIN/KAITEST/KAITEST-100";

		DescriptionPojo svDescription = new DescriptionPojo("följdtillstånd efter fraktur på handleds- och handnivå").setDescriptionId("789");

		Map<String, DescriptionPojo.Acceptability> svAcceptabilityMap = new HashMap<>();
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

		job = awaitJobCompletion(branchPath, job);

		@SuppressWarnings("unchecked")
		ArgumentCaptor<Collection<ConceptPojo>> conceptsSavedCaptor = ArgumentCaptor.forClass(Collection.class);
		ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);

		Mockito.verify(snowstormClientMock).saveUpdateConceptsNoValidation(conceptsSavedCaptor.capture(), stringArgumentCaptor.capture());

		assertEquals(branchPath, stringArgumentCaptor.getValue());

		Map<String, DescriptionPojo> savedByTerm = new HashMap<>();
		for (ConceptPojo conceptPojo : conceptsSavedCaptor.getValue()) {
			for (DescriptionPojo description : conceptPojo.getDescriptions()) {
				savedByTerm.put(description.getTerm(), description);
			}
		}
		assertTrue(savedByTerm.keySet().containsAll(Arrays.asList(
				"The event",
				"A human bite",
				"följdtillstånd efter fraktur på handleds- och/eller handnivå",
				"följdtillstånd efter fraktur på handleds- och handnivå")));

		assertDescription(savedByTerm.get("The event"), "272379006", "en",
				"900000000000448009", "900000000000013009", Map.of("900000000000508004", ACCEPTABLE));
		assertDescription(savedByTerm.get("A human bite"), "242605002", "en",
				"900000000000448009", "900000000000013009", Map.of("900000000000509007", ACCEPTABLE));
		// 210958007 Disorder due to and following fracture... — new Swedish preferred demotes existing preferred
		assertDescription(savedByTerm.get("följdtillstånd efter fraktur på handleds- och/eller handnivå"), "210958007", "sv",
				"900000000000448009", "900000000000013009", Map.of("46011000052107", PREFERRED));
		assertEquals(Map.of("46011000052107", ACCEPTABLE),
				savedByTerm.get("följdtillstånd efter fraktur på handleds- och handnivå").getAcceptabilityMap());

		List<ChangeResult<DescriptionPojo>> changeResults = componentTransformService.loadDescriptionTransformationJobResults(branchPath, job.getId());
		assertEquals(Arrays.asList(TRUE, TRUE, FALSE, TRUE, TRUE),
				changeResults.stream().map(ChangeResult::getSuccess).toList());
		assertEquals("Simple validation failed: At least one valid acceptability entry is required.", changeResults.get(2).getMessage());
	}

	@Test
	void testUpdateAcceptability() throws BusinessServiceException, TimeoutException {
		String branchPath = "MAIN/KAITEST/KAITEST-103";

		DescriptionPojo svDescription = new DescriptionPojo("följdtillstånd efter fraktur på handleds- och handnivå").setDescriptionId("2148514019");
		Map<String, DescriptionPojo.Acceptability> svAcceptabilityMap = new HashMap<>();
		svDescription.setLang("sv");
		svDescription.setType(DescriptionPojo.Type.SYNONYM);
		svAcceptabilityMap.put("46011000052107", PREFERRED);
		svAcceptabilityMap.put("500191000057100", PREFERRED); // Laboratory Medicine language reference set
		svDescription.setAcceptabilityMap(svAcceptabilityMap);
		svDescription.setModuleId("45991000052106");

		// Update acceptability to N for Laboratory Medicine language reference set
		Mockito.when(snowstormClientMock.getBranch(any())).thenReturn(new Branch());
		Mockito.when(snowstormClientMock.getDefaultModuleId(branchPath)).thenReturn("45991000052106");
		Mockito.when(authoringServicesClientMock.retrieveProject(any())).thenReturn(new AuthoringProject());
		Mockito.when(snowstormClientMock.getFullConcepts(any(), any())).thenReturn(Arrays.asList(
				new ConceptPojo("210958007").add(new DescriptionPojo("Bite (event)").setDescriptionId("456")).add(svDescription)));

		Mockito.when(snowstormClientMock.saveUpdateConceptsNoValidation(any(), any())).thenReturn(new ConceptChangeBatchStatus(ConceptChangeBatchStatus.Status.COMPLETED));
		ComponentTransformationJob job = componentTransformService.queueBatchTransformation(new ComponentTransformationRequest(
				"description-update-tsv", branchPath, null, null, null, null, 100, getClass().getResourceAsStream("description-update-tsv-test.tsv"), false));

		job = awaitJobCompletion(branchPath, job);

		List<ChangeResult<DescriptionPojo>> changeResults = componentTransformService.loadDescriptionTransformationJobResults(branchPath, job.getId());
		assertEquals(1, changeResults.size());
		assertEquals(TRUE, changeResults.get(0).getSuccess());

		ArgumentCaptor<Collection<ConceptPojo>> conceptsSavedCaptor = ArgumentCaptor.forClass(Collection.class);
		ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);

		Mockito.verify(snowstormClientMock).saveUpdateConceptsNoValidation(conceptsSavedCaptor.capture(), stringArgumentCaptor.capture());

		assertEquals(branchPath, stringArgumentCaptor.getValue());

		DescriptionPojo descriptionSV = null;
		for (ConceptPojo conceptPojo : conceptsSavedCaptor.getValue()) {
			for (DescriptionPojo description : conceptPojo.getDescriptions()) {
				if ("följdtillstånd efter fraktur på handleds- och handnivå".equals(description.getTerm())) {
					descriptionSV = description;
				}
			}
		}
		assertNotNull(descriptionSV);

		assertEquals("följdtillstånd efter fraktur på handleds- och handnivå", descriptionSV.getTerm());
		assertEquals("sv", descriptionSV.getLang());
		Map<String, DescriptionPojo.Acceptability> acceptabilityMap = descriptionSV.getAcceptabilityMap();
		assertEquals(1, acceptabilityMap.size());
		assertTrue(acceptabilityMap.containsKey("46011000052107"));
		assertEquals(PREFERRED, acceptabilityMap.get("46011000052107"));
	}

	@Test
	void testUpdateDescriptionAgainstInvalidModule() throws BusinessServiceException {
		String branchPath = "MAIN/KAITEST/KAITEST-101";

		DescriptionPojo enDescription = new DescriptionPojo("Test").setDescriptionId("2148514019");
		Map<String, DescriptionPojo.Acceptability> enAcceptabilityMap = new HashMap<>();
		enDescription.setLang("en");
		enDescription.setType(DescriptionPojo.Type.SYNONYM);
		enAcceptabilityMap.put("900000000000509007", PREFERRED);
		enAcceptabilityMap.put("900000000000508004", PREFERRED);
		enDescription.setAcceptabilityMap(enAcceptabilityMap);
		enDescription.setModuleId("900000000000207008"); // core module

		Mockito.when(authoringServicesClientMock.retrieveProject(any())).thenReturn(new AuthoringProject());
		Mockito.when(snowstormClientMock.getBranch(any())).thenReturn(new Branch());
		Mockito.when(snowstormClientMock.getDefaultModuleId(branchPath)).thenReturn("45991000052106"); // sv module
		Mockito.when(snowstormClientMock.getFullConcepts(any(), any())).thenReturn(Arrays.asList(
				new ConceptPojo("272379006").add(enDescription)
		));

		ComponentTransformationJob job = componentTransformService.queueBatchTransformation(new ComponentTransformationRequest(
				"description-inactivate-tsv", branchPath, null, null, null, null, 100, getClass().getResourceAsStream("description-inactivate-tsv-test.tsv"), false));

		job = awaitJobCompletion(branchPath, job);

		List<ChangeResult<DescriptionPojo>> changeResults = componentTransformService.loadDescriptionTransformationJobResults(branchPath, job.getId());
		assertEquals(1, changeResults.size());
		assertEquals(FALSE, changeResults.get(0).getSuccess());
		assertEquals("Could not update description in the core module.", changeResults.get(0).getMessage());
	}

	@Test
	void testCreateAddsExistingInternationalDescriptionToLanguageRefset() throws BusinessServiceException, TimeoutException {
		String branchPath = "MAIN/CANSHARE/CANSHARE-100";
		String canshareLangRefset = "231621000210105";

		DescriptionPojo internationalDescription = new DescriptionPojo("Event").setDescriptionId("123456789012");
		internationalDescription.setLang("en");
		internationalDescription.setType(DescriptionPojo.Type.SYNONYM);
		internationalDescription.setModuleId("900000000000207008"); // core module
		Map<String, DescriptionPojo.Acceptability> internationalAcceptability = new HashMap<>();
		internationalAcceptability.put("900000000000509007", PREFERRED);
		internationalDescription.setAcceptabilityMap(internationalAcceptability);

		DescriptionPojo previousCansharePreferred = new DescriptionPojo("Occurrence").setDescriptionId("987654321098");
		previousCansharePreferred.setLang("en");
		previousCansharePreferred.setType(DescriptionPojo.Type.SYNONYM);
		previousCansharePreferred.setModuleId("21000220103");
		Map<String, DescriptionPojo.Acceptability> previousAcceptability = new HashMap<>();
		previousAcceptability.put(canshareLangRefset, PREFERRED);
		previousCansharePreferred.setAcceptabilityMap(previousAcceptability);

		Mockito.when(snowstormClientMock.getBranch(any())).thenReturn(new Branch());
		Mockito.when(snowstormClientMock.getDefaultModuleId(branchPath)).thenReturn("21000220103");
		Mockito.when(snowstormClientMock.getFullConcepts(any(), any())).thenReturn(Collections.singletonList(
				new ConceptPojo("272379006").add(internationalDescription).add(previousCansharePreferred)
		));
		Mockito.when(snowstormClientMock.runValidation(any(), any())).thenReturn(new ArrayList<>());
		Mockito.when(snowstormClientMock.saveUpdateConceptsNoValidation(any(), any())).thenReturn(new ConceptChangeBatchStatus(ConceptChangeBatchStatus.Status.COMPLETED));

		ComponentTransformationJob job = componentTransformService.queueBatchTransformation(new ComponentTransformationRequest(
				"description-create-tsv", branchPath, null, null, null, null, 100,
				getClass().getResourceAsStream("description-create-lrs-existing-tsv-test.tsv"), false));

		job = awaitJobCompletion(branchPath, job);

		@SuppressWarnings("unchecked")
		ArgumentCaptor<Collection<ConceptPojo>> conceptsSavedCaptor = ArgumentCaptor.forClass(Collection.class);
		Mockito.verify(snowstormClientMock).saveUpdateConceptsNoValidation(conceptsSavedCaptor.capture(), any());

		ConceptPojo savedConcept = conceptsSavedCaptor.getValue().iterator().next();
		long eventDescriptionCount = savedConcept.getDescriptions().stream()
				.filter(d -> "Event".equals(d.getTerm()))
				.count();
		assertEquals(1, eventDescriptionCount, "Existing international description should be reused, not duplicated");

		DescriptionPojo eventDescription = savedConcept.getDescriptions().stream()
				.filter(d -> "Event".equals(d.getTerm()))
				.findFirst()
				.orElse(null);
		assertNotNull(eventDescription);
		assertEquals("123456789012", eventDescription.getDescriptionId());
		assertEquals("900000000000207008", eventDescription.getModuleId(), "Core module description must not be reassigned");
		assertEquals(PREFERRED, eventDescription.getAcceptabilityMap().get(canshareLangRefset));
		assertEquals(PREFERRED, eventDescription.getAcceptabilityMap().get("900000000000509007"));

		DescriptionPojo demotedDescription = savedConcept.getDescriptions().stream()
				.filter(d -> "Occurrence".equals(d.getTerm()))
				.findFirst()
				.orElse(null);
		assertNotNull(demotedDescription);
		assertEquals(ACCEPTABLE, demotedDescription.getAcceptabilityMap().get(canshareLangRefset));

		List<ChangeResult<DescriptionPojo>> changeResults = componentTransformService.loadDescriptionTransformationJobResults(branchPath, job.getId());
		assertEquals(1, changeResults.size());
		assertEquals(TRUE, changeResults.get(0).getSuccess());
		assertEquals("123456789012", changeResults.get(0).getComponent().getDescriptionId());
	}

	@Test
	void testCreateMixedNewAndExistingDescriptionsForLanguageRefset() throws BusinessServiceException, TimeoutException {
		String branchPath = "MAIN/CANSHARE/CANSHARE-101";
		String canshareLangRefset = "231621000210105";

		DescriptionPojo internationalDescription = new DescriptionPojo("Event").setDescriptionId("123456789012");
		internationalDescription.setLang("en");
		internationalDescription.setType(DescriptionPojo.Type.SYNONYM);
		internationalDescription.setModuleId("900000000000207008");
		Map<String, DescriptionPojo.Acceptability> internationalAcceptability = new HashMap<>();
		internationalAcceptability.put("900000000000509007", PREFERRED);
		internationalDescription.setAcceptabilityMap(internationalAcceptability);

		Mockito.when(snowstormClientMock.getBranch(any())).thenReturn(new Branch());
		Mockito.when(snowstormClientMock.getDefaultModuleId(branchPath)).thenReturn("21000220103");
		Mockito.when(snowstormClientMock.getFullConcepts(any(), any())).thenReturn(Collections.singletonList(
				new ConceptPojo("272379006").add(internationalDescription)
		));
		Mockito.when(snowstormClientMock.runValidation(any(), any())).thenReturn(new ArrayList<>());
		Mockito.when(snowstormClientMock.saveUpdateConceptsNoValidation(any(), any())).thenReturn(new ConceptChangeBatchStatus(ConceptChangeBatchStatus.Status.COMPLETED));

		ComponentTransformationJob job = componentTransformService.queueBatchTransformation(new ComponentTransformationRequest(
				"description-create-tsv", branchPath, null, null, null, null, 100,
				getClass().getResourceAsStream("description-create-lrs-mixed-tsv-test.tsv"), false));

		job = awaitJobCompletion(branchPath, job);

		@SuppressWarnings("unchecked")
		ArgumentCaptor<Collection<ConceptPojo>> conceptsSavedCaptor = ArgumentCaptor.forClass(Collection.class);
		Mockito.verify(snowstormClientMock).saveUpdateConceptsNoValidation(conceptsSavedCaptor.capture(), any());

		ConceptPojo savedConcept = conceptsSavedCaptor.getValue().iterator().next();

		DescriptionPojo eventDescription = savedConcept.getDescriptions().stream()
				.filter(d -> "Event".equals(d.getTerm()))
				.findFirst()
				.orElse(null);
		assertNotNull(eventDescription);
		assertEquals("123456789012", eventDescription.getDescriptionId());
		assertEquals(PREFERRED, eventDescription.getAcceptabilityMap().get(canshareLangRefset));

		DescriptionPojo newDescription = savedConcept.getDescriptions().stream()
				.filter(d -> "CanShare event synonym".equals(d.getTerm()))
				.findFirst()
				.orElse(null);
		assertNotNull(newDescription);
		assertEquals("21000220103", newDescription.getModuleId());
		assertEquals(ACCEPTABLE, newDescription.getAcceptabilityMap().get(canshareLangRefset));

		List<ChangeResult<DescriptionPojo>> changeResults = componentTransformService.loadDescriptionTransformationJobResults(branchPath, job.getId());
		assertEquals(2, changeResults.size());
		assertEquals(TRUE, changeResults.get(0).getSuccess());
		assertEquals(TRUE, changeResults.get(1).getSuccess());
	}

	@Test
	void testCreateNewPreferredDemotesExistingPreferredOfSameType() throws BusinessServiceException, TimeoutException {
		String branchPath = "MAIN/CANSHARE/CANSHARE-102";
		String canshareLangRefset = "231621000210105";

		DescriptionPojo existingPreferred = new DescriptionPojo("Event").setDescriptionId("123456789012");
		existingPreferred.setLang("en");
		existingPreferred.setType(DescriptionPojo.Type.SYNONYM);
		existingPreferred.setModuleId("900000000000207008");
		Map<String, DescriptionPojo.Acceptability> existingAcceptability = new HashMap<>();
		existingAcceptability.put("900000000000509007", PREFERRED);
		existingAcceptability.put(canshareLangRefset, PREFERRED);
		existingPreferred.setAcceptabilityMap(existingAcceptability);

		Mockito.when(snowstormClientMock.getBranch(any())).thenReturn(new Branch());
		Mockito.when(snowstormClientMock.getDefaultModuleId(branchPath)).thenReturn("21000220103");
		Mockito.when(snowstormClientMock.getFullConcepts(any(), any())).thenReturn(Collections.singletonList(
				new ConceptPojo("272379006").add(existingPreferred)
		));
		Mockito.when(snowstormClientMock.runValidation(any(), any())).thenReturn(new ArrayList<>());
		Mockito.when(snowstormClientMock.saveUpdateConceptsNoValidation(any(), any())).thenReturn(new ConceptChangeBatchStatus(ConceptChangeBatchStatus.Status.COMPLETED));

		ComponentTransformationJob job = componentTransformService.queueBatchTransformation(new ComponentTransformationRequest(
				"description-create-tsv", branchPath, null, null, null, null, 100,
				getClass().getResourceAsStream("description-create-lrs-new-preferred-tsv-test.tsv"), false));

		job = awaitJobCompletion(branchPath, job);

		@SuppressWarnings("unchecked")
		ArgumentCaptor<Collection<ConceptPojo>> conceptsSavedCaptor = ArgumentCaptor.forClass(Collection.class);
		Mockito.verify(snowstormClientMock).saveUpdateConceptsNoValidation(conceptsSavedCaptor.capture(), any());

		ConceptPojo savedConcept = conceptsSavedCaptor.getValue().iterator().next();

		DescriptionPojo newPreferred = savedConcept.getDescriptions().stream()
				.filter(d -> "CanShare preferred term".equals(d.getTerm()))
				.findFirst()
				.orElse(null);
		assertNotNull(newPreferred);
		assertEquals(PREFERRED, newPreferred.getAcceptabilityMap().get(canshareLangRefset));

		DescriptionPojo demoted = savedConcept.getDescriptions().stream()
				.filter(d -> "Event".equals(d.getTerm()))
				.findFirst()
				.orElse(null);
		assertNotNull(demoted);
		assertEquals(ACCEPTABLE, demoted.getAcceptabilityMap().get(canshareLangRefset));
		assertEquals(PREFERRED, demoted.getAcceptabilityMap().get("900000000000509007"),
				"Preferred membership in other language refsets must be left unchanged");

		List<ChangeResult<DescriptionPojo>> changeResults = componentTransformService.loadDescriptionTransformationJobResults(branchPath, job.getId());
		assertEquals(1, changeResults.size());
		assertEquals(TRUE, changeResults.get(0).getSuccess());
	}

	@Test
	void testDescriptionReplacements() throws BusinessServiceException, TimeoutException {
		String branchPath = "MAIN/KAITEST/KAITEST-100";

		DescriptionPojo svDescription = new DescriptionPojo("följdtillstånd efter fraktur på handleds- och handnivå");
		Map<String, DescriptionPojo.Acceptability> svAcceptabilityMap = new HashMap<>();
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
		svAcceptabilityMap = new HashMap<>();
		svAcceptabilityMap.put("46011000052107", PREFERRED);
		svDescription1.setAcceptabilityMap(svAcceptabilityMap);
		svDescription1.setModuleId("45991000052106");
		svDescription1.setReleased(true);
		svDescription1.setDescriptionId("2579921000052110");

		DescriptionPojo svDescription2 = new DescriptionPojo("följdtillstånd efter fraktur på handleds- och handnivå 2");
		svDescription2.setLang("sv");
		svDescription2.setType(DescriptionPojo.Type.SYNONYM);
		svAcceptabilityMap = new HashMap<>();
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

		job = awaitJobCompletion(branchPath, job);

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
		assertEquals(2, changeResults.size());
		assertEquals(TRUE, changeResults.get(0).getSuccess());
		assertEquals(TRUE, changeResults.get(1).getSuccess());
	}

	private void assertDescription(DescriptionPojo description, String conceptId, String lang,
			String caseSignificanceId, String typeId, Map<String, DescriptionPojo.Acceptability> acceptability) {
		assertEquals(conceptId + "|" + lang + "|" + caseSignificanceId + "|" + typeId,
				description.getConceptId() + "|" + description.getLang() + "|"
						+ description.getCaseSignificance().getConceptId() + "|" + description.getType().getConceptId());
		assertEquals(acceptability, description.getAcceptabilityMap());
	}
}
