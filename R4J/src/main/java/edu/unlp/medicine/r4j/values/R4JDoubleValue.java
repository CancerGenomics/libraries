package edu.unlp.medicine.r4j.values;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;

public class R4JDoubleValue extends R4JPrimitiveValue {

	public R4JDoubleValue(REXP anAdaptee) {
		super(anAdaptee);
	}

	@Override
	public Object getValue() {
		try {
			return this.getAdaptee().asDouble();
		} catch (REXPMismatchException e) {
			//TODO LOG
			e.printStackTrace();
		}
		return null;
	}

}
