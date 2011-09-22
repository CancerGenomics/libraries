package edu.unlp.medicine.r4j.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unlp.medicine.r4j.constants.OSDependentConstants;
import edu.unlp.medicine.r4j.utils.FileSystemUtils;



/**
 * Main class of the bridge. It is the entry point to get an RSession.
 * @author Matias Butti
 *
 */
public class R4J {
	
	private static Logger LOGGER = LoggerFactory.getLogger(R4J.class);
	
	public void executeFile(String filePathWithRScript, String result){
		
	}
	
	public void executeFile(String filePathWithRScript){
		try {
			Process p = Runtime.getRuntime().exec(OSDependentConstants.PATH_TO_R + " -f " + filePathWithRScript);
			
			
		} catch (IOException e) {
			new RException("There was an I/O error trying to execute R as an external program.");
		}	
	}
	
	/**
	 * It creates a clean RSession.  
	 * @param path The path of the temporal file to write the scripts of the session.
	 * @return It returns the session.
	 * @throws RException If there is any I/O problem trying to create the file the method will throw an RException and the session will not be created.
	 */
	private static R4JSession getRSessionForPath(String inputFilepath, String outputFileName) throws RException{
				try {
					return new R4JSession(inputFilepath, outputFileName);
				} catch (RException e) {
					LOGGER.error(e.getMessage());
					throw e;
				}
				
	}

	/**
	 * It creates a clean RSession in a generic temporal file.  
	 * @param path The path of the temporal file to write the scripts of the session.
	 * @return It returns the session.
	 * @throws RException If there is any I/O problem trying to create the file the method will throw an RException and the session will not be created.
	 */
	public R4JSession getRSession(String inputPath, String outputFileName) throws RException{
		
		
		return getRSessionForPath(inputPath, outputFileName);
	}

	
	/**
	 * It creates a clean RSession in a generic temporal file.  
	 * @param path The path of the temporal file to write the scripts of the session.
	 * @return It returns the session.
	 * @throws RException If there is any I/O problem trying to create the file the method will throw an RException and the session will not be created.
	 */
	public R4JSession getRSession(String outputFileName) throws RException{
		
		String tempfileNameForRScripts = "Thread" + Thread.currentThread().getId() + "rTemp.txt";
	    String inputFilePath = FileSystemUtils.completePathToUserFolder(tempfileNameForRScripts);
		

	    
		return getRSessionForPath(inputFilePath, outputFileName);
	}


}
