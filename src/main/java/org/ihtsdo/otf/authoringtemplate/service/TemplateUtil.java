package org.ihtsdo.otf.authoringtemplate.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.ihtsdo.otf.rest.client.snowowl.pojo.ConceptMiniPojo;
import org.ihtsdo.otf.rest.client.snowowl.pojo.ConceptPojo;
import org.ihtsdo.otf.rest.client.snowowl.pojo.RelationshipPojo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snomed.authoringtemplate.domain.ConceptTemplate;
import org.snomed.authoringtemplate.domain.Description;
import org.snomed.authoringtemplate.domain.DescriptionType;
import org.snomed.authoringtemplate.domain.LexicalTemplate;
import org.snomed.authoringtemplate.domain.Relationship;
import org.snomed.authoringtemplate.domain.SimpleSlot;
import org.snomed.authoringtemplate.domain.logical.Attribute;
import org.snomed.authoringtemplate.domain.logical.AttributeGroup;
import org.snomed.authoringtemplate.domain.logical.LogicalTemplate;

public class TemplateUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(TemplateUtil.class);
	
	private static Pattern FSN_PATTERN = Pattern.compile("(.*)(\\(.*\\))");

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
	
	
	public static Set<String> getSlots(String ... termTemplates) {
		Set<String> slots = new HashSet<>();
		for (String termTemplate : termTemplates) {
			Matcher matcher = TemplateService.TERM_SLOT_PATTERN.matcher(termTemplate);
			while (matcher.find()) {
				slots.add(matcher.group(1));
			}
		}
		return slots;
	}
	
	public static Set<String> getTermTemplates(ConceptTemplate conceptTemplate) {
		Set<String> termTemplates = new HashSet<>();
		for (Description desc : conceptTemplate.getConceptOutline().getDescriptions()) {
			if (desc.getTermTemplate() != null) {
				termTemplates.add(desc.getTermTemplate());
			}
		}
		return termTemplates;
	}
	
	public static Map<String,String> getLexicalTermNameSlotMap(ConceptTemplate conceptTemplate) {
		Map<String, String> termNameSlotMap = new HashMap<>();
		for (LexicalTemplate lexical : conceptTemplate.getLexicalTemplates()) {
			termNameSlotMap.put(lexical.getName(), lexical.getTakeFSNFromSlot());
		}
		return termNameSlotMap;
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
	
	public static Map<Pattern, Set<String>> compilePatterns(Set<String> termTemplates) {
		Map<Pattern, Set<String>> result = new HashMap<>();
		for (String termPattern : termTemplates) {
			Set<String> slots = getSlots(termPattern);
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
		for (RelationshipPojo pojo : conceptPojo.getRelationships()) {
			if (!pojo.isActive()) {
				continue;
			}
			if (attributeSlots.keySet().contains(pojo.getType().getConceptId())) {
				for (String slot : attributeSlots.get(pojo.getType().getConceptId())) {
					result.putIfAbsent(slot, pojo.getTarget());
				}
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
	
	public static List<SimpleSlot> getSlotsRequiringInput(List<Relationship> relationships) {
		return relationships.stream().filter(r -> isSlotRequiringInput(r.getTargetSlot()))
				.map(Relationship::getTargetSlot).collect(Collectors.toList());
	}

	private static boolean isSlotRequiringInput(SimpleSlot targetSlot) {
		return targetSlot != null && targetSlot.getSlotReference() == null;
	}

	public static String getDescriptionFromFSN(String fsn) {
		if (fsn != null) {
			Matcher matcher = FSN_PATTERN.matcher(fsn);
			while (matcher.find()) {
				return matcher.group(1).trim();
			}
		}
		return fsn;
	}


	public static Set<String> getAttributeTypes(LogicalTemplate logical) {
		Set<String> result = new HashSet<>();
		for (Attribute attr : logical.getUngroupedAttributes()) {
			result.add(attr.getType());
		}

		for (AttributeGroup attributeGrp : logical.getAttributeGroups()) {
			for (Attribute attr : attributeGrp.getAttributes()) {
				result.add(attr.getType());
			}
		}
		return result;
	}
}
