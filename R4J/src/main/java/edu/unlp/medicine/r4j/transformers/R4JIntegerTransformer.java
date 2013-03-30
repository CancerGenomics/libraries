package edu.unlp.medicine.r4j.transformers;

import org.rosuda.REngine.REXP;

import edu.unlp.medicine.r4j.values.R4JIntegerValue;
import edu.unlp.medicine.r4j.values.R4JValue;

public class R4JIntegerTransformer implements IR4JvalueTransformer {

	@Override
	public R4JValue transform(final REXP source) {
		return new R4JIntegerValue(source);
	}

}
