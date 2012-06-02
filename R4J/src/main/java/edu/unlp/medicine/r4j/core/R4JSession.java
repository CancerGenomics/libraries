package edu.unlp.medicine.r4j.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unlp.medicine.r4j.constants.OSDependentConstants;
import edu.unlp.medicine.r4j.constants.R_EXECUTION_MODE_ENUM;
import edu.unlp.medicine.r4j.utils.FileSystemUtils;
import edu.unlp.medicine.r4j.utils.FileSystemUtilsForAbsolutePath;
import edu.unlp.medicine.r4j.utils.FileSystemUtilsForDefaultR4JFolder;
import edu.unlp.medicine.r4j.utils.RUtils;
import edu.unlp.medicine.r4j.utils.StringUtils;
import edu.unlp.medicine.utils.FileManager;

/**
 * <p>
 * It models an R session. The actual implementation writes a file in the path
 * nonFlushedScriptsFilePath (apart of the log whose path is received as
 * parameter) with all non executed r statements. When the flush() method is
 * called, all the statements in this file will be executed. For executing the
 * statement in the nonFlushedScriptsFilePath file, R is run as an external
 * program using the -f parameter passing the mentioned file as parameter. It is
 * important to mention that the R program is run on the working directory
 * System.getProperty("user.home"). So, the starting point of any relative path
 * used in R (including the R files executed) will be start in this path.
 * </p>
 * <p>
 * The session creates a folder inside the System.getProperty("user.home")/R4J
 * folder. The name of this folder is a mandatory constructor parameter and this
 * folder is known as UserFolder. In this folder you can find LOG.TXT with all
 * the log of this session. RESULTS.txt with all the R output during this
 * session. It is also the .rdata file which will be used to restore the
 * workspace with the variables of the previous flush() invokes.
 * </p>
 * <p>
 * Inside the UserFolder will be also a folder called
 * R4J-session-<sessionId>Thread-<THREADID>. In this folder there will be one
 * file per flush with the R statements executed in this flush. Moreover, there
 * will be one file per variable containing the actual value.
 * </p>
 * <p>
 * This implementation tries to get the values of many variables in one R
 * execution instead of executing one R proces each time the user needs the
 * value of an R variable.
 * </p>
 * <p>
 * For getting the value of a set of variables you should: 1-Create an instance
 * of ListOfRVariablesToRefresh. If you need to partition the uery to r in many
 * executions (for example if there are many variables and so bigs) you should
 * pass as parameter in the constructor the maximium number of variables.
 * 2-Agregar todas las variablars que se quieren consultar utilizando el método
 * aListOfRVariablesToRefresh>>addVariableToQuery. In this method you have to
 * pass the variable name and the type and (optionally) the expression to
 * evaluate and assign to the variable. If you dont pass the expression, you
 * have to add the assigmnet statement using the traditional
 * addStatement(variable<-expresion). 3-Execute the method
 * R4JSessionFaster>>getValuesFromR(aListOfRVariablesToRefresh). This will
 * update the cache with the variable values. 4-Query the variable using the
 * methods R4JSessionFaster>>getSingleValueFromCache() and
 * getArrayValueFromCache.
 * </p>
 * <p>
 * You can also use the methods R4JSessionFaster>>getSingleValueFromR() and
 * getArrayValueFromR to go to R and get the value. Both methods put he variable
 * value in the cache but also returns the value, so it is not necessary to get
 * the value from cache.
 * 
 * </p>
 * <p>
 * Examples: In this example, all the variables will be get with just one R
 * execution.
 * </p>
 * <code><pre>
 * R4JSessionFaster rj4SessionFaster = R4JFactory.getR4JInstance().getRSessionFaster("testGetRVariablesUsingFaster2");
 * 		
 * ListOfRVariablesToRefresh listOfRVariablesToRefresh = new ListOfRVariablesToRefresh();
 * listOfRVariablesToRefresh.addVariableToQuery("singleNumericValue", R_VARIABLE_TYPE.NUMERIC, "c(7)");
 * listOfRVariablesToRefresh.addVariableToQuery("multipleNumericValue", R_VARIABLE_TYPE.NUMERIC,"c(8,1,2)");
 * listOfRVariablesToRefresh.addVariableToQuery("singleValueString", R_VARIABLE_TYPE.STRING,"c('hola')");
 * listOfRVariablesToRefresh.addVariableToQuery("multipleValueString", R_VARIABLE_TYPE.STRING,"c('hola','como','te','va')");
 * rj4SessionFaster.getValuesFromR(listOfRVariablesToRefresh);
 * 
 * String value = rj4SessionFaster.getSingleValueFromCache("singleNumericValue", R_VARIABLE_TYPE.NUMERIC);
 * List<String> value= rj4SessionFaster.getArrayValueFromCache("multipleNumericValue", R_VARIABLE_TYPE.NUMERIC).size()==3;
 * String value = rj4SessionFaster.getArrayValueFromCache("singleValueString", R_VARIABLE_TYPE.STRING).equals("\"hola\"");
 * List<String> value= rj4SessionFaster.getArrayValueFromCache("multipleValueString", R_VARIABLE_TYPE.STRING).size()==4;
 * 
 * </pre>
 * </code>
 * 
 * @author Matias
 * 
 */
