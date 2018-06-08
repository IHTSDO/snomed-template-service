package org.ihtsdo.otf.authoringtemplate.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.ihtsdo.otf.authoringtemplate.domain.ConceptMini;
import org.ihtsdo.otf.authoringtemplate.domain.ConceptOutline;
import org.ihtsdo.otf.authoringtemplate.domain.ConceptTemplate;
import org.ihtsdo.otf.authoringtemplate.domain.DefinitionStatus;
import org.ihtsdo.otf.authoringtemplate.domain.Description;
import org.ihtsdo.otf.authoringtemplate.domain.DescriptionType;
import org.ihtsdo.otf.authoringtemplate.domain.Relationship;
import org.ihtsdo.otf.authoringtemplate.domain.logical.Attribute;
import org.ihtsdo.otf.authoringtemplate.domain.logical.AttributeGroup;
import org.ihtsdo.otf.authoringtemplate.domain.logical.LogicalTemplate;
import org.ihtsdo.otf.authoringtemplate.service.exception.ServiceException;
import org.ihtsdo.otf.rest.client.RestClientException;
import org.ihtsdo.otf.rest.client.snowowl.SnowOwlRestClientFactory;
import org.ihtsdo.otf.rest.client.snowowl.pojo.ConceptMiniPojo;
import org.ihtsdo.otf.rest.client.snowowl.pojo.ConceptPojo;
import org.ihtsdo.otf.rest.client.snowowl.pojo.DescriptionPojo;
import org.ihtsdo.otf.rest.client.snowowl.pojo.RelationshipPojo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConceptTemplateTransformService {
	private static final String STATED = "900000000000010007";

	private static final String EXISTENTIAL = "EXISTENTIAL";

	@Autowired
	private SnowOwlRestClientFactory terminologyClientFactory;
	
	@Autowired
	private TemplateService templateService;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ConceptTemplateTransformService.class);
	
	/**
	 * @param branchPath
	 * @param conceptsToTransform
	 * @param templateName
	 * @return
	 * @throws ServiceException
	 */
	public List<ConceptPojo> transform(String branchPath, String sourceTemplate, Set<String> conceptsToTransform, String templateName) throws ServiceException {
		List<ConceptPojo> result = new ArrayList<>();
		try {
			ConceptTemplate source = templateService.loadOrThrow(sourceTemplate);
			ConceptTemplate destination = templateService.loadOrThrow(templateName);
			validate(source, destination);
			for (String conceptId : conceptsToTransform) {
				try {
					ConceptPojo conceptPojo = terminologyClientFactory.getClient().getConcept(branchPath, conceptId);
					Map<String, String> slotValueMap = getSlotValueMap(source, conceptPojo);
					Map<String, String> attributeSlotMap = getAttributeValueMap(source, conceptPojo);
					if (conceptPojo == null) {
						throw new ServiceException(String.format("Failed to find concept %s from branch %s ", conceptId, branchPath));
					}
					result.add(performTransform(conceptPojo, destination, slotValueMap, attributeSlotMap));
				} catch (RestClientException e) {
					new ServiceException(String.format("Failed to transform concept %s to template %s ", conceptId, templateName), e);
				}
			}
		} catch ( IOException e) {
			throw new ServiceException("Failed to load template " + templateName, e);
		}
		return result;
		
	}

	
	private Map<String, String> getAttributeValueMap(ConceptTemplate conceptTemplate, ConceptPojo conceptPojo) throws ServiceException {
		Map<String, String> result = new HashMap<>();
		LogicalTemplateParserService parser = new LogicalTemplateParserService();
		Map<String, Set<String>> attributeSlots = new HashMap<>();
		try {
			LogicalTemplate logical = parser.parseTemplate(conceptTemplate.getLogicalTemplate());
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
			List<RelationshipPojo> statedRels = conceptPojo.getRelationships().stream()
					  .filter(r -> r.getCharacteristicType().equals(STATED))
					  .collect(Collectors.toList());
			for (RelationshipPojo pojo : statedRels) {
				if (attributeSlots.keySet().contains(pojo.getType().getConceptId())) {
					for (String slot : attributeSlots.get(pojo.getType().getConceptId())) {
						result.putIfAbsent(slot, pojo.getTarget().getConceptId());
					}
				}
			}
		} catch (IOException e) {
			throw new ServiceException("Failed to parse logical template " + conceptTemplate.getLogicalTemplate(), e);
		}
		return result;
	}
	
	private Map<String, String> getSlotValueMap(ConceptTemplate conceptTemplate, ConceptPojo conceptPojo) throws ServiceException {
		Map<String, String> result = new HashMap<>();
		Set<String> termTemplates = getTermTemplates(conceptTemplate, DescriptionType.SYNONYM);
		Set<String> fsnTemplates = getTermTemplates(conceptTemplate, DescriptionType.FSN);
		for (DescriptionPojo pojo : conceptPojo.getDescriptions()) {
			if (DescriptionType.FSN.name().equals(pojo.getType())) {
				mapSlots(fsnTemplates, pojo, result);
			} 
		}
		
		for (DescriptionPojo pojo : conceptPojo.getDescriptions()) {
			if (DescriptionType.SYNONYM.name().equals(pojo.getType())) {
				mapSlots(termTemplates, pojo, result);
			}
		}
		return result;
	}


	private void mapSlots(Set<String> termTemplates, DescriptionPojo pojo, Map<String, String> result) {
		for (String termPattern : termTemplates) {
			List<String> slots = TemplateUtil.getSlots(termPattern);
			if (slots.isEmpty()) {
				continue;
			}
			Pattern pattern = TemplateUtil.constructTermPattern(termPattern);
			 Matcher matcher = pattern.matcher(pojo.getTerm());
			 if (matcher.matches()) {
				 if (matcher.groupCount() == slots.size()) {
					 for (int i =0; i < matcher.groupCount(); i++) {
						 result.putIfAbsent(slots.get(i), matcher.group(i+1));
					 }
				 }
			 }
		}
	}


	private Set<String> getTermTemplates(ConceptTemplate conceptTemplate, DescriptionType type) {
		Set<String> termTemplates = new HashSet<>();
		for (Description desc : conceptTemplate.getConceptOutline().getDescriptions()) {
			if ((type == desc.getType()) && desc.getTermTemplate() != null) {
				termTemplates.add(desc.getTermTemplate());
			}
		}
		return termTemplates;
	}
	
	private Set<String> getSlotsFromTemplate(ConceptTemplate conceptTemplate) throws IOException {
		Set<String> slots = new HashSet<>();
		LogicalTemplateParserService parser = new LogicalTemplateParserService();
		LogicalTemplate logical = parser.parseTemplate(conceptTemplate.getLogicalTemplate());
		for (Attribute attr : logical.getUngroupedAttributes()) {
			if (attr.getSlotName() != null) {
				slots.add(attr.getSlotName());
			}
		}
		for (AttributeGroup attributeGrp : logical.getAttributeGroups()) {
			for (Attribute att : attributeGrp.getAttributes()) {
				if (att.getSlotName() != null) {
					slots.add(att.getSlotName());
				}
			}
		}
		return slots;
	}
	
	void validate(ConceptTemplate source, ConceptTemplate destination) throws ServiceException, IOException {
		Set<String> sourceSlots = getSlotsFromTemplate(source);
		Set<String> destinationSlots = getSlotsFromTemplate(destination);
		LOGGER.info("Source slots {} destination slots {}", sourceSlots, destinationSlots);
		if (!sourceSlots.containsAll(destinationSlots)) {
			StringBuilder msgBuilder = new StringBuilder();
			int counter = 0;
			for (String slot : destinationSlots) {
				if (!sourceSlots.contains(slot)) {
					if (counter++ > 0) {
						msgBuilder.append(",");
					}
					msgBuilder.append(slot);
				}
			}
			throw new ServiceException(String.format("Destination template %s has slot %s that doesn't exist in the source template %s",
													destination.getName(), msgBuilder.toString(), source.getName()));
		}
	}

	private ConceptPojo performTransform(ConceptPojo conceptPojo, ConceptTemplate conceptTemplate, Map<String, String> slotValueMap, Map<String, String> attributeSlotMap) {
		ConceptPojo transformed = conceptPojo;
		ConceptOutline conceptOutline = conceptTemplate.getConceptOutline();
		org.ihtsdo.otf.rest.client.snowowl.pojo.DefinitionStatus definitionStatus = org.ihtsdo.otf.rest.client.snowowl.pojo.DefinitionStatus.PRIMITIVE;
		if (DefinitionStatus.FULLY_DEFINED == conceptOutline.getDefinitionStatus()) {
			definitionStatus =  org.ihtsdo.otf.rest.client.snowowl.pojo.DefinitionStatus.FULLY_DEFINED;
		}
		transformed.setDefinitionStatus(definitionStatus);
		transformDescriptions(transformed, conceptOutline, slotValueMap);
		transformRelationships(transformed, conceptOutline, attributeSlotMap);
		return transformed;
	}

	private void transformRelationships(ConceptPojo transformed, ConceptOutline conceptOutline, Map<String, String> attributeSlotMap) {
		//map relationship by group
		List<RelationshipPojo> statedRels = transformed.getRelationships().stream()
									  .filter(r -> r.getCharacteristicType().equals(STATED))
									  .collect(Collectors.toList());
		
		Map<Integer, Map<String, Relationship>> newRelGroupMap = new HashMap<>();
		for (Relationship rel : conceptOutline.getRelationships()) {
			if (newRelGroupMap.get(rel.getGroupId()) == null) {
				newRelGroupMap.put(rel.getGroupId(), new HashMap<>());
			}
			if (rel.getTarget() != null) {
				String key = rel.getTarget().getConceptId() + rel.getType().getConceptId();
				newRelGroupMap.get(rel.getGroupId()).put(key, rel);
			}
			
		}
		
		for (RelationshipPojo pojo : statedRels) {
			String key = pojo.getTarget().getConceptId() + pojo.getType().getConceptId();
			if (newRelGroupMap.get(pojo.getGroupId()) != null && newRelGroupMap.get(pojo.getGroupId()).containsKey(key)) {
				pojo.setActive(true);
			} else {
				//retire if it is active
				pojo.setActive(false);
			}
		}
		
		Map<Integer, Map<String, RelationshipPojo>> relGroupMap = new HashMap<>();
		for (RelationshipPojo pojo : statedRels) {
			if (relGroupMap.get(pojo.getGroupId()) == null) {
				relGroupMap.put(pojo.getGroupId(), new HashMap<>());
			}
			String key = pojo.getTarget().getConceptId() + pojo.getType().getConceptId();
			relGroupMap.get(pojo.getGroupId()).put(key, pojo);
		}
		
		for (Relationship relationship : conceptOutline.getRelationships()) {
			if (relationship.getTarget() != null) {
				String key = relationship.getTarget().getConceptId() + relationship.getType().getConceptId();
				if (relGroupMap.get(relationship.getGroupId()) != null && relGroupMap.get(relationship.getGroupId()).containsKey(key)) {
					relGroupMap.get(relationship.getGroupId()).get(key).setActive(true);
					continue;
				} 
			} 
			//add new one
			RelationshipPojo relPojo = constructRelationshipPojo(relationship, attributeSlotMap);
			relPojo.setModuleId(conceptOutline.getModuleId());
			relPojo.setSourceId(transformed.getConceptId());
			transformed.add(relPojo);
			
		}
	}

	private void transformDescriptions(ConceptPojo transformed, ConceptOutline conceptOutline, Map<String, String> slotValueMap) {
		List<String> previousTerms = transformed.getDescriptions().stream()
								 .filter(t -> t.isActive())
								 .map(t -> t.getTerm())
								 .collect(Collectors.toList());
		List<String> newTerms = new ArrayList<>();
		if (conceptOutline.getDescriptions() != null) {
			for (Description desc : conceptOutline.getDescriptions()) {
				String term = desc.getTerm();
				if (term == null && desc.getTermTemplate() != null) {
					term = desc.getTermTemplate();
					for (String slot : slotValueMap.keySet()) {
						term = term.replace(slot, slotValueMap.get(slot));
					}
				}
				newTerms.add(term);
				if (!previousTerms.contains(term)) {
					DescriptionPojo descPojo = conscturctDescriptionPojo(desc, term);
					descPojo.setConceptId(transformed.getConceptId());
					descPojo.setModuleId(conceptOutline.getModuleId());
					transformed.add(descPojo);
				}
			}
		}
		//inactivation
		for (DescriptionPojo pojo : transformed.getDescriptions()) {
			if (pojo.isActive() && !newTerms.contains(pojo.getTerm())) {
				pojo.setActive(false);
			}
		}
	}

	private DescriptionPojo conscturctDescriptionPojo(Description desc, String term) {
		DescriptionPojo pojo = new DescriptionPojo();
		pojo.setAcceptabilityMap(desc.getAcceptabilityMap());
		pojo.setActive(true);
		pojo.setCaseSignificance(desc.getCaseSignificance().name());
		pojo.setTerm(term);
		pojo.setType(desc.getType().name());
		pojo.setLang(desc.getLang());
		return pojo;
	}

	private RelationshipPojo constructRelationshipPojo(Relationship relationship, Map<String, String> attributeSlotMap) {
		RelationshipPojo pojo = new RelationshipPojo();
		pojo.setActive(true);
		pojo.setCharacteristicType(relationship.getCharacteristicType());
		pojo.setGroupId(relationship.getGroupId());
		pojo.setModifier(EXISTENTIAL);
		ConceptMini target = new ConceptMini();
		if (relationship.getTargetSlot() != null) {
			target.setConceptId(attributeSlotMap.get(relationship.getTargetSlot()));
		} else {
			target = relationship.getTarget();
		}
		pojo.setTarget(constructConceptMiniPojo(target));
		pojo.setType(constructConceptMiniPojo(relationship.getType()));
		return pojo;
	}

	private ConceptMiniPojo constructConceptMiniPojo(ConceptMini conceptMini) {
		ConceptMiniPojo pojo = new ConceptMiniPojo();
		if (conceptMini != null) {
			pojo.setConceptId(conceptMini.getConceptId());
		}
		return pojo;
	}
}
