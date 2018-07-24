package org.ihtsdo.otf.authoringtemplate.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.ihtsdo.otf.authoringtemplate.service.exception.ServiceException;
import org.ihtsdo.otf.rest.client.RestClientException;
import org.ihtsdo.otf.rest.client.snowowl.SnowOwlRestClientFactory;
import org.ihtsdo.otf.rest.client.snowowl.pojo.ConceptPojo;
import org.ihtsdo.otf.rest.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snomed.authoringtemplate.domain.ConceptTemplate;
import org.snomed.authoringtemplate.domain.Description;
import org.snomed.authoringtemplate.domain.DescriptionType;
import org.snomed.authoringtemplate.domain.logical.Attribute;
import org.snomed.authoringtemplate.domain.logical.AttributeGroup;
import org.snomed.authoringtemplate.domain.logical.LogicalTemplate;
import org.snomed.authoringtemplate.service.LogicalTemplateParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConceptTemplateSearchService {

	private static final String AND = "AND";

	private static final String OR = "OR";

	private static final String CARDINALITY_SEPARATOR = "..";

	@Autowired 
	private LogicalTemplateParserService logicalTemplateParser;
	
	@Autowired
	private SnowOwlRestClientFactory terminologyClientFactory;
	
	@Autowired
	private TemplateService templateService;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ConceptTemplateSearchService.class);

	private static final int MAX = 200000;
	
	public Set<String> searchConceptsByTemplate(String templateName, String branchPath, 
			Boolean logicalMatch, Boolean lexicalMatch, boolean stated) throws ServiceException, ResourceNotFoundException {
		
			LOGGER.info("Search concepts for template={}, on branchPath={}, with logigicalMatch={}, lexicalMatch={} and stated={}",
					templateName, branchPath, logicalMatch, lexicalMatch, stated);
			if (logicalMatch == null) {
				throw new IllegalArgumentException("logicalMatch parameter must be specified.");
			}
			
			if (lexicalMatch != null) {
				if (!logicalMatch.booleanValue()) {
					throw new IllegalArgumentException("logicalMatch parameter must be true when lexicalMatch is set.");
				}
			}
			try {
				ConceptTemplate conceptTemplate = templateService.loadOrThrow(templateName);
				//parse logical
				LogicalTemplate logical = logicalTemplateParser.parseTemplate(conceptTemplate.getLogicalTemplate());
				if (lexicalMatch != null) {
					Set<String> logicalResult = performLogicalSearch(conceptTemplate, logical, branchPath, true, stated);
					return performLexicalSearch(conceptTemplate, logical, logicalResult, branchPath, lexicalMatch);
				} else {
					return performLogicalSearch(conceptTemplate, logical, branchPath, logicalMatch, stated);
				}
		} catch (IOException e) {
			throw new ServiceException("Failed to load tempate " + templateName);
		}
		
	}
	
	private Set<String> performLexicalSearch(ConceptTemplate conceptTemplate, LogicalTemplate logical, 
			Set<String> logicalMatched, String branchPath, boolean lexicalMatch) throws ServiceException {
		
		Set<String> result = new HashSet<>();
		List<Description> descriptions = conceptTemplate.getConceptOutline().getDescriptions();
		List<Pattern> patterns = new ArrayList<>();
		for (Description description : descriptions) {
			if (description.getTermTemplate() != null) {
				patterns.add(TemplateUtil.constructTermPattern(description.getTermTemplate()));
			}
		}
		Set<String> fsnTempaltes = TemplateUtil.getTermTemplates(conceptTemplate, DescriptionType.FSN);
		Set<String> synoymTempaltes = TemplateUtil.getTermTemplates(conceptTemplate, DescriptionType.SYNONYM);
		Map<Pattern, Set<String>> fsnPaterrnSlotsMap = TemplateUtil.compilePatterns(fsnTempaltes);
		Map<Pattern, Set<String>> synoymPaterrnSlotsMap = TemplateUtil.compilePatterns(synoymTempaltes);
		Map<String, Set<String>> attributeTypeSlotsMap = TemplateUtil.getAttributeTypeSlotMap(logical);
		
		try {
			
			Collection<ConceptPojo> concepts = terminologyClientFactory.getClient().searchConcepts(branchPath, new ArrayList<>(logicalMatched));
			for (ConceptPojo conceptPojo : concepts) {
				List<String> synoyms = conceptPojo.getDescriptions()
						.stream()
						.filter(d->d.isActive())
						.filter(d -> d.getType().equals(DescriptionType.SYNONYM.name()))
						.map(d -> d.getTerm())
						.collect(Collectors.toList());
				
				List<String> fsns = conceptPojo.getDescriptions()
						.stream()
						.filter(d->d.isActive())
						.filter(d -> d.getType().equals(DescriptionType.FSN.name()))
						.map(d -> d.getTerm())
						.collect(Collectors.toList());
				
				boolean isMatched = false;
				for (Pattern pattern : fsnPaterrnSlotsMap.keySet()) {
					isMatched = isPatternMatched(pattern, fsns);
					if (!isMatched) {
						break;
					} 
				}
				for (Pattern pattern : synoymPaterrnSlotsMap.keySet()) {
					isMatched = isPatternMatched(pattern, synoyms);
					if (!isMatched) {
						break;
					} 
				}
				//perform slot checking
//				Map<String, ConceptMiniPojo> slotValueMap = TemplateUtil.getAttributeSlotValueMap(attributeTypeSlotsMap, conceptPojo);
				if (lexicalMatch && isMatched) {
					result.add(conceptPojo.getConceptId());
				} else if (!lexicalMatch && !isMatched){
					result.add(conceptPojo.getConceptId());
				}
			}
			LOGGER.info("Logical search results={} and lexical search results={}", logicalMatched.size(), result.size());
			return result;
		} catch (RestClientException e) {
			throw new ServiceException("Failed to complete lexical template search.", e);
		}
		
	}
	
	private boolean isPatternMatched(Pattern pattern, Collection<String> terms) {
		for (String term : terms) {
			if (pattern.matcher(term).matches()) {
				return true;
			}
		}
		return false;
	}

	private Set<String> performLogicalSearch(ConceptTemplate conceptTemplate, LogicalTemplate logical,
			String branchPath, boolean logicalMatch, boolean stated) throws ServiceException {
		try {
			List<String> focusConcepts = logical.getFocusConcepts();
			//domain concept from template is only for inferred search
			String domainEcl = !stated ? conceptTemplate.getDomain() : null;
			if (domainEcl == null || domainEcl.isEmpty()) {
				domainEcl = constructEclQuery(focusConcepts, Collections.emptyList(), Collections.emptyList());
			}
			LOGGER.debug("Domain ECL=" + domainEcl);
			List<AttributeGroup> attributeGroups = logical.getAttributeGroups();
			List<Attribute> unGroupedAttriburtes = logical.getUngroupedAttributes();
			String logicalEcl = constructEclQuery(focusConcepts, attributeGroups, unGroupedAttriburtes);
			String ecl = constructLogicalSearchEcl(domainEcl, logicalEcl, logicalMatch);
			LOGGER.debug("Logic template ECL=" + logicalEcl);
			Set<String> results = terminologyClientFactory.getClient().eclQuery(branchPath, ecl, MAX, stated);
			LOGGER.info("Logical search ECL={} stated={}", ecl, stated);
			LOGGER.info("Logical results {}", results.size());
			return results;
		} catch (Exception e) {
			throw new ServiceException("Failed to complete logical template search for template " + conceptTemplate.getName(), e);
		}
	}

	private String constructLogicalSearchEcl(String domainEcl, String logicalEcl, boolean logicalMatch) {
		StringBuilder builder = new StringBuilder();
		builder.append("(");
		builder.append(domainEcl);
		builder.append(")");
		if (logicalMatch) {
			builder.append(" AND ");
		} else {
			builder.append(" MINUS ");
		}
		builder.append("(");
		builder.append(logicalEcl);
		builder.append(")");
		return builder.toString();
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
				boolean isCompound = false;
				if (attribute.getAllowableRangeECL().contains(OR) || attribute.getAllowableRangeECL().contains(AND)) {
					isCompound = true;
					queryBuilder.append("(");
				}
				queryBuilder.append(attribute.getAllowableRangeECL());
				if (isCompound) {
					queryBuilder.append(")");
				}
			} else if (attribute.getSlotReference() != null) {
				queryBuilder.append(attribute.getSlotReference());
			}
		}
		return queryBuilder.toString();
	}

	public String constructEclQuery(List<String> focusConcepts, List<AttributeGroup> attributeGroups,
			List<Attribute> unGroupedAttriburtes) throws ServiceException {
		List<Attribute> attributes = new ArrayList<>();
		attributes.addAll(unGroupedAttriburtes);
		StringBuilder queryBuilder = new StringBuilder();
		if (focusConcepts == null || focusConcepts.isEmpty()) {
			throw new ServiceException("No focus concepts defined!");
		}
		queryBuilder.append("<<" + focusConcepts.get(0));
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
