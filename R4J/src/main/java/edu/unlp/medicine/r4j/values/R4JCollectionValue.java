package edu.unlp.medicine.r4j.values;

import java.util.ArrayList;
import java.util.List;

import org.rosuda.REngine.REXP;

import edu.unlp.medicine.r4j.exceptions.R4JValueMismatchException;

public class R4JCollectionValue extends R4JValue {
	
    public R4JCollectionValue(REXP anAdaptee) {
		super(anAdaptee);
	}

	private List<R4JValue> listOfValues;
    
	@Override
	public Object asNativeJavaObject() throws R4JValueMismatchException {
		// TODO Auto-generated method stub
		return null;
	}
	
	protected List<R4JValue> getListOfValues() {
		if (this.listOfValues == null) {
			this.listOfValues = new ArrayList<R4JValue>();
		}
		return this.listOfValues;
	}
	
	public void addValue(final R4JValue value) {
		this.getListOfValues().add(value);
	}

}
