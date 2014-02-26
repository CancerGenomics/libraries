/**
 * 
 */
package edu.unlp.medicine.r4j.exceptions;

/**
 * This exception indicates a transformer is not defined.
 * 
 * @author Diego Garcia
 *
 */
@SuppressWarnings("serial")
public class R4JTransformerNotFoundException extends R4JException  {
	
	private Class rClass;
	
	public R4JTransformerNotFoundException(final String message, final Throwable error, final Class theClass) {
		super(message, error);
		this.rClass = theClass;
	}
	

	public Class getrClass() {
		return rClass;
	}	

}
