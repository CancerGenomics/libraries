package edu.unlp.medicine.r4j.core;



public class RVariableDescription {
	
	String name;
	R_VARIABLE_TYPE type;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public R_VARIABLE_TYPE getType() {
		return type;
	}
	public void setType(R_VARIABLE_TYPE type) {
		this.type = type;
	}
	public RVariableDescription(String name, R_VARIABLE_TYPE type) {
		super();
		this.name = name;
		this.type = type;
	}
	
	

}