public class R4JSession {

	/**
	 * BufferedWriter with all the r statements executed along this session
	 */
	private BufferedWriter sessionScriptsLogBufferedWriter;

	/**
	 * Non flushed scripts
	 */
	String nonFlushedScriptsFilePath;
	String originalNonFlushedScriptsFilePath;
	BufferedWriter nonFlushedScriptsBufferedWriter;

	/**
	 * File path to the output results
	 */
	String outputFilePath;

	/**
	 * Session id to allow working with more than one session in the same
	 * thread.
	 */
	static int sessionId = 0;

	/**
	 * This is a workaround. If i do executeRASExternalProgramWith-F() and then
	 * resetFile() then it doenst work. That is why there are 1 file for each
	 * flush.
	 */
	int scriptFileNumber = 0;

	/**
	 * This is the fileSystemUtils configured with the userFolderName received
	 * as constructor parameter.
	 */
	FileSystemUtils fileSystemUtils;

	/**
	 * This maps contains all the variables previosly got from R. E<ch variable
	 * it is represented as a Stirng list and it can be accessed using the
	 * methods getSinglValueFromCache or getArrayValueFromCache
	 */
	Map<String, List<String>> variableCache = new HashMap<String, List<String>>();

	/**
	 * CONFIGURATION
	 */
	static String TEMP_FOLDER = "TMP";
	static String LOG_FILE_NAME = "LOG.TXT";
	static String RESULTS_FILE_NAME = "RESULTS.TXT";
	private static Logger LOGGER = LoggerFactory.getLogger(R4J.class);

	/**
	 * 
	 * @param sessionName
	 *            The name of the session. This session will be used to create a
	 *            folder in the temp folder
	 * @throws RException
	 */
	public R4JSession(String sessionName) throws RException {
		fileSystemUtils = new FileSystemUtilsForDefaultR4JFolder(this.getTempFolderName4ThisSession(), sessionName);
		createFolderSession();
	}

	/**
	 * 
	 * @param sessionName
	 * @param path
	 *            The path of
	 * @throws RException
	 */
	public R4JSession(String sessionName, String path) throws RException {
		fileSystemUtils = new FileSystemUtilsForAbsolutePath(this.getTempFolderName4ThisSession(), sessionName, path);
		createFolderSession();
	}

	// ////////////////////////////////////////////////////////////////
	// ///////////////////////GETTING VARIABLES////////////////////////
	// ////////////////////////////////////////////////////////////////

