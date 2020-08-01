package gg.bayes.challenge.rest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "No such match id")
public class NoSuchMatchException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4816597165159147460L;

}
