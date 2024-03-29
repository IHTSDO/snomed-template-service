package org.ihtsdo.otf.transformationandtemplate.service.template;

import org.ihtsdo.otf.transformationandtemplate.service.exception.ServiceException;
import org.ihtsdo.otf.utils.StringUtils;
import org.ihtsdo.otf.rest.client.RestClientException;
import org.ihtsdo.otf.rest.client.terminologyserver.SnowstormRestClient;
import org.ihtsdo.otf.rest.client.terminologyserver.SnowstormRestClientFactory;
import org.ihtsdo.otf.rest.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snomed.authoringtemplate.domain.ConceptTemplate;
import org.snomed.authoringtemplate.domain.Relationship;
import org.snomed.authoringtemplate.domain.SimpleSlot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
public class TemplateService {

	static final String OPTIONAL = "(optional)";

	public static final Pattern TERM_SLOT_PATTERN = Pattern.compile("\\$([^\\$]*)\\$");

	@Autowired
	private TemplateStore templateStore;

	@Autowired
	private SnowstormRestClientFactory terminologyClientFactory;

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public String create(String name, ConceptTemplate conceptTemplate) throws IOException, ServiceException {
		if (load(name) != null) {
			throw new IllegalArgumentException("Template with name '" + name + "' already exists.");
		}

		conceptTemplate.setVersion(1);

		// TODO Validate that lexicalTemplates and terms within ConceptTemplate descriptions match

		templateStore.save(name, conceptTemplate);

		return name;
	}

	public ConceptTemplate load(String name) throws IOException {
		return templateStore.load(name);
	}

	public ConceptTemplate loadOrThrow(String templateName) throws IOException, ResourceNotFoundException {
		ConceptTemplate template = load(templateName);
		if (template == null) {
			throw new ResourceNotFoundException("template", templateName);
		}
		return template;
	}

	// TODO Keep old versions of the template
	public ConceptTemplate update(String name, ConceptTemplate conceptTemplateUpdate) throws IOException, ServiceException {
		final ConceptTemplate existingTemplate = load(name);
		if (existingTemplate == null) {
			throw new IllegalArgumentException("Template with name '" + name + "' does not exist.");
		}
		conceptTemplateUpdate.setVersion(existingTemplate.getVersion() + 1);
		templateStore.save(name, conceptTemplateUpdate);

		return conceptTemplateUpdate;
	}

	public Set<ConceptTemplate> listAll(String branchPath, String[] descendantOf, String[] ancestorOf) throws IOException {
		Set<ConceptTemplate> templates = listAll();
		SnowstormRestClient terminologyClient = terminologyClientFactory.getClient();
		if (!StringUtils.isEmpty(descendantOf) || !StringUtils.isEmpty(ancestorOf)) {
			// Group templates by focus concept to reduce the number of ECL queries
			Map<String, List<ConceptTemplate>> templatesByFocusConcept = templates.stream().collect(Collectors.groupingBy(ConceptTemplate::getFocusConcept));
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			return templatesByFocusConcept.entrySet().stream().filter(entry -> {
				SecurityContextHolder.getContext().setAuthentication(authentication);
				String focusConcept = entry.getKey();
				String ecl = "";
				for (int i = 0; descendantOf != null && i < descendantOf.length; i++) {
					if (i > 0) ecl += " OR ";
					ecl += "(" + focusConcept + " AND <<" + descendantOf[i] + ")";
				}
				for (int i = 0; ancestorOf != null && i < ancestorOf.length; i++) {
					if (!StringUtils.isEmpty(descendantOf) || i > 0) ecl += " OR ";
					ecl += "(" + focusConcept + " AND >>" + ancestorOf[i] + ")";
				}
				try {
					return terminologyClient.eclQueryHasAnyMatches(branchPath, ecl);
				} catch (RestClientException e) {
					logger.error("Failed to filter templates using ECL", e);
					return false;
				}
			}).map(Map.Entry::getValue).flatMap(List::stream).collect(Collectors.toSet());
		}
		return templates;
	}

	public Set<ConceptTemplate> listAll() throws IOException {
		return templateStore.loadAll();
	}

	public void writeEmptyInputFile(String templateName, OutputStream outputStream) throws IOException, ResourceNotFoundException {
		ConceptTemplate template = loadOrThrow(templateName);
		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, UTF_8))) {
			List<Relationship> relationships = template.getConceptOutline()
					.getClassAxioms()
					.stream()
					.findFirst()
					.get()
					.getRelationships();

			StringBuilder header = new StringBuilder();
			for (Relationship relationship : relationships) {
				SimpleSlot targetSlot = relationship.getTargetSlot();
				if (isSlotRequiringInput(targetSlot)) {
					String slotName = targetSlot.getSlotName();
					if (TemplateUtil.isOptional(relationship)) {
						slotName = slotName + OPTIONAL;
					}
					if (header.length() > 0) header.append("\t");
					header.append(slotName != null ? slotName : "slot");
				}
			}
			for (String additionalSlot : template.getAdditionalSlots()) {
				if (header.length() > 0) header.append("\t");
				header.append(additionalSlot);
			}
			writer.write(header.toString());
			writer.newLine();
		}
	}

	public boolean isSlotRequiringInput(SimpleSlot targetSlot) {
		return targetSlot != null && targetSlot.getSlotReference() == null;
	}

	public void reloadCache() throws IOException, ServiceException {
		templateStore.init();
	}
}
