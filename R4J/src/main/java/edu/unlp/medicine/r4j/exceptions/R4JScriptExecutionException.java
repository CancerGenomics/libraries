package edu.unlp.medicine.r4j.exceptions;

@SuppressWarnings("serial")
public class R4JScriptExecutionException extends R4JException  {
	

	public R4JScriptExecutionException(final String message) {
		super();
		this.setMessage(message);
	}
	
	public R4JScriptExecutionException(final String message, final Throwable error) {
		super(message, error);
	}
	
	public void setMessage(String message){
		this.setMessage(message);
	}
	
	public R4JScriptExecutionException(final String message, final Throwable error, String script) {
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
