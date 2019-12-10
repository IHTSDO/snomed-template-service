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
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.ConceptPojo;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.DescriptionPojo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snomed.authoringtemplate.domain.ConceptOutline;
import org.snomed.authoringtemplate.domain.ConceptTemplate;
import org.snomed.authoringtemplate.domain.Description;

public class DescriptionTransformer {

	private ConceptPojo conceptToTransform;
	private Map<String, Set<DescriptionPojo>> slotValueMap;
	private String inactivationReason;
	private ConceptTemplate conceptTemplate;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DescriptionTransformer.class);

	private static final Comparator<DescriptionPojo> DESCRIPTION_POJO_COMPARATOR = Comparator
			.comparing(DescriptionPojo::getType, Comparator.naturalOrder())
			.thenComparing(DescriptionPojo::isActive, Comparator.nullsFirst(Boolean::compareTo).reversed())
			.thenComparing(DescriptionPojo::getDescriptionId, Comparator.nullsFirst(String::compareTo))
			.thenComparing(DescriptionPojo::getTerm, Comparator.naturalOrder());

	public DescriptionTransformer(ConceptPojo conceptToTransform, ConceptTemplate conceptTemplate,
			Map<String, Set<DescriptionPojo>> slotValueMap, String inactivationReason) {
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
		ConceptOutline conceptOutline = conceptTemplate.getConceptOutline();
		String moduleId = conceptToTransform.getModuleId();
		if (conceptTemplate.getConceptOutline().getModuleId() !=null) {
			moduleId = conceptTemplate.getConceptOutline().getModuleId();
		}
		
		List<Description> lexicalTransformed = LexicalTemplateTransformService.transformDescriptions(conceptTemplate.getLexicalTemplates(), 
				conceptOutline.getDescriptions(), slotValueMap);
		List<DescriptionPojo> newDescriptions = new ArrayList<>();
		String newFsn = null;
		List<String> newPts = new ArrayList<>();
		for (Description desc : lexicalTransformed) {
			String term = desc.getTerm();
			if (FSN == desc.getType()) {
				newFsn = term;
			} else {
				if (desc.getAcceptabilityMap().values().contains(PREFERRED)) {
					newPts.add(term);
				}
			}
			if (!previousActiveTermMap.keySet().contains(term)) {
				DescriptionPojo descPojo = getDescriptionPojo(desc, term, moduleId);
				descPojo.setConceptId(conceptToTransform.getConceptId());
				newDescriptions.add(descPojo);
			} else {
				// Update Acceptability
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
						updateAcceptabilityMap(pojo.getAcceptabilityMap(), ACCEPTABLE);
					}
				}
			}
		}

		List<DescriptionPojo> updated = new ArrayList<>(newDescriptions);
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
		Set<DescriptionPojo> descriptions = new TreeSet<>(DESCRIPTION_POJO_COMPARATOR);
		descriptions.addAll(updated);
		if (updated.size() != descriptions.size()) {
			throw new ServiceException(String.format("The total sorted descriptions %s doesn't match the total before sorting %s", descriptions.size(), updated.size()));
		}
		conceptToTransform.setDescriptions(descriptions);
	}

	private void updateAcceptabilityMap(Map<String, String> acceptabilityMap, String newValue) {
		for (String refsetId : acceptabilityMap.keySet()) {
			acceptabilityMap.put(refsetId, newValue);
		}
	}

	private DescriptionPojo getDescriptionPojo(Description desc, String term, String moduleId) {
		DescriptionPojo pojo = new DescriptionPojo();
		pojo.setAcceptabilityMap(desc.getAcceptabilityMap());
		pojo.setActive(true);
		pojo.setCaseSignificance(desc.getCaseSignificance().name());
		pojo.setTerm(term);
		pojo.setType(desc.getType().name());
		pojo.setLang(desc.getLang());
		pojo.setModuleId(moduleId);
		return pojo;
	}

}
