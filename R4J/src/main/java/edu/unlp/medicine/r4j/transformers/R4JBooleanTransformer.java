package edu.unlp.medicine.r4j.transformers;

import org.rosuda.REngine.REXP;

import edu.unlp.medicine.r4j.values.R4JBooleanValue;
import edu.unlp.medicine.r4j.values.R4JValue;

public class R4JBooleanTransformer implements IR4JvalueTransformer {

	@Override
	public R4JValue transform(final REXP source) {
		return new R4JBooleanValue(source);
	}

}
