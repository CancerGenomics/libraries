package edu.unlp.medicine.r4j.utils;

import java.io.File;

import edu.unlp.medicine.r4j.constants.OSDependentConstants;

public class FileSystemUtils {
	
	String r4jFolder = "R4J";
	File r4jFolderFile;

	String tempFolderName;
	File tempFolderFile;
	
	String userFolderName;
	File userFolderFile;
	
	public FileSystemUtils(String tempFolderName, String userFolderName){
		
		this.tempFolderName = tempFolderName;
		this.userFolderName = userFolderName;
		
		mkDir(OSDependentConstants.USER_HOME + OSDependentConstants.FILE_SEPARATOR + r4jFolder);
		mkDir(this.getUserFolderPath());
		mkDir(this.getTempFolderPath());
		
		
	}
	

	private void mkDir(String path) {
		r4jFolderFile = new File(path);
		r4jFolderFile.mkdir();
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
	
	
	

//	public void deleteWorkingDirectory() {
//		File[] files = temp.listFiles();
//	      for(int i=0; i<files.length; i++) {
//	           files[i].delete();
//	      }
//	    folderFile.delete();
//	}
		
	

}
