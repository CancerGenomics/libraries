package edu.unlp.medicine.r4j.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unlp.medicine.r4j.constants.OSDependentConstants;
import edu.unlp.medicine.r4j.exceptions.R4JCreatConnectionException;
import edu.unlp.medicine.r4j.exceptions.R4JScriptExecutionException;
import edu.unlp.medicine.r4j.exceptions.R4JTransformerNotFoundException;
import edu.unlp.medicine.r4j.systemProperties.R4JSystemPropertiesExpected;
import edu.unlp.medicine.r4j.transformers.R4JTransformerUtils;
import edu.unlp.medicine.r4j.utils.FileSystemUtils;
import edu.unlp.medicine.r4j.values.R4JValue;

/**
 * This class defines the API to write R code. You have the evaluate and voidEvaluate method. With those methods you can execute any R script.
 * But, you have also convenient methods as shortcuts to many lines in r script. For example, plotInFile. 
 * 
 * Error Handling:
 * If you have errors executing the evaluate method or any convenient method, the error will be logged in the "R log file", in the log4j file and
 * {@link R4JScriptExecutionException} will be thrown, to let you know that the script was not correctly executed. In catch of this error, just 
 * manage your program error and dont worry about writing the error in any place because it is all correctly registered in the r log file.
 * Note: Remember that the R log file path is defined using the @link {@link R4JSystemPropertiesExpected#R_REQUIRED_LIBRARIES_FILE_PATH_PROPERTY}. 
 * If you donw set thie property, the r log file will be created in user folder with name Bioplat.scripts.r.
 * 
 * 
 *  
 * @author Matias Butti
 *
 */
public class R4JConnection implements IR4JConnection {
	// Logger Object
	private static Logger logger = LoggerFactory.getLogger(R4JConnection.class);
	
	static final int MAX_ATTEMPTS_FOR_STARTING = 10;

	private RConnection connection;
	R4JServer server;
	
	boolean log=false;
	BufferedWriter rLog=null;
	
	Map<String, StringBuilder> tempLogs = new HashMap<String, StringBuilder>();



	//////////////////////////////////////////////
	////////////CONSTRUCTOR///////////////////////
	//////////////////////////////////////////////

	/**
	 * Connection with no log.
	 * @param connectionName
	 * @param server
	 * @throws R4JCreatConnectionException
	 */
	public R4JConnection(R4JServer server) throws R4JCreatConnectionException {
		this.server = server;
		
		this.setShouldRBeLogged();
		
		createLogFile();
		
		int attempt = 1;
		boolean started = false;
		RserveException lastExc=null;
		while(attempt<MAX_ATTEMPTS_FOR_STARTING && !started){
			try{
				connection = new RConnection(RServeConfigurator.getInstance().getHost(), server.getPort());
				started=true;
			} catch (RserveException e) {
				attempt++;		
				lastExc = e;
			}
		}
		if (!started) {
			logger.error("Error in opening a connection to the Rserve on port " + server.getPort());
			throw new R4JCreatConnectionException(lastExc.getMessage(), lastExc);
		}
		
		
	}

	
	
	private void setShouldRBeLogged() {
		this.log=false;
		try{
			this.log= Boolean.valueOf(System.getProperty(R4JSystemPropertiesExpected.R_LOG));	
		}
		catch(Exception e){
			
		}

		
	}



	//////////////////////////////////////////////
	////////////LOG///////////////////////////////
	//////////////////////////////////////////////
	/* (non-Javadoc)
	 * @see edu.unlp.medicine.r4j.server.IR4JConnection#turnOnLog()
	 */
	@Override
	public void turnOnLog(){
		this.log=true;
		
	}

	/* (non-Javadoc)
	 * @see edu.unlp.medicine.r4j.server.IR4JConnection#turnOffLog()
	 */
	@Override
	public void turnOffLog(){
		this.log=false;
	}
	

	public void addTemporalLog(String name){
		this.getTempLogs().put(name, new StringBuilder(""));
	}
	
	/**
	 * It allows to register a temporal log, for example to get the whole log of an operation composed of many invokes to R4JConnection 
	 * @param logName
	 * @return
	 */
	public String getTemporalLog(String logName){
		StringBuilder tl = this.getTempLogs().get(logName);
		if (tl!=null) return tl.toString();
		else return "";
	}
	
