package edu.unlp.medicine.r4j.core;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unlp.medicine.r4j.constants.OSDependentConstants;
import edu.unlp.medicine.r4j.constants.RConstants;
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
public class R4JSession {

	String filePath;
	BufferedWriter actualSessionFile;
	R4J r4j;
	private static Logger LOGGER = LoggerFactory.getLogger(R4J.class);

	public R4JSession(String inputPath, String outputFileName) throws RException {
		//variables
		String outputFilePath="pathToOutputFile";
		
		setInputPath(inputPath);
		
		this.assignPathToUserHome(outputFilePath, outputFileName);
		this.addStatement("sink("+outputFilePath+")");
		
	}
	
	public R4JSession(String path) throws RException {
		setInputPath(path);
	}

	public void setInputPath(String path) throws RException {
		filePath = path;
		try {
			actualSessionFile = new BufferedWriter(new FileWriter(path));
		} catch (IOException e) {
			throw new RException("It is not possible to create the r temporal file: " + path);
		}

	}
	
	public void execute() throws RException {
		try {
			actualSessionFile.close();
			R4JFactory.getR4JInstance().executeFile(filePath);
		} catch (IOException e) {
			throw new RException("The temporal file: " + filePath + " could not be closed");
		}
	}
	
	public void addStatement(String line){
		try {
			actualSessionFile.write(line);
			actualSessionFile.write(OSDependentConstants.LINE_SEPARATOR);
		} catch (IOException e) {
			LOGGER.error("The line: " + line + "couldnt be written. It will not considered in the actual R session");
		}
	}
	
	public void assign(String variableName, String expression){
		this.addStatement(variableName + "<-" + expression);
	}
	
	public void assign(String variableName, List<String> array){
		
		StringBuilder rArray = new StringBuilder("");
		rArray.append("c(");
		for (String element : array) {
			rArray.append(element);
			rArray.append(",");
		}
		rArray.deleteCharAt(rArray.length()-1);
		rArray.append(")");
		this.addStatement(variableName + "<-" + rArray);
	}
	
	
	
	
	public void loadLibrary(String libraryName){
		String libraryWithQuotes = StringUtils.addQuotes(libraryName);
		addStatement("load(" + libraryWithQuotes + ")");
	}
	
	
	public void plotInFile(String imageFileName, String expressionToPlot){
		this.imageCreate(imageFileName);
		this.addStatement("plot(" + expressionToPlot + ")");
		this.imageRelease();
	}
	
	
	public void concat(String variable4ResultName,  List<String> elements){
		StringBuilder script = new StringBuilder("");
		script.append(variable4ResultName + "<-paste(");
		for (String elementToPaste: elements) {
			script.append(elementToPaste);
			script.append(",");
		}
		if (elements.size()>0){
			script.deleteCharAt(script.length()-1);
		}
		script.append(")");
		this.addStatement(script.toString());
	}
	
	public void assignPathToUserHome(String variableName, String fileName){
		List<String> pathParts = RUtils.getPathPartsOfAFile(fileName);        
        this.concat(variableName, pathParts);
	}
	
//	public String getString(String libraryName){
//		String libraryWithQuotes = StringUtils.addQuotes(libraryName);
//		addScriptLine("load(" + libraryWithQuotes + ")");
//	}
//
//	public int getInt(){
//		
//	}
//	
//	public int getDouble(){
//		
//	}
//	
//	public getIntArray(){
//		
//	}
	

	private void imageRelease(){
		this.addStatement("dev.off()");
		
	}
	
	private void imageCreate(String imageName){
		//R Variables
		String pathInJavaFormat = "pathInJavaFormat";
		//End r variables
		
		List<String> pathPartsOfImageFile = RUtils.getPathPartsOfAFile(imageName);        
        this.concat(pathInJavaFormat, pathPartsOfImageFile);
        this.addStatement("png(" + pathInJavaFormat + ")");
	}
	
	
	
	
}
