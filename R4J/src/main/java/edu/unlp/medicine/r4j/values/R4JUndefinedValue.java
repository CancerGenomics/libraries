package edu.unlp.medicine.r4j.values;

import org.rosuda.REngine.REXP;

import edu.unlp.medicine.r4j.exceptions.R4JValueMismatchException;

public class R4JUndefinedValue extends R4JValue {

	public R4JUndefinedValue(REXP anAdaptee) {
		super(anAdaptee);
	}
	
	public R4JUndefinedValue() {
		this(null);
	}

	@Override
	public Object asNativeJavaObject() throws R4JValueMismatchException {
		throw new R4JValueMismatchException(this, "native Java Object");
	}

}
