package edu.unlp.medicine.r4j.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

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
 * It models an R session. The actual implementation writes a file in the path
 * nonFlushedScriptsFilePath (apart of the log whose path is received as
 * parameter) with all non executed r statements. When the flush() method is
 * called, all the statements in this file will be executed. For executing the
 * statement in the nonFlushedScriptsFilePath file, R is run as an external
 * program using the -f parameter passing the mentioned file as parameter. It is
 * important to mention that the R program is run on the working directory
 * System.getProperty("user.home"). So, the starting point of any relative path
 * used in R (including the R files executed) will be start in this path.
 * 
 * The session creates a folder inside the System.getProperty("user.home")/R4J
 * folder. The name of this folder is a mandatory constructor parameter and this
 * folder is known as UserFolder. In this folder you can find LOG.TXT with all
 * the log of this session. RESULTS.txt with all the R output during this
 * session. It is also the .rdata file which will be used to restore the
 * workspace with the variables of the previous flush() invokes.
 * 
 * Inside the UserFolder will be also a folder called
 * R4J-session-<sessionId>Thread-<THREADID>. In this folder there will be one
 * file per flush with the R statements executed in this flush.
 * 
 * It is important to call flush every time you want to execute R statements.
 * All the statements which returns something do flush automatically. For
 * example getValue!!
 * 
 * @author Matias
 * 
 */
public class R4JSession {

	// BufferedWriter with all the r statements executed along this session
	BufferedWriter sessionScriptsLogBufferedWriter;

	// Non flushed scripts
	String nonFlushedScriptsFilePath;
	BufferedWriter nonFlushedScriptsBufferedWriter;

	// File path to the output results
	String outputFilePath;

	// Session id to allow working with more than one session in the same
	// thread.
	static int sessionId = 0;

	// This is a workaround. If i do executeRASExternalProgramWith-F() and then
	// resetFile() then it doenst work. That is why there are 1 file for each
	// flush.
	int scriptFileNumber = 0;

	// This is the fileSystemUtils configured with the userFolderName received
	// as constructor parameter.
	FileSystemUtils fileSystemUtils;

	static String TEMP_FOLDER = "TMP";
	static String LOG_FILE_NAME = "LOG.TXT";
	static String RESULTS_FILE_NAME = "RESULTS.TXT";
	private static Logger LOGGER = LoggerFactory.getLogger(R4J.class);

	
	
	/**
	 * 
	 * @param sessionScriptsLogFileName
	 * @param resultsFileName
	 * @throws RException
	 */
	public R4JSession(String sessionName) throws RException {
		fileSystemUtils = new FileSystemUtilsForDefaultR4JFolder(this.getTempFolderName4ThisSession(), sessionName);
		createFolderSession();
	}

	
	public R4JSession(String sessionName, String path) throws RException {
		fileSystemUtils = new FileSystemUtilsForAbsolutePath(this.getTempFolderName4ThisSession(), sessionName, path);
		createFolderSession();
}
	
	private void createFolderSession() {
		try {

			// It sets the Log
			String sessionScriptsLogPath = (fileSystemUtils.completePathToUserFolder(LOG_FILE_NAME));
			sessionScriptsLogBufferedWriter = FileManager.createFile(sessionScriptsLogPath);

			// Initialize the bufferedWriter to keep the non executed scripts.
			// Those scripts will be executed when calling flush() on this
			// session.
			nonFlushedScriptsFilePath = fileSystemUtils.completePathToTempFolder("scriptsForExecute-Thread" + Thread.currentThread().getId() + ".txt");
			this.resetNonFlushedScriptsBufferedWriter();

			// Sink into the resultFile recevied as parameter.
			outputFilePath = fileSystemUtils.completePathToUserFolder(RESULTS_FILE_NAME);
			this.sinkInto(outputFilePath, false);

		} catch (IOException e) {

			e.printStackTrace();
		}

		
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

	/**
	 * It flushes and then it runs R for executing the expression for querying
	 * the variable name. For reading the data it queries the
	 * rProcess.getInputStram(). It should be used for variables holding single
	 * values (not arrays, not matrix).
	 * 
	 * @param variableName
	 *            a variable with a single value.
	 * @return The variable value in the R session.
	 * @throws RException
	 */
	public String getSingleValue(String variableName) throws RException {
		flush();
		Process rProcessForGettingVariable = runRProcessForExecutingExpression(variableName, false);
		return processRResponseForGettingSingleValue(rProcessForGettingVariable.getInputStream(), variableName);
	}

	/**
	 * It flushes and then it runs R for executing the expression for querying
	 * the variable name. For reading the data it queries the
	 * rProcess.getInputStram(). It should be used for variables holding values
	 * in an array (not matrix).
	 * 
	 * @param variableName
	 *            a variable with an array value.
	 * @return The variable value in the R session.
	 * @throws RException
	 */
	public List<String> getArrayValue(String variableName) throws RException {
		flush();
		Process rProcessForGettingVariable = runRProcessForExecutingExpression(variableName, true);

		// printStream(rProcessForGettingVariable.getInputStream());
		return processRResponseForGettingArrayValue(rProcessForGettingVariable.getInputStream(), variableName);
	}

	private List<String> processRResponseForGettingArrayValue(InputStream rInputStream, String variableName) throws RException {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(rInputStream));

		List<String> result = new ArrayList<String>();
		try {
			System.out.println(bufferedReader.readLine());
			String resultLine = bufferedReader.readLine();
			System.out.println(resultLine);
			String[] resultLineParts = resultLine.split(OSDependentConstants.BLANK_CHAR);
			for (int i = 1; i < resultLineParts.length; i++) {
				result.add(resultLineParts[i]);
			}

			return result;
		} catch (IOException e) {
			throw new RException("It was not possible to read the variable " + variableName);
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new RException("It was not possible to read the variable " + variableName);
		}
	}

