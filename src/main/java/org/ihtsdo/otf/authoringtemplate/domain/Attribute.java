package org.ihtsdo.otf.authoringtemplate.domain;

public class Attribute implements HasCardinality {

	private String type;
	private String value;
	private String allowableRangeECL;
	private String cardinalityMin;
	private String cardinalityMax;
	private String slotName;

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setAllowableRangeECL(String allowableRangeECL) {
		this.allowableRangeECL = allowableRangeECL;
	}

	public String getAllowableRangeECL() {
		return allowableRangeECL;
	}

	@Override
	public void setCardinalityMin(String cardinalityMin) {
		this.cardinalityMin = cardinalityMin;
	}

	@Override
	public void setCardinalityMax(String cardinalityMax) {
		this.cardinalityMax = cardinalityMax;
	}

	public String getCardinalityMin() {
		return cardinalityMin;
	}

	public String getCardinalityMax() {
		return cardinalityMax;
	}

	public void setSlotName(String slotName) {
		this.slotName = slotName;
	}

	public String getSlotName() {
		return slotName;
	}
}
