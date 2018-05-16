package org.ihtsdo.otf.authoringtemplate.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.ihtsdo.otf.authoringtemplate.domain.ConceptTemplate;
import org.ihtsdo.otf.authoringtemplate.domain.Description;
import org.ihtsdo.otf.authoringtemplate.domain.logical.Attribute;
import org.ihtsdo.otf.authoringtemplate.domain.logical.AttributeGroup;
import org.ihtsdo.otf.authoringtemplate.domain.logical.LogicalTemplate;
import org.ihtsdo.otf.authoringtemplate.service.exception.ServiceException;
import org.ihtsdo.otf.rest.client.RestClientException;
import org.ihtsdo.otf.rest.client.snowowl.SnowOwlRestClientFactory;
import org.ihtsdo.otf.rest.client.snowowl.pojo.SimpleDescriptionPojo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConceptTemplateSearchService {

	private static final String CARDINALITY_SEPARATOR = "..";

	@Autowired 
	private LogicalTemplateParserService logicalTemplateParser;
	
	@Autowired
	private SnowOwlRestClientFactory terminologyClientFactory;
	
	@Autowired
	private TemplateService templateService;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ConceptTemplateSearchService.class);

	private static final int MAX = 1000;
	
	public Set<String> searchConceptsByTemplate(String templateName, String branchPath, 
			boolean isLogicalOnly, boolean conformToTemplate) throws ServiceException {
		try {
			LOGGER.info("Search concepts for temlateName=" + templateName);
			ConceptTemplate conceptTemplate= templateService.load(templateName);
			LogicalTemplate logicalTemplate = logicalTemplateParser.parseTemplate(conceptTemplate.getLogicalTemplate());

			if (!isLogicalOnly) {
				Set<String> logicalResult = performLogicalSearch(logicalTemplate, branchPath, true);
				return performLexicalSearch(conceptTemplate, logicalResult, branchPath, conformToTemplate);
			} else {
				return performLogicalSearch(logicalTemplate, branchPath, conformToTemplate);
			}
		} catch (IOException | RestClientException e) {
			throw new ServiceException("Failed to parse template " + templateName, e);
		}
	}
	
	private Set<String> performLexicalSearch(ConceptTemplate conceptTemplate, Set<String> logicalMatched, String branchPath, boolean conformToTemplate) throws RestClientException {
		Set<String> result = new HashSet<>();
		// TODO search by lexical template
//		List<LexicalTemplate> lexicalTemplates = conceptTemplate.getLexicalTemplates();
		List<Description> descriptions = conceptTemplate.getConceptOutline().getDescriptions();
		List<Pattern> patterns = new ArrayList<>();
		for (Description description : descriptions) {
			if (description.getTermTemplate() != null) {
				patterns.add(constructTermPattern(description.getTermTemplate()));
			}
		}
		
		Map<String,Set<SimpleDescriptionPojo>> descriptionsMap = terminologyClientFactory.getClient().getDescriptions(branchPath,logicalMatched);
		for (String conceptId : descriptionsMap.keySet()) {
			List<SimpleDescriptionPojo> activeDescriptions = descriptionsMap.get(conceptId)
					.stream()
					.filter(d->d.isActive())
					.collect(Collectors.toList());
			
			boolean isMatched = false;
			for (Pattern pattern : patterns) {
				for (SimpleDescriptionPojo descriptionPojo : activeDescriptions) {
					if (pattern.matcher(descriptionPojo.getTerm()).matches()) {
						isMatched = true;
						break;
					} else {
						isMatched = false;
					}
				}
				if (!isMatched) {
					break;
				}
			}
			
			if (conformToTemplate && isMatched) {
				result.add(conceptId);
			} else if (!conformToTemplate && !isMatched){
				result.add(conceptId);
			}
		}
		return result;
	}

	private Set<String> performLogicalSearch(LogicalTemplate logical, String branchPath, boolean conformToTemplate) throws RestClientException {
		List<String> focusConcepts = logical.getFocusConcepts();
		List<AttributeGroup> attributeGroups = logical.getAttributeGroups();
		List<Attribute> unGroupedAttriburtes = logical.getUngroupedAttributes();
		String ecl = constructEclQuery(focusConcepts, attributeGroups, unGroupedAttriburtes);
		LOGGER.info("ECL=" + ecl);
		Set<String> logicalMatched = terminologyClientFactory.getClient().eclQuery(branchPath, ecl, MAX);
		if (conformToTemplate) {
			return logicalMatched;
		} else {
			String domainEcl = constructEclQuery(focusConcepts, Collections.emptyList(), Collections.emptyList());
			LOGGER.info("Domain ECL=" + domainEcl);
			Set<String> domainResult = terminologyClientFactory.getClient().eclQuery(branchPath, domainEcl, MAX);
			domainResult.removeAll(logicalMatched);
			return domainResult;
		}
	}

	private Pattern constructTermPattern(String termTemplate) {
		String result = termTemplate;
		//$actionTerm$ of $procSiteTerm$ using computed tomography guidance (procedure)
		Matcher matcher = TemplateService.TERM_SLOT_PATTERN.matcher(termTemplate);
		while (matcher.find()) {
				String termSlot = matcher.group();
				result = result.replace(termSlot, ".*");
			}
		result = result.replace("(", "\\(");
		result = result.replace(")", "\\)");
		LOGGER.info("term pattern regex=" + result);
		Pattern pattern = Pattern.compile(result);
		return pattern;
	}

	private Map<String,String> mapSlots(List<Attribute> attributes) {
		Map<String, String> slotMap = new HashMap<>();
		for (Attribute attribute : attributes) {
			if (attribute.getSlotName() != null) {
				String slotValue = null;
				if (attribute.getValue() != null) {
					slotValue = attribute.getValue();
				} else if (attribute.getAllowableRangeECL() != null) {
					slotValue = attribute.getAllowableRangeECL();
				}
				slotMap.put(attribute.getSlotName(), slotValue);
			}
		}
		return slotMap;
	}
	
	private String replaceSlot(String ecl, List<Attribute> attributes) {
		Map<String, String> slotMap = mapSlots(attributes);
		String result = ecl;
		for (String name : slotMap.keySet()) {
			result = result.replaceAll("=" + name, "=" + slotMap.get(name));
		}
		return result;
	}

	private String convertAttributeToEcl(List<Attribute> attributes) {
		StringBuilder queryBuilder = new StringBuilder();
		int counter = 0;
		for (Attribute attribute : attributes) {
			if (counter++ > 0) {
				queryBuilder.append(",");
			}
			
			if (attribute.getCardinalityMin() != null) {
				queryBuilder.append("[" + attribute.getCardinalityMin() + CARDINALITY_SEPARATOR);
			}
			
			if (attribute.getCardinalityMax() != null) {
				queryBuilder.append(attribute.getCardinalityMax() + "]");
			}
			
			queryBuilder.append(attribute.getType());
			queryBuilder.append( "=");
			if (attribute.getValue() != null) {
				queryBuilder.append(attribute.getValue());
			}
			else if (attribute.getAllowableRangeECL() != null) {
				queryBuilder.append(attribute.getAllowableRangeECL());
			} else if (attribute.getSlotReference() != null) {
				queryBuilder.append(attribute.getSlotReference());
			}
			
		}
		return queryBuilder.toString();
	}

	public String constructEclQuery(List<String> focusConcepts, List<AttributeGroup> attributeGroups,
			List<Attribute> unGroupedAttriburtes) {
		List<Attribute> attributes = new ArrayList<>();
		attributes.addAll(unGroupedAttriburtes);
		StringBuilder queryBuilder = new StringBuilder();
		//TODO improve for multiple concepts
		for (String concept : focusConcepts) {
			queryBuilder.append("<<" + concept);
		}
		if (!attributeGroups.isEmpty() || !unGroupedAttriburtes.isEmpty()) {
			queryBuilder.append(":");
		}
		queryBuilder.append(convertAttributeToEcl(unGroupedAttriburtes));
		if (!unGroupedAttriburtes.isEmpty() && !attributeGroups.isEmpty()) {
			queryBuilder.append(",");
		}
		int groupCounter = 0;
		for (AttributeGroup group : attributeGroups) {
			attributes.addAll(group.getAttributes());
			if (groupCounter++ > 0) {
				queryBuilder.append(",");
			}
			
			if (group.getCardinalityMin() != null) {
				queryBuilder.append("[" + group.getCardinalityMin() + CARDINALITY_SEPARATOR);
			}
			if (group.getCardinalityMax() != null) {
				queryBuilder.append(group.getCardinalityMax() + "]");
			}
			queryBuilder.append("{");
			queryBuilder.append(convertAttributeToEcl(group.getAttributes()));
			queryBuilder.append("}");
		}
		 queryBuilder.toString();
		 return replaceSlot( queryBuilder.toString(), attributes);
		 
	}
}