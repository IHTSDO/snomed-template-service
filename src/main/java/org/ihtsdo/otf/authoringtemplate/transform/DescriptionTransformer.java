package org.ihtsdo.otf.authoringtemplate.transform;

import static org.ihtsdo.otf.authoringtemplate.service.Constants.ACCEPTABLE;
import static org.ihtsdo.otf.authoringtemplate.service.Constants.PREFERRED;
import static org.snomed.authoringtemplate.domain.DescriptionType.FSN;
import static org.snomed.authoringtemplate.domain.DescriptionType.SYNONYM;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.ihtsdo.otf.authoringtemplate.service.exception.ServiceException;
import org.ihtsdo.otf.rest.client.snowowl.pojo.ConceptPojo;
import org.ihtsdo.otf.rest.client.snowowl.pojo.DescriptionPojo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snomed.authoringtemplate.domain.ConceptOutline;
import org.snomed.authoringtemplate.domain.Description;

public class DescriptionTransformer {
	private static final String TERM_SLOT_INDICATOR = "$";
	private ConceptPojo conceptToTransform;
	private ConceptOutline conceptOutline;
	private Map<String, String> slotValueMap;
	private String inactivationReason;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DescriptionTransformer.class);
	
	public DescriptionTransformer(ConceptPojo conceptToTransform, ConceptOutline conceptOutline,
			Map<String, String> slotValueMap, String inactivationReason) {
		this.conceptToTransform = conceptToTransform;
		this.conceptOutline = conceptOutline;
		this.slotValueMap = slotValueMap;
		this.inactivationReason = inactivationReason;
	}

	public void transform() throws ServiceException {
		Map<String, DescriptionPojo> previousActiveTermMap = new HashMap<>();
		for (DescriptionPojo pojo : conceptToTransform.getDescriptions()) {
			if (pojo.isActive()) {
				previousActiveTermMap.put(pojo.getTerm(), pojo);
			}
		}
		List<DescriptionPojo> newDescriptions = new ArrayList<>();
		String newFsn = null;
		List<String> newPts = new ArrayList<>();
		if (conceptOutline.getDescriptions() != null) {
			for (Description desc : conceptOutline.getDescriptions()) {
				String term = desc.getTerm();
				if (desc.getTermTemplate() != null) {
					term = desc.getTermTemplate();
					for (String slot : slotValueMap.keySet()) {
						term = term.replace(TERM_SLOT_INDICATOR + slot + TERM_SLOT_INDICATOR, slotValueMap.get(slot).toLowerCase());
					}
				}
				if (FSN == desc.getType()) {
					newFsn = term;
				} else {
					if (desc.getAcceptabilityMap().values().contains(PREFERRED)) {
						newPts.add(term);
					}
				}
				if (!previousActiveTermMap.keySet().contains(term)) {
					DescriptionPojo descPojo = conscturctDescriptionPojo(desc, term);
					descPojo.setConceptId(conceptToTransform.getConceptId());
					newDescriptions.add(descPojo);
				} else {
					//update Acceptability
					if (desc.getAcceptabilityMap() != null && !desc.getAcceptabilityMap().isEmpty()) {
						previousActiveTermMap.get(term).setAcceptabilityMap(desc.getAcceptabilityMap());
					}
				}
			}
		}
		
		for (DescriptionPojo pojo : conceptToTransform.getDescriptions()) {
			if (FSN.name().equals(pojo.getType())) {
				if (newFsn != null && !newFsn.equals(pojo.getTerm())) {
					pojo.setActive(false);
					pojo.setInactivationIndicator(inactivationReason);
					pojo.setEffectiveTime(null);
				}
			} else if (SYNONYM.name().equals(pojo.getType())){
				if (pojo.getAcceptabilityMap() != null && pojo.getAcceptabilityMap().values().contains(PREFERRED)) {
					if (!newPts.contains(pojo.getTerm())) {
						updateAcceptablityMap(pojo.getAcceptabilityMap(), ACCEPTABLE);
					}
				}
			}
		}
		
		List<DescriptionPojo> updated = new ArrayList<>();
		updated.addAll(newDescriptions);
		int counter = 0;
		for (DescriptionPojo pojo : conceptToTransform.getDescriptions()) {
			if (pojo.isActive() || pojo.isReleased()) {
				updated.add(pojo);
			} else {
				counter++;
			}
		}
		if (counter > 0) {
			LOGGER.info("Total unpublished inactive descriptions removed:" + counter);
		}
		Set<DescriptionPojo> descriptions = new TreeSet<DescriptionPojo>( getDescriptionPojoComparator());
		descriptions.addAll(updated);
		if (updated.size() != descriptions.size()) {
			throw new ServiceException(String.format("The total sorted descriptions %s doesn't match the total before sorting %s", descriptions.size(), updated.size()));
		}
		conceptToTransform.setDescriptions(descriptions);
	}
	
	

	private void updateAcceptablityMap(Map<String, String> acceptabilityMap, String newValue) {
		for (String refsetId : acceptabilityMap.keySet()) {
			acceptabilityMap.put(refsetId, newValue);
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
		pojo.setModuleId(getModuleId());
		return pojo;
	}
	
	private String getModuleId() {
		return conceptOutline.getModuleId() !=null ? conceptOutline.getModuleId() : conceptToTransform.getModuleId();
	}

	public static Comparator<DescriptionPojo> getDescriptionPojoComparator() {
		Comparator<DescriptionPojo> comparator = Comparator
				.comparing(DescriptionPojo::getType, Comparator.naturalOrder())
				.thenComparing(DescriptionPojo:: isActive, Comparator.nullsFirst(Boolean::compareTo).reversed())
				.thenComparing(DescriptionPojo::getDescriptionId, Comparator.nullsFirst(String::compareTo))
				.thenComparing(DescriptionPojo::getTerm, Comparator.naturalOrder());
		return comparator;
	}
}
