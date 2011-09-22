package edu.unlp.medicine.r4j.utils;

import java.util.ArrayList;

import edu.unlp.medicine.r4j.constants.OSDependentConstants;

public class RUtils {
	
	public static ArrayList<String> getPathPartsOfAFile(String fileName){
		String filePathInJavaFormat =  OSDependentConstants.USER_HOME + OSDependentConstants.FILE_SEPARATOR + fileName;
        String[] parts = filePathInJavaFormat.split("\\"+ OSDependentConstants.FILE_SEPARATOR);
        
        ArrayList<String> pathParts = new ArrayList<String>();
        for (int i = 0; i < parts.length-1; i++) {
        	pathParts.add(StringUtils.addQuotes(parts[i]));
        	pathParts.add(".Platform$file.sep");
		}
        pathParts.add(StringUtils.addQuotes(fileName));
        pathParts.add("sep=" + OSDependentConstants.DOUBLE_QUOTE + OSDependentConstants.DOUBLE_QUOTE);
        return pathParts; 
	}

}
