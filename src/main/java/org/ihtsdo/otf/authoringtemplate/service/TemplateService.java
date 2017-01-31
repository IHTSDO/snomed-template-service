package org.ihtsdo.otf.authoringtemplate.service;

import org.assertj.core.util.Arrays;
import org.ihtsdo.otf.authoringtemplate.domain.*;
import org.ihtsdo.otf.authoringtemplate.domain.logical.Attribute;
import org.ihtsdo.otf.authoringtemplate.domain.logical.AttributeGroup;
import org.ihtsdo.otf.authoringtemplate.domain.logical.LogicalTemplate;
import org.ihtsdo.otf.authoringtemplate.service.exception.ResourceNotFoundException;
import org.ihtsdo.otf.authoringtemplate.service.termserver.TerminologyServerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@Service
public class TemplateService {

	@Autowired
	private LogicalTemplateParserService logicalParserService;

	@Autowired
	private JsonStore jsonStore;

	@Autowired
	private TerminologyServerAdapter terminology;

	private static final Pattern SIX_TO_EIGHTEEN_DIGITS = Pattern.compile("\\d{6,18}");

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

	private ConceptTemplate loadOrThrow(String templateName) throws IOException, ResourceNotFoundException {
		ConceptTemplate template = load(templateName);
		if (template == null) {
			throw new ResourceNotFoundException("template", templateName);
		}
		return template;
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

	public Set<ConceptTemplate> listAll(String branchPath, String[] descendantOf, String[] ancestorOf) throws IOException {
		Set<ConceptTemplate> templates = listAll();
		if (!Arrays.isNullOrEmpty(descendantOf) || !Arrays.isNullOrEmpty(ancestorOf)) {
			return templates.stream().parallel().filter(conceptTemplate -> {
				String focusConcept = conceptTemplate.getFocusConcept();
				String ecl = "(" + focusConcept + ") AND (";
				for (int i = 0; descendantOf != null && i < descendantOf.length; i++) {
					if (i > 0) ecl += " OR ";
					ecl += "<<" + descendantOf[i];
				}
				for (int i = 0; ancestorOf != null && i < ancestorOf.length; i++) {
					if (!Arrays.isNullOrEmpty(descendantOf) || i > 0) ecl += " OR ";
					ecl += ">>" + ancestorOf[i];
				}
				ecl += ")";
				return terminology.eclQueryHasAnyMatches(branchPath, ecl);
			}).collect(Collectors.toSet());
		}
		return templates;
	}

	public Set<ConceptTemplate> listAll() throws IOException {
		return jsonStore.loadAll(ConceptTemplate.class);
	}

	public JsonStore getJsonStore() {
		return jsonStore;
	}

	public void writeEmptyInputFile(String branchPath, String templateName, OutputStream outputStream) throws IOException, ResourceNotFoundException {
		ConceptTemplate template = loadOrThrow(templateName);
		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, Constants.UTF_8))) {
			String header = "";
			for (Relationship relationship : template.getConceptOutline().getRelationships()) {
				SimpleSlot targetSlot = relationship.getTargetSlot();
				if (isSlotRequiringInput(targetSlot)) {
					String slotName = targetSlot.getSlotName();
					String range = targetSlot.getAllowableRangeECL();
					if (!header.isEmpty()) header += "\t";
					if (slotName != null) header += slotName + " ";
					header += "(" + range + ")";
				}
			}
			writer.write(header);
			writer.newLine();
		}
	}

	private boolean isSlotRequiringInput(SimpleSlot targetSlot) {
		return targetSlot != null && targetSlot.getSlotReference() == null;
	}

	public void generateConcepts(String branchPath, String templateName, InputStream inputStream) throws IOException, ResourceNotFoundException {
		ConceptTemplate template = loadOrThrow(templateName);
		ConceptOutline conceptOutline = template.getConceptOutline();
		List<SimpleSlot> slotsRequiringInput = getSlotsRequiringInput(conceptOutline.getRelationships());
		List<String> errorMessages = new ArrayList<>();
		List<List<String>> columnValues = new ArrayList<>();

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
			// Validate header
			validateHeader(reader.readLine(), slotsRequiringInput);

			// Collect values with basic validation
			LongStream.range(0, slotsRequiringInput.size()).forEach(v -> columnValues.add(new ArrayList<>()));
			String line;
			int lineNum = 1;
			while ((line = reader.readLine()) != null) {
				lineNum++;
				if (line.isEmpty()) {
					continue;
				}
				String[] values = line.split("\\t");
				if (values.length != slotsRequiringInput.size()) {
					errorMessages.add(String.format("Line %s has %s columns, expecting %s", lineNum, values.length, slotsRequiringInput.size()));
				}
				for (int column = 0; column < values.length; column++) {
					String conceptId = values[column];
					if (isValidConceptId(conceptId)) {
						errorMessages.add(getError(conceptId, "is not a valid concept identifier", lineNum, column));
					}
					columnValues.get(column).add(conceptId);
				}
			}
		}

		// Validate values against slot constraints, column at a time
		if (errorMessages.isEmpty() && !columnValues.get(0).isEmpty()) {
			int slotIndex = -1;
			for (SimpleSlot simpleSlot : slotsRequiringInput) {
				slotIndex++;
				List<String> slotValues = columnValues.get(slotIndex);
				String slotEcl = simpleSlot.getAllowableRangeECL();
				StringBuilder validationEcl = new StringBuilder(slotEcl)
						.append(" AND (");
				for (String slotValue : slotValues) {
					validationEcl.append(slotValue)
							.append(" OR ");
				}
				// Remove last OR
				validationEcl.delete(validationEcl.length() - 4, validationEcl.length());
				validationEcl.append(")");

				Set<String> validSlotValues = terminology.eclQuery(branchPath, validationEcl.toString(), slotValues.size());
				Set<String> invalidSlotValues = new HashSet<>(slotValues);
				invalidSlotValues.removeAll(validSlotValues);
				if (!invalidSlotValues.isEmpty()) {
					errorMessages.add(String.format("Column %s has the constraint %s. " +
									"The following given values do not match this constraint: %s",
							slotIndex + 1,
							slotEcl,
							invalidSlotValues));
				}
			}
		}

		// Generate unsaved concepts
		Map<String, SimpleSlot> slotNameMap = createSlotNameMap(slotsRequiringInput);
		List<SimpleSlot> slotsFilledByReference = getSlotsFilledByReference(conceptOutline.getRelationships());

	}

	private String getError(String value, String message, int lineNum, int column) {
		return "Value '" + value + "' on line " + lineNum + " column " + (column + 1) + " " + message + ".";
	}

	private boolean isValidConceptId(String conceptId) {
		if (SIX_TO_EIGHTEEN_DIGITS.matcher(conceptId).matches()) {
			String partitionIdentifier = conceptId.substring(conceptId.length() - 3, conceptId.length() - 5);
			return partitionIdentifier.equals("00") || partitionIdentifier.equals("10");
		}
		return false;
	}

	private void validateHeader(String header, List<SimpleSlot> slotsRequiringInput) throws IOException {
		int expectedColumnCount = slotsRequiringInput.size();
		String[] columns = header.split("\\t");
		int columnCount = columns.length;
		Assert.isTrue(columnCount == expectedColumnCount, "Slots requiring input is " + expectedColumnCount + " but first line of file has " + columnCount + " columns.");
	}

	private List<SimpleSlot> getSlotsRequiringInput(List<Relationship> relationships) {
		return relationships.stream().filter(r -> isSlotRequiringInput(r.getTargetSlot()))
				.map(Relationship::getTargetSlot).collect(Collectors.toList());
	}

	private List<SimpleSlot> getSlotsFilledByReference(List<Relationship> relationships) {
		return relationships.stream().filter(r -> r.getTargetSlot() != null && r.getTargetSlot().getSlotReference() != null)
				.map(Relationship::getTargetSlot).collect(Collectors.toList());
	}

	private Map<String, SimpleSlot> createSlotNameMap(List<SimpleSlot> slots) {
		Map<String, SimpleSlot> map = new HashMap<>();
		slots.forEach(slot -> {if (slot.getSlotName() != null) map.put(slot.getSlotName(), slot);});
		return map;
	}
}
