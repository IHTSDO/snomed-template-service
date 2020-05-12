package org.ihtsdo.otf.transformationandtemplate.service.template;

import org.ihtsdo.otf.rest.client.terminologyserver.pojo.ConceptPojo;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.DescriptionPojo;
import org.ihtsdo.otf.transformationandtemplate.service.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snomed.authoringtemplate.domain.ConceptOutline;
import org.snomed.authoringtemplate.domain.ConceptTemplate;
import org.snomed.authoringtemplate.domain.Description;

import java.util.*;

import static org.ihtsdo.otf.rest.client.terminologyserver.pojo.DescriptionPojo.Acceptability.ACCEPTABLE;
import static org.ihtsdo.otf.rest.client.terminologyserver.pojo.DescriptionPojo.Acceptability.PREFERRED;
import static org.snomed.authoringtemplate.domain.DescriptionType.FSN;

public class DescriptionTemplateTransformer {

	private final ConceptPojo conceptToTransform;
	private final Map<String, Set<DescriptionPojo>> slotValueMap;
	private final String inactivationReason;
	private final ConceptTemplate conceptTemplate;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DescriptionTemplateTransformer.class);

	private static final Comparator<DescriptionPojo> DESCRIPTION_POJO_COMPARATOR = Comparator
			.comparing(DescriptionPojo::getType, Comparator.naturalOrder())
			.thenComparing(DescriptionPojo::isActive, Comparator.nullsFirst(Boolean::compareTo).reversed())
			.thenComparing(DescriptionPojo::getDescriptionId, Comparator.nullsFirst(String::compareTo))
			.thenComparing(DescriptionPojo::getTerm, Comparator.naturalOrder());

	public DescriptionTemplateTransformer(ConceptPojo conceptToTransform, ConceptTemplate conceptTemplate,
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
				if (desc.getAcceptabilityMap().containsValue(PREFERRED.name())) {
					newPts.add(term);
				}
			}
			if (!previousActiveTermMap.containsKey(term)) {
				DescriptionPojo descPojo = getDescriptionPojo(desc, term, moduleId);
				descPojo.setConceptId(conceptToTransform.getConceptId());
				newDescriptions.add(descPojo);
			} else {
				// Update Acceptability
				if (desc.getAcceptabilityMap() != null && !desc.getAcceptabilityMap().isEmpty()) {
					previousActiveTermMap.get(term).setAcceptabilityMap(getAcceptabilityMapFromConstantStringMap(desc.getAcceptabilityMap()));
				}
			}
		}
		
		for (DescriptionPojo pojo : conceptToTransform.getDescriptions()) {
			if (!pojo.isActive()) {
				continue;
			}
			if (pojo.getType() == DescriptionPojo.Type.FSN) {
				if (newFsn != null && !newFsn.equals(pojo.getTerm())) {
					pojo.setActive(false);
					pojo.setInactivationIndicator(inactivationReason);
					pojo.setEffectiveTime(null);
				}
			} else if (pojo.getType() == DescriptionPojo.Type.SYNONYM){
				if (pojo.getAcceptabilityMap() != null && pojo.getAcceptabilityMap().containsValue(PREFERRED)) {
					if (!newPts.contains(pojo.getTerm())) {
						pojo.getAcceptabilityMap().replaceAll((i, v) -> ACCEPTABLE);
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

	private DescriptionPojo getDescriptionPojo(Description desc, String term, String moduleId) {
		DescriptionPojo pojo = new DescriptionPojo();
		pojo.setAcceptabilityMap(getAcceptabilityMapFromConstantStringMap(desc.getAcceptabilityMap()));
		pojo.setActive(true);
		pojo.setCaseSignificance(DescriptionPojo.CaseSignificance.valueOf(desc.getCaseSignificance().name()));
		pojo.setTerm(term);
		pojo.setType(DescriptionPojo.Type.valueOf(desc.getType().name()));
		pojo.setLang(desc.getLang());
		pojo.setModuleId(moduleId);
		return pojo;
	}

	private Map<String, DescriptionPojo.Acceptability> getAcceptabilityMapFromConstantStringMap(Map<String, String> acceptabilityMap) {
		Map<String, DescriptionPojo.Acceptability> map = new TreeMap<>();
		acceptabilityMap.forEach((key, value) -> map.put(key, DescriptionPojo.Acceptability.valueOf(value)));
		return map;
	}

}