	/**
	 * This method is for refresh many variables in one R execution. The aim is
	 * to improve the performance.
	 * 
	 * Instead of executing getSingleValue or getListValue for each variable you
	 * need, you should package all the in a ListOfRVariablesToRefresh and call
	 * this method to query all the values in one R call. After calling this
	 * method, you can use the getSingleValue or getListValue passing the
	 * variable name as parameter to get the corresponding value.
	 * 
	 */
	public void getValuesFromR(ListOfRVariablesToRefresh listOfRVariablesToRefresh) {
		for (String assignmentExpression : listOfRVariablesToRefresh.getExpressionsToEvaluate()) {
			this.addStatement(assignmentExpression);
		}
		Map<String, List<String>> newVariableValues;
		if (listOfRVariablesToRefresh.getNumberMaxOfVariablesInOneExecution() == -1) {
			newVariableValues = this.accessRAndGetTheValuesOfVariables(listOfRVariablesToRefresh.getVariablesToGet());
		} else {
			newVariableValues = this.accessRAndGetTheValuesOfBigVariables(listOfRVariablesToRefresh.getVariablesToGet(), listOfRVariablesToRefresh.getNumberMaxOfVariablesInOneExecution());
		}

		for (String variableName : newVariableValues.keySet()) {
			variableCache.put(variableName, newVariableValues.get(variableName));
		}

	}

	/**
	 * It gets the value from the cache. The value must be get from R (using
	 * getSingleValueFromR or getVariblesFromR
	 * 
	 * @param variableName
	 *            The name of the variable
	 * @param type
	 *            The type of the variable
	 * @return The variable value as an String
	 */
	public String getSingleValueFromCache(String variableName, R_VARIABLE_TYPE type) {

		return this.variableCache.get(variableName).get(0);

	}

	/**
	 * It gets the value from R (The expression is just the name of the
	 * variable), put the result in the cache and returns the value.
	 * 
	 * @param variableName
	 *            The name of the variable
	 * @param type
	 *            The type of the variable
	 * @return The variable value as an String
	 */
	public String getSingleValueFromR(String variableName, R_VARIABLE_TYPE type) {
		ListOfRVariablesToRefresh listOfRVariablesToRefresh = new ListOfRVariablesToRefresh();
		listOfRVariablesToRefresh.addVariableToQuery(variableName, type);
		this.getValuesFromR(listOfRVariablesToRefresh);
		return this.variableCache.get(variableName).get(0);
	}

	/**
	 * It gets the value from R (The expression is just the name of the
	 * variable), put the result in the cache and returns the value.
	 * 
	 * @param variableName
	 *            The name of the variable
	 * @param type
	 *            The type of the variable
	 * @param expression
	 *            The result of it will be the value to assign to the variable.
	 * @return The variable value.
	 */
	public String getSingleValueFromR(String variableName, R_VARIABLE_TYPE type, String expression) {
		ListOfRVariablesToRefresh listOfRVariablesToRefresh = new ListOfRVariablesToRefresh();
		listOfRVariablesToRefresh.addVariableToQuery(variableName, type, expression);
		this.getValuesFromR(listOfRVariablesToRefresh);
		return this.variableCache.get(variableName).get(0);
	}

	/**
	 * It gets the value from the cache. The value must be get from R (using
	 * getSingleValueFromR or getVariblesFromR
	 * 
	 * @param variableName
	 *            The name of the variable
	 * @param type
	 *            The type of the variable
	 * @return The variable value as an String
	 */
	public List<String> getArrayValueFromCache(String variableName, R_VARIABLE_TYPE type) {

		return this.variableCache.get(variableName);

	}

	/**
	 * It gets the value from R (The expression is just the name of the
	 * variable), put the result in the cache and returns the value.
	 * 
	 * @param variableName
	 *            The name of the variable
	 * @param type
	 *            The type of the variable
	 * 
	 * @return The variable value as an String
	 */
	public List<String> getArrayValueFromR(String variableName, R_VARIABLE_TYPE type) {

		ListOfRVariablesToRefresh listOfRVariablesToRefresh = new ListOfRVariablesToRefresh();
		listOfRVariablesToRefresh.addVariableToQuery(variableName, type);
		this.getValuesFromR(listOfRVariablesToRefresh);
		return this.variableCache.get(variableName);

	}

