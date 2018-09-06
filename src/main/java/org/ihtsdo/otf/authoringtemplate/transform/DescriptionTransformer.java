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

import org.ihtsdo.otf.authoringtemplate.service.LexicalTemplateTransformService;
import org.ihtsdo.otf.authoringtemplate.service.exception.ServiceException;
import org.ihtsdo.otf.rest.client.snowowl.pojo.ConceptPojo;
import org.ihtsdo.otf.rest.client.snowowl.pojo.DescriptionPojo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snomed.authoringtemplate.domain.ConceptOutline;
import org.snomed.authoringtemplate.domain.ConceptTemplate;
import org.snomed.authoringtemplate.domain.Description;

public class DescriptionTransformer {
	private ConceptPojo conceptToTransform;
	private Map<String, String> slotValueMap;
	private String inactivationReason;
	private ConceptTemplate conceptTemplate;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DescriptionTransformer.class);
	
	public DescriptionTransformer(ConceptPojo conceptToTransform, ConceptTemplate conceptTemplate,
			Map<String, String> slotValueMap, String inactivationReason) {
		this.conceptToTransform = conceptToTransform;
		this.conceptTemplate = conceptTemplate;
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
		ConceptOutline conceptOutline = conceptTemplate.getConceptOutline();
		String moduleId = conceptToTransform.getModuleId();
		if (conceptTemplate.getConceptOutline().getModuleId() !=null) {
			moduleId = conceptTemplate.getConceptOutline().getModuleId();
		}
		LexicalTemplateTransformService.transformDescriptions(conceptTemplate.getLexicalTemplates(), 
				conceptOutline.getDescriptions(), slotValueMap);
		for (Description desc : conceptOutline.getDescriptions()) {
			String term = desc.getTerm();
			if (FSN == desc.getType()) {
				newFsn = term;
			} else {
				if (desc.getAcceptabilityMap().values().contains(PREFERRED)) {
					newPts.add(term);
				}
			}
			if (!previousActiveTermMap.keySet().contains(term)) {
				DescriptionPojo descPojo = conscturctDescriptionPojo(desc, term, moduleId);
				descPojo.setConceptId(conceptToTransform.getConceptId());
				newDescriptions.add(descPojo);
			} else {
				//update Acceptability
				if (desc.getAcceptabilityMap() != null && !desc.getAcceptabilityMap().isEmpty()) {
					previousActiveTermMap.get(term).setAcceptabilityMap(desc.getAcceptabilityMap());
				}
			}
		}
		
		for (DescriptionPojo pojo : conceptToTransform.getDescriptions()) {
			if (!pojo.isActive()) {
				continue;
			}
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

	private DescriptionPojo conscturctDescriptionPojo(Description desc, String term, String moduleid) {
		DescriptionPojo pojo = new DescriptionPojo();
		pojo.setAcceptabilityMap(desc.getAcceptabilityMap());
		pojo.setActive(true);
		pojo.setCaseSignificance(desc.getCaseSignificance().name());
		pojo.setTerm(term);
		pojo.setType(desc.getType().name());
		pojo.setLang(desc.getLang());
		pojo.setModuleId(moduleid);
		return pojo;
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
