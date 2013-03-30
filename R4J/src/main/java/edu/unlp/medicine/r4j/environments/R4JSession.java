package edu.unlp.medicine.r4j.environments;

import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unlp.medicine.r4j.exceptions.R4JConnectionException;
import edu.unlp.medicine.r4j.exceptions.R4JTransformerNotFoundException;
import edu.unlp.medicine.r4j.values.R4JValue;

public class R4JSession {
	// Logger Object
	private static Logger logger = LoggerFactory.getLogger(R4JSession.class);
	private String session;
	private IR4JBridge bridge;

	public R4JSession(final String aSession) {
		this.session = aSession;
		bridge = new R4JRServerBridge();
	}

	public void loadLibrary(final String libraryName) throws R4JConnectionException {
		try {
			String expression = "library(" + libraryName + ")";

			if (logger.isDebugEnabled()) {
				logger.debug(expression);
			}

			this.getBridge().evaluate(expression);
		} catch (R4JTransformerNotFoundException e) {
			logger.error(e.getMessage(), e);
		}
	}

	public void open() throws R4JConnectionException {
		this.getBridge().open();
	}

	public boolean isOpen() {
		return this.getBridge().isOpen();
	}

	public void close() {
		this.getBridge().close();

	}

	public void assign(String variableName, String expression) throws R4JConnectionException {
		if (logger.isDebugEnabled()) {
			logger.debug(variableName + "=" + expression);
		}
		this.getBridge().assign(variableName, expression);

	}

	public void assign(String variableName, R4JValue value) throws R4JConnectionException {
		if (logger.isDebugEnabled()) {
			logger.debug(variableName + "=" + value.getClass().getSimpleName());
		}
		this.getBridge().assign(variableName, value);

	}

	public void loadPlatforms(final String platformsName) {
		this.getBridge().loadPlatforms(platformsName);
	}

	public R4JValue evaluate(String expression) throws R4JConnectionException, R4JTransformerNotFoundException {
		if (logger.isDebugEnabled()) {
			logger.debug("Evaluate:" + expression);
		}
		return this.getBridge().evaluate(expression);
	}

	public void voidEvaluate(final String expression) throws R4JConnectionException {
		if (logger.isDebugEnabled()) {
			logger.debug("Evaluate:" + expression);
		}
		this.getBridge().voidEvaluate(expression);
	}

	public R4JValue parseAndEval(final String expression) throws R4JConnectionException {
		if (logger.isDebugEnabled()) {
			logger.debug("Evaluate:" + expression);
		}
		return this.getBridge().parseAndEval(expression);

	}

	public byte[] plot(final String expressionToPlot) {
		return this.getBridge().plot(expressionToPlot);
	}

	public byte[] plotSurvivalCurve(final String script) {
		byte[] image = null;
		if (script != null) {
			int position = script.indexOf("plot");
			if (position > 0) {
				String expressionToPlot = script.substring(0, position - 1);
				try {
					// this.voidEvaluate(expressionToPlot);
					Scanner scanner = new Scanner(expressionToPlot);
					String expression = "";
					while (scanner.hasNext()) {
						expression = scanner.nextLine();
						this.voidEvaluate(expression);
					}
					image = this.plot("survFitResult1");
				} catch (R4JConnectionException e) {
					this.logger.error(e.getMessage(), e);
				}
			}
		}
		return image;
	}

	protected IR4JBridge getBridge() {
		return this.bridge;
	}

}
