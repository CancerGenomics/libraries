package edu.unlp.medicine.r4j.utils;

import edu.unlp.medicine.r4j.constants.OSDependentConstants;

public class FileSystemUtils {
	
	public static String completePathToUserFolder(String fileName){
		return OSDependentConstants.USER_HOME + OSDependentConstants.FILE_SEPARATOR + fileName;
		
	}

}
