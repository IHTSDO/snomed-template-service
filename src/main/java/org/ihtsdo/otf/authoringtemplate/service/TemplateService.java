package org.ihtsdo.otf.authoringtemplate.service;

import org.assertj.core.util.Arrays;
import org.ihtsdo.otf.authoringtemplate.domain.*;
import org.ihtsdo.otf.authoringtemplate.rest.error.InputError;
import org.ihtsdo.otf.authoringtemplate.service.exception.ServiceException;
import org.ihtsdo.otf.rest.client.RestClientException;
import org.ihtsdo.otf.rest.client.snowowl.SnowOwlRestClient;
import org.ihtsdo.otf.rest.client.snowowl.SnowOwlRestClientFactory;
import org.ihtsdo.otf.rest.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

@Service
public class TemplateService {

	public static final Pattern TERM_SLOT_PATTERN = Pattern.compile("\\$([^\\$]*)\\$");

	@Autowired
	private TemplateStore templateStore;

	@Autowired
	private SnowOwlRestClientFactory terminologyClientFactory;

	@Value("${batch.maxSize}")
	private int batchMaxSize;

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private static final Pattern SIX_TO_EIGHTEEN_DIGITS = Pattern.compile("\\d{6,18}");

	public String create(String name, ConceptTemplate conceptTemplate) throws IOException {
		if (load(name) != null) {
			throw new IllegalArgumentException("Template with name '" + name + "' already exists.");
		}

		conceptTemplate.setVersion(1);

		// TODO Validate that lexicalTemplates and terms within ConceptTemplate descriptions match

		templateStore.save(name, conceptTemplate);

		return name;
	}

	public ConceptTemplate load(String name) throws IOException {
		return templateStore.load(name);
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
		final ConceptTemplate existingTemplate = load(name);
		if (existingTemplate == null) {
			throw new IllegalArgumentException("Template with name '" + name + "' does not exist.");
		}
		conceptTemplateUpdate.setVersion(existingTemplate.getVersion() + 1);
		templateStore.save(name, conceptTemplateUpdate);

		return conceptTemplateUpdate;
	}

	public Set<ConceptTemplate> listAll(String branchPath, String[] descendantOf, String[] ancestorOf) throws IOException {
		Set<ConceptTemplate> templates = listAll();
		SnowOwlRestClient terminologyClient = terminologyClientFactory.getClient();
		if (!Arrays.isNullOrEmpty(descendantOf) || !Arrays.isNullOrEmpty(ancestorOf)) {
			// Group templates by focus concept to reduce the number of ECL queries
			Map<String, List<ConceptTemplate>> templatesByFocusConcept = templates.stream().collect(Collectors.groupingBy(ConceptTemplate::getFocusConcept));
			SecurityContext securityContext = SecurityContextHolder.getContext();
			return templatesByFocusConcept.entrySet().stream().filter(entry -> {
				SecurityContextHolder.setContext(securityContext);
				String focusConcept = entry.getKey();
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
				try {
					return terminologyClient.eclQueryHasAnyMatches(branchPath, ecl);
				} catch (RestClientException e) {
					logger.error("Failed to filter templates using ECL", e);
					return false;
				}
			}).map(Map.Entry::getValue).flatMap(List::stream).collect(Collectors.toSet());
		}
		return templates;
	}

	public Set<ConceptTemplate> listAll() throws IOException {
		return templateStore.loadAll();
	}

