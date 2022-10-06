package org.ihtsdo.otf.transformationandtemplate.service.template;

import org.ihtsdo.otf.utils.StringUtils;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.DescriptionPojo;
import org.ihtsdo.otf.transformationandtemplate.service.ConstantStrings;
import org.ihtsdo.otf.transformationandtemplate.service.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snomed.authoringtemplate.domain.CaseSignificance;
import org.snomed.authoringtemplate.domain.Description;
import org.snomed.authoringtemplate.domain.DescriptionType;
import org.snomed.authoringtemplate.domain.LexicalTemplate;
import org.snomed.authoringtemplate.domain.LexicalTemplate.ReplacementRule;

import java.util.*;
import java.util.stream.Collectors;

import static org.ihtsdo.otf.rest.client.terminologyserver.pojo.DescriptionPojo.Acceptability.PREFERRED;
import static org.snomed.authoringtemplate.domain.CaseSignificance.ENTIRE_TERM_CASE_SENSITIVE;
import static org.snomed.authoringtemplate.domain.CaseSignificance.INITIAL_CHARACTER_CASE_INSENSITIVE;

public class LexicalTemplateTransformService {

	private static final String TERM_SLOT_INDICATOR = "$";
	private static final Logger LOGGER = LoggerFactory.getLogger(LexicalTemplateTransformService.class);

	public static List<Description> transformDescriptions(List<LexicalTemplate> lexicalTemplates,
			List<Description> descriptions, Map<String, Set<DescriptionPojo>> slotValueMap) throws ServiceException {
		
		Map<String, LexicalTemplate> lexicalTemplateMap = new HashMap<>();
		for (LexicalTemplate template : lexicalTemplates) {
			lexicalTemplateMap.put(template.getName(), template);
		}
		
		Map<String, DescriptionPojo> slotFsnValueMap = new HashMap<>();
		for (String slot : slotValueMap.keySet()) {
			Optional<DescriptionPojo> optionalFsnPojo = slotValueMap.get(slot).stream()
					.filter(DescriptionPojo::isActive)
					.filter(v -> v.getType() == DescriptionPojo.Type.FSN)
					.findFirst();
			if (optionalFsnPojo.isPresent()) {
				slotFsnValueMap.put(slot, optionalFsnPojo.get());
			} else {
				LOGGER.warn("No FSN found for slot {}", slot);
			}
		}
		Map<String, List<DescriptionPojo>> slotPtValueMap = new HashMap<>();
		for (String slot : slotValueMap.keySet()) {
			List<DescriptionPojo> ptPojos = slotValueMap.get(slot).stream()
					.filter(DescriptionPojo::isActive)
					.filter(v -> v.getType() == DescriptionPojo.Type.SYNONYM)
					.filter(v -> v.getAcceptabilityMap().containsValue(PREFERRED))
					.collect(Collectors.toList());
			slotPtValueMap.put(slot, ptPojos);
		}
		return performTransformation(descriptions, slotFsnValueMap, slotPtValueMap, lexicalTemplateMap);
	}
	
	private static List<Description> performTransformation(List<Description> descriptions,
			Map<String, DescriptionPojo> slotFsnValueMap,
			Map<String, List<DescriptionPojo>> slotPtValueMap,
			Map<String, LexicalTemplate> lexicalTemplateMap) throws ServiceException {
		
		List<Description> updated = new ArrayList<>();
		List<Description> pts = descriptions.stream()
				.filter(d -> DescriptionType.SYNONYM == d.getType())
				.filter(d -> d.getAcceptabilityMap() != null)
				.filter(d -> d.getAcceptabilityMap().containsValue(ConstantStrings.PREFERRED))
				.collect(Collectors.toList());
		
		List<Description> others = new ArrayList<>(descriptions);
		others.removeAll(pts);
		for (Description description : others) {
			Description toTransform = description.clone();
			String term = toTransform.getTermTemplate();
			Set<String> termSlotNames = TemplateUtil.getSlots(Collections.singleton(term));
			Map<String, DescriptionPojo.CaseSignificance> termAndCaseSignificanceMap = new HashMap<>();
			List<String> sortedSlotNames = sortSlotNames(lexicalTemplateMap, termSlotNames);
			for (String slotName : sortedSlotNames) {
				LexicalTemplate template = lexicalTemplateMap.get(slotName);
				String termSlot = TERM_SLOT_INDICATOR + slotName + TERM_SLOT_INDICATOR;
				DescriptionPojo fsnPojo;
				if (template == null) {
					// Additional slot
					fsnPojo = slotFsnValueMap.get(slotName); 
					String slotValue = TemplateUtil.getDescriptionFromFSN(fsnPojo);
					term = term.replace(termSlot, slotValue);
					termAndCaseSignificanceMap.put(slotValue, fsnPojo.getCaseSignificance());
				} else {
					term = applyFsnTransformation(term, template, slotFsnValueMap, termAndCaseSignificanceMap, termSlot);
				}
			}
			updateFinalCaseSignificanceId(term, termAndCaseSignificanceMap, toTransform);
			updated.add(toTransform);
		}
		updated.addAll(transformPreferredTerms(pts, slotFsnValueMap, slotPtValueMap, lexicalTemplateMap));
		return updated;
	}

