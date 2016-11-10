package org.ihtsdo.otf.authoringtemplate.service;

import org.ihtsdo.otf.authoringtemplate.domain.*;
import org.ihtsdo.otf.authoringtemplate.domain.logical.Attribute;
import org.ihtsdo.otf.authoringtemplate.domain.logical.AttributeGroup;
import org.ihtsdo.otf.authoringtemplate.domain.logical.LogicalTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TemplateService {

	@Autowired
	private LogicalTemplateParserService logicalParserService;

	@Autowired
	private JsonStore jsonStore;

	public String create(String name, ConceptTemplate conceptTemplate) throws IOException {
		if (jsonStore.load(name, ConceptTemplate.class) != null) {
			throw new IllegalArgumentException("Template with name '" + name + "' already exists.");
		}

		conceptTemplate.setVersion(1);
		mapToConceptTemplate(conceptTemplate, name, conceptTemplate.getLogicalTemplate());

		// TODO Validate that lexicalTemplates and terms within ConceptTemplate descriptions match

		jsonStore.save(name, conceptTemplate);

		return name;
	}

	public ConceptTemplate load(String name) throws IOException {
		return jsonStore.load(name, ConceptTemplate.class);
	}

	// TODO Keep old versions of the template
	public ConceptTemplate update(String name, ConceptTemplate conceptTemplateUpdate) throws IOException {
		final ConceptTemplate existingTemplate = jsonStore.load(name, ConceptTemplate.class);
		if (existingTemplate == null) {
			throw new IllegalArgumentException("Template with name '" + name + "' does not exist.");
		}

		conceptTemplateUpdate.setVersion(existingTemplate.getVersion() + 1);
		mapToConceptTemplate(conceptTemplateUpdate, name, conceptTemplateUpdate.getLogicalTemplate());
		jsonStore.save(name, conceptTemplateUpdate);

		return conceptTemplateUpdate;
	}

	private void mapToConceptTemplate(ConceptTemplate conceptTemplate, String name, String logicalTemplateString) throws IOException {
		final LogicalTemplate logicalTemplate = logicalParserService.parseTemplate(logicalTemplateString);
		conceptTemplate.setName(name);
		conceptTemplate.setFocusConcept(logicalTemplate.getFocusConcepts().isEmpty() ? null : logicalTemplate.getFocusConcepts().get(0));
		updateRelationships(conceptTemplate.getConceptOutline().getRelationships(), logicalTemplate);
		updateDescriptions(conceptTemplate.getLexicalTemplates(), conceptTemplate.getConceptOutline().getDescriptions());
	}

	private void updateRelationships(List<Relationship> relationships, LogicalTemplate logicalTemplate) {
		relationships.clear();

		// Add Parents
		for (String focusConcept : logicalTemplate.getFocusConcepts()) {
			final Relationship relationship = new Relationship();
			relationship.setGroupId(0);
			relationship.setType(new ConceptMini(Concepts.ISA));
			relationship.setTarget(new ConceptMini(focusConcept));
			relationships.add(relationship);
		}

		// Add Attribute Groups
		final List<AttributeGroup> attributeGroups = logicalTemplate.getAttributeGroups();
		for (int i = 0; i < attributeGroups.size(); i++) {
			final AttributeGroup attributeGroup = attributeGroups.get(i);
			for (Attribute attribute : attributeGroup.getAttributes()) {
				final Relationship relationship = new Relationship();
				relationship.setCardinalityMin(attribute.getCardinalityMin());
				relationship.setCardinalityMax(attribute.getCardinalityMax());
				relationship.setGroupId(i + 1);
				relationship.setType(new ConceptMini(attribute.getType()));
				relationship.setTarget(getConceptMiniOrNull(attribute.getValue()));
				if (attribute.getAllowableRangeECL() != null) {
					relationship.setTargetSlot(new SimpleSlot(attribute.getSlotName(), attribute.getAllowableRangeECL()));
				}
				if (attribute.getSlotReference() != null) {
					relationship.setTargetSlot(new SimpleSlot(attribute.getSlotReference()));
				}
				relationships.add(relationship);
			}
		}
	}

	private void updateDescriptions(List<LexicalTemplate> lexicalTemplates, List<Description> descriptions) {
		Map<String, String> nameToDisplayNameMap = new HashMap<>();
		for (LexicalTemplate lexicalTemplate : lexicalTemplates) {
			nameToDisplayNameMap.put(lexicalTemplate.getName(),
					lexicalTemplate.getDisplayName() != null ? lexicalTemplate.getDisplayName() : lexicalTemplate.getName());
		}

		for (Description description : descriptions) {
			final String termTemplate = description.getTermTemplate();
			String initialTerm = termTemplate;
			final Pattern termSlotPattern = Pattern.compile("\\$([^\\$]*)\\$");
			final Matcher matcher = termSlotPattern.matcher(termTemplate);
			while (matcher.find()) {
				final String group = matcher.group(1);
				final String replacement = nameToDisplayNameMap.get(group);
				if (replacement == null) {
					throw new IllegalArgumentException("Term template contains lexical template name which does not exist:" + group);
				}
				initialTerm = initialTerm.replace("$" + group + "$", "[" + replacement + "]");
			}
			description.setInitialTerm(initialTerm);
		}
	}

	private ConceptMini getConceptMiniOrNull(String value) {
		return value == null ? null : new ConceptMini(value);
	}

	public Set<ConceptTemplate> listAll() throws IOException {
		return jsonStore.loadAll(ConceptTemplate.class);
	}

	public JsonStore getJsonStore() {
		return jsonStore;
	}
}