	/**
	 * It gets the value from R (The expression is just the name of the
	 * variable), put the result in the cache and returns the value.
	 * 
	 * @param variableName
	 *            The name of the variable
	 * @param type
	 *            The type of the variable
	 * @param type
	 *            The result of it will be the value to assing to the variable.
	 * @return The variable value as an String
	 */
	public List<String> getArrayValueFromR(String variableName, R_VARIABLE_TYPE type, String expression) {
		ListOfRVariablesToRefresh listOfRVariablesToRefresh = new ListOfRVariablesToRefresh();
		listOfRVariablesToRefresh.addVariableToQuery(variableName, type, expression);
		this.getValuesFromR(listOfRVariablesToRefresh);
		return this.variableCache.get(variableName);

	}

	// /////////////////////////////////////////////////
	// ////////API//////////////////////////////////
	// /////////////////////////////////////////////////

	public void nullVariable(String variableName) {
		this.addStatement(variableName + "<-" + "NA");
		flush();
	}

	public boolean isNull(String variableName) {
		flush();
		String isNull = this.getSingleValueFromR("isNull" + variableName, R_VARIABLE_TYPE.BOOLEAN, "is.na(" + variableName + ")");
		return Boolean.parseBoolean(isNull);
	}

	public void assign(String variableName, String expression) {
		this.addStatement(variableName + "<-" + expression);
	}

	public void concat(String variable4ResultName, List<String> elements) {
		StringBuilder script = new StringBuilder("");
		script.append(variable4ResultName + "<-paste(");
		for (String elementToPaste : elements) {
			script.append(elementToPaste);
			script.append(",");
		}
		if (elements.size() > 0) {
			script.deleteCharAt(script.length() - 1);
		}
		script.append(")");
		this.addStatement(script.toString());
	}

	public void assign(String variableName, List<String> array) {

		StringBuilder rArray = new StringBuilder("");
		rArray.append("c(");
		for (String element : array) {
			rArray.append(element);
			rArray.append(",");
		}
		rArray.deleteCharAt(rArray.length() - 1);
		rArray.append(")");
		this.addStatement(variableName + "<-" + rArray);
	}

	public void loadLibrary(String libraryName) {
		String libraryWithQuotes = StringUtils.addQuotes(libraryName);
		addStatement("load(" + libraryWithQuotes + ")");
	}

	public void plotInFile(String imageFileName, String expressionToPlot) {
		this.imageCreate(imageFileName);
		this.addStatement("plot(" + expressionToPlot + ", col=c('red', 'blue', 'green', 'orange', 'yellow', 'brown'))");
		this.imageRelease();
	}

	public String sinkInto(String outputFilePath, boolean append) {
		// r variables
		String rTemp = "RTemp";
		// end variables

		this.assignPathToRTemp(rTemp, outputFilePath);
		this.addStatement("sink(" + rTemp + ", append=" + RUtils.getRBoolean(append) + ")");
		return outputFilePath;
	}

	public void addStatementsOfTheFile(String filePath) {
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(filePath));
			String line = br.readLine();
			while (line != null) {
				if (!line.equals(OSDependentConstants.LINE_SEPARATOR)) {
					this.addStatement(line);
				}
				line = br.readLine();
			}
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	public void flush() throws RException {

		try {
			this.quit();
			nonFlushedScriptsBufferedWriter.close();
			this.runRProcessForExecutingFile(nonFlushedScriptsFilePath, false);
			this.preparNewFile();

		} catch (IOException e) {
			throw new RException("The temporal file could not be closed");
		}

	}

	public void addStatement(String line) {
		try {
			sessionScriptsLogBufferedWriter.write(line);
			sessionScriptsLogBufferedWriter.write(OSDependentConstants.LINE_SEPARATOR);

			nonFlushedScriptsBufferedWriter.write(line);
			nonFlushedScriptsBufferedWriter.flush();
			nonFlushedScriptsBufferedWriter.write(OSDependentConstants.LINE_SEPARATOR);
		} catch (IOException e) {
			LOGGER.error("The line: " + line + "couldnt be written. It will not considered in the actual R session");
		}
	}

