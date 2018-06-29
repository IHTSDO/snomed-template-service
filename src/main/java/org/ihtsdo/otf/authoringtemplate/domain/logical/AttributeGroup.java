package org.ihtsdo.otf.authoringtemplate.domain.logical;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AttributeGroup implements HasCardinality {

	private List<Attribute> attributes;
	private String cardinalityMin = "1";  //Default cardinality is 1 to many
	private String cardinalityMax = "*";

	public AttributeGroup() {
		attributes = new ArrayList<>();
	}

	public void addAttribute(Attribute attribute) {
		attributes.add(attribute);
	}
	
	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}

	public List<Attribute> getAttributes() {
		return attributes;
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

	public String toStringVerbose() {
		StringBuilder builder = new StringBuilder();
		builder.append("AttributeGroup [");
		if (attributes != null)
			builder.append("attributes=").append(attributes).append(", ");
		if (cardinalityMin != null)
			builder.append("cardinalityMin=").append(cardinalityMin).append(", ");
		if (cardinalityMax != null)
			builder.append("cardinalityMax=").append(cardinalityMax);
		builder.append("]");
		return builder.toString();
	}
	
	@Override
	public String toString() {
		return (cardinalityMin == null? "": "[[~" + cardinalityMin + ".." + cardinalityMax + "]]") +
				"{" + attributes.stream()
				.map(attribute -> attribute.toString())
				.collect (Collectors.joining(", ")) 
				+ "}";
	}
}
