package org.ihtsdo.otf.transformationandtemplate.service.script;

import org.ihtsdo.otf.exception.TermServerScriptException;
import org.snomed.otf.scheduler.domain.Job;

public interface JobClass extends Runnable {
	
	Job getJob();

	void initialise() throws TermServerScriptException;

}