	private static List<String> sortSlotNames(Map <String, LexicalTemplate> lexicalTemplateMap, Set <String> termSlotNames) {
		List<String> orderedTermSlotNames = new ArrayList<>();
		List <LexicalTemplate> orderedLexicalTemplates = new ArrayList<>();
		List<String> nonOrderedSlots = new ArrayList<>();
		for (String slotName : termSlotNames) {
			LexicalTemplate template = lexicalTemplateMap.get(slotName);
			if (template != null && template.getOrder() != null) {
				orderedLexicalTemplates.add(template);
			} else {
				nonOrderedSlots.add(slotName);
			}
		}
		if (orderedLexicalTemplates.size() != 0) {
			orderedLexicalTemplates.sort(Comparator.comparing(LexicalTemplate::getOrder));
			orderedLexicalTemplates.forEach(item -> orderedTermSlotNames.add(item.getName()));
		}
		orderedTermSlotNames.addAll(nonOrderedSlots);
		return orderedTermSlotNames;
	}

	private static List<Description> transformPreferredTerms(List<Description> pts,
			Map<String, DescriptionPojo> slotFsnValueMap, Map<String, List<DescriptionPojo>> slotPtValueMap, Map<String, LexicalTemplate> lexicalTemplateMap) {

		List<Description> result = new ArrayList<>();
		for (Description description : pts) {
			List<Description> temp = new ArrayList<>();
			for (String refsetId : description.getAcceptabilityMap().keySet()) {
				Description pt = description.clone();
				Map<String, String> acceptabilityMap = new HashMap<>();
				acceptabilityMap.put(refsetId, ConstantStrings.PREFERRED);
				pt.setAcceptabilityMap(acceptabilityMap);
				String term = pt.getTermTemplate();
				Set<String> termSlotNames = TemplateUtil.getSlots(Collections.singleton(term));
				Map<String, DescriptionPojo.CaseSignificance> termAndCaseSignificanceMap = new HashMap<>();
				List<String> sortedSlotNames = sortSlotNames(lexicalTemplateMap, termSlotNames);
				for (String slotName : sortedSlotNames) {
					LexicalTemplate template = lexicalTemplateMap.get(slotName);
					String termSlot = TERM_SLOT_INDICATOR + slotName + TERM_SLOT_INDICATOR;
					DescriptionPojo fsnPojo;
					if (template == null) {
						// Additional slot
						fsnPojo = slotFsnValueMap.get(slotName); 
						String slotValue = TemplateUtil.getDescriptionFromFSN(fsnPojo);
						term = term.replace(termSlot, slotValue);
						termAndCaseSignificanceMap.put(slotValue, fsnPojo.getCaseSignificance());
					} else {
						term = applyPreferredTermTransformation(term, template, slotPtValueMap, refsetId, termAndCaseSignificanceMap, termSlot);
					}
				}
				updateFinalCaseSignificanceId(term, termAndCaseSignificanceMap, pt);
				temp.add(pt);
			}
			// Check and merge if the term is same
			Set<String> ptTerms = temp.stream().map(Description::getTerm).collect(Collectors.toSet());
			if (ptTerms.size() == 1) {
				Map<String, String> acceptabilityMap = new HashMap<>();
				for (String key : description.getAcceptabilityMap().keySet()) {
					acceptabilityMap.put(key, ConstantStrings.PREFERRED);
				}
				Description mergedPt = temp.get(0);
				mergedPt.setAcceptabilityMap(acceptabilityMap);
				result.add(mergedPt);
			} else {
				result.addAll(temp);
			}
		}
		return result;
	}

	private static String applyPreferredTermTransformation(String term, LexicalTemplate template,
			Map<String, List<DescriptionPojo>> slotPtValueMap, String refsetId, Map<String, DescriptionPojo.CaseSignificance> termAndCaseSignificanceMap, String termSlot) {
		
		DescriptionPojo ptPojo = null;
		if (slotPtValueMap.containsKey(template.getTakeFSNFromSlot())) {
			for (DescriptionPojo pojo : slotPtValueMap.get(template.getTakeFSNFromSlot())) {
				if (pojo.getAcceptabilityMap().containsKey(refsetId)) {
					if (PREFERRED == pojo.getAcceptabilityMap().get(refsetId)) {
						ptPojo = pojo;
						break;
					}
				}
			}
		}
		if (ptPojo == null) {
			term = performTermReplacementWhenSlotIsAbsent(term, template);
		} else {
			if (isAdditionalTermReplacementRequired(template, ptPojo)) {
				term = performTermReplacementWithSlotValuesMatched(term, template, ptPojo);
			} else {
				String slotValue = TemplateUtil.getDescriptionFromPT(ptPojo);
				if (template.getRemoveParts() != null && !template.getRemoveParts().isEmpty()) {
					for (String partToRemove : template.getRemoveParts()) {
						slotValue = slotValue.replaceAll(partToRemove, "");
					}
					if (CaseSignificance.CASE_INSENSITIVE.name().equals(ptPojo.getCaseSignificance().name())) {
						slotValue = StringUtils.deCapitalize(slotValue);
					}
				}
				termAndCaseSignificanceMap.put(slotValue, ptPojo.getCaseSignificance());
				term = term.replace(termSlot, slotValue);
			}
		}
		return term;
	}

