package org.ihtsdo.otf.authoringtemplate.domain;

import org.springframework.beans.BeanUtils;

public class Relationship {

	private String characteristicType = "STATED_RELATIONSHIP";
	private int groupId;
	private ConceptMini type;
	private ConceptMini target;
	private SimpleSlot targetSlot;
	private String cardinalityMin;
	private String cardinalityMax;

	public Relationship() {
	}

	public String getCharacteristicType() {
		return characteristicType;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public ConceptMini getType() {
		return type;
	}

	public void setType(ConceptMini type) {
		this.type = type;
	}

	public ConceptMini getTarget() {
		return target;
	}

	public Relationship setTarget(ConceptMini target) {
		this.target = target;
		return this;
	}

	public SimpleSlot getTargetSlot() {
		return targetSlot;
	}

	public void setTargetSlot(SimpleSlot targetSlot) {
		this.targetSlot = targetSlot;
	}

	public void setCardinalityMin(String cardinalityMin) {
		this.cardinalityMin = cardinalityMin;
	}

	public String getCardinalityMin() {
		return cardinalityMin;
	}

	public void setCardinalityMax(String cardinalityMax) {
		this.cardinalityMax = cardinalityMax;
	}

	public String getCardinalityMax() {
		return cardinalityMax;
	}

	public Relationship clone() {
		Relationship clone = new Relationship();
		BeanUtils.copyProperties(this, clone);
		return clone;
	}

	@Override
	public String toString() {
		return "Relationship{" +
				"characteristicType='" + characteristicType + '\'' +
				", groupId=" + groupId +
				", type=" + type +
				", target=" + target +
				", targetSlot=" + targetSlot +
				", cardinalityMin='" + cardinalityMin + '\'' +
				", cardinalityMax='" + cardinalityMax + '\'' +
				'}';
	}
}
