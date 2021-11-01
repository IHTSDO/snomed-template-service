package org.ihtsdo.otf.transformationandtemplate.domain;

import org.ihtsdo.otf.rest.client.terminologyserver.pojo.ConceptMiniPojo;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.IConcept;

public class Concept extends ConceptMiniPojo implements IConcept {
	private boolean active;
	private String definitionStatus;
	private String moduleId;
	
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public String getDefinitionStatus() {
		return definitionStatus;
	}
	public void setDefinitionStatus(String definitionStatus) {
		this.definitionStatus = definitionStatus;
	}
	public String getModuleId() {
		return moduleId;
	}
	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}
	public String getId() {
		return getConceptId();
	}
	public String getFsnTerm() {
		return super.getFsn().getTerm();
	}
	@Override
	public String toString() {
		return getId() + "|" + getFsnTerm() + "|";
	}
}
