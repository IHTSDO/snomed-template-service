package org.ihtsdo.otf.authoringtemplate.transform;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.snomed.authoringtemplate.domain.*;
import org.ihtsdo.otf.rest.client.snowowl.pojo.*;

public class DescriptionTransformer {

	private ConceptPojo conceptToTransform;
	private ConceptOutline conceptOutline;
	private Map<String, String> slotValueMap;
	private String inactivationReason;
	
	public DescriptionTransformer(ConceptPojo conceptToTransform, ConceptOutline conceptOutline,
			Map<String, String> slotValueMap, String inactivationReason) {
		this.conceptToTransform = conceptToTransform;
		this.conceptOutline = conceptOutline;
		this.slotValueMap = slotValueMap;
		this.inactivationReason = inactivationReason;
	}

	public void transform() {
		List<String> previousTerms = conceptToTransform.getDescriptions().stream()
				.filter(t -> t.isActive())
				.map(t -> t.getTerm())
				.collect(Collectors.toList());
		List<String> newTerms = new ArrayList<>();
		if (conceptOutline.getDescriptions() != null) {
			for (Description desc : conceptOutline.getDescriptions()) {
				String term = desc.getTerm();
				if (desc.getTermTemplate() != null) {
					term = desc.getTermTemplate();
					for (String slot : slotValueMap.keySet()) {
						term = term.replace(slot, slotValueMap.get(slot));
					}
				}
				newTerms.add(term);
				if (!previousTerms.contains(term)) {
					DescriptionPojo descPojo = conscturctDescriptionPojo(desc, term);
					descPojo.setConceptId(conceptToTransform.getConceptId());
					descPojo.setModuleId(conceptOutline.getModuleId());
					conceptToTransform.add(descPojo);
				}
			}
		}
		//inactivation
		//TODO need to clarify that we need to in-activate all active terms that don't exist in the new template
		for (DescriptionPojo pojo : conceptToTransform.getDescriptions()) {
			if (pojo.isActive() && !newTerms.contains(pojo.getTerm())) {
				pojo.setActive(false);
				pojo.setEffectiveTime(null);
				pojo.setInactivationIndicator(inactivationReason);
			}
		}
	}
	
	private DescriptionPojo conscturctDescriptionPojo(Description desc, String term) {
		DescriptionPojo pojo = new DescriptionPojo();
		pojo.setAcceptabilityMap(desc.getAcceptabilityMap());
		pojo.setActive(true);
		pojo.setCaseSignificance(desc.getCaseSignificance().name());
		pojo.setTerm(term);
		pojo.setType(desc.getType().name());
		pojo.setLang(desc.getLang());
		return pojo;
	}
}
