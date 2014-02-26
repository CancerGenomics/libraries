package edu.unlp.medicine.r4j.values;

import org.rosuda.REngine.REXP;

import edu.unlp.medicine.r4j.exceptions.R4JValueMismatchException;

/**
 * This class leads the hierarchy of primitive R values.
 * @author Diego Garcia
 *
 */
public abstract class R4JPrimitiveValue extends R4JValue {

	public R4JPrimitiveValue(final REXP anAdaptee) {
		super(anAdaptee);
	}

	@Override
	public Object asNativeJavaObject() throws R4JValueMismatchException {
		return this.getValue();
	}
	
	/**
	 * This method returns the primitive value in a native java object.
	 * If there are any errors returns null.
	 * @return
	 */
	public abstract Object getValue();

}