	public void close() {
		// fileSystemUtils.deleteWorkingDirectory();
		try {
			sessionScriptsLogBufferedWriter.close();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	// ////////////////////////PRIVATE/////////////////////////////

	private void assignPathToRTemp(String variableName, String filePath) {
		List<String> pathParts = RUtils.getPathPartsOfAFile(filePath);
		this.concat(variableName, pathParts);
	}

	private void imageRelease() {
		this.addStatement("dev.off()");

	}

	private void imageCreate(String imageName) {
		// R Variables
		String pathInJavaFormat = "pathInJavaFormat";
		// End r variables

		List<String> pathPartsOfImageFile = RUtils.getPathPartsOfAFile(fileSystemUtils.completePathToUserFolder(imageName));
		this.concat(pathInJavaFormat, pathPartsOfImageFile);
		this.addStatement("png(" + pathInJavaFormat + ")");
	}

	private void printStream(InputStream inputStream) {
		InputStreamReader isr = new InputStreamReader(inputStream);
		try {
			// if (isr.ready()) {

			BufferedReader errorStream = new BufferedReader(isr);
			String line = errorStream.readLine();
			while (line != null) {
				// System.out.println(line);
				line = errorStream.readLine();
			}
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	private void resetNonFlushedScriptsBufferedWriter() {
		try {
			scriptFileNumber++;
			nonFlushedScriptsFilePath = originalNonFlushedScriptsFilePath + scriptFileNumber;
			nonFlushedScriptsBufferedWriter = FileManager.createFile(nonFlushedScriptsFilePath);

		} catch (IOException e) {

			LOGGER.error("The non flushed scripts file could not be reset");
		}

	}

	private String getTempFolderName4ThisSession() {
		sessionId++;
		return "R4J-session-" + sessionId + "Thread-" + Thread.currentThread().getId();
	}

	private void preparNewFile() {
		this.resetNonFlushedScriptsBufferedWriter();

		this.sinkInto(outputFilePath, false);
	}

	private void createFolderSession() {
		try {

			// It sets the Log
			String sessionScriptsLogPath = (fileSystemUtils.completePathToUserFolder(LOG_FILE_NAME));
			sessionScriptsLogBufferedWriter = FileManager.createFile(sessionScriptsLogPath);

			// Initialize the bufferedWriter to keep the non executed scripts.
			// Those scripts will be executed when calling flush() on this
			// session.

			originalNonFlushedScriptsFilePath = fileSystemUtils.completePathToTempFolder("scriptsForExecute-Thread" + Thread.currentThread().getId() + ".txt");
			nonFlushedScriptsFilePath = originalNonFlushedScriptsFilePath;
			this.resetNonFlushedScriptsBufferedWriter();

			// Sink into the resultFile recevied as parameter.
			outputFilePath = fileSystemUtils.completePathToUserFolder(RESULTS_FILE_NAME);
			this.sinkInto(outputFilePath, false);

		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	/**
	 * It returns in a map the content for each variable as a String list. For
	 * the variables that returns just one value, it also return it as a list
	 * but of just one element. It is faster than execute the
	 * getSingleStringValue or getArrayStringValue, because it can execute in
	 * just one R process execution all the variables while the others executes
	 * an R process, one per variable.
	 * 
	 * @param variables
	 * @return
	 */
	private Map<String, List<String>> accessRAndGetTheValuesOfBigVariables(List<RVariableDescription> variableNames, int numberMaxOfVariablesInOneExecution) {

		Map<String, List<String>> result = new HashMap<String, List<String>>();
		int times = variableNames.size() / numberMaxOfVariablesInOneExecution;

		for (int i = 0; i < times; i++) {
			doRAccess(variableNames.subList(i * numberMaxOfVariablesInOneExecution, ((i + 1) * numberMaxOfVariablesInOneExecution)), result);
		}
		doRAccess(variableNames.subList(times * numberMaxOfVariablesInOneExecution, variableNames.size()), result);

		return result;

	}

	private Map<String, List<String>> accessRAndGetTheValuesOfVariables(List<RVariableDescription> variableNames) {
		Map<String, List<String>> result = new HashMap<String, List<String>>();
		doRAccess(variableNames, result);
		return result;
	}

	private void doRAccess(List<RVariableDescription> variableNames, Map<String, List<String>> result) {

		for (RVariableDescription rVariableDescription : variableNames) {
			// SINK into a file for this variable
			this.sinkInto(fileSystemUtils.completePathToTempFolder(rVariableDescription.getName()), false);

			if (rVariableDescription.type == R_VARIABLE_TYPE.NUMERIC) {
				this.addStatement("as.numeric(" + rVariableDescription.getName() + ")");
			} else if (rVariableDescription.type == R_VARIABLE_TYPE.STRING) {
				this.addStatement("as.character(" + rVariableDescription.getName() + ")");
			} else if (rVariableDescription.type == R_VARIABLE_TYPE.BOOLEAN) {
				this.addStatement(rVariableDescription.getName());
			}

			// Variable

		}
		flush();
		BufferedReader bufferedReader;
		List<String> contentOfActualVariable = null;
		// PROCESAR ARCHIVOS
		for (RVariableDescription rVariableDescription : variableNames) {

			try {
				bufferedReader = new BufferedReader(new FileReader(fileSystemUtils.completePathToTempFolder(rVariableDescription.getName())));

				if (rVariableDescription.type == R_VARIABLE_TYPE.NUMERIC) {
					contentOfActualVariable = this.processRResponseForGettingNumericArray(bufferedReader, rVariableDescription.getName());
				} else if (rVariableDescription.type == R_VARIABLE_TYPE.STRING) {
					contentOfActualVariable = this.processRResponseForGettingStringArray(bufferedReader, rVariableDescription.getName());
				} else if (rVariableDescription.type == R_VARIABLE_TYPE.BOOLEAN) {
					contentOfActualVariable = this.processRResponseForGettingBooleanArray(bufferedReader, rVariableDescription.getName());
				}
				result.put(rVariableDescription.getName(), contentOfActualVariable);
			} catch (FileNotFoundException e) {
				LOGGER.error("The file: " + fileSystemUtils.completePathToTempFolder(rVariableDescription.getName()) + " is not available Perhaps it doesnt exis in the R session. The variable: " + rVariableDescription.getName() + " will not be avaiable");
			}

		}

	}

	private List<String> processRResponseForGettingBooleanArray(BufferedReader bufferedReader, String name) {
		List<String> result = new ArrayList<String>();
		try {

			// String resultLine = bufferedReader.readLine();
			String resultLine = bufferedReader.readLine();

			while (resultLine != null) {

				resultLine = resultLine.trim();
				String[] resultLineParts = resultLine.split(OSDependentConstants.BLANK_CHAR + "+");
				for (int i = 1; i < resultLineParts.length; i++) {
					result.add(resultLineParts[i]);

				}
				resultLine = bufferedReader.readLine();
			}

			return result;
		} catch (IOException e) {
			throw new RException("It was not possible to read the variable " + name);
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new RException("It was not possible to read the variable " + name);
		}

	}

	/**
	 * Method for getting an array of strings that must be sorrounded by some
	 * char; for example quotes
	 * 
	 * @param inputStream
	 * @param variableName
	 * @return
	 */
	private List<String> processRResponseForGettingStringArray(BufferedReader bufferedReader, String variableName) {

		List<String> result = new ArrayList<String>();

		String resultLine;
		try {
			// resultLine = bufferedReader.readLine();
			resultLine = bufferedReader.readLine();
			StringBuffer actualString = new StringBuffer("");
			while (resultLine != null) {

				int actualPos = 0;

				while (actualPos < resultLine.length() - 1) {
					actualString = new StringBuffer("");
					char actualChar = resultLine.charAt(actualPos);
					while (actualChar != '"' && actualPos < resultLine.length() - 1) {
						actualPos++;
						actualChar = resultLine.charAt(actualPos);
					}

					if (actualPos < resultLine.length() - 1) {
						actualPos++;
						actualChar = resultLine.charAt(actualPos);
						while (actualChar != '"') {
							actualString.append(resultLine.charAt(actualPos));
							actualPos++;
							actualChar = resultLine.charAt(actualPos);
						}

						result.add('"' + actualString.toString() + '"');

						if (actualPos < resultLine.length() - 1) {
							actualPos++;
							actualChar = resultLine.charAt(actualPos);
						}
					}
				}

				resultLine = bufferedReader.readLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;

	}

	private List<String> processRResponseForGettingNumericArray(BufferedReader bufferedReader, String variableName) throws RException {

		List<String> result = new ArrayList<String>();
		try {

			// String resultLine = bufferedReader.readLine();
			String resultLine = bufferedReader.readLine();

			while (resultLine != null) {

				resultLine = resultLine.trim();
				String[] resultLineParts = resultLine.split(OSDependentConstants.BLANK_CHAR + "+");
				for (int i = 1; i < resultLineParts.length; i++) {
					result.add(resultLineParts[i]);
				}
				resultLine = bufferedReader.readLine();
			}

			return result;
		} catch (IOException e) {
			throw new RException("It was not possible to read the variable " + variableName);
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new RException("It was not possible to read the variable " + variableName);
		}
	}

	private void runRProcessForExecutingExpressionOrFile(String expressionOrFilePath, boolean async, R_EXECUTION_MODE_ENUM executionMode) {
		String userHome = fileSystemUtils.getUserFolderPath();
		try {
			if (executionMode == R_EXECUTION_MODE_ENUM.EXPRESSION) {

				Runtime.getRuntime().exec(getCommandStringForExecutingR() + "-e " + expressionOrFilePath, null, new File(userHome));
			} else {
				BufferedReader br = new BufferedReader(new FileReader(expressionOrFilePath));
				String l = br.readLine();
				System.out.println("**********************************");
				System.out.println("Starting to execute: " + expressionOrFilePath);
				while (l != null) {
					System.out.println(l);
					l = br.readLine();
				}

				System.out.println(getCommandStringForExecutingR() + "-f " + expressionOrFilePath);
				Process rProcess = Runtime.getRuntime().exec(getCommandStringForExecutingR() + "-f " + expressionOrFilePath, null, new File(userHome));
				printStream(rProcess.getInputStream());

			}
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	// private Process runRProcessForExecutingExpressionOrFile(String
	// expressionOrFilePath, boolean async, R_EXECUTION_MODE_ENUM executionMode)
	// throws RException {
	// try {
	// String userHome = fileSystemUtils.getUserFolderPath();
	// Process rProcess;
	// if (executionMode == R_EXECUTION_MODE_ENUM.EXPRESSION) {
	// rProcess = Runtime.getRuntime().exec(getCommandStringForExecutingR() +
	// "-e " + expressionOrFilePath, null, new File(userHome));
	// } else {
	// System.out.println(getCommandStringForExecutingR() + "-f " +
	// expressionOrFilePath);
	// rProcess = Runtime.getRuntime().exec(getCommandStringForExecutingR() +
	// "-f " + expressionOrFilePath, null, new File(userHome));
	//
	// printStream(rProcess.getErrorStream());
	//
	// // WORKAROUND. If the input stream is full then the process
	// // doesnt finish and the process.waitFor() doesnt return.
	// // Considering that this statement is not for getting a value
	// // and i dont need the inputStream text, i "clean" it.
	// if (!async)
	// printStream(rProcess.getInputStream());
	// }
	// if (!async) {
	// // printStream(rProcess.getErrorStream());
	// // printStream(rProcess.getInputStream());
	//
	// rProcess.waitFor();
	// }
	//
	// return rProcess;
	// } catch (IOException e) {
	// LOGGER.error("It was not possible to execute R.");
	// throw new
	// RException("Error trying to execute the R process as external program");
	//
	// } catch (InterruptedException e) {
	// LOGGER.error("Problem executing R process");
	// throw new
	// RException("Error trying to execute the R process as external program");
	//
	// }
	//
	// }

	private void quit() {
		this.addStatement("q(save='yes')");

	}

	private void runRProcessForExecutingFile(String filePath, boolean async) throws RException {
		runRProcessForExecutingExpressionOrFile(filePath, async, R_EXECUTION_MODE_ENUM.FILE);
	}

	/**
	 * It returns the string containing the r path and the common parameters for
	 * any r execution from R4J (-q and --save)
	 * 
	 * @return
	 */
	private String getCommandStringForExecutingR() {
		return OSDependentConstants.DOUBLE_QUOTE + OSDependentConstants.PATH_TO_R + OSDependentConstants.DOUBLE_QUOTE + " --save --restore -q ";
	}
}