	private static void updateFinalCaseSignificanceId(String term, Map<String, DescriptionPojo.CaseSignificance> termAndCaseSignificanceMap, Description description) {
		// Remove extra spaces between words
		term = term.replaceAll("\\s+"," ").trim();
		Set<String> slotValues = new HashSet<>(termAndCaseSignificanceMap.keySet());
		for (String value : slotValues) {
			if (term.startsWith(value) && ENTIRE_TERM_CASE_SENSITIVE.name().equals(termAndCaseSignificanceMap.get(value).name())) {
				description.setCaseSignificance(ENTIRE_TERM_CASE_SENSITIVE);
				break;
			}
		}
		if (ENTIRE_TERM_CASE_SENSITIVE != description.getCaseSignificance()) {
			term = StringUtils.capitalize(term);
			if (termAndCaseSignificanceMap.values().stream()
					.anyMatch(caseSignificance -> caseSignificance.name().equals(ENTIRE_TERM_CASE_SENSITIVE.name()) || caseSignificance.name().equals(INITIAL_CHARACTER_CASE_INSENSITIVE.name()))) {
				description.setCaseSignificance(INITIAL_CHARACTER_CASE_INSENSITIVE);
			}
		} 
		description.setTerm(term);
	}

	private static String applyFsnTransformation(String term, LexicalTemplate template,
			Map<String, DescriptionPojo> slotFsnValueMap, Map<String, DescriptionPojo.CaseSignificance> termAndCaseSignificanceMap, String termSlot) {

		DescriptionPojo fsnPojo = slotFsnValueMap.get(template.getTakeFSNFromSlot());
		if (fsnPojo == null) {
			term = performTermReplacementWhenSlotIsAbsent(term, template);
		} else {
			if (isAdditionalTermReplacementRequired(template, fsnPojo)) {
				term = performTermReplacementWithSlotValuesMatched(term, template, fsnPojo);
			} else {
				String slotValue = TemplateUtil.getDescriptionFromFSN(fsnPojo);
				if (template.getRemoveParts() != null && !template.getRemoveParts().isEmpty()) {
					for (String partToRemove : template.getRemoveParts()) {
						slotValue = slotValue.replaceAll(partToRemove, "");
					}
					if (CaseSignificance.CASE_INSENSITIVE.name().equals(fsnPojo.getCaseSignificance().name())) {
						slotValue = StringUtils.deCapitalize(slotValue);
					}
				}
				termAndCaseSignificanceMap.put(slotValue, fsnPojo.getCaseSignificance());
				term = term.replace(termSlot, slotValue);
			}
		}
		return term;
	}

	private static boolean isAdditionalTermReplacementRequired(LexicalTemplate template, DescriptionPojo description) {
		if (template.getTermReplacements() != null && !template.getTermReplacements().isEmpty()) {
			for (ReplacementRule rule : template.getTermReplacements()) {
				if (rule.getSlotValues() != null) {
					if (rule.getSlotValues().contains(description.getConceptId())) {
						return true;
					}
				}
				if (rule.getSlotTermStartsWith() != null &&
					description.getTerm().startsWith(rule.getSlotTermStartsWith())) {
					return true;
				}
			}
		} 
		return false;
	}

	private static String performTermReplacementWithSlotValuesMatched(String term, LexicalTemplate template, DescriptionPojo description) {
		String result = term;
		if (template.getTermReplacements() != null) {
			for (ReplacementRule rule : template.getTermReplacements()) {
				if ((rule.getSlotValues() != null && rule.getSlotValues().contains(description.getConceptId())) ||
					(rule.getSlotTermStartsWith() != null && description.getTerm().startsWith(rule.getSlotTermStartsWith()))) {
					result = result.replace(rule.getExistingTerm(), rule.getReplacement());
					LOGGER.debug(term + " is replaced by " + result);
				}
			}
		} 
		return result;
	}

	private static String performTermReplacementWhenSlotIsAbsent(String term, LexicalTemplate template) {
		String result = term;
		if (template.getTermReplacements() == null || template.getTermReplacements().isEmpty()) {
			// Perform default replacement
			result = result.replace(TERM_SLOT_INDICATOR + template.getName() + TERM_SLOT_INDICATOR, "");
		} else {
			for (ReplacementRule rule : template.getTermReplacements()) {
				if (rule.isSlotAbsent()) {
					result = result.replace(rule.getExistingTerm(), rule.getReplacement());
					break;
				}
			}
		}
		LOGGER.debug("{} is replaced due to absent slot by {}", term, result);
		return result;
	}
}
