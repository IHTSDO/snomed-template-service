package org.ihtsdo.otf.authoringtemplate.service;

import static org.snomed.authoringtemplate.domain.CaseSignificance.ENTIRE_TERM_CASE_SENSITIVE;
import static org.snomed.authoringtemplate.domain.CaseSignificance.INITIAL_CHARACTER_CASE_INSENSITIVE;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.ihtsdo.otf.authoringtemplate.service.exception.ServiceException;
import org.ihtsdo.otf.rest.client.snowowl.pojo.DescriptionPojo;
import org.snomed.authoringtemplate.domain.Description;
import org.snomed.authoringtemplate.domain.DescriptionType;
import org.snomed.authoringtemplate.domain.LexicalTemplate;

public class LexicalTemplateTransformService {
	private static final String TERM_SLOT_INDICATOR = "$";
	public static void transformDescriptions(List<LexicalTemplate> lexicalTemplates,
			List<Description> descriptions, Map<String, Set<DescriptionPojo>> slotValueMap) throws ServiceException {
		
		Map<String, LexicalTemplate> lexicalTemplateMap = new HashMap<>();
		for (LexicalTemplate template : lexicalTemplates) {
			lexicalTemplateMap.put(template.getName(), template);
		}
		
		Map<String, DescriptionPojo> slotFsnValueMap = new HashMap<>();
		for (String slot : slotValueMap.keySet()) {
			DescriptionPojo fsnPojo = slotValueMap.get(slot).stream()
					.filter(v -> v.isActive())
					.filter(v -> DescriptionType.FSN.name().equals(v.getType()))
					.findFirst().get();
			slotFsnValueMap.put(slot, fsnPojo);
		}
		performTransformation(descriptions, slotFsnValueMap, lexicalTemplateMap);
	}
	
	private static void performTransformation(List<Description> descriptions,
			Map<String, DescriptionPojo> slotFsnValueMap, Map<String, LexicalTemplate> lexicalTemplateMap) throws ServiceException {
		
		for (Description description : descriptions) {
			String term = description.getTermTemplate();
			SortedSet<String> termSlotNames = TemplateUtil.getSlots(term);
			SortedSet<String> caseSignificanceIdSet = new TreeSet<>();
			for (String slotName : termSlotNames) {
				LexicalTemplate template = lexicalTemplateMap.get(slotName);
				String termSlot = TERM_SLOT_INDICATOR + slotName + TERM_SLOT_INDICATOR;
				DescriptionPojo fsnPojo = null;
				if (template == null) {
					//Additional slot
					fsnPojo = slotFsnValueMap.get(slotName); 
					term = term.replace(termSlot, TemplateUtil.getDescriptionFromFSN(fsnPojo));
					caseSignificanceIdSet.add(fsnPojo.getCaseSignificance());
				} else {
					term = applyLexicalTemplateTransformation(term, template, slotFsnValueMap, caseSignificanceIdSet, termSlot);
				}
			}
			updateFinalCaseSignificanceId(term, caseSignificanceIdSet, description);
		}
	}

	private static void updateFinalCaseSignificanceId(String term, SortedSet<String> caseSignificanceIdSet,
			Description description) {
		//use caseSignificanceIdSet and case significance id from term template to determine the final value
		//remove extra spaces between words
		term = term.replaceAll("\\s+"," ").trim();
		if (ENTIRE_TERM_CASE_SENSITIVE.name().equals(caseSignificanceIdSet.first())) {
			description.setCaseSignificance(ENTIRE_TERM_CASE_SENSITIVE);
		} else {
			if (caseSignificanceIdSet.contains(ENTIRE_TERM_CASE_SENSITIVE.name())
				|| caseSignificanceIdSet.contains(INITIAL_CHARACTER_CASE_INSENSITIVE.name())) {
				
				description.setCaseSignificance(INITIAL_CHARACTER_CASE_INSENSITIVE);
			}
			term = StringUtils.capitalize(term);
		}
		description.setTerm(term);
	}

	private static String applyLexicalTemplateTransformation(String term, LexicalTemplate template, 
			Map<String, DescriptionPojo> slotFsnValueMap, Set<String> caseSignificanceIdSet, String termSlot) throws ServiceException {
		DescriptionPojo fsnPojo = slotFsnValueMap.get(template.getTakeFSNFromSlot());
		if (fsnPojo == null) {
			if (template.getRemoveFromTermTemplateWhenSlotAbsent() == null) {
				throw new ServiceException("No logical concept value found for non-optional slot " 
						+ template.getTakeFSNFromSlot() + " referenced in term slot " + termSlot);
			}
			for (String part : template.getRemoveFromTermTemplateWhenSlotAbsent()) {
				term = term.replace(part, "");
			}
		} else {
			String slotValue = TemplateUtil.getDescriptionFromFSN(fsnPojo);
			caseSignificanceIdSet.add(fsnPojo.getCaseSignificance());
			if (template.getRemoveParts() != null && !template.getRemoveParts().isEmpty()) {
				slotValue = fsnPojo.getTerm();
				for (String partToRemove : template.getRemoveParts()) {
					slotValue = slotValue.replaceAll(partToRemove, "");
				}
			}
			term = term.replace(termSlot, slotValue);
		}
		return term;
	}
}
