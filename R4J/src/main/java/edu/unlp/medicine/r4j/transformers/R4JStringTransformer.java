package edu.unlp.medicine.r4j.transformers;

import org.rosuda.REngine.REXP;

import edu.unlp.medicine.r4j.values.R4JStringValue;
import edu.unlp.medicine.r4j.values.R4JValue;

public class R4JStringTransformer implements IR4JvalueTransformer {

	@Override
	public R4JValue transform(REXP source) {
		return new R4JStringValue(source);
	}

}
