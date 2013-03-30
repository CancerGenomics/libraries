package edu.unlp.medicine.r4j.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class to make an object for getting many R variable values in one shot (just one R execution)
 * @author Matias
 *
 */
public class ListOfRVariablesToRefresh {
	
	int numberMaxOfVariablesInOneExecution=-1;
	List<RVariableDescription> variablesToGet = new ArrayList<RVariableDescription>();
	List<String> expressionsToEvaluate = new ArrayList<String>();
	
	public List<RVariableDescription> getVariablesToGet() {
		return variablesToGet;
	}

	public void setVariablesToGet(List<RVariableDescription> variablesToGet) {
		this.variablesToGet = variablesToGet;
	}

	public int getNumberMaxOfVariablesInOneExecution() {
		return numberMaxOfVariablesInOneExecution;
	}

	public void setNumberMaxOfVariablesInOneExecution(int numberMaxOfVariablesInOneExecution) {
		this.numberMaxOfVariablesInOneExecution = numberMaxOfVariablesInOneExecution;
	}

	public ListOfRVariablesToRefresh(int numberMaxOfVariablesInOneExecution) {
		super();
		this.numberMaxOfVariablesInOneExecution = numberMaxOfVariablesInOneExecution;
	}

	public ListOfRVariablesToRefresh() {
	}
	
	public void addVariableToQuery(String variableName, R_VARIABLE_TYPE type){
		variablesToGet.add(new RVariableDescription(variableName, type));	
	}

	
	
	public void addVariableToQuery(String variableName, R_VARIABLE_TYPE type, String expression){
		expressionsToEvaluate.add(variableName + "<-"+ expression);
		this.addVariableToQuery(variableName, type);
	}

	public List<String> getExpressionsToEvaluate() {
		return expressionsToEvaluate;
	}

	public void setExpressionsToEvaluate(List<String> expressionsToEvaluate) {
		this.expressionsToEvaluate = expressionsToEvaluate;
	}



	
}
