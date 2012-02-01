package edu.unlp.medicine.r4j.utils;

import edu.unlp.medicine.r4j.constants.OSDependentConstants;

public class FileSystemUtilsForDefaultR4JFolder extends FileSystemUtils {

	public FileSystemUtilsForDefaultR4JFolder(String tempFolderName, String userFolderName) {
		super(tempFolderName, userFolderName);
		// TODO Auto-generated constructor stub
	}
	
	public String getUserFolderPath(){
		return OSDependentConstants.USER_HOME + OSDependentConstants.FILE_SEPARATOR + r4jFolder + OSDependentConstants.FILE_SEPARATOR + userFolderName;
	}
	
	public String getTempFolderPath(){
		return this.getUserFolderPath() + OSDependentConstants.FILE_SEPARATOR + tempFolderName;
	}
	
	public String completePathToUserFolder(String fileName){
		return this.getUserFolderPath() + OSDependentConstants.FILE_SEPARATOR + fileName;
	}

	public String completePathToTempFolder(String fileName){
		return this.getTempFolderPath() + OSDependentConstants.FILE_SEPARATOR + fileName;
	}
	


}
