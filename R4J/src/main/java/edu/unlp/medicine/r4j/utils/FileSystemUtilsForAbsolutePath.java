package edu.unlp.medicine.r4j.utils;

import edu.unlp.medicine.r4j.constants.OSDependentConstants;

public class FileSystemUtilsForAbsolutePath extends FileSystemUtils{

	String path;
	
	public FileSystemUtilsForAbsolutePath(String tempFolderName, String userFolderName, String path) {
		super(tempFolderName, userFolderName);
		this.path = path;
		
	}
	
	public String getUserFolderPath(){
		return path + OSDependentConstants.FILE_SEPARATOR + userFolderName;
	}
	
	public String getTempFolderPath(){
		return path + OSDependentConstants.FILE_SEPARATOR + tempFolderName;
	}
	
	public String completePathToUserFolder(String fileName){
		return this.getUserFolderPath() + OSDependentConstants.FILE_SEPARATOR + fileName;
	}

	public String completePathToTempFolder(String fileName){
		return this.getTempFolderPath() + OSDependentConstants.FILE_SEPARATOR + fileName;
	}

}
