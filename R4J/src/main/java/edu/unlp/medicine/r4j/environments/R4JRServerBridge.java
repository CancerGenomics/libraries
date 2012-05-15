package edu.unlp.medicine.r4j.environments;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unlp.medicine.r4j.exceptions.R4JConnectionException;
import edu.unlp.medicine.r4j.exceptions.R4JTransformerNotFoundException;
import edu.unlp.medicine.r4j.transformers.R4JTransformerUtils;
import edu.unlp.medicine.r4j.values.R4JValue;

public class R4JRServerBridge implements IR4JBridge {
	// Logger Object
	private static Logger logger = LoggerFactory.getLogger(R4JRServerBridge.class);
	private RConnection connection;

	public R4JRServerBridge() {
		super();
	}

	@Override
	public void open() throws R4JConnectionException {
		try {
			connection = new RConnection(RServeConfigurator.getInstance().getHost(), RServeConfigurator.getInstance().getPort());
		} catch (Exception ex) {
			logger.error("Error in opening a connection to the Rserve on port" + RServeConfigurator.getInstance().getPort(), ex);
			throw new R4JConnectionException(ex.getMessage(), ex);
		}

	}

	@Override
	public void close() {
		if (connection != null && connection.isConnected())
			connection.close();
	}

	@Override
	public void assign(String variableName, String expression) throws R4JConnectionException {
		try {
			connection.assign(variableName, expression);
		} catch (RserveException e) {
			logger.error("Failed to assign the variable named " + variableName + " value " + expression, e);
			throw new R4JConnectionException(e.getMessage(), e);
		}

	}

	@Override
	public R4JValue evaluate(String expression) throws R4JConnectionException, R4JTransformerNotFoundException {
		R4JValue result;
		try {
			REXP value = this.connection.eval(expression);
			result = R4JTransformerUtils.transform(value);
		} catch (RserveException e) {
			logger.error("Error evaluating expression " + expression, e);
			throw new R4JConnectionException(e.getMessage(), e);
		}
		return result;
	}

	@Override
	public void loadPlatforms(String platformsName) {

		REXP platforms;
		try {
			platforms = connection.eval("getPlatforms(" + platformsName + ")");
			connection.assign("platforms", platforms);
		} catch (RserveException e) {
			logger.error("Error loading platform " + platformsName, e);
			e.printStackTrace();
		}

	}

	@Override
	public void assign(String variableName, R4JValue value) throws R4JConnectionException {
		try {
			connection.assign(variableName, value.getNativeValue());
		} catch (RserveException e) {
			logger.error("Failed to assign the variable named " + variableName + " : " + value.getClass().getSimpleName(), e);
			throw new R4JConnectionException(e.getMessage(), e);
		}

	}

}
