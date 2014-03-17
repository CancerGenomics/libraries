package edu.unlp.medicine.r4j.exceptions;

@SuppressWarnings("serial")
public class R4JServerStartException extends R4JException {
	


	public R4JServerStartException(final String message, final Throwable error, String possibleCauses,
			String commandToStartTheR4JServer) {
		super(message, error);
		this.possibleCauses = possibleCauses;
		this.commandToStartTheR4JServer = commandToStartTheR4JServer;
	}
	
	
	String possibleCauses;
	String commandToStartTheR4JServer;
	
	public String getPossibleCauses() {
		return possibleCauses;
	}
	public void setPossibleCauses(String possibleCauses) {
		this.possibleCauses = possibleCauses;
	}
	public String getCommandToStartTheR4JServer() {
		return commandToStartTheR4JServer;
	}
	public void setCommandToStartTheR4JServer(String commandToStartTheR4JServer) {
		this.commandToStartTheR4JServer = commandToStartTheR4JServer;
	}

	
	@Override
	public String toString() {
		
		return this.getMessage() + ". " + this.getCommandToStartTheR4JServer() + ". " + this.getPossibleCauses();
	}
	

}
