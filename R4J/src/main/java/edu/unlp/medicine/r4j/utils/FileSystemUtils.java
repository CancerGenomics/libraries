package edu.unlp.medicine.r4j.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unlp.medicine.r4j.constants.OSDependentConstants;
import edu.unlp.medicine.r4j.server.R4JConnection;

public class FileSystemUtils {

	public static final String R4J_FOLDER = "R4J";
	private static final Logger LOGGER = LoggerFactory.getLogger(R4JConnection.class);
	
	
	public static String getUserFolderPath(){
		return OSDependentConstants.USER_HOME + OSDependentConstants.FILE_SEPARATOR + R4J_FOLDER;
	}
	
	
	public static  String completePathToUserFolder(String fileName){
		return getUserFolderPath() + OSDependentConstants.FILE_SEPARATOR + fileName;
	}

	public static BufferedWriter createBufferedWriter(String filePath) throws IOException{
		try {
			File file = new File(filePath);
			file.getParentFile().mkdirs();
			return new BufferedWriter(new FileWriter(file));
		} catch (IOException e) {
			
			LOGGER.error("Error trying to create the file on path: " + filePath + e);
			throw e;
		}
		
		
	}


	public static BufferedWriter createFile(String path) throws IOException{
		return createFile(path, false);
	}
	
	public static BufferedWriter createFile(String path, boolean append) throws IOException{
		BufferedWriter bufferedWriter;
		try {
			bufferedWriter = new BufferedWriter(new FileWriter(path, append));
		} catch (IOException e) {
			File file = new File(path);
			file.getParentFile().mkdirs();
			try {
				bufferedWriter = new BufferedWriter(new FileWriter(path, append));
			} catch (IOException e1) {
				e.printStackTrace();
				throw e1;
			}
		}
		return bufferedWriter;
	}

	
}
