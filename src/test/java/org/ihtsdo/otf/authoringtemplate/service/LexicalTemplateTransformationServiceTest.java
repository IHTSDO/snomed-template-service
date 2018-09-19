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
import java.util.SortedMap;
import java.util.TreeMap;

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
		SortedMap<String, String> slotValueMap = new TreeMap<>();
		slotValueMap.put("clinicalCourse", "Sudden onset AND short duration (qualifier value)");
		slotValueMap.put("bodyStructure", "Bone structure of right tibia (body structure)");
		slotValueMap.put("substance", "DPB1*1401 (substance)");
		Map<String, CaseSignificance> csMap = new HashMap<>();
		csMap.put("clinicalCourse", INITIAL_CHARACTER_CASE_INSENSITIVE);
		csMap.put("bodyStructure", CASE_INSENSITIVE);
		csMap.put("substance", ENTIRE_TERM_CASE_SENSITIVE);
		Map<String, Set<DescriptionPojo>> slotDescriptionValuesMap = TestDataHelper.constructSlotDescriptionValuesMap(slotValueMap, csMap, DescriptionType.FSN);
		LexicalTemplateTransformService.transformDescriptions(lexicalTemplates, descriptions, 
				slotDescriptionValuesMap);
		assertEquals(2, descriptions.size());
		assertEquals("Sudden onset AND short duration contact dermatitis of bone of right tibia caused by DPB1*1401 (disorder)", descriptions.get(0).getTerm());
		assertEquals(INITIAL_CHARACTER_CASE_INSENSITIVE, descriptions.get(0).getCaseSignificance());
	}
	
	@Test
	public void testTermWithEntireTermCaseSensitive() throws ServiceException {
		SortedMap<String, String> slotValueMap = new TreeMap<>();
		slotValueMap.put("clinicalCourse", "Sudden onset AND short duration (qualifier value)");
		slotValueMap.put("bodyStructure", "Bone structure of C5-C7 (body structure)");
		slotValueMap.put("substance", "DPB1*1401 (substance)");
		Map<String, CaseSignificance> csMap = new HashMap<>();
		csMap.put("clinicalCourse", ENTIRE_TERM_CASE_SENSITIVE);
		csMap.put("bodyStructure", CASE_INSENSITIVE );
		csMap.put("substance", INITIAL_CHARACTER_CASE_INSENSITIVE );
		Map<String, Set<DescriptionPojo>> slotDescriptionValuesMap = TestDataHelper.constructSlotDescriptionValuesMap(slotValueMap, csMap, DescriptionType.FSN);
		LexicalTemplateTransformService.transformDescriptions(lexicalTemplates, descriptions, 
				slotDescriptionValuesMap);
		assertEquals(2, descriptions.size());
		assertEquals("Sudden onset AND short duration contact dermatitis of bone of C5-C7 caused by DPB1*1401 (disorder)", descriptions.get(0).getTerm());
		assertEquals(ENTIRE_TERM_CASE_SENSITIVE, descriptions.get(0).getCaseSignificance());
		
	}
	
	@Test
	public void testTermWithCaseInSensitive() throws ServiceException {
		SortedMap<String, String> slotValueMap = new TreeMap<>();
		slotValueMap.put("clinicalCourse", "Chronic aggressive (qualifier value)");
		slotValueMap.put("bodyStructure", "Transplant (body structure)");
		slotValueMap.put("substance", "Blood material (substance)");
		Map<String, CaseSignificance> csMap = new HashMap<>();
		csMap.put("clinicalCourse", CASE_INSENSITIVE);
		csMap.put("bodyStructure", CASE_INSENSITIVE );
		csMap.put("substance", CASE_INSENSITIVE );
		Map<String, Set<DescriptionPojo>> slotDescriptionValuesMap = TestDataHelper.constructSlotDescriptionValuesMap(slotValueMap, csMap, DescriptionType.FSN);
		LexicalTemplateTransformService.transformDescriptions(lexicalTemplates, descriptions, 
				slotDescriptionValuesMap);
		assertEquals(2, descriptions.size());
		assertEquals("Chronic aggressive contact dermatitis of transplant caused by blood material (disorder)", descriptions.get(0).getTerm());
		assertEquals(CASE_INSENSITIVE, descriptions.get(0).getCaseSignificance());
		
	}
}
