package edu.unlp.medicine.r4j.utils;

import java.io.File;

import edu.unlp.medicine.r4j.constants.OSDependentConstants;

public abstract class FileSystemUtils {
	
	String r4jFolder = "R4J";
	File r4jFolderFile;

	String tempFolderName;
	String userFolderName;
	
	
	public FileSystemUtils(String tempFolderName, String userFolderName){
		
		this.tempFolderName = tempFolderName;
		this.userFolderName = userFolderName;
		
//		mkDir(OSDependentConstants.USER_HOME + OSDependentConstants.FILE_SEPARATOR + r4jFolder);
//		mkDir(this.getUserFolderPath());
//		mkDir(this.getTempFolderPath());
		
		
	}
	

//	private void mkDir(String path) {
//		r4jFolderFile = new File(path);
//		r4jFolderFile.mkdir();
//	}

	public abstract String getUserFolderPath();
	
	public abstract String getTempFolderPath();
	
	public abstract String completePathToUserFolder(String fileName);

	public abstract String completePathToTempFolder(String fileName);
	
	
	

//	public void deleteWorkingDirectory() {
//		File[] files = temp.listFiles();
//	      for(int i=0; i<files.length; i++) {
//	           files[i].delete();
//	      }
//	    folderFile.delete();
//	}
		
	

}
