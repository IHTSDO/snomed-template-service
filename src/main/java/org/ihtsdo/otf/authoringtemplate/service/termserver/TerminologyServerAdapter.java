package org.ihtsdo.otf.authoringtemplate.service.termserver;

public interface TerminologyServerAdapter {
	boolean eclQueryHasAnyMatches(String branchPath, String ecl);
}
