package gg.bayes.challenge.rest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "No such hero")
public class NoSuchHeroException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1239114180000368529L;

}