	public void writeEmptyInputFile(String branchPath, String templateName, OutputStream outputStream) throws IOException, ResourceNotFoundException {
		ConceptTemplate template = loadOrThrow(templateName);
		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, Constants.UTF_8))) {
			String header = "";
			for (Relationship relationship : template.getConceptOutline().getRelationships()) {
				SimpleSlot targetSlot = relationship.getTargetSlot();
				if (isSlotRequiringInput(targetSlot)) {
					String slotName = targetSlot.getSlotName();
					if (!header.isEmpty()) header += "\t";
					header += slotName != null ? slotName : "slot";
				}
			}
			for (String additionalSlot : template.getAdditionalSlots()) {
				if (!header.isEmpty()) header += "\t";
				header += additionalSlot;
			}
			writer.write(header);
			writer.newLine();
		}
	}

	private boolean isSlotRequiringInput(SimpleSlot targetSlot) {
		return targetSlot != null && targetSlot.getSlotReference() == null;
	}

	public List<ConceptOutline> generateConcepts(String branchPath, String templateName, InputStream inputStream) throws IOException, ServiceException {
		Assert.notNull(inputStream, "Batch file is required.");

		ConceptTemplate template = loadOrThrow(templateName);
		ConceptOutline conceptOutline = template.getConceptOutline();
		List<SimpleSlot> slotsRequiringInput = getSlotsRequiringInput(conceptOutline.getRelationships());
		List<String> additionalSlots = template.getAdditionalSlots();
		List<String> errorMessages = new ArrayList<>();
		List<List<String>> columnValues = new ArrayList<>();

		// Read input file
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
			// Validate header
			int expectedColumnCount = slotsRequiringInput.size() + additionalSlots.size();
			validateHeader(reader.readLine(), expectedColumnCount);

			// Collect values with basic validation
			LongStream.range(0, expectedColumnCount).forEach(v -> columnValues.add(new ArrayList<>()));
			String line;
			int lineNum = 1;
			while ((line = reader.readLine()) != null) {
				lineNum++;
				if (line.isEmpty()) {
					continue;
				}
				String[] values = line.split("\\t");
				if (values.length != expectedColumnCount) {
					errorMessages.add(String.format("Line %s has %s columns, expecting %s", lineNum, values.length, expectedColumnCount));
				}
				for (int column = 0; column < values.length; column++) {
					String conceptId = values[column];
					if (column < slotsRequiringInput.size() && !isValidConceptId(conceptId)) {
						errorMessages.add(getError(conceptId, "is not a valid concept identifier", lineNum, column));
					}
					columnValues.get(column).add(conceptId);
				}
			}
		}
		int batchSize = columnValues.get(0).size();
		if (batchSize > batchMaxSize) {
			errorMessages.add(String.format("Batch input file contains %s rows, the maximum permitted is %s.", batchSize, batchMaxSize));
		}
		throwAnyInputErrors(errorMessages);

		if (batchSize == 0) {
			throw new InputError("Batch input file doesn't contain any rows.");
		}

		// Validate values against slot constraints, column at a time
		int slotIndex = -1;
		try {
			SnowOwlRestClient client = terminologyClientFactory.getClient();
			for (SimpleSlot simpleSlot : slotsRequiringInput) {
				slotIndex++;
				List<String> slotValues = columnValues.get(slotIndex);
				String slotEcl = simpleSlot.getAllowableRangeECL();
				StringBuilder validationEcl = new StringBuilder()
						.append("(")
						.append(slotEcl)
						.append(") AND (");
				for (String slotValue : slotValues) {
					validationEcl.append(slotValue)
							.append(" OR ");
				}
				// Remove last OR
				validationEcl.delete(validationEcl.length() - 4, validationEcl.length());
				validationEcl.append(")");

				Set<String> validSlotValues = client.eclQuery(branchPath, validationEcl.toString(), slotValues.size());
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
		} catch (RestClientException e) {
			throw new ServiceException("Error validating slots using terminologyClientFactory server.", e);
		}
		throwAnyInputErrors(errorMessages);

		// Generate unsaved concepts
		List<String> slotNames = slotsRequiringInput.stream().filter(slot -> slot.getSlotName() != null).map(SimpleSlot::getSlotName).collect(Collectors.toList());
		slotNames.addAll(additionalSlots);

		List<ConceptOutline> generatedConcepts = new ArrayList<>();

		// Create x blank concepts
		IntStream.range(0, batchSize).forEach(i -> generatedConcepts.add(new ConceptOutline()));

		// Push each template relationship into all concepts
		AtomicInteger column = new AtomicInteger(0);
		template.getConceptOutline().getRelationships().forEach(relationship -> {
			SimpleSlot targetSlot = relationship.getTargetSlot();
			if (targetSlot == null) {
				generatedConcepts.forEach(concept -> concept.addRelationship(relationship));
			}
			if (targetSlot != null) {
				String slotName = targetSlot.getSlotName();
				if (slotName != null) {
					int slotColumn = column.getAndIncrement();
					addRelationshipToAllConcepts(relationship, slotColumn, columnValues, generatedConcepts);
				} else {
					addRelationshipToAllConcepts(relationship, slotNames.indexOf(targetSlot.getSlotReference()), columnValues, generatedConcepts);
				}
			}
		});

		// Push each template description into all concepts
		AtomicInteger descriptionIndex = new AtomicInteger(0);
		template.getConceptOutline().getDescriptions().forEach(description -> {
			generatedConcepts.forEach(concept -> concept.addDescription(description.clone()));

			Set<String> additionalSlotsToProcess = new HashSet<>();
			String termTemplate = description.getTermTemplate();
			if (termTemplate != null) {
				Matcher matcher = TERM_SLOT_PATTERN.matcher(termTemplate);
				while (matcher.find()) {
					String termSlot = matcher.group(1);
					if (additionalSlots.contains(termSlot)) {
						additionalSlotsToProcess.add(termSlot);
					}
				}
			}
			for (String additionalSlotToProcess : additionalSlotsToProcess) {
				int index = additionalSlots.indexOf(additionalSlotToProcess);
				List<String> additionalSlotValues = columnValues.get(slotsRequiringInput.size() + index);
				for (int i = 0; i < generatedConcepts.size(); i++) {
					Description generatedDescription = generatedConcepts.get(i).getDescriptions().get(descriptionIndex.get());
					if (generatedDescription.getTerm() == null) {
						generatedDescription.setTerm(generatedDescription.getTermTemplate());
					}
					generatedDescription.setTerm(generatedDescription.getTerm().replace("$" + additionalSlotToProcess + "$", additionalSlotValues.get(i)));
				}
			}
			descriptionIndex.incrementAndGet();
		});

		return generatedConcepts;
	}

	private void addRelationshipToAllConcepts(Relationship relationship, int slotColumn, List<List<String>> columnValues, List<ConceptOutline> generatedConcepts) {
		List<String> values = columnValues.get(slotColumn);
		for (int i = 0; i < values.size(); i++) {
			generatedConcepts.get(i).addRelationship(relationship.clone().setTarget(new ConceptMini(values.get(i))));
		}
	}

	private void throwAnyInputErrors(List<String> errorMessages) {
		if (!errorMessages.isEmpty()) {
			throw new InputError(errorMessages);
		}
	}

	private String getError(String value, String message, int lineNum, int column) {
		return "Value '" + value + "' on line " + lineNum + " column " + (column + 1) + " " + message + ".";
	}

	private boolean isValidConceptId(String conceptId) {
		try {
			if (SIX_TO_EIGHTEEN_DIGITS.matcher(conceptId).matches()) {
				String partitionIdentifier = conceptId.substring(conceptId.length() - 3, conceptId.length() - 1);
				return partitionIdentifier.equals("00") || partitionIdentifier.equals("10");
			}
		} catch (StringIndexOutOfBoundsException e) {
			e.printStackTrace();
		}
		return false;
	}

	private void validateHeader(String header, int expectedColumnCount) throws IOException {
		Assert.notNull(header, "Input file is empty.");
		String[] columns = header.split("\\t");
		int columnCount = columns.length;
		Assert.isTrue(columnCount == expectedColumnCount, String.format("There are %s slots requiring input in the selected template is but the header line of the input file has %s columns.", expectedColumnCount, columnCount));
	}

	private List<SimpleSlot> getSlotsRequiringInput(List<Relationship> relationships) {
		return relationships.stream().filter(r -> isSlotRequiringInput(r.getTargetSlot()))
				.map(Relationship::getTargetSlot).collect(Collectors.toList());
	}

	public void reloadCache() throws IOException {
		templateStore.init();
	}
}
