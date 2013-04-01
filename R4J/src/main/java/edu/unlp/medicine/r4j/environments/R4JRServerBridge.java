package edu.unlp.medicine.r4j.environments;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
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
			// connection.assign(variableName,
			// this.connection.parseAndEval(expression));
		} catch (Exception e) {
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
	public void voidEvaluate(final String expression) throws R4JConnectionException {
		try {
			this.connection.voidEval(expression);
		} catch (RserveException e) {
			logger.error("Error evaluating expression " + expression, e);
			throw new R4JConnectionException(e.getMessage(), e);
		}

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

	@Override
	public byte[] plot(final String expressionToPlot) {
		byte[] imageAsByte = null;
		try {
			String device = "jpeg"; // device we'll call (this would work with
									// pretty much any bitmap device)

			// if Cairo is installed, we can get much nicer graphics, so try to
			// load it
//			if (this.connection.parseAndEval("suppressWarnings(require('Cairo',quietly=TRUE))").asInteger() > 0)
//				device = "CairoJPEG"; // great, we can use Cairo device
//			else
//				logger.info("(consider installing Cairo package for better bitmap output)");

			// we are careful here - not all R binaries support jpeg so we
			// rather capture any failures
			REXP xp = this.connection.parseAndEval("try(" + device + "('test.jpg',quality=100, width = 250, height = 350))");

			if (xp.inherits("try-error")) { // if the result is of the class
											// try-error then there was a
											// problem
				logger.error("Can't open " + device + " graphics device:\n" + xp.asString());
				// this is analogous to 'warnings', but for us it's sufficient
				// to get just the 1st warning
				REXP w = this.connection.eval("if (exists('last.warning') && length(last.warning)>0) names(last.warning)[1] else 0");
				if (w.isString())
					logger.error(w.asString());
				return null;
			}

			// this.connection.parseAndEval("data(iris); attach(iris); plot(Sepal.Length, Petal.Length, col=unclass(Species)); dev.off()");
			// this.assign("expressionToPlot", expressionToPlot);
			// this.connection.parseAndEval("data(iris); attach(iris); plot(expressionToPlot,col=c('red', 'blue', 'green', 'orange', 'yellow', 'brown')); dev.off()");

			this.connection.parseAndEval("plot(" + expressionToPlot + ",col=c('red', 'blue', 'green', 'orange', 'yellow', 'brown')); dev.off()");

			// There is no I/O API in REngine because it's actually more
			// efficient to use R for this
			// we limit the file size to 1MB which should be sufficient and we
			// delete the file as well
			
			
			
			xp = this.connection.parseAndEval("r=readBin('test.jpg','raw',1024*1024); unlink('test.jpg'); r");
			imageAsByte = xp.asBytes();

			// Image img =
			// Toolkit.getDefaultToolkit().createImage(xp.asBytes());

		} catch (RserveException rse) { // RserveException (transport layer -
										// e.g. Rserve is not running)
			logger.error(rse.getMessage(), rse);
		} catch (REXPMismatchException mme) { // REXP mismatch exception (we got
												// something we didn't think we
												// get)
			logger.error(mme.getMessage(), mme);
		} catch (Exception e) { // something else
			logger.error("Something went wrong, but it's not the Rserve: " + e.getMessage());
		}

		return imageAsByte;
	}

	@Override
	public boolean isOpen() {
		return this.connection.isConnected();
	}

	@Override
	public R4JValue parseAndEval(String expression) throws R4JConnectionException {
		R4JValue value = null;
		try {
			REXP exp = this.connection.parseAndEval(expression);
			value = R4JTransformerUtils.transform(exp);
		} catch (Exception e) {
			logger.error("Error evaluating expression " + expression, e);
			throw new R4JConnectionException(e.getMessage(), e);
		}
		return value;
	}
}
