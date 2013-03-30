package edu.unlp.medicine.r4j.values;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPList;
import org.rosuda.REngine.REXPMismatchException;

import edu.unlp.medicine.r4j.exceptions.R4JTransformerNotFoundException;
import edu.unlp.medicine.r4j.exceptions.R4JValueMismatchException;
import edu.unlp.medicine.r4j.transformers.R4JTransformerUtils;

public class R4JObjectValue extends R4JValue {

	public R4JObjectValue(REXP anAdaptee) {
		super(anAdaptee);
	}

	@Override
	public Object asNativeJavaObject() throws R4JValueMismatchException {
		throw new R4JValueMismatchException(this, "native Java Object");
	}

	/**
	 * This method returns the attributes names of the object.
	 * Returns null if there is an error.
	 * 
	 * @return
	 */
	public String[] getAttributes() {
		try {
			REXPList _attr = this.getAdaptee()._attr();
			if (_attr != null) {
				return _attr.asList().keys();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * This method returns the attribute value given as parameter.
	 * Returns R4JUndefinedValue if there is an error.
	 * 
	 * @param attributeName
	 * @return
	 * @throws R4JTransformerNotFoundException 
	 */
	public R4JValue getValue(final String attributeName) {
		REXP value = this.getAdaptee().getAttribute(attributeName);
		if (value != null) {
			try {
				return R4JTransformerUtils.transform(value);
			} catch (R4JTransformerNotFoundException e) {
				e.printStackTrace();
			}
		}
		return new R4JUndefinedValue(this.getAdaptee());
	}

}