	/**
	 * If the variable {@link R4JSystemPropertiesExpected#SCRIPT_LOG_FILE_PATH_PROPERTY} is set, creates the file to the path indicated by this variable. 
	 * If not, it creates the file  Bioplat.scripts.r on the user folder.
	 * If the file exists it will be replaced!
	 */
	private void createLogFile() {
		String logPath="";
		try {
			logPath = System.getProperty(R4JSystemPropertiesExpected.SCRIPT_LOG_FILE_PATH_PROPERTY);
			
			if (logPath==null){
				logPath = FileSystemUtils.completePathToUserFolder("Bioplat.scripts.r");
				System.setProperty(R4JSystemPropertiesExpected.SCRIPT_LOG_FILE_PATH_PROPERTY, logPath );
			}
			
			
			rLog = FileSystemUtils.createFile(logPath, false);
		} catch (IOException e) {
			logger.error("The file " + logPath + " could not be created. The scrip'ts will not be logged even if the log is turnedOn.");
			this.log = false;
		}
		
	}




	
	/* (non-Javadoc)
	 * @see edu.unlp.medicine.r4j.server.IR4JConnection#changeLogFilePath(java.lang.String, boolean)
	 */
	@Override
	public void changeLogFilePath(String filePath, boolean append){
		
		try {
			rLog = FileSystemUtils.createFile(filePath, append);
		} catch (IOException e1) {
			logger.error("It was not possible to close the rLog file." + e1);
			
		}
		
		try {
			
			this.rLog = new BufferedWriter(new FileWriter(filePath));
		} catch (IOException e) {
			logger.error("It was not possible to create the rLog file: " + filePath + " you asked through changeLogFilePath method." + e);
			
		}
	}
	

	
	//////////////////////////////////////////////
	/////////////////OPEN AND CLOSE///////////////
	//////////////////////////////////////////////

	/* (non-Javadoc)
	 * @see edu.unlp.medicine.r4j.server.IR4JConnection#isOpen()
	 */
	@Override
	public boolean isOpen() {
		return this.connection.isConnected();
	}


	


	//////////////////////////////////////////////
	/////////////////API//////////////////////////
	//////////////////////////////////////////////	

	
	public void newSession(String name){
		this.connection.eval("######" + name + "######");
		this.clean();
	}
	
	public void clean(){
		try {
			this.connection.eval("rm(list=ls(all=TRUE))");
		} catch (R4JScriptExecutionException e) {
			e.printStackTrace();
		}

	}
	
	/* (non-Javadoc)
	 * @see edu.unlp.medicine.r4j.server.IR4JConnection#loadLibrary(java.lang.String)
	 */
	@Override
	public void loadLibrary(final String libraryName) throws R4JScriptExecutionException {
		String expression = "library(" + libraryName + ")";
		try {
			writeLog(expression);
			this.connection.voidEval(expression);

		} catch (RserveException e) {
			String errorMessage = "Error trying to load library: " + libraryName + ". Possible causes: 1)The library was not installed in R  2)You have misspelled the library name";
			handleScrtipError(errorMessage, e, expression);
		} 
	}

	


	/* (non-Javadoc)
	 * @see edu.unlp.medicine.r4j.server.IR4JConnection#assign(java.lang.String, java.lang.String)
	 */
	@Override
	public void assign(String variableName, String expression) throws R4JScriptExecutionException {
		writeLog(variableName + "<-" + expression);
		try {
			//this.connection.eval(expression);
			connection.eval(variableName + "<-" + expression);
		} catch (RserveException e) {
			String errorMessage = "Failed to assign the variable named " + variableName + " with the expression " + expression + ". Possible causes: 1-The expression is not correct.";
			handleScrtipError(errorMessage, e, expression);		
		}
	}
	
	
	

