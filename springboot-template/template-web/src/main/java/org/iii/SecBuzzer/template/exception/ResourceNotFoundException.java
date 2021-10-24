package org.iii.SecBuzzer.template.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends Exception {
	private static final long serialVersionUID = 4598832761362080275L;

	public ResourceNotFoundException(String message) {
		super(message);
	}
}