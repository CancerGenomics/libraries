package edu.unlp.medicine.r4j.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main class of the bridge. You can execute files in an independent way
 * (executeFile) or you can get a session for keeping the state of the variables
 * (getSession).
 * 
 * @author Matias Butti
 * 
 */
public class R4J {

	private static Logger LOGGER = LoggerFactory.getLogger(R4J.class);

/**
 * Execute a file with R scripts. 
 * @param userFolderName
 * @param filePathToExecuteWhenStart
 * @throws RException
 */
	public void executeFile(String userFolderName, String filePathToExecuteWhenStart) throws RException {
		R4JSession r4JSession = new R4JSession(userFolderName);
		r4JSession.addStatementsOfTheFile(filePathToExecuteWhenStart);
		r4JSession.flush();
		r4JSession.close();
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
	public R4JSession getRSession(String userFolderName) throws RException {
		try {
			return new R4JSession(userFolderName);
		} catch (RException e) {
			LOGGER.error(e.getMessage());
			throw e;
		}
	}

}
