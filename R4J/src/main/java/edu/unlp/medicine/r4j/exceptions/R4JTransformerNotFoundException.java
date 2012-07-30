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
public class R4JTransformerNotFoundException extends Exception {
	
	private Class rClass;
	
	public R4JTransformerNotFoundException(final Class theClass) {
		this.rClass = theClass;
	}
	
	public R4JTransformerNotFoundException (final String message) {
		super(message);
	}

	public Class getrClass() {
		return rClass;
	}	

}
