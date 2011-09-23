package edu.unlp.medicine.r4j.core;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unlp.medicine.r4j.constants.OSDependentConstants;

/**
 * Main class of the bridge. It is the entry point to get an RSession.
 * 
 * @author Matias Butti
 * 
 */
public class R4J {

	private static Logger LOGGER = LoggerFactory.getLogger(R4J.class);


	
	/**
	 * It creates a clean RSession.
	 * 
	 * @param path
	 *            The path of the temporal file to write the scripts of the
	 *            session.
	 * @return It returns the session.
	 * @throws RException
	 *             If there is any I/O problem trying to create the file the
	 *             method will throw an RException and the session will not be
	 *             created.
	 */
	public R4JSession getRSession(String rFileNameForWritingTheScripts, String outputFileName, String filePathToExecuteWhenStart) throws RException {
		try {
			String rFileNameForWritingTheScriptsWithThreadId = rFileNameForWritingTheScripts + "-Thread" + Thread.currentThread().getId();
			R4JSession r4JSession = new R4JSession(rFileNameForWritingTheScriptsWithThreadId, outputFileName);
			
			BufferedReader br = new BufferedReader(new FileReader(filePathToExecuteWhenStart));
			String line = br.readLine();
			while (line!=null){
				if (!line.equals(OSDependentConstants.LINE_SEPARATOR)){
					r4JSession.addStatement(line);
				}
				line = br.readLine();
			}
			return r4JSession;

			
		} catch (RException e) {
			LOGGER.error(e.getMessage());
			throw e;
		} catch (IOException e) {
			throw new RException("IOException trying to read the received file");
			
		}
	}
	
	
	

	/**
	 * It creates a clean RSession.
	 * 
	 * @param path
	 *            The path of the temporal file to write the scripts of the
	 *            session.
	 * @return It returns the session.
	 * @throws RException
	 *             If there is any I/O problem trying to create the file the
	 *             method will throw an RException and the session will not be
	 *             created.
	 */
	public R4JSession getRSession(String rFileNameForWritingTheScripts, String outputFileName) throws RException {
		try {
			String rFileNameForWritingTheScriptsWithThreadId = rFileNameForWritingTheScripts + "-Thread" + Thread.currentThread().getId() + ".txt";
			return new R4JSession(rFileNameForWritingTheScriptsWithThreadId, outputFileName);
		} catch (RException e) {
			LOGGER.error(e.getMessage());
			throw e;
		}
	}

	
}
