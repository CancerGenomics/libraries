package edu.unlp.medicine.r4j.values;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPLogical;
import org.rosuda.REngine.REXPMismatchException;

public class R4JBooleanValue extends R4JPrimitiveValue {

	public R4JBooleanValue(REXP anAdaptee) {
		super(anAdaptee);
	}

	@Override
	public Object getValue() {
		try {
			Integer value = this.getAdaptee().asInteger();
			return (REXPLogical.TRUE == value) ? Boolean.TRUE : Boolean.FALSE;
		} catch (REXPMismatchException e) {
			//TODO LOG
			e.printStackTrace();
		}
		return null;
	}

}
