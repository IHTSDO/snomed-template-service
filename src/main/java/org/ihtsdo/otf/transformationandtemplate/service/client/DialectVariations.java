package org.ihtsdo.otf.transformationandtemplate.service.client;

import java.util.Map;
import java.util.Set;

public class DialectVariations {

	private Map<String,String> map;
	private Map<String, Set<String>> synonyms;
	
	public Map<String, String> getMap() {
		return map;
	}
	public void setMap(Map<String, String> map) {
		this.map = map;
	}
	public Map<String, Set<String>> getSynonyms() {
		return synonyms;
	}
	public void setSynonyms(Map<String, Set<String>> synonyms) {
		this.synonyms = synonyms;
	}

	@Override
	public String toString() {
		return "DialectVariations{" +
				"map=" + (map != null ? map.keySet() : "") +
				", synonyms=" + (synonyms != null ? synonyms.keySet() : "") +
				'}';
	}
}