	/**
	 * R returns the result in the following way: > result [1] 14 This method
	 * will get the second line and then pick up the second part of the split
	 * using the blank char.
	 * 
	 * @param rInputStream
	 * @param variableName
	 * @return
	 * @throws RException
	 */
	private String processRResponseForGettingSingleValue(InputStream rInputStream, String variableName) throws RException {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(rInputStream));
		try {
			bufferedReader.readLine();
			String resultLine = bufferedReader.readLine();
			return resultLine.split(OSDependentConstants.BLANK_CHAR)[1];
		} catch (IOException e) {
			throw new RException("It was not possible to read the variable " + variableName);
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new RException("It was not possible to read the variable " + variableName);
		}
	}

	private Process runRProcessForExecutingExpression(String expression, boolean async) throws RException {
		return runRProcessForExecutingExpressionOrFile(expression, async, R_EXECUTION_MODE_ENUM.EXPRESSION);
	}

	private Process runRProcessForExecutingExpressionOrFile(String expressionOrFilePath, boolean async, R_EXECUTION_MODE_ENUM executionMode) throws RException {
		try {
			String userHome = fileSystemUtils.getUserFolderPath();
			Process rProcess;
			if (executionMode == R_EXECUTION_MODE_ENUM.EXPRESSION) {
				rProcess = Runtime.getRuntime().exec(getCommandStringForExecutingR() + "-e " + expressionOrFilePath, null, new File(userHome));
			} else {
				System.out.println(getCommandStringForExecutingR() + "-f " + expressionOrFilePath);
				rProcess = Runtime.getRuntime().exec(getCommandStringForExecutingR() + "-f " + expressionOrFilePath, null, new File(userHome));
					
				printStream(rProcess.getErrorStream());
				
				
				
				
				// WORKAROUND. If the input stream is full then the process
				// doesnt finish and the process.waitFor() doesnt return.
				// Considering that this statement is not for getting a value
				// and i dont need the inputStream text, i "clean" it.
				if (!async)
					printStream(rProcess.getInputStream());
			}
			if (!async) {
				// printStream(rProcess.getErrorStream());
				// printStream(rProcess.getInputStream());

				rProcess.waitFor();
			}

			return rProcess;
		} catch (IOException e) {
			LOGGER.error("It was not possible to execute R.");
			throw new RException("Error trying to execute the R process as external program");

		} catch (InterruptedException e) {
			LOGGER.error("Problem executing R process");
			throw new RException("Error trying to execute the R process as external program");

		}

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

	private void quit() {
		this.addStatement("q()");

	}

	private Process runRProcessForExecutingFile(String filePath, boolean async) throws RException {
		return runRProcessForExecutingExpressionOrFile(filePath, async, R_EXECUTION_MODE_ENUM.FILE);
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
				System.out.println(line);
				line = errorStream.readLine();
			}

			// StringBuffer buffer = new StringBuffer();
			// int ch;
			//
			// while ((ch = errorStream.read()) > -1) {
			// buffer.append((char) ch);
			// }
			// System.out.println(buffer.toString());

			// }
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	

	
	private void resetNonFlushedScriptsBufferedWriter() {
		try {
			scriptFileNumber++;
			nonFlushedScriptsFilePath = nonFlushedScriptsFilePath + scriptFileNumber;
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

	// public String getString(String libraryName){
	// String libraryWithQuotes = StringUtils.addQuotes(libraryName);
	// addScriptLine("load(" + libraryWithQuotes + ")");
	// }
	//
	// public int getInt(){
	//
	// }
	//
	// public int getDouble(){
	//
	// }
	//
	// public getIntArray(){
	//
	// }

}
