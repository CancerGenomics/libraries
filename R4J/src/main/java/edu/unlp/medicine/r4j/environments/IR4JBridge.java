package edu.unlp.medicine.r4j.environments;

import edu.unlp.medicine.r4j.exceptions.R4JConnectionException;
import edu.unlp.medicine.r4j.exceptions.R4JTransformerNotFoundException;
import edu.unlp.medicine.r4j.values.R4JValue;

public interface IR4JBridge {
	void open() throws R4JConnectionException;

	void close();

	void assign(final String variableName, final String expression) throws R4JConnectionException;

	void assign(final String variableName, final R4JValue value) throws R4JConnectionException;

	R4JValue evaluate(final String expression) throws R4JConnectionException, R4JTransformerNotFoundException;

	void loadPlatforms(final String platformsName);

	byte[] plot(final String expressionToPlot);

	void voidEvaluate(final String expression) throws R4JConnectionException;

	boolean isOpen();

}
