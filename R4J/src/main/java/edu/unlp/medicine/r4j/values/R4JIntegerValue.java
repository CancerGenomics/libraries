package edu.unlp.medicine.r4j.values;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;

public class R4JIntegerValue extends R4JPrimitiveValue {

	public R4JIntegerValue(REXP anAdaptee) {
		super(anAdaptee);
	}

	@Override
	public Object getValue() {
		try {
			return this.getAdaptee().asInteger();
		} catch (REXPMismatchException e) {
			//TODO LOG
			e.printStackTrace();
		}
		return null;
	}

}
