package org.ihtsdo.otf.authoringtemplate.transform;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.ihtsdo.otf.rest.client.snowowl.pojo.ConceptPojo;
import org.ihtsdo.otf.rest.client.snowowl.pojo.DescriptionPojo;
import org.snomed.authoringtemplate.domain.ConceptOutline;
import org.snomed.authoringtemplate.domain.Description;

public class DescriptionTransformer {
	private static final String TERM_SLOT_INDICATOR = "$";
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
		
		Map<String, DescriptionPojo> previousActiveTermMap = new HashMap<>();
		for (DescriptionPojo pojo : conceptToTransform.getDescriptions()) {
			if (pojo.isActive()) {
				previousActiveTermMap.put(pojo.getTerm(), pojo);
			}
		}
		
		List<String> newTerms = new ArrayList<>();
		if (conceptOutline.getDescriptions() != null) {
			for (Description desc : conceptOutline.getDescriptions()) {
				String term = desc.getTerm();
				if (desc.getTermTemplate() != null) {
					term = desc.getTermTemplate();
					for (String slot : slotValueMap.keySet()) {
						term = term.replace(TERM_SLOT_INDICATOR + slot + TERM_SLOT_INDICATOR, slotValueMap.get(slot).toLowerCase());
					}
				}
				newTerms.add(term);
				if (!previousActiveTermMap.keySet().contains(term)) {
					DescriptionPojo descPojo = conscturctDescriptionPojo(desc, term);
					descPojo.setConceptId(conceptToTransform.getConceptId());
					conceptToTransform.add(descPojo);
				} else {
					//update Acceptability
					if (desc.getAcceptabilityMap() != null && !desc.getAcceptabilityMap().isEmpty()) {
						previousActiveTermMap.get(term).setAcceptabilityMap(desc.getAcceptabilityMap());
					}
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
		Set<DescriptionPojo> descriptions = new TreeSet<DescriptionPojo>( new DescriptionPojoComparator());
		descriptions.addAll(conceptToTransform.getDescriptions());
		conceptToTransform.setDescriptions(descriptions);
	}
	
	
	private DescriptionPojo conscturctDescriptionPojo(Description desc, String term) {
		DescriptionPojo pojo = new DescriptionPojo();
		pojo.setAcceptabilityMap(desc.getAcceptabilityMap());
		pojo.setActive(true);
		pojo.setCaseSignificance(desc.getCaseSignificance().name());
		pojo.setTerm(term);
		pojo.setType(desc.getType().name());
		pojo.setLang(desc.getLang());
		pojo.setModuleId(getModuleId());
		return pojo;
	}
	
	private String getModuleId() {
		return conceptOutline.getModuleId() !=null ? conceptOutline.getModuleId() : conceptToTransform.getModuleId();
	}

	private  static class DescriptionPojoComparator implements Comparator<DescriptionPojo>{

		@Override
		public int compare(DescriptionPojo o1, DescriptionPojo o2) {
			if (o1.isActive() == o2.isActive()) {
				if (!o1.getType().equals(o2.getType())) {
					return o1.getType().compareTo(o2.getType());
				} else {
					return o1.getTerm().compareTo(o2.getTerm());
				}
			} else {
				return Boolean.valueOf(o2.isActive()).compareTo( Boolean.valueOf(o1.isActive()));
			}
		}
	}
}
