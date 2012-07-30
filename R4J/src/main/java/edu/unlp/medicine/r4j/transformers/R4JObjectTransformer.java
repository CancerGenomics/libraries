package edu.unlp.medicine.r4j.transformers;

import org.rosuda.REngine.REXP;

import edu.unlp.medicine.r4j.values.R4JObjectValue;
import edu.unlp.medicine.r4j.values.R4JValue;

public class R4JObjectTransformer implements IR4JvalueTransformer {

	@Override
	public R4JValue transform(final REXP source) {
		return new R4JObjectValue(source);
	}

}
