package org.ihtsdo.otf.authoringtemplate.service;
import static org.ihtsdo.otf.authoringtemplate.service.Constants.PREFERRED;
import static org.junit.Assert.assertEquals;
import static org.snomed.authoringtemplate.domain.CaseSignificance.CASE_INSENSITIVE;
import static org.snomed.authoringtemplate.domain.CaseSignificance.ENTIRE_TERM_CASE_SENSITIVE;
import static org.snomed.authoringtemplate.domain.CaseSignificance.INITIAL_CHARACTER_CASE_INSENSITIVE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ihtsdo.otf.authoringtemplate.service.exception.ServiceException;
import org.ihtsdo.otf.authoringtemplate.transform.TestDataHelper;
import org.ihtsdo.otf.rest.client.snowowl.pojo.DescriptionPojo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.snomed.authoringtemplate.domain.CaseSignificance;
import org.snomed.authoringtemplate.domain.Description;
import org.snomed.authoringtemplate.domain.DescriptionType;
import org.snomed.authoringtemplate.domain.LexicalTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
public class LexicalTemplateTransformationServiceTest {
	
	private List<LexicalTemplate> lexicalTemplates;
	private List<Description> descriptions;
	
	@Before
	public void setUp() {
		lexicalTemplates = new ArrayList<>();
		descriptions = new ArrayList<>();
		LexicalTemplate course = new LexicalTemplate("course", "[course]", "clinicalCourse", Collections.emptyList());
		course.setRemoveFromTermTemplateWhenSlotAbsent(Arrays.asList("$course$"));
		LexicalTemplate bodysStructure = new LexicalTemplate("body structure", "[body structure]", "bodyStructure", Arrays.asList("Structure of",
				"structure"));
		bodysStructure.setRemoveFromTermTemplateWhenSlotAbsent(Arrays.asList("of $body structure$"));
		LexicalTemplate substance = new LexicalTemplate("substance", "[substance]", "substance", Collections.emptyList());
		substance.setRemoveFromTermTemplateWhenSlotAbsent(Arrays.asList("caused by $substance$"));
		
		lexicalTemplates.add(course);
		lexicalTemplates.add(bodysStructure);
		lexicalTemplates.add(substance);
		Description fsn = new Description("$course$ contact dermatitis of $body structure$ caused by $substance$ (disorder)");
		fsn.setLang("en");
		fsn.setType(DescriptionType.FSN);
		fsn.setCaseSignificance(CaseSignificance.CASE_INSENSITIVE);
		fsn.setAcceptabilityMap(TestDataHelper.constructAcceptabilityMap(PREFERRED, PREFERRED));
		descriptions.add(fsn);
		
		Description pt = new Description("$course$ contact dermatitis of $body structure$ caused by $substance$");
		pt.setLang("en");
		pt.setType(DescriptionType.SYNONYM);
		pt.setCaseSignificance(CaseSignificance.CASE_INSENSITIVE);
		pt.setAcceptabilityMap(TestDataHelper.constructAcceptabilityMap(PREFERRED, PREFERRED));
		descriptions.add(pt);
	}
	
