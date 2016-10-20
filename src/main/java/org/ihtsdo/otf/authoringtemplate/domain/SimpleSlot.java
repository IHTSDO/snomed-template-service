package org.ihtsdo.otf.authoringtemplate.domain;

public class SimpleSlot {

	private String slotName;
	private String allowableRangeECL;
	private String slotReference;

	public SimpleSlot() {
	}

	public SimpleSlot(String slotName, String allowableRangeECL) {
		this.slotName = slotName;
		this.allowableRangeECL = allowableRangeECL;
	}

	public SimpleSlot(String slotReference) {
		this.slotReference = slotReference;
	}

	public String getSlotName() {
		return slotName;
	}

	public void setSlotName(String slotName) {
		this.slotName = slotName;
	}

	public String getAllowableRangeECL() {
		return allowableRangeECL;
	}

	public void setAllowableRangeECL(String allowableRangeECL) {
		this.allowableRangeECL = allowableRangeECL;
	}

	public String getSlotReference() {
		return slotReference;
	}

	public void setSlotReference(String slotReference) {
		this.slotReference = slotReference;
	}

	@Override
	public String toString() {
		return "SimpleSlot{" +
				"slotName='" + slotName + '\'' +
				", allowableRangeECL='" + allowableRangeECL + '\'' +
				", slotReference='" + slotReference + '\'' +
				'}';
	}
}
