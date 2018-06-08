package org.ihtsdo.otf.authoringtemplate.service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TemplateUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(TemplateUtil.class);

	public static Pattern constructTermPattern(String termTemplate) {
		String result = termTemplate;
		//$actionTerm$ of $procSiteTerm$ using computed tomography guidance (procedure)
		Matcher matcher = TemplateService.TERM_SLOT_PATTERN.matcher(termTemplate);
		while (matcher.find()) {
			String termSlot = matcher.group();
			result = result.replace(termSlot, ".+");
		}
		result = result.replace("(", "\\(");
		result = result.replace(")", "\\)");
		result = result.replace(".+","(.+)");
		LOGGER.info("term pattern regex=" + result);
		Pattern pattern = Pattern.compile(result);
		return pattern;
	}
	
	
	
	public static List<String> getSlots(String termTemplate) {
		List<String> slots = new ArrayList<>();
		Matcher matcher = TemplateService.TERM_SLOT_PATTERN.matcher(termTemplate);
		while (matcher.find()) {
			slots.add(matcher.group());
		}
		return slots;
	}
}
