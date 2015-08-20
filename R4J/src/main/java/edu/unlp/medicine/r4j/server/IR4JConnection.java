package edu.unlp.medicine.r4j.server;

import java.util.List;

import edu.unlp.medicine.r4j.exceptions.R4JScriptExecutionException;
import edu.unlp.medicine.r4j.exceptions.R4JTransformerNotFoundException;
import edu.unlp.medicine.r4j.values.R4JValue;

/**
 * 
 * @author Matias Butti
 * 
 * Interface for R4CCOnnection implementations. See {@link R4JConnection}
 *
 */
public interface IR4JConnection {

	//////////////////////////////////////////////
	////////////LOG///////////////////////////////
	//////////////////////////////////////////////
	/**
	 * It turns on the log.
	 */
	public abstract void turnOnLog();

	/**
	 * It turns off the log.
	 */
	public abstract void turnOffLog();

	/**
	 * It closes the previous log file path and redirect to the new one.
	 * @param filePath
	 */
	public abstract void changeLogFilePath(String filePath, boolean append);

	//////////////////////////////////////////////
	/////////////////OPEN AND CLOSE///////////////
	//////////////////////////////////////////////
	
	/**
	 * @return It returns true if this connection is open
	 */
	public abstract boolean isOpen();

	//////////////////////////////////////////////
	/////////////////API//////////////////////////
	//////////////////////////////////////////////	
	public abstract void loadLibrary(final String libraryName)
			throws R4JScriptExecutionException;

	/**
	 * It starts a new session. It just writes the name of the session in the log and it cleans all teh variables.
	 */
//	public void newSession(String name);
	
	/**
	 * It cleans all the variables.
	 */
//	public void clean();
	
	/**
	 * It assigns in R environment, the result of evaluating the expression to the variable with name variableName.
	 * @param variableName The variable name in R environment
	 * @param expression The variable expression in R environment
	 */
	public abstract void assign(String variableName, String expression)
			throws R4JScriptExecutionException;

	/**
	 * It assigns a Java array to a Java in R. 
	 * @param variableName Variable name in R environment
	 * @param array java ArryaList.
	 * @throws R4JScriptExecutionException
	 */
	public abstract <E> void assign(String variableName, List<E> array)
			throws R4JScriptExecutionException;

	/**
	 * It executes the script in the specified file.
	 * @param filePath The path of the file with the script.
	 * @throws R4JScriptExecutionException if any line has error
	 */
	public abstract void executeScriptsFromFile(String filePath)
			throws R4JScriptExecutionException;

	/**
	 * It evaluates if a variable exists in the R environment.
	 * @param varName Variable name in R environment.
	 * @return true if exists, false if not.
	 * @throws R4JScriptExecutionException 
	 */
	public abstract boolean existVar(String varName)
			throws R4JScriptExecutionException;

	/**
	 * Returns the value of the variable
	 * @param variableName The variable name in the R environment.
	 * @return an R4JValue (Wrapper with convenient methods for converting an r object to java value.
	 * @throws R4JScriptExecutionException
	 * @throws R4JTransformerNotFoundException
	 */
	public abstract R4JValue getVarValue(String variableName)
			throws R4JScriptExecutionException;

	/**
	 * It loads the microarray platform.
	 * @param platformsName microarray platform name
	 * @throws R4JScriptExecutionException 
	 */
	public abstract void loadPlatforms(String platformsName, String variableNameWhereToAssignThePlatforms) throws R4JScriptExecutionException;

	/**
	 * It sets NULL to the variable in R environment.
	 * @param variableName The variable name in r environment.
	 * @throws R4JScriptExecutionException
	 */
	public abstract void makeNullAVar(String variableName)
			throws R4JScriptExecutionException;

	/**
	 * Returns true if the variable is null in the r environment
	 * @param variableName The variable name in the r environment
	 * @return true if the variable is null.
	 * @throws R4JScriptExecutionException
	 */
	public abstract boolean isNullVar(String variableName)
			throws R4JScriptExecutionException;

	/**
	 * It evaluates, in R environment, the expression.
	 * @param ecpression the expression to evaluate in R enviroment.
	 * @exception R4JScriptExecutionException It is thrown if there is an error executing the expression in the R environment.
	 *  
	 */
	public abstract R4JValue eval(String expression)
			throws R4JScriptExecutionException;

	/**
	 * It evaluates, in R environment, the expression.
	 * @param ecpression the expression to evaluate in R environment.
	 * @exception R4JScriptExecutionException It is thrown if there is an error executing the expression in the R environment.
	 *  
	 */
	public abstract void voidEval(final String expression)
			throws R4JScriptExecutionException;



	/**
	 * It plots the expressionToPlot in the imagePath.
	 * @param imagePath
	 * @param expressionToPlot
	 * @throws R4JScriptExecutionException
	 * @throws R4JTransformerNotFoundException
	 */
	public abstract void plotInFile(String imagePath, String expressionToPlot)
			throws R4JScriptExecutionException;

	
	/**
	 * Adds a comment to the R log
	 * @param comment
	 */
	public abstract void addRComment(String comment);

	
	/**
	 * It closes the Rserve connection and closes the log file.	
	 */
	public abstract void close();
	
	/**
	 * It creates a new R environment (it is like a r session)
	 * @return
	 */
	public String newEnvironment();

}