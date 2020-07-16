package org.ihtsdo.otf.transformationandtemplate.service.componenttransform;

import com.google.common.base.Strings;
import org.apache.commons.lang.StringUtils;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.AxiomPojo;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.RelationshipPojo;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.SnomedComponent;
import org.ihtsdo.otf.rest.exception.BusinessServiceException;
import org.ihtsdo.otf.rest.exception.ProcessingException;
import org.ihtsdo.otf.transformationandtemplate.domain.ChangeType;
import org.ihtsdo.otf.transformationandtemplate.domain.ComponentTransformationRequest;
import org.ihtsdo.otf.transformationandtemplate.domain.TransformationRecipe;
import org.ihtsdo.otf.transformationandtemplate.service.Concepts;
import org.ihtsdo.otf.transformationandtemplate.service.client.ChangeResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snomed.otf.owltoolkit.conversion.AxiomRelationshipConversionService;
import org.snomed.otf.owltoolkit.conversion.ConversionException;
import org.snomed.otf.owltoolkit.domain.AxiomRepresentation;
import org.snomed.otf.owltoolkit.domain.Relationship;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;

import static java.lang.String.format;
import static org.ihtsdo.otf.utils.SnomedIdentifierUtils.isValidConceptIdFormat;
import static org.ihtsdo.otf.utils.SnomedIdentifierUtils.isValidRefsetMemberIdFormat;

@Service
public class AxiomService {

	@Autowired
	private TransformationInputStreamFactory transformationStreamFactory;

	@Autowired
	private HighLevelAuthoringServiceFactory authoringServiceFactory;

	private final AxiomRelationshipConversionService axiomRelationshipConversionService = new AxiomRelationshipConversionService(Collections.emptySet());

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private static final List<Function<AxiomPojo, String>> SIMPLE_VALIDATION_FUNCTIONS = new ArrayList<>(Arrays.asList(
			axiomPojo -> axiomPojo.getConceptId() != null &&
					isValidConceptIdFormat(axiomPojo.getConceptId()) ? null : "Concept id format",
			axiomPojo -> StringUtils.isEmpty(axiomPojo.getAxiomId()) ||
					isValidRefsetMemberIdFormat(axiomPojo.getAxiomId()) ? null : "Axiom id format (check characters lowercase)",
			axiomPojo -> axiomPojo.getOwlExpression() != null &&
					axiomPojo.getOwlExpression().length() > 10 ? null : "owlExpression length"
	));

	public List<ChangeResult<? extends SnomedComponent>> startBatchTransformation(TransformationRecipe recipe, ComponentTransformationRequest request) throws BusinessServiceException {
		if (recipe.getChangeType() == ChangeType.UPDATE) {
			return updateAxioms(recipe, request);
		}
		throw new ProcessingException(format("Change type %s for component %s is not implemented.", recipe.getChangeType(), recipe.getChangeType()));
	}

	private List<ChangeResult<? extends SnomedComponent>> updateAxioms(TransformationRecipe recipe, ComponentTransformationRequest request) throws BusinessServiceException {
		HighLevelAuthoringService authoringServiceForCurrentUser = authoringServiceFactory.createServiceForCurrentUser();
		List<ChangeResult<AxiomPojo>> changes = new ArrayList<>();
		List<AxiomPojo> axioms = new ArrayList<>();
		readAxiomChanges(request, recipe, changes, axioms);
		return authoringServiceForCurrentUser.updateAxioms(request, axioms, changes);
	}

	private void readAxiomChanges(ComponentTransformationRequest request, TransformationRecipe recipe, List<ChangeResult<AxiomPojo>> changes, List<AxiomPojo> axioms) throws BusinessServiceException {
		try (TransformationStream transformationStream = transformationStreamFactory.createTransformationStream(recipe, request)) {
			ComponentTransformation componentTransformation;
			while ((componentTransformation = transformationStream.next()) != null) {
				AxiomPojo axiom = new AxiomPojo();
				ChangeResult<AxiomPojo> changeResult = new ChangeResult<>(axiom);
				changes.add(changeResult);

				axiom.setConceptId(componentTransformation.getValueString("conceptId"));
				axiom.setAxiomId(componentTransformation.getValueString("axiomId"));
				axiom.setOwlExpression(componentTransformation.getValueString("owlExpression"));

				// Simple validation
				if (valid(axiom, changeResult)) {
					axioms.add(axiom);
				}

				String owlExpression = axiom.getOwlExpression();
				if (!Strings.isNullOrEmpty(owlExpression)) {
					try {
						AxiomRepresentation axiomRepresentation = axiomRelationshipConversionService.convertAxiomToRelationships(owlExpression);
						if (axiomRepresentation == null) {
							// Will be null for property chains, property behaviours and other ontology axioms.
							changeResult.fail("This type of OWL expression is not supported by this process.");
							continue;
						}
						if (axiomRepresentation.getLeftHandSideNamedConcept() != null) {
							if (!axiom.getConceptId().equals(axiomRepresentation.getLeftHandSideNamedConcept().toString())) {
								changeResult.fail(format("OWL expression left hand side named concept \"%s\" does not match given conceptId \"%s\".",
										axiomRepresentation.getLeftHandSideNamedConcept(), axiom.getConceptId()));
								continue;
							}
							axiom.setDefinitionStatusId(axiomRepresentation.isPrimitive() ? Concepts.PRIMITIVE : Concepts.FULLY_DEFINED);
							axiom.setRelationships(convertToRelationships(axiomRepresentation.getRightHandSideRelationships()));
							axiom.setGci(false);
						} else if (axiomRepresentation.getRightHandSideNamedConcept() != null) {
							if (!axiom.getConceptId().equals(axiomRepresentation.getRightHandSideNamedConcept().toString())) {
								changeResult.fail(format("OWL expression right hand side named concept \"%s\" does not match given conceptId \"%s\".",
										axiomRepresentation.getRightHandSideNamedConcept(), axiom.getConceptId()));
								continue;
							}
							// GCIs are always defined
							axiom.setDefinitionStatusId(Concepts.FULLY_DEFINED);
							axiom.setRelationships(convertToRelationships(axiomRepresentation.getLeftHandSideRelationships()));
							axiom.setGci(true);
						}
					} catch (ConversionException e) {
						logger.info("OWL expression conversion to relationships failed. This is just an INFO message to help debugging.", e);
						changeResult.fail(format("OWL expression conversion to relationships failed: %s.", e.getMessage()));
					}
				}
			}
		} catch (IOException e) {
			throw new BusinessServiceException("Failed to read transformation stream.", e);
		}
		logger.info("{} of {} axioms passed simple internal checks.", axioms.size(), changes.size());
	}

	private Set<RelationshipPojo> convertToRelationships(Map<Integer, List<Relationship>> rightHandSideRelationships) {
		Set<RelationshipPojo> relationships = new HashSet<>();
		for (Integer group : rightHandSideRelationships.keySet()) {
			for (Relationship relationship : rightHandSideRelationships.get(group)) {
				relationships.add(new RelationshipPojo(group, "" + relationship.getTypeId(), "" + relationship.getDestinationId(), "STATED_RELATIONSHIP"));
			}
		}
		return relationships;
	}

	// Some basic validation like identifier formats
	private boolean valid(AxiomPojo axiom, ChangeResult<AxiomPojo> changeResult) {
		for (Function<AxiomPojo, String> validationFunction : SIMPLE_VALIDATION_FUNCTIONS) {
			String message = validationFunction.apply(axiom);
			if (message != null) {
				changeResult.fail(format("Initial validation failed: %s.", message));
				return false;
			}
		}
		return true;
	}

}
