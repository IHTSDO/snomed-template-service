package org.ihtsdo.otf.transformationandtemplate.service;

import org.apache.commons.lang.StringUtils;
import org.ihtsdo.otf.transformationandtemplate.service.exception.ServiceException;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snomed.authoringtemplate.domain.*;
import org.snomed.authoringtemplate.domain.logical.Attribute;
import org.snomed.authoringtemplate.domain.logical.AttributeGroup;
import org.snomed.authoringtemplate.domain.logical.LogicalTemplate;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
	
	
	public static Set<String> getSlots(Collection<String> termTemplates) {
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
	
	public static Map<String, String> getLexicalTermNameSlotMap(ConceptTemplate conceptTemplate) {
		Map<String, String> termNameSlotMap = new HashMap<>();
		for (LexicalTemplate lexical : conceptTemplate.getLexicalTemplates()) {
			if (lexical.getTakeFSNFromSlot() != null) {
				termNameSlotMap.put(lexical.getName(), lexical.getTakeFSNFromSlot());
			}
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
			Set<String> slots = getSlots(Collections.singleton(termPattern));
			if (slots.isEmpty()) {
				continue;
			}
			Pattern pattern = TemplateUtil.constructTermPattern(termPattern);
			result.putIfAbsent(pattern, slots);
		}
		return result;
	}
	
	public static Map<String, Set<ConceptMiniPojo>> getSlotNameToAttributeValueMap(Map<String, Attribute> slotNameToAttributeMap, ConceptPojo conceptPojo) {
		Map<String, Set<String>> attributeIdToSlotsMap = new HashMap<>();
		for (String slotName : slotNameToAttributeMap.keySet()) {
			attributeIdToSlotsMap.computeIfAbsent(slotNameToAttributeMap.get(slotName).getType(), slots -> new HashSet<>()).add(slotName);
		}
		
		Map<String, Set<ConceptMiniPojo>> attributeTypeToTargetValuesMap = new HashMap<>();
		
		for (AxiomPojo axiom : conceptPojo.getClassAxioms()) {
			for (RelationshipPojo relationship : axiom.getRelationships()) {
				if (!relationship.isActive()) {
					continue;
				}
				attributeTypeToTargetValuesMap.computeIfAbsent(relationship.getType().getConceptId(), values -> new HashSet<ConceptMiniPojo>())
					.add(relationship.getTarget());
				
			}
		}
		
		Map<String , Set<ConceptMiniPojo>> result = new HashMap<>();
		for (String attributeTyeId : attributeIdToSlotsMap.keySet()) {
			for (String slotName : attributeIdToSlotsMap.get(attributeTyeId)) {
				result.put(slotName, attributeTypeToTargetValuesMap.get(attributeTyeId));
			}
		}
		return result;
	}
	
	public static Map<String, Attribute> getSlotToAttributeMap(LogicalTemplate logical, boolean includeOptionalAttribute) {
		
		Map<String, Attribute> slotToAttributeMap = new HashMap<>();
		for (Attribute attribute : logical.getUngroupedAttributes()) {
			if (!includeOptionalAttribute && ("0".equals(attribute.getCardinalityMin()))) {
				continue;
			}
			if (attribute.getSlotName() != null) {
				slotToAttributeMap.put(attribute.getSlotName(), attribute);
			}
		}

		for (AttributeGroup attributeGrp : logical.getAttributeGroups()) {
			for (Attribute attribute : attributeGrp.getAttributes()) {
				if (!includeOptionalAttribute && ("0".equals(attribute.getCardinalityMin()))) {
					continue;
				}
				if (attribute.getSlotName() != null) {
					slotToAttributeMap.put(attribute.getSlotName(), attribute);
				}
			}
		}
		return slotToAttributeMap;
	}
	
	public static List<SimpleSlot> getSlotsRequiringInput(Collection<Relationship> relationships) {
		return relationships.stream().filter(r -> isSlotRequiringInput(r.getTargetSlot()))
				.map(Relationship::getTargetSlot).collect(Collectors.toList());
	}

	private static boolean isSlotRequiringInput(SimpleSlot targetSlot) {
		return targetSlot != null && targetSlot.getSlotReference() == null;
	}

	public static String getDescriptionFromFSN(String fsn) {
		if (fsn != null) {
			Matcher matcher = FSN_PATTERN.matcher(fsn);
			if (matcher.find()) {
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

	public static void validateTermSlots(ConceptTemplate conceptTemplate, boolean checkAdditionalSlots) throws ServiceException {
		// Check term slots can be found in replacement slots
		Set<String> termTemplates = getTermTemplates(conceptTemplate);
		Set<String> termSlots = getSlots(termTemplates);
		Map<String, String> lexicalTermNameSlotMap = getLexicalTermNameSlotMap(conceptTemplate);
		Set<String> slotsDefinedInLexical = new HashSet<>(lexicalTermNameSlotMap.keySet());
		if (checkAdditionalSlots) {
			slotsDefinedInLexical.addAll(conceptTemplate.getAdditionalSlots());
		}

		// Check term names in term templates are defined in the lexical templates
		if (!slotsDefinedInLexical.containsAll(termSlots)) {
			Set<String> slotsNotFound = new HashSet<>(termSlots);
			slotsNotFound.removeAll(slotsDefinedInLexical);
			throw new ServiceException(String.format("Template %s has term slot %s that is not defined in the lexical template",
					conceptTemplate.getName(), slotsNotFound));
		}
		List<Relationship> relationships = conceptTemplate.getConceptOutline().getClassAxioms().stream().findFirst().get().getRelationships();
		Set<String> logicalSlots = getSlotsRequiringInput(relationships)
				.stream().map(SimpleSlot::getSlotName).collect(Collectors.toSet());

		Set<String> logicalSlotsReferencedInLexical = new HashSet<>(lexicalTermNameSlotMap.values());
		if (!logicalSlots.containsAll(logicalSlotsReferencedInLexical)) {
			Set<String> slotsNotFound = new HashSet<>(logicalSlotsReferencedInLexical);
			slotsNotFound.removeAll(logicalSlots);
			throw new ServiceException(String.format("Template %s has slot referenced in the lexical template %s but doesn't exist in the logical template",
					conceptTemplate.getName(), slotsNotFound));
		}
	}
	
	
	public static boolean isOptional(Relationship relationship) {
		return relationship.getCardinalityMin() == null || "0".equals(relationship.getCardinalityMin());
	}

	public static String getDescriptionFromPT(DescriptionPojo ptPojo) {
		String term = ptPojo.getTerm();
		if (!CaseSignificance.ENTIRE_TERM_CASE_SENSITIVE.name().equals(ptPojo.getCaseSignificance())) {
			return StringUtils.uncapitalize(term);
		} 
		return term;
	}

	public static String getDescriptionFromFSN(DescriptionPojo fsnPojo) {
		String term = getDescriptionFromFSN(fsnPojo.getTerm());
		if (!CaseSignificance.ENTIRE_TERM_CASE_SENSITIVE.name().equals(fsnPojo.getCaseSignificance())) {
			return StringUtils.uncapitalize(term);
		} 
		return term;
	}
	
	public static String constructRangeValidationEcl(String slotRangeEcl, List<String> conceptIds) {
		StringBuilder eclBuilder = new StringBuilder();
		eclBuilder.append("(");
		eclBuilder.append(slotRangeEcl);
		eclBuilder.append(") AND (");
		
		for (int i = 0; i< conceptIds.size(); i++) {
			if (i > 0) {
				eclBuilder.append(" OR ");
			}
			eclBuilder.append(conceptIds.get(i));
		}
		eclBuilder.append(")");
		return eclBuilder.toString();
	}
}
