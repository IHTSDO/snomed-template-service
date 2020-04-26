package org.ihtsdo.otf.transformationandtemplate.service.transform;

import java.util.Map;

import org.ihtsdo.otf.rest.client.terminologyserver.pojo.ConceptMiniPojo;
import org.snomed.authoringtemplate.domain.ConceptTemplate;
import org.snomed.authoringtemplate.domain.logical.Attribute;

public class TransformationInputData {

	private String branchPath;
	private ConceptTemplate destinationTemplate;
	private Map<String, ConceptMiniPojo> conceptIdMap;
	private Map<String, Attribute> slotToAttributeTypeMap;
	private TemplateTransformRequest transformRequest;

	public TransformationInputData(TemplateTransformRequest transformRequest) {
		this.transformRequest = transformRequest;
	}
	
	public TemplateTransformRequest getTransformRequest() {
		return transformRequest;
	}

	public void setTransformRequest(TemplateTransformRequest transformRequest) {
		this.transformRequest = transformRequest;
	}

	public void setBranchPath(String branchPath) {
		this.branchPath = branchPath;
	}

	public String getBranchPath() {
		return this.branchPath;
	}

	public void setDestinationTemplate(ConceptTemplate destination) {
		this.destinationTemplate = destination;
	}

	public ConceptTemplate getDestinationTemplate() {
		return destinationTemplate;
	}

	public void setDestination(ConceptTemplate destination) {
		this.destinationTemplate = destination;
	}

	public void setConceptIdMap(Map<String, ConceptMiniPojo> conceptIdMap) {
		this.conceptIdMap = conceptIdMap;
	}

	public Map<String, ConceptMiniPojo> getConceptIdMap() {
		return conceptIdMap;
	}

	public void setDestinationSlotToAttributeMap(Map<String, Attribute> slotToAttributeMap) {
		this.slotToAttributeTypeMap = slotToAttributeMap;
	}

	public Map<String, Attribute> getDestinationSlotToAttributeMap() {
		return this.slotToAttributeTypeMap;
	}
}
