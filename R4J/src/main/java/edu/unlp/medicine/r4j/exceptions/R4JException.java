package edu.unlp.medicine.r4j.exceptions;

@SuppressWarnings("serial")
public class R4JException extends Exception {


	public R4JException() {
		
	}

	public R4JException(String message, Throwable error) {
		super(message, error);
	}

}
