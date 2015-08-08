package edu.unlp.medicine.r4j.utils;

import java.util.ArrayList;
import java.util.List;

import edu.unlp.medicine.r4j.constants.OSDependentConstants;

public class RUtils {
	
	public static ArrayList<String> getPathPartsOfAFile(String filePath){
		
        String[] parts = filePath.split("\\"+ OSDependentConstants.FILE_SEPARATOR);
        
        ArrayList<String> pathParts = new ArrayList<String>();
        int i=0;
        for (i = 0; i < parts.length-1; i++) {
        	pathParts.add(StringUtils.addQuotes(parts[i]));
        	pathParts.add(".Platform$file.sep");
		}
        pathParts.add(StringUtils.addQuotes(parts[i]));
        
        pathParts.add("sep=" + OSDependentConstants.DOUBLE_QUOTE + OSDependentConstants.DOUBLE_QUOTE);
        return pathParts; 
	}
	
	public static String getRBoolean(boolean b){
		if (b) return "TRUE";
		return "FALSE";
		
	}

	public static String javaStringListAsRStringList(List<String> aList){
			StringBuffer result = new StringBuffer("c(\"");
			
			for (String element : aList) {
				result.append(element  + "\",\"");
			}
			result.delete(result.length()-3, result.length());
			result.append("\")");
			return result.toString();
			
	}

	
}
