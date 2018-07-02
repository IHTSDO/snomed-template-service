package org.ihtsdo.otf.authoringtemplate.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.snomed.authoringtemplate.domain.*;
import org.snomed.authoringtemplate.domain.logical.*;
import org.ihtsdo.otf.rest.client.snowowl.pojo.ConceptMiniPojo;
import org.ihtsdo.otf.rest.client.snowowl.pojo.ConceptPojo;
import org.ihtsdo.otf.rest.client.snowowl.pojo.DescriptionPojo;
import org.ihtsdo.otf.rest.client.snowowl.pojo.RelationshipPojo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TemplateUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(TemplateUtil.class);

	public static Pattern constructTermPattern(String termTemplate) {
		String result = termTemplate;
		//$actionTerm$ of $procSiteTerm$ using computed tomography guidance (procedure)
		Matcher matcher = TemplateService.TERM_SLOT_PATTERN.matcher(termTemplate);
		while (matcher.find()) {
			String termSlot = matcher.group();
			result = result.replace(termSlot, ".+");
		}
		result = result.replace("(", "\\(");
		result = result.replace(")", "\\)");
		result = result.replace(".+","(.+)");
		LOGGER.info("term pattern regex=" + result);
		Pattern pattern = Pattern.compile(result);
		return pattern;
	}
	
	
	
	public static List<String> getSlots(String termTemplate) {
		List<String> slots = new ArrayList<>();
		Matcher matcher = TemplateService.TERM_SLOT_PATTERN.matcher(termTemplate);
		while (matcher.find()) {
			slots.add(matcher.group());
		}
		return slots;
	}
	
	public static Set<String> getTermTemplates(ConceptTemplate conceptTemplate, DescriptionType type) {
		Set<String> termTemplates = new HashSet<>();
		for (Description desc : conceptTemplate.getConceptOutline().getDescriptions()) {
			if ((type == desc.getType()) && desc.getTermTemplate() != null) {
				termTemplates.add(desc.getTermTemplate());
			}
		}
		return termTemplates;
	}
	
	public static Map<Pattern, List<String>> compilePatterns(Set<String> termTemplates) {
		Map<Pattern, List<String>> result = new HashMap<>();
		for (String termPattern : termTemplates) {
			List<String> slots = TemplateUtil.getSlots(termPattern);
			if (slots.isEmpty()) {
				continue;
			}
			Pattern pattern = TemplateUtil.constructTermPattern(termPattern);
			result.putIfAbsent(pattern, slots);
		}
		return result;
	}
	
	public static void mapSlots(Map<Pattern, List<String>> termTemplatePatterns, String term, Map<String, String> result) {
		for (Pattern termPattern : termTemplatePatterns.keySet()) {
			 Matcher matcher = termPattern.matcher(term);
			 if (matcher.matches()) {
				 List<String> slots = termTemplatePatterns.get(termPattern);
				if (matcher.groupCount() == slots.size()) {
					 for (int i =0; i < matcher.groupCount(); i++) {
						 result.putIfAbsent(slots.get(i), matcher.group(i+1));
					 }
				 }
			 }
		}
	}
	
	public static Map<String, ConceptMiniPojo> getAttributeSlotValueMap(Map<String, Set<String>> attributeSlots, ConceptPojo conceptPojo) {
		Map<String, ConceptMiniPojo> result = new HashMap<>();
		List<RelationshipPojo> statedRels = conceptPojo.getRelationships().stream()
				.filter(r -> r.isActive())
				.filter(r -> r.getCharacteristicType().equals(Constants.STATED))
				.collect(Collectors.toList());
		
		for (RelationshipPojo pojo : statedRels) {
			if (attributeSlots.keySet().contains(pojo.getType().getConceptId())) {
				for (String slot : attributeSlots.get(pojo.getType().getConceptId())) {
					result.putIfAbsent(slot, pojo.getTarget());
				}
			}
		}
		return result;
	}

	public static Map<String, String> getSlotValueMap(Map<Pattern, List<String>> fsnTemplatePatterns, 
			Map<Pattern, List<String>> synonymTemplatePatterns, ConceptPojo conceptPojo) {
		Map<String, String> result = new HashMap<>();
		for (DescriptionPojo pojo : conceptPojo.getDescriptions()) {
			if (DescriptionType.FSN.name().equals(pojo.getType())) {
				TemplateUtil.mapSlots(fsnTemplatePatterns, pojo.getTerm(), result);
			} 
		}
		
		for (DescriptionPojo pojo : conceptPojo.getDescriptions()) {
			if (DescriptionType.SYNONYM.name().equals(pojo.getType())) {
				TemplateUtil.mapSlots(synonymTemplatePatterns, pojo.getTerm(), result);
			}
		}
		return result;
	}
	
	
	public static Map<String, Set<String>>  getAttributeTypeSlotMap(LogicalTemplate logical) {
		Map<String, Set<String>> attributeSlots = new HashMap<>();
		for (Attribute attr : logical.getUngroupedAttributes()) {
			if (attr.getSlotName() != null) {
				if (!attributeSlots.containsKey(attr.getType())) {
					attributeSlots.put(attr.getType(), new HashSet<>());
				}
				attributeSlots.get(attr.getType()).add(attr.getSlotName());
			}
		}

		for (AttributeGroup attributeGrp : logical.getAttributeGroups()) {
			for (Attribute attr : attributeGrp.getAttributes()) {
				if (attr.getSlotName() != null) {
					if (!attributeSlots.containsKey(attr.getType())) {
						attributeSlots.put(attr.getType(), new HashSet<>());
					}
					attributeSlots.get(attr.getType()).add(attr.getSlotName());
				}
			}
		}
		return attributeSlots;
	}
	
}