	/* (non-Javadoc)
	 * @see edu.unlp.medicine.r4j.server.IR4JConnection#assign(java.lang.String, java.util.List)
	 */
	@Override
	public <E> void assign(String variableName, List<E> array) throws R4JScriptExecutionException {
		StringBuilder expression = new StringBuilder("");
		try {
			expression.append("c(");
			for (E element : array) {
				expression.append(String.valueOf(element));
				expression.append(",");
			}
			expression.deleteCharAt(expression.length() - 1);
			expression.append(")");
			connection.eval(variableName + "<-" + expression);
		} catch (RserveException e) {
			String errorMessage = "Failed to assign the variable named " + variableName + " with a Java array of dimension " + array.size();
			handleScrtipError(errorMessage, e, expression.toString());
		}
	}

	
	

	/* (non-Javadoc)
	 * @see edu.unlp.medicine.r4j.server.IR4JConnection#executeScriptsFromFile(java.lang.String)
	 */
	@Override
	public void executeScriptsFromFile(String filePath) throws R4JScriptExecutionException {
		BufferedReader br;
		String line="";
		try {
			br = new BufferedReader(new FileReader(filePath));
			line = br.readLine();
			while (line != null) {
				if (!line.equals(OSDependentConstants.LINE_SEPARATOR)) {
					connection.eval(line);
				}
				line = br.readLine();
			}
		} catch (FileNotFoundException e) {
			logger.error("R4JConnection>>executeScriptsFromFile: The file with the R script (" + filePath + ") does not exist");
		} catch (IOException e) {
			logger.error("R4JConnection>>executeScriptsFromFile: IOException trying to open the R script file (" + filePath + ")");
		} catch (RserveException e) {
			String errorMessage = "Error executing the line: " + line + " while executing the R script in: " + filePath + ". All the lines in the file, from the one with error will not be executed.";
			handleScrtipError(errorMessage, e, line);

		}
	}
	
	

	/* (non-Javadoc)
	 * @see edu.unlp.medicine.r4j.server.IR4JConnection#existVar(java.lang.String)
	 */
	@Override
	public boolean existVar(String varName) throws R4JScriptExecutionException{
		String script = "exists(\"" + varName + "\")";
		writeLog(script);
		try {
			REXP rexp = connection.eval(script);
			return R4JTransformerUtils.transform(rexp).asInteger()==1;
		} catch (RserveException e) {
			String errorMessage = "Failed checking if the variable " + varName + " exists in R environment";
			handleScrtipError(errorMessage, e, script);
			return false;
		} catch (R4JTransformerNotFoundException e) {
			String errorMessage = "Failed checking if the variable " + varName + " exists in R environment";
			handleScrtipError(errorMessage, e, script);
			return false;
		}
	}
	

