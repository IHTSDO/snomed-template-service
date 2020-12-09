package org.ihtsdo.otf.transformationandtemplate.service.client;

import org.ihtsdo.otf.rest.client.terminologyserver.pojo.CodeSystem;

import java.util.Map;

public class AuthoringProject {
	private String key;
	private String title;
	private String branchPath;
	private String branchState;
	private Long branchHeadTimestamp;
	private Long branchBaseTimestamp;
	private Map<String, Object> metadata;
	private CodeSystem codeSystem;


	public String getKey() {
		return key;
	}

	public String getTitle() {
		return title;
	}

	public String getBranchPath() {
		return branchPath;
	}

	public Long getBranchHeadTimestamp() {
		return branchHeadTimestamp;
	}

	public void setBranchHeadTimestamp(Long branchHeadTimestamp) {
		this.branchHeadTimestamp = branchHeadTimestamp;
	}

	public Long getBranchBaseTimestamp() {
		return branchBaseTimestamp;
	}

	public void setBranchBaseTimestamp(Long branchBaseTimestamp) {
		this.branchBaseTimestamp = branchBaseTimestamp;
	}

	public String getBranchState() {
		return branchState;
	}

	public Map<String, Object> getMetadata() {
		return metadata;
	}

	public void setMetadata(Map<String, Object> metadata) {
		this.metadata = metadata;
	}

	public void setCodeSystem(CodeSystem codeSystem) {
		this.codeSystem = codeSystem;
	}

	public CodeSystem getCodeSystem() {
		return codeSystem;
	}
}
