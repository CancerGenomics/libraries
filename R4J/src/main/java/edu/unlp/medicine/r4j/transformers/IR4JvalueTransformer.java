package edu.unlp.medicine.r4j.transformers;

import org.rosuda.REngine.REXP;

import edu.unlp.medicine.r4j.values.R4JValue;

/**
 * This interface represents the transformation of an REXP to a R4JValue
 * 
 * @author Diego García
 * 
 */
public interface IR4JvalueTransformer {

	R4JValue transform(final REXP source);

}
