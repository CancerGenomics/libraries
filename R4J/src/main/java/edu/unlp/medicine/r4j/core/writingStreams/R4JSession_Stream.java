package edu.unlp.medicine.r4j.core.writingStreams;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unlp.medicine.r4j.constants.OSDependentConstants;
import edu.unlp.medicine.r4j.core.RException;
import edu.unlp.medicine.r4j.utils.FileSystemUtils;
import edu.unlp.medicine.r4j.utils.RUtils;
import edu.unlp.medicine.r4j.utils.StringUtils;

/**
 * It models a R session. All the execution will be part of the same session. In
 * the actual implementation all the lines of the file will be executed again
 * and again. When the problem of writing the OutputStream of the R process, it
 * will not necessary to execute again because the process is the same and R
 * keeps the previous execution session.
 * 
 * @author Matias
 * 
 */
public class R4JSession_Stream {

	String filePath;
	BufferedWriter actualSessionFile;
	String outputFileName;
	Process rProcess;
	FileSystemUtils fileSystemUtils;
	
	private static Logger LOGGER = LoggerFactory.getLogger(R4J_Stream.class);

	public R4JSession_Stream(String rFileNameForWritingTheScripts, String outputFileName) throws RException {
		this.outputFileName = outputFileName;
		startRProcess();
		setRFilePathForWritingTheScripts(fileSystemUtils.completePathToUserFolder(rFileNameForWritingTheScripts));
		sinkInto(outputFileName, false);
	}

	public String sinkInto(String outputFileName, boolean append) {
		// r variables
		String varOutputFilePath = "pathToOutputFile";
		// end variables
		String outputFilePath = fileSystemUtils.completePathToUserFolder(outputFileName);
		this.assignPathToUserHome(varOutputFilePath, outputFileName);
		this.executeStatement("sink(" + varOutputFilePath + ", append=" + RUtils.getRBoolean(append) + ")");
		return outputFilePath;
	}

	private void startRProcess() throws RException {
		try {
			rProcess = Runtime.getRuntime().exec(OSDependentConstants.DOUBLE_QUOTE + OSDependentConstants.PATH_TO_R + OSDependentConstants.DOUBLE_QUOTE + " --no-save");

		} catch (IOException e) {
			throw new RException("It is not possible to start R");
		}

	}

	public R4JSession_Stream(String path) throws RException {
		setRFilePathForWritingTheScripts(path);
	}

	public void setRFilePathForWritingTheScripts(String path) throws RException {
		filePath = path;
		try {
			actualSessionFile = new BufferedWriter(new FileWriter(path));

		} catch (IOException e) {
			throw new RException("It is not possible to create the r temporal file: " + path);
		}

	}

	public List<String> getValue(String variableName) throws RException {

		String outputFilePath = sinkInto("TemporalForQueryVariableValue" + "Thread" + Thread.currentThread().getId() + ".txt", false);
		this.executeStatement(variableName);
		sinkInto(outputFileName, true);

		return this.readVariableResult(outputFilePath, variableName);

	}

	private List<String> readVariableResult(String outputFilePath, String variableName) {

		List<String> listOfResults = new ArrayList<String>();

		try {
			BufferedReader resultFile = new BufferedReader(new FileReader(outputFilePath));
			String[] resultParts = null;
			String line = resultFile.readLine();
			String previousLine = resultFile.readLine();
			while (line != null) {
				previousLine = line;
				if (line.equals(OSDependentConstants.LINE_SEPARATOR)) {
					line = resultFile.readLine();
				}
			}
			if (previousLine != null) {
				String result = previousLine.substring(4);
				resultParts = result.split(" ");
			} else {
				LOGGER.error("The variable name: " + variableName + " doesnt exist.");
			}

			for (String value : resultParts) {
				listOfResults.add(value);
			}

		} catch (IOException e) {
			LOGGER.error("IOException trying to get the the value of the variable: " + variableName);
		}
		return listOfResults;
	}

	
	
	public void executeStatement(String line) {
		try {
			actualSessionFile.write(line + "\n");
			actualSessionFile.flush();

			
			//WritableByteChannel outChannel = Channels.newChannel(rProcess.getOutputStream());
			
			//outChannel.write(ByteBuffer.wrap(line.getBytes()));
			//outChannel.write(ByteBuffer.wrap(OSDependentConstants.LINE_SEPARATOR.getBytes()));
			PrintWriter bw = new PrintWriter(new OutputStreamWriter(rProcess.getOutputStream()));

			//bw.write(line);
			
			
			// actualSessionFile.flush();

		} catch (IOException e) {
			LOGGER.error("The line: " + line + "couldnt be written. It will not considered in the actual R session");
		}
	}

	public void assign(String variableName, String expression) {
		this.executeStatement(variableName + "<-" + expression);
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
		this.executeStatement(variableName + "<-" + rArray);
	}

	public void loadLibrary(String libraryName) {
		String libraryWithQuotes = StringUtils.addQuotes(libraryName);
		executeStatement("load(" + libraryWithQuotes + ")");
	}

	public void plotInFile(String imageFileName, String expressionToPlot) {
		this.imageCreate(imageFileName);
		this.executeStatement("plot(" + expressionToPlot + ")");
		this.imageRelease();
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
		this.executeStatement(script.toString());
	}

	public void assignPathToUserHome(String variableName, String fileName) {
		List<String> pathParts = RUtils.getPathPartsOfAFile(fileName);
		this.concat(variableName, pathParts);
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

	private void imageRelease() {
		this.executeStatement("dev.off()");

	}

	private void imageCreate(String imageName) {
		// R Variables
		String pathInJavaFormat = "pathInJavaFormat";
		// End r variables

		List<String> pathPartsOfImageFile = RUtils.getPathPartsOfAFile(imageName);
		this.concat(pathInJavaFormat, pathPartsOfImageFile);
		this.executeStatement("png(" + pathInJavaFormat + ")");
	}

	protected void finalize() throws Throwable {
		actualSessionFile.close();
		super.finalize(); // not necessary if extending Object.
	}

	public void printStreamError() {
		this.printStream(rProcess.getErrorStream());
	}

	public void printConsole() {
		this.printStream(rProcess.getInputStream());
	}

	private void printStream(InputStream inputStream) {
		InputStreamReader isr = new InputStreamReader(inputStream);
		try {
			if (isr.ready()){
		
					BufferedReader errorStream = new BufferedReader(isr);
		
					StringBuffer buffer = new StringBuffer();
					int ch;
		
			
					while ((ch = errorStream.read()) > -1) {
						buffer.append((char)ch);
					}
					LOGGER.info(buffer.toString());
			}
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		}
}
