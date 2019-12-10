package org.ihtsdo.otf.authoringtemplate.rest.error;

import java.util.ArrayList;
import java.util.List;

public class InputError extends RuntimeException {

	private static final long serialVersionUID = 1000054542741285670L;
	
	private List<String> messages;

	public InputError(List<String> messages) {
		this.messages = messages;
	}

	public InputError(String message) {
		messages = new ArrayList<>();
		messages.add(message);
	}

	public List<String> getMessages() {
		return messages;
	}

	@Override
	public String getMessage() {
		return String.join("\n", messages);
	}
}