	/* (non-Javadoc)
	 * @see edu.unlp.medicine.r4j.server.IR4JConnection#getVarValue(java.lang.String)
	 */
	@Override
	public R4JValue getVarValue(String variableName) throws R4JScriptExecutionException {
		writeLog(variableName);
		try {
			REXP rexp = connection.eval(variableName);
			return R4JTransformerUtils.transform(rexp);
		} catch (RserveException e) {
			String errorMessage;
			if (!this.existVar(variableName)) errorMessage = "The variable: " + variableName + "does not exist in the R environment";
			else errorMessage = "Error trying to evaluate the variable: " + variableName;
			handleScrtipError(errorMessage, e, variableName);
		} catch (R4JTransformerNotFoundException e) {
			String errorMessage = "Error transforming the result value " + variableName;
			handleScrtipError(errorMessage, e, variableName);
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see edu.unlp.medicine.r4j.server.IR4JConnection#loadPlatforms(java.lang.String)
	 */
	@Override
	public void loadPlatforms(String platformsName, String variableNameWhereToAssignThePlatforms) throws R4JScriptExecutionException {
		String getPlatformsScript = variableNameWhereToAssignThePlatforms + "<-getPlatforms(" + platformsName + ")";
		try {
			writeLog(getPlatformsScript);
			connection.voidEval(getPlatformsScript);
		} catch (RserveException e) {
			String errorMessage = "Error loading platform " + platformsName;
			handleScrtipError(errorMessage, e, getPlatformsScript);
		}
	}

	

	/* (non-Javadoc)
	 * @see edu.unlp.medicine.r4j.server.IR4JConnection#makeNullAVar(java.lang.String)
	 */
	@Override
	public void makeNullAVar(String variableName) throws R4JScriptExecutionException {
		String script = variableName + "<-" + "NA";
		try {
			writeLog(script);
			connection.eval(script);
		} catch (RserveException e) {
			String errorMessage = "Error setting NULL on " + variableName;
			handleScrtipError(errorMessage, e, script);
		}
	}

	

	/* (non-Javadoc)
	 * @see edu.unlp.medicine.r4j.server.IR4JConnection#isNullVar(java.lang.String)
	 */
	@Override
	public boolean isNullVar(String variableName) throws R4JScriptExecutionException{
		String script = "is.na(" + variableName + ")";
		writeLog(script);
		try {
			REXP rexp = this.connection.eval(script);
			return R4JTransformerUtils.transform(rexp).asInteger()==1;
		} catch (RserveException e) {
			String errorMessage = "Error asking if " + variableName + " is NULL";
			handleScrtipError(errorMessage, e, script);
		} catch (R4JTransformerNotFoundException e) {
			String errorMessage = "Error asking if " + variableName + " is NULL";
			handleScrtipError(errorMessage, e, script);
		}
		return true;
	}
	
	
	

	/* (non-Javadoc)
	 * @see edu.unlp.medicine.r4j.server.IR4JConnection#eval(java.lang.String)
	 */
	@Override
	public R4JValue eval(String expression) throws R4JScriptExecutionException {
		writeLog(expression);		
		R4JValue result=null;
		try {
			REXP value = this.connection.eval(expression);
			result = R4JTransformerUtils.transform(value);
			return result;
		} catch (RserveException e) {
			String errorMessage = "Error evaluating the expression: " + expression;
			handleScrtipError(errorMessage, e, expression);
		} catch (R4JTransformerNotFoundException e) {
			String errorMessage = "Error transforming the result of evaluating: " + expression;
			handleScrtipError(errorMessage, e, expression);		
		}
		return result;
	}



	/* (non-Javadoc)
	 * @see edu.unlp.medicine.r4j.server.IR4JConnection#voidEval(java.lang.String)
	 */
	@Override
	public void voidEval(final String expression) throws R4JScriptExecutionException {
		writeLog(expression);		
		try {
			this.connection.voidEval(expression);
		} catch (RserveException e) {
			String errorMessage = "Error evaluating the expression: " + expression;
			handleScrtipError(errorMessage, e, expression);
		} 
		}
	


	/* (non-Javadoc)
	 * @see edu.unlp.medicine.r4j.server.IR4JConnection#plot(java.lang.String)
	 */
	@Override
	public byte[] executePlotAndGetImage(final String plotExpression) throws R4JScriptExecutionException {
		byte[] imageAsByte = null;
		writeLog(plotExpression);
		try {
			String device = "jpeg"; // device we'll call (this would work with pretty much any bitmap device)

			// we are careful here - not all R binaries support jpeg so we rather capture any failures
			REXP xp = this.connection.parseAndEval("try(" + device + "('test.jpg',quality=100, width = 300, height = 300))");

			if (xp.inherits("try-error")) { // if the result is of the class try-error then there was a problem
				logger.error("Can't open " + device + " graphics device:\n" + xp.asString());
				// this is analogous to 'warnings', but for us it's sufficient  to get just the 1st warning
				REXP w = this.connection.eval("if (exists('last.warning') && length(last.warning)>0) names(last.warning)[1] else 0");
				if (w.isString())
					logger.error(w.asString());
				return null;
			}

			this.connection.parseAndEval(plotExpression + "; dev.off()");
			

			// There is no I/O API in REngine because it's actually more efficient to use R for this we limit the file size to 1MB which should be sufficient and we delete the file as well
			xp = this.connection.parseAndEval("r=readBin('test.jpg','raw',1024*1024); unlink('test.jpg'); r");
			imageAsByte = xp.asBytes();


		} catch (RserveException rse) { 
			String errorMessage = "Error plotting image";
			handleScrtipError(errorMessage, rse, plotExpression);
		} catch (REXPMismatchException mme) { 
			String errorMessage = "Error plotting image";
			handleScrtipError(errorMessage, mme, plotExpression);
		} catch (Exception e) { 
			String errorMessage = "Error plotting image";
			handleScrtipError(errorMessage, e, plotExpression);
		}

		return imageAsByte;
	}
	

//	/* (non-Javadoc)
//	 * @see edu.unlp.medicine.r4j.server.IR4JConnection#plotSurvivalCurve(java.lang.String)
//	 */
//	@Override
//	public byte[] plotSurvivalCurve(final String script) throws R4JScriptExecutionException {
//		byte[] image = null;
//		if (script != null) {
//			int position = script.indexOf("plot");
//			if (position > 0) {
//				String expressionToPlot = script.substring(0, position - 1);
//				try {
//					// this.voidEvaluate(expressionToPlot);
//					Scanner scanner = new Scanner(expressionToPlot);
//					String expression = "";
//					while (scanner.hasNext()) {
//						expression = scanner.nextLine();
//						this.voidEval(expression);
//					}
//					image = this.plot(expression.split("<-")[0]);
//				} catch (R4JScriptExecutionException e) {
//					String errorMessage = "Error plotting the survival curve";
//					handleScrtipError(errorMessage, e, expressionToPlot);
//				}
//			}
//		}
//		return image;
//	}

	

	/* (non-Javadoc)
	 * @see edu.unlp.medicine.r4j.server.IR4JConnection#plotInFile(java.lang.String, java.lang.String)
	 */
	@Override
	public void plotInFile(String imagePath, String expressionToPlot) throws R4JScriptExecutionException  {
		
		String exp1 = "png(\"" + imagePath + "\")";
		this.writeLog(exp1);
		try {
			this.eval(exp1);
			
			String exp2="plot(" + expressionToPlot + ", col=c('red', 'blue', 'green', 'orange', 'yellow', 'brown'))";
			this.writeLog(exp2);
			this.eval(exp2);
			
			//this.imageRelease();

		} catch (R4JScriptExecutionException e) {
			String errorMessage = "Error plotting the expression: " + expressionToPlot + " in file " + imagePath;
			handleScrtipError(errorMessage, e, expressionToPlot);
		} 
//			catch (R4JTransformerNotFoundException e) {
//			String errorMessage = "Error releasing the file: " + imagePath;
//			handleScrtipError(errorMessage, e, expressionToPlot);
//		}
		
	}
	
	

	/* (non-Javadoc)
	 * @see edu.unlp.medicine.r4j.server.IR4JConnection#close()
	 */
	@Override
	public void close() {
			try {
				if (rLog!=null) rLog.close();
				connection.close();
			} catch (IOException e) {
				logger.error("Problem closing the log file");
			}
			
		
	}

	
	
	

/**
 * Adds a comment to the R log
 * @param comment
 */
public void addRComment(String comment) {
	this.writeLog(comment);
	
}
	
	

public RConnection getConnection(){
	return connection;
}





///////////////////////////////////////////////////////////
///////////////////////PRIVATE////////////////////////////
//////////////////////////////////////////////////////////
private void handleScrtipError(String errorMessage, Exception e, String expression) throws R4JScriptExecutionException {
	this.handleScriptError(errorMessage);
	throw new R4JScriptExecutionException(errorMessage, e, expression);
}



private void handleScriptError(String errorMessage){
	logger.error("R ERROR: " + errorMessage);
	logError(errorMessage);
	
}


private void imageRelease() throws R4JScriptExecutionException, R4JTransformerNotFoundException {
	String expression = "dev.off()"; 
	this.writeLog(expression);
	this.eval(expression);

}


/**
 * It logs an error
 * @param errorMessage
 */
public void logError(String errorMessage){
	try {
		if (log) {
			rLog.write("#ERROR "+ errorMessage);
			rLog.newLine();
			rLog.flush();
		}
	} catch (IOException e) {
		logger.error("It was not possible to write the rLog file. " + e);
	}		
	
}


private void writeLog(String expression) {
	try {
		if (log) {
			rLog.write(expression);
			rLog.newLine();
			rLog.flush();
		}
		for (StringBuilder sb : this.getTempLogs().values()) {
			sb.append(expression + "\n");
		}
	} catch (IOException e) {
		logger.error("It was not possible to write the rLog file. " + e);
	}
}



public Map<String, StringBuilder> getTempLogs() {
	return tempLogs;
}



public void setTempLogs(Map<String, StringBuilder> tempLogs) {
	this.tempLogs = tempLogs;
}




}