	@Test
	public void testTermWithInitialCharacterCaseInsensitive() throws ServiceException {
		Map<String, String> slotValueMap = new HashMap<>();
		slotValueMap.put("clinicalCourse", "Sudden onset AND short duration (qualifier value)");
		slotValueMap.put("bodyStructure", "Bone structure of right tibia (body structure)");
		slotValueMap.put("substance", "DPB1*1401 (substance)");
		Map<String, CaseSignificance> csMap = new HashMap<>();
		csMap.put("clinicalCourse", INITIAL_CHARACTER_CASE_INSENSITIVE);
		csMap.put("bodyStructure", CASE_INSENSITIVE);
		csMap.put("substance", ENTIRE_TERM_CASE_SENSITIVE);
		Map<String, Set<DescriptionPojo>> slotDescriptionValuesMap = TestDataHelper.constructSlotDescriptionValuesMap(slotValueMap, csMap, DescriptionType.FSN);
		
		Map<String, String> slotPtValueMap = new HashMap<>();
		slotPtValueMap.put("clinicalCourse", "Sudden onset AND short duration");
		slotPtValueMap.put("bodyStructure", "Bone structure of right tibia");
		slotPtValueMap.put("substance", "DPB1*1401");
		Map<String, CaseSignificance> csPtMap = new HashMap<>();
		csPtMap.put("clinicalCourse", INITIAL_CHARACTER_CASE_INSENSITIVE);
		csPtMap.put("bodyStructure", CASE_INSENSITIVE);
		csPtMap.put("substance", ENTIRE_TERM_CASE_SENSITIVE);
		Map<String, Set<DescriptionPojo>> slotPtValuesMap = TestDataHelper.constructSlotDescriptionValuesMap(slotPtValueMap, csPtMap, DescriptionType.SYNONYM);
		for (String slot : slotPtValuesMap.keySet()) {
			slotDescriptionValuesMap.get(slot).addAll(slotPtValuesMap.get(slot));
		}
		
		List<Description> results = LexicalTemplateTransformService.transformDescriptions(lexicalTemplates, descriptions, slotDescriptionValuesMap);
		assertEquals(2, results.size());
		assertEquals("Sudden onset AND short duration contact dermatitis of bone of right tibia caused by DPB1*1401 (disorder)", results.get(0).getTerm());
		assertEquals(INITIAL_CHARACTER_CASE_INSENSITIVE, results.get(0).getCaseSignificance());
	}
	
	@Test
	public void testTermWithEntireTermCaseSensitive() throws ServiceException {
		Map<String, String> slotValueMap = new HashMap<>();
		slotValueMap.put("clinicalCourse", "Sudden onset AND short duration (qualifier value)");
		slotValueMap.put("bodyStructure", "Bone structure of C5-C7 (body structure)");
		slotValueMap.put("substance", "DPB1*1401 (substance)");
		Map<String, CaseSignificance> csMap = new HashMap<>();
		csMap.put("clinicalCourse", ENTIRE_TERM_CASE_SENSITIVE);
		csMap.put("bodyStructure", CASE_INSENSITIVE );
		csMap.put("substance", INITIAL_CHARACTER_CASE_INSENSITIVE );
		Map<String, Set<DescriptionPojo>> slotDescriptionValuesMap = TestDataHelper.constructSlotDescriptionValuesMap(slotValueMap, csMap, DescriptionType.FSN);
		
		Map<String, String> slotPtValueMap = new HashMap<>();
		slotPtValueMap.put("clinicalCourse", "Sudden onset AND short duration");
		slotPtValueMap.put("bodyStructure", "Bone structure of C5-C7");
		slotPtValueMap.put("substance", "DPB1*1401");
		Map<String, CaseSignificance> csPtMap = new HashMap<>();
		csPtMap.put("clinicalCourse", ENTIRE_TERM_CASE_SENSITIVE);
		csPtMap.put("bodyStructure", CASE_INSENSITIVE );
		csPtMap.put("substance", INITIAL_CHARACTER_CASE_INSENSITIVE );
		
		Map<String, Set<DescriptionPojo>> slotPtValuesMap = TestDataHelper.constructSlotDescriptionValuesMap(slotPtValueMap, csPtMap, DescriptionType.SYNONYM);
		for (String slot : slotPtValuesMap.keySet()) {
			slotDescriptionValuesMap.get(slot).addAll(slotPtValuesMap.get(slot));
		}
		
		List<Description> results = LexicalTemplateTransformService.transformDescriptions(lexicalTemplates, descriptions, slotDescriptionValuesMap);
		assertEquals(2, results.size());
		assertEquals("Sudden onset AND short duration contact dermatitis of bone of C5-C7 caused by DPB1*1401 (disorder)", results.get(0).getTerm());
		assertEquals(ENTIRE_TERM_CASE_SENSITIVE, results.get(0).getCaseSignificance());
		
	}
	
