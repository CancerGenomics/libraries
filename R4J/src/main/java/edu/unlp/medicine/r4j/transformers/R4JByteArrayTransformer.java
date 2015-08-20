package edu.unlp.medicine.r4j.transformers;

import org.rosuda.REngine.REXP;

import edu.unlp.medicine.r4j.values.R4JByteArrayValue;
import edu.unlp.medicine.r4j.values.R4JValue;

public class R4JByteArrayTransformer implements IR4JvalueTransformer {

	@Override
	public R4JValue transform(REXP source) {
		return new R4JByteArrayValue(source);
	}

}
