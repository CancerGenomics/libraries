package edu.unlp.medicine.r4j.exceptions;

import edu.unlp.medicine.r4j.values.R4JValue;

/**
 * This class represents an inconsistency in the transformation of R4JValue to
 * native objects
 * 
 * @author Diego García
 * 
 */
@SuppressWarnings("serial")
public class R4JValueMismatchException extends R4JException  {
	private R4JValue sender;
	private String access;


	
	public R4JValueMismatchException(final Throwable error, final R4JValue rexp, final String s) {
		super("attempt to access " + rexp.getClass().getName() + " as " + s, error);
		sender = rexp;
		access = s;
	}
	
	
	public R4JValue getSender() {
		return sender;
	}

	public String getAccess() {
		return access;
	}

}
