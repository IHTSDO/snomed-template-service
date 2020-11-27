package org.ihtsdo.otf.transformationandtemplate.service.client;

import org.ihtsdo.otf.rest.client.terminologyserver.pojo.DescriptionPojo;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.SnomedComponent;

public class DescriptionReplacementPojo implements SnomedComponent {

    private DescriptionPojo inactivatedDescription;

    private DescriptionPojo createdDescription;

    private DescriptionPojo updatedDescription;

    public String getId() {
        return inactivatedDescription != null ? inactivatedDescription.getDescriptionId() : null;
    }

    public String getConceptId() {
        return inactivatedDescription != null ? inactivatedDescription.getConceptId() : null;
    }

    public DescriptionPojo getInactivatedDescription() {
        return inactivatedDescription;
    }

    public void setInactivatedDescription(DescriptionPojo inactivatedDescription) {
        this.inactivatedDescription = inactivatedDescription;
    }

    public DescriptionPojo getCreatedDescription() {
        return createdDescription;
    }

    public void setCreatedDescription(DescriptionPojo createdDescription) {
        this.createdDescription = createdDescription;
    }

    public DescriptionPojo getUpdatedDescription() {
        return updatedDescription;
    }

    public void setUpdatedDescription(DescriptionPojo updatedDescription) {
        this.updatedDescription = updatedDescription;
    }

    @Override
    public String toString() {
        return "DescriptionReplacementPojo{" +
                "descriptionId='" + (getInactivatedDescription() != null ? getInactivatedDescription().getDescriptionId() : "") + '\'' +
                ", replacementDescriptionId=" + (getUpdatedDescription() != null ? getUpdatedDescription().getDescriptionId() : "") +
                ", newReplacementTerm='" + (getCreatedDescription() != null ? getCreatedDescription().getDescriptionId() : "") + '\'' +
                '}';
    }
}
