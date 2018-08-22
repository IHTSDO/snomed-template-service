package org.ihtsdo.otf.authoringtemplate.service;

import static org.ihtsdo.otf.authoringtemplate.service.TemplateService.TERM_SLOT_PATTERN;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import org.ihtsdo.otf.authoringtemplate.rest.error.InputError;
import org.ihtsdo.otf.authoringtemplate.service.exception.ServiceException;
import org.ihtsdo.otf.rest.client.RestClientException;
import org.ihtsdo.otf.rest.client.snowowl.SnowOwlRestClient;
import org.ihtsdo.otf.rest.client.snowowl.SnowOwlRestClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snomed.authoringtemplate.domain.ConceptMini;
import org.snomed.authoringtemplate.domain.ConceptOutline;
import org.snomed.authoringtemplate.domain.ConceptTemplate;
import org.snomed.authoringtemplate.domain.Description;
import org.snomed.authoringtemplate.domain.Relationship;
import org.snomed.authoringtemplate.domain.SimpleSlot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.google.common.collect.Iterables;

@Service
public class TemplateConceptCreateService {

	private static final Pattern SIX_TO_EIGHTEEN_DIGITS = Pattern.compile("\\d{6,18}");
	
	@Value("${batch.maxSize}")
	private int batchMaxSize;
	
	@Autowired
	private TemplateService templateService;
	
	@Autowired
	private SnowOwlRestClientFactory terminologyClientFactory;

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	public List<ConceptOutline> generateConcepts(String branchPath, String templateName, InputStream inputStream) throws IOException, ServiceException {
		Assert.notNull(inputStream, "Batch file is required.");

		ConceptTemplate template = templateService.loadOrThrow(templateName);
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

		logger.info("Generating batch of {} concepts on branch '{}' using template '{}'", batchSize, branchPath, templateName);

		// Validate values against slot constraints, column at a time
		int slotIndex = -1;
		try {
			SnowOwlRestClient client = terminologyClientFactory.getClient();
			for (SimpleSlot simpleSlot : slotsRequiringInput) {
				slotIndex++;
				Set<String> slotValues = new HashSet<>(columnValues.get(slotIndex));
				Set<String> invalidSlotValues = new HashSet<>(slotValues);
				String slotEcl = simpleSlot.getAllowableRangeECL();
				for (List<String> slotValuePartition : Iterables.partition(slotValues, 100)) {
					StringBuilder validationEcl = new StringBuilder()
							.append("(")
							.append(slotEcl)
							.append(") AND (");
					for (String slotValue : slotValuePartition) {
						validationEcl.append(slotValue)
								.append(" OR ");
					}
					// Remove last OR
					validationEcl.delete(validationEcl.length() - 4, validationEcl.length());
					validationEcl.append(")");

					Set<String> validSlotValues = client.eclQuery(branchPath, validationEcl.toString(), slotValuePartition.size());
					invalidSlotValues.removeAll(validSlotValues);
				}
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
		return relationships.stream().filter(r -> templateService.isSlotRequiringInput(r.getTargetSlot()))
				.map(Relationship::getTargetSlot).collect(Collectors.toList());
	}
}