	@Test
	public void testTermWithCaseInSensitive() throws ServiceException {
		Map<String, String> slotValueMap = new HashMap<>();
		slotValueMap.put("clinicalCourse", "Chronic aggressive (qualifier value)");
		slotValueMap.put("bodyStructure", "Transplant (body structure)");
		slotValueMap.put("substance", "Blood material (substance)");
		Map<String, CaseSignificance> csMap = new HashMap<>();
		csMap.put("clinicalCourse", CASE_INSENSITIVE);
		csMap.put("bodyStructure", CASE_INSENSITIVE );
		csMap.put("substance", CASE_INSENSITIVE );
		Map<String, Set<DescriptionPojo>> slotDescriptionValuesMap = TestDataHelper.constructSlotDescriptionValuesMap(slotValueMap, csMap, DescriptionType.FSN);
		
		Map<String, String> slotPtValueMap = new HashMap<>();
		slotPtValueMap.put("clinicalCourse", "Chronic aggressive");
		slotPtValueMap.put("bodyStructure", "Transplant");
		slotPtValueMap.put("substance", "Blood material");
		Map<String, CaseSignificance> csPtMap = new HashMap<>();
		csPtMap.put("clinicalCourse", CASE_INSENSITIVE);
		csPtMap.put("bodyStructure", CASE_INSENSITIVE );
		csPtMap.put("substance", CASE_INSENSITIVE );
		
		Map<String, Set<DescriptionPojo>> slotPtValuesMap = TestDataHelper.constructSlotDescriptionValuesMap(slotPtValueMap, csPtMap, DescriptionType.SYNONYM);
		for (String slot : slotPtValuesMap.keySet()) {
			slotDescriptionValuesMap.get(slot).addAll(slotPtValuesMap.get(slot));
		}
		
		List<Description> results = LexicalTemplateTransformService.transformDescriptions(lexicalTemplates, descriptions, slotDescriptionValuesMap);
		assertEquals(2, results.size());
		assertEquals("Chronic aggressive contact dermatitis of transplant caused by blood material (disorder)", results.get(0).getTerm());
		assertEquals(CASE_INSENSITIVE, results.get(0).getCaseSignificance());
		
	}
	
	@Test
	public void testTermWithoutOptionalSlot() throws ServiceException {
		Map<String, String> slotValueMap = new HashMap<>();
		slotValueMap.put("bodyStructure", "Transplant (body structure)");
		slotValueMap.put("substance", "Blood material (substance)");
		Map<String, CaseSignificance> csMap = new HashMap<>();
		csMap.put("bodyStructure", CASE_INSENSITIVE );
		csMap.put("substance", CASE_INSENSITIVE );
		Map<String, Set<DescriptionPojo>> slotDescriptionValuesMap = TestDataHelper.constructSlotDescriptionValuesMap(slotValueMap, csMap, DescriptionType.FSN);
		
		Map<String, String> slotPtValueMap = new HashMap<>();
		slotPtValueMap.put("bodyStructure", "Transplant");
		slotPtValueMap.put("substance", "Blood material");
		Map<String, CaseSignificance> csPtMap = new HashMap<>();
		csPtMap.put("bodyStructure", CASE_INSENSITIVE );
		csPtMap.put("substance", CASE_INSENSITIVE );
		
		Map<String, Set<DescriptionPojo>> slotPtValuesMap = TestDataHelper.constructSlotDescriptionValuesMap(slotPtValueMap, csPtMap, DescriptionType.SYNONYM);
		for (String slot : slotPtValuesMap.keySet()) {
			slotDescriptionValuesMap.get(slot).addAll(slotPtValuesMap.get(slot));
		}
		
		List<Description> results = LexicalTemplateTransformService.transformDescriptions(lexicalTemplates, descriptions, slotDescriptionValuesMap);
		assertEquals(2, results.size());
		assertEquals("Contact dermatitis of transplant caused by blood material (disorder)", results.get(0).getTerm());
		assertEquals(CASE_INSENSITIVE, results.get(0).getCaseSignificance());
	}
}
