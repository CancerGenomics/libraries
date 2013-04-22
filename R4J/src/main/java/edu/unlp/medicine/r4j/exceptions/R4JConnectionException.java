package edu.unlp.medicine.r4j.exceptions;

public class R4JConnectionException extends Exception {
	public R4JConnectionException(final String message, final Throwable error) {
		super(message, error);
	}
	
	public R4JConnectionException(final String message, final Throwable error, String script) {
		super(message, error);
		this.setScript(script);
	}

	
	public String script;

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}
	
	

}
