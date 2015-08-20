package edu.unlp.medicine.r4j.values;

import org.rosuda.REngine.REXP;

import edu.unlp.medicine.r4j.exceptions.R4JValueMismatchException;

public class R4JByteArrayValue extends R4JValue {

	public R4JByteArrayValue(REXP anAdaptee) {
		super(anAdaptee);
	}

	@Override
	public Object asNativeJavaObject() throws R4JValueMismatchException {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
