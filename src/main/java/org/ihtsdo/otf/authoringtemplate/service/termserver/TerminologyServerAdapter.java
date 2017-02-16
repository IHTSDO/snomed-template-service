package org.ihtsdo.otf.authoringtemplate.service.termserver;

import java.util.Set;

public interface TerminologyServerAdapter {
	Set<String> eclQuery(String branchPath, String ecl, int limit);
	boolean eclQueryHasAnyMatches(String branchPath, String ecl);
}
