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



	/**
	 * 
	 * @param sessionName
	 *            The name of the session. It will be used as the folder name.
	 * @param path
	 *            The absolute path in which the folder with the session name
	 *            will be created. This is used in case you want to use your own
	 *            folder to keep the results instead of using the default r4j
	 *            folder.
	 * @return
	 * @throws RException
	 */

	public R4JSession getRSession(String sessionName, String path) throws RException {
		try {
			return new R4JSession(sessionName, path);
		} catch (RException e) {
			LOGGER.error(e.getMessage());
			throw e;
		}
	}

	
}
