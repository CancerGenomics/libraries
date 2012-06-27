package edu.unlp.medicine.r4j.values;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REXPString;

public class R4JStringValue extends R4JPrimitiveValue {

	public R4JStringValue(REXP anAdaptee) {
		super(anAdaptee);
	}
	
	public R4JStringValue(String anValue) {
		this(new REXPString(anValue));
	}

	@Override
	public Object getValue() {
		try {
			return this.getAdaptee().asString();
		} catch (REXPMismatchException e) {
			//TODO LOG
			e.printStackTrace();
		}
		return null;
	}

}
