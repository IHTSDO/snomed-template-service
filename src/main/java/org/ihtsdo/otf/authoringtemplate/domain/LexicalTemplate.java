package org.ihtsdo.otf.authoringtemplate.domain;

import java.util.List;

public class LexicalTemplate {

	private String name;
	private String takeFSNFromSlot;
	private List<String> removeParts;

	public LexicalTemplate() {
	}

	public LexicalTemplate(String name, String takeFSNFromSlot, List<String> removeParts) {
		this.name = name;
		this.takeFSNFromSlot = takeFSNFromSlot;
		this.removeParts = removeParts;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTakeFSNFromSlot() {
		return takeFSNFromSlot;
	}

	public void setTakeFSNFromSlot(String takeFSNFromSlot) {
		this.takeFSNFromSlot = takeFSNFromSlot;
	}

	public List<String> getRemoveParts() {
		return removeParts;
	}

	public void setRemoveParts(List<String> removeParts) {
		this.removeParts = removeParts;
	}
}
