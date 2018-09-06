package org.ihtsdo.otf.authoringtemplate.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.snomed.authoringtemplate.domain.Description;
import org.snomed.authoringtemplate.domain.LexicalTemplate;

public class LexicalTemplateTransformService {
	private static final String TERM_SLOT_INDICATOR = "$";
	public static void transformDescriptions(List<LexicalTemplate> lexicalTemplates,
			List<Description> descriptions, Map<String, String> slotValueMap) {
		
		Map<String, LexicalTemplate> lexicalTemplateMap = new HashMap<>();
		for (LexicalTemplate template : lexicalTemplates) {
			lexicalTemplateMap.put(template.getName(), template);
		}
		
		for (Description description : descriptions) {
			String term = description.getTermTemplate();
			Set<String> termSlotNames = TemplateUtil.getSlots(term);
			for (String slotName : termSlotNames) {
				LexicalTemplate template = lexicalTemplateMap.get(slotName);
				if (template == null) {
					//Additional slot
					term = term.replace(TERM_SLOT_INDICATOR + slotName + TERM_SLOT_INDICATOR, slotValueMap.get(slotName));
					continue;
				}
				if (!slotValueMap.containsKey(template.getTakeFSNFromSlot())) {
					if (template.getRemoveFromTermTemplateWhenSlotAbsent() != null) {
						boolean capitalize = false;
						for (String part : template.getRemoveFromTermTemplateWhenSlotAbsent()) {
							if (term.startsWith(part)) {
								capitalize = true;
							}
							term = term.replace(part, "");
							if (capitalize) {
								term = StringUtils.capitalize(term.trim());
							}
						}
					}
				} else {
					String slotValue = slotValueMap.get(template.getTakeFSNFromSlot());
					if (slotValue != null) {
						if (template.getRemoveParts() != null) {
							for (String partToRemove : template.getRemoveParts()) {
								slotValue = slotValue.replaceAll(partToRemove, "");
							}
						}
						term = term.replace(TERM_SLOT_INDICATOR + slotName + TERM_SLOT_INDICATOR, slotValue.toLowerCase());
					}
				}
			}
			//remove extra spaces between words
			term = term.replaceAll("\\s+"," ").trim();
			description.setTerm(term);
		}
	}
}
