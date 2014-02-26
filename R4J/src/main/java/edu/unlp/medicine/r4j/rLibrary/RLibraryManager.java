package edu.unlp.medicine.r4j.rLibrary;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unlp.medicine.r4j.exceptions.R4JCreatConnectionException;
import edu.unlp.medicine.r4j.exceptions.R4JScriptExecutionException;
import edu.unlp.medicine.r4j.server.R4JConnection;
import edu.unlp.medicine.r4j.server.R4JServer;
import edu.unlp.medicine.r4j.values.R4JValue;

public class RLibraryManager {

	/**
	 * Logger Object
	 */
	private static Logger logger = LoggerFactory.getLogger(RLibraryManager.class);
	R4JServer r4jServer;
	
	public RLibraryManager(R4JServer r4jServer) {
		this.r4jServer = r4jServer;
	}

	/**
	 * This method validates if the libraries are installed. returns a
	 * collection of libraries not installed
	 * @throws R4JCreatConnectionException 
	 */
	public List<RLibrary> getLibrariesNotInstalled(final Iterator<RLibrary> libraries) throws R4JCreatConnectionException {
		List<RLibrary> librariesNotInstalled = new ArrayList<RLibrary>();
		R4JConnection connection=null;
		
			connection = r4jServer.getDefaultConnection();
			RLibrary library;
			while (libraries.hasNext()) {
				library = libraries.next();
				if (!isInstalled(library, connection)) {
					// libreria no instalada
					logger.warn("Library not installed: " + library.getName());
					librariesNotInstalled.add(library);
				}
			}
		
		return librariesNotInstalled;

	}

	/**
	 * This method returns true if the library is installed. False otherwise.
	 * This method, also load every asked package, so it is not necessary to reload it.
	 * 
	 * @param library
	 * @param session 
	 * @return
	 */
	private boolean isInstalled(RLibrary library, R4JConnection session) {
		R4JValue value;
		try {
			value = session.eval("suppressWarnings(require('" + library.getName() + "',quietly=TRUE))");
			return value.asInteger() > 0;
		} catch (R4JScriptExecutionException e) {
			
			return false;
		}
		
	}

	/**
	 * This method installs the libraries. Also returns the libraries that
	 * failed to install.
	 * 
	 * @param libraries
	 * @return
	 * @throws R4JCreatConnectionException 
	 */
	public List<RLibrary> installRLibraries(final List<RLibrary> libraries) throws R4JCreatConnectionException {
		List<RLibrary> librariesNotInstalled = new ArrayList<RLibrary>();
		R4JConnection connection = null;
		try {
			connection = r4jServer.getDefaultConnection();
			Iterator<RLibrary> iterator = libraries.iterator();
			RLibrary library;
			while (iterator.hasNext()) {
				library = iterator.next();
				connection.eval(library.getInstallation());
				if (!this.isInstalled(library, connection)) {
					librariesNotInstalled.add(library);
				}
			}

		} catch (R4JScriptExecutionException e) {
			e.printStackTrace();
		
			logger.error(e.getMessage());
		}
		return librariesNotInstalled;

	}

}
