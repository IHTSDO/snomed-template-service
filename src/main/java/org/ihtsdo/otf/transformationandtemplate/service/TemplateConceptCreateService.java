package org.ihtsdo.otf.transformationandtemplate.service;

import com.google.common.collect.Iterables;
import org.ihtsdo.otf.transformationandtemplate.rest.error.InputError;
import org.ihtsdo.otf.transformationandtemplate.service.exception.ServiceException;
import org.ihtsdo.otf.rest.client.RestClientException;
import org.ihtsdo.otf.rest.client.terminologyserver.SnowOwlRestClient;
import org.ihtsdo.otf.rest.client.terminologyserver.SnowOwlRestClientFactory;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.ConceptPojo;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.DescriptionPojo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snomed.authoringtemplate.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

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
		List<Relationship> relationships = conceptOutline.getClassAxioms().stream().findFirst().get().getRelationships();
		List<SimpleSlot> slotsRequiringInput = TemplateUtil.getSlotsRequiringInput(relationships);
		List<List<String>> slotColumnValues = getSlotInputValues(inputStream, template);
		int batchSize = slotColumnValues.get(0).size();
		logger.info("Validating {} slot value concepts on branch '{}'", batchSize, branchPath);
		validateSlotValues(branchPath, slotsRequiringInput, slotColumnValues);
		logger.info("Generating batch of {} concepts on branch '{}' using template '{}'", batchSize, branchPath, templateName);
		return createConceptsWithSlotValues(branchPath, slotsRequiringInput, slotColumnValues, template);
		
	}

	private List<ConceptOutline> createConceptsWithSlotValues(String branchPath, List<SimpleSlot> slotsRequiringInput,
			List<List<String>> slotColumnValues, ConceptTemplate template) throws ServiceException {
		List<String> additionalSlots = template.getAdditionalSlots();
		// Generate unsaved concepts
		List<String> slotNames = slotsRequiringInput.stream()
						.filter(slot -> slot.getSlotName() != null)
						.map(SimpleSlot::getSlotName)
						.collect(Collectors.toList());
		slotNames.addAll(additionalSlots);
		
		List<ConceptOutline> generatedConcepts = constructConceptOutlines(template, slotColumnValues, slotNames);
				
		// clone each template description into all concepts
		template.getConceptOutline().getDescriptions().forEach(description -> generatedConcepts.forEach(concept -> concept.addDescription(description.clone())));

		for (int i = 0; i < generatedConcepts.size(); i++) {
			List<String> slotRowValues = new ArrayList<>();
			for (int k = 0; k < slotNames.size(); k ++) {
				if (i < slotColumnValues.get(k).size()) {
					slotRowValues.add(slotColumnValues.get(k).get(i));
				}
			}
			Map<String, Set<DescriptionPojo>> slotValuesMap = createSlotConceptPojoMap(branchPath, slotNames, slotRowValues, additionalSlots.size());
			List<Description> transformed = LexicalTemplateTransformService.transformDescriptions(template.getLexicalTemplates(), generatedConcepts.get(i).getDescriptions(), slotValuesMap);
			generatedConcepts.get(i).setDescriptions(transformed);
		}
		return generatedConcepts;
	}
	
	private List<ConceptOutline> constructConceptOutlines(ConceptTemplate template, List<List<String>> slotColumnValues, List<String> slotNames) {
		// convert columnValues into values by row
		List<List<String>> slotValuesByRow = new ArrayList<>();
		for (int i = 0; i < slotColumnValues.get(0).size(); i++) {
			List<String> values = new ArrayList<>();
			for (int k = 0; k < slotColumnValues.size(); k++) {
				values.add(slotColumnValues.get(k).get(i));
			}
			slotValuesByRow.add(values);
		}
		List<ConceptOutline> generatedConcepts = new ArrayList<>();
		IntStream.range(0, slotValuesByRow.size()).forEach(i -> generatedConcepts.add(constructConceptOutline(template, slotValuesByRow.get(i), slotNames)));
		return generatedConcepts;
	}
	
	private ConceptOutline constructConceptOutline(ConceptTemplate template, List<String> slotValues, List<String> slotNames) {
		ConceptOutline conceptOutline = new ConceptOutline();
		
		for (Axiom axiom : template.getConceptOutline().getClassAxioms()) {
			List<Relationship> relationships = new ArrayList<>();
			for (Relationship relationship : axiom.getRelationships()) {
				SimpleSlot targetSlot = relationship.getTargetSlot();
				if (targetSlot == null) {
					relationships.add(relationship.clone());
				}
				if (targetSlot != null) {
					String slotName = targetSlot.getSlotName();
					int valueIndex;
					if (slotName != null) {
						valueIndex = slotNames.indexOf(slotName);
					} else {
						valueIndex = slotNames.indexOf(targetSlot.getSlotReference());
					}
					String slotValue = valueIndex == -1 ? "" : slotValues.get(valueIndex);
					if (slotValue.trim().isEmpty() && TemplateUtil.isOptional(relationship)) {
						// skip as it is optional 
					} else {
						relationships.add(relationship.clone().setTarget(new ConceptMini(slotValue)));
					}
				}
			}
			Axiom generated = new Axiom();
			generated.setRelationships(relationships);
			conceptOutline.addAxiom(generated);
		}
		return conceptOutline;
	}

	private Map<String, Set<DescriptionPojo>> createSlotConceptPojoMap(String branchPath, List<String> slotNames, List<String> slotValues, int additionalSlots) throws ServiceException {
		Map<String, Set<DescriptionPojo>> slotValueMap = new HashMap<>();
		SnowOwlRestClient client = terminologyClientFactory.getClient();
		Map<String, ConceptPojo> coneptIdPojoMap = new HashMap<>();
		try {
			List<String> conceptIds = slotValues.subList(0, slotValues.size() - additionalSlots)
					.stream()
					.filter(Objects::nonNull)
					.collect(Collectors.toList());
			List<ConceptPojo> conceptPojos = client.searchConcepts(branchPath, conceptIds);
			for (ConceptPojo pojo : conceptPojos) {
				coneptIdPojoMap.put(pojo.getConceptId(), pojo);
			}
		} catch (RestClientException e) {
			throw new ServiceException("Failed to get FSNs for concepts from branch " + branchPath, e);
		}
		for (int i = 0; i < slotNames.size(); i++) {
			if (i < (slotValues.size() - additionalSlots)) {
				if (coneptIdPojoMap.get(slotValues.get(i)) != null) {
					slotValueMap.put(slotNames.get(i), coneptIdPojoMap.get(slotValues.get(i)).getDescriptions());
				} 
			} else {
				slotValueMap.put(slotNames.get(i), constructDescriptionPojoSet(slotValues.get(i), DescriptionType.FSN.name()));
			}
		}
		return slotValueMap;
	}

	private Set<DescriptionPojo> constructDescriptionPojoSet(String term, String type) {
		Set<DescriptionPojo> result = new HashSet<>();
		DescriptionPojo pojo = new DescriptionPojo();
		pojo.setTerm(term);
		pojo.setType(type);
		pojo.setCaseSignificance(CaseSignificance.ENTIRE_TERM_CASE_SENSITIVE.name());
		result.add(pojo);
		return result;
	}

	private void validateSlotValues(String branchPath, List<SimpleSlot> slotsRequiringInput, List<List<String>> slotInputValues) throws ServiceException {
		// Validate values against slot constraints, column at a time
		int slotIndex = -1;
		List<String> errorMessages = new ArrayList<>();
		try {
			SnowOwlRestClient client = terminologyClientFactory.getClient();
			for (SimpleSlot simpleSlot : slotsRequiringInput) {
				slotIndex++;
				Set<String> slotValuesToValidate = slotInputValues.get(slotIndex)
						.stream()
						.filter(v -> !v.trim().isEmpty())
						.collect(Collectors.toSet());
				Set<String> invalidSlotValues = new HashSet<>(slotValuesToValidate);
				String slotEcl = simpleSlot.getAllowableRangeECL();
				for (List<String> slotValuePartition : Iterables.partition(slotValuesToValidate, 100)) {
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
	}
	
	private List<List<String>> getSlotInputValues(InputStream inputStream, ConceptTemplate template) throws IOException {
		List<List<String>> columnValues = new ArrayList<>();
		List<String> errorMessages = new ArrayList<>();
		List<Relationship> relationships = template.getConceptOutline().getClassAxioms().stream().findFirst().get().getRelationships();
		List<SimpleSlot> slotsRequiringInput = TemplateUtil.getSlotsRequiringInput(relationships);
		List<String> additionalSlots = template.getAdditionalSlots();
		int expectedColumnCount = slotsRequiringInput.size() + additionalSlots.size();
		// Read input file
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
			// Validate header
			String header = reader.readLine();
			validateHeader(header, expectedColumnCount);
			List<Integer> optionalFieldIndexes = getOptionalFields(header);
			// Collect values with basic validation
			LongStream.range(0, expectedColumnCount).forEach(v -> columnValues.add(new ArrayList<>()));
			String line;
			int lineNum = 1;
			while ((line = reader.readLine()) != null) {
				lineNum++;
				if (line.trim().isEmpty()) {
					continue;
				}
				String[] values = line.split("\\t", -1);
				if (values.length != expectedColumnCount) {
					errorMessages.add(String.format("Line %s has %s columns, expecting %s", lineNum, values.length, expectedColumnCount));
				}
				for (int column = 0; column < values.length; column++) {
					String conceptId = values[column].trim();
					if (column < slotsRequiringInput.size() && !isValidConceptId(conceptId)) {
						if (!optionalFieldIndexes.contains(column)) {
							errorMessages.add(getError(conceptId, "is not a valid concept identifier", lineNum, column));
						}
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
		return columnValues;
	}
	
	private List<Integer> getOptionalFields(String header) {
		List<Integer> result = new ArrayList<>();
		String[] columns = header.split("\\t", -1);
		for (int i=0; i < columns.length; i++) {
			if (columns[i].endsWith(TemplateService.OPTIONAL)) {
				result.add(i);
			}
		}
		return result;
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

	private void validateHeader(String header, int expectedColumnCount) {
		Assert.notNull(header, "Input file is empty.");
		String[] columns = header.split("\\t");
		int columnCount = columns.length;
		Assert.isTrue(columnCount == expectedColumnCount, String.format("There are %s slots requiring input in the selected template" +
				" is but the header line of the input file has %s columns.", expectedColumnCount, columnCount));
	}
}
