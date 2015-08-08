package edu.unlp.medicine.r4j.server;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unlp.medicine.r4j.constants.OSDependentConstants;
import edu.unlp.medicine.r4j.exceptions.R4JCreatConnectionException;
import edu.unlp.medicine.r4j.exceptions.R4JScriptExecutionException;
import edu.unlp.medicine.r4j.exceptions.R4JServerShutDownException;
import edu.unlp.medicine.r4j.exceptions.R4JServerStartException;
import edu.unlp.medicine.r4j.exceptions.RequiredLibraryNotPresentException;
import edu.unlp.medicine.r4j.rLibrary.RLibrary;
import edu.unlp.medicine.r4j.rLibrary.RLibraryManager;
import edu.unlp.medicine.r4j.rLibrary.RLibraryScanner;
import edu.unlp.medicine.r4j.systemProperties.R4JSystemPropertiesExpected;

/**
 * It represents an RServer server. You can have as many servers as you need. It
 * will startup in different ports (you should instantiate this class and call
 * {@link #start()}. Once you have the server, you ask for the connection (using
 * the method {@link #getDefaultConnection()} and then work with this object to
 * execute r scripts.
 * 
 * It is as simple as this: R4JServer server = new R4JServer(); R4JValue result
 * = server.getDefaultConnection().evaluate("3+5");
 * Assert.assertTrue(result.asInteger() == 8); server.shutDown();
 * 
 * * The class is prepared for getting more than one connection to the same
 * server but it is not supported in RServe (at least in Windows doesnt work).
 * When RServe support it, you can use the
 * {@link #createConnectionToRServe()} method for create a new connection
 * to the same server.
 * 
 * You can indicate a file with the required r packages. They will
 * 
 * 
 * @author Matias
 * 
 */
public class R4JServer {

	private static final Logger LOGGER = LoggerFactory.getLogger(R4JServer.class);

	private List<RLibrary> requiredRLibrariesNotInstalled = new ArrayList<RLibrary>();
	private  Process rServeOSProcess;

	private static final int MAX_ATTEMPTS_FOR_STARTING = 10;

	private static R4JServer instance = null;

	private int port;

	List<R4JConnection> connections = new ArrayList<R4JConnection>();

	/**
	 * It creates the server.
	 * 
	 * @throws R4JServerStartException
	 */
	//TODO hacer private y crear una jerarquia con un server siempre remoto y otro local. que se decida con un método create, no con un new Server
    public R4JServer() throws R4JServerStartException{
		//FIXME ahora no arranca más es remoto, ver R4JConnection
		start();
	}

	// //////////////////////////////////////////////////
	// //////////////////////API/////////////////////////
	// //////////////////////////////////////////////////

//	/**
//	 * It starts the R4J Server. For doing it, it does the following steps 1-It
//	 * will try in the default port (6311) but if it is occupied, Iit will look
//	 * for a free one. 2-It waits until it is up. 3-It checks that all the
//	 * required R libs (specified in @link
//	 * {@link R4JSystemPropertiesExpected#R_REQUIRED_LIBRARIES_FILE_PATH_PROPERTY}) are
//	 * installed.
//	 *
//	 * @throws R4JServerStartException
//	 * @throws RequiredLibraryNotPresentException
//	 *             There is some required library (specified in @link
//	 *             {@link R4JSystemPropertiesExpected#R_REQUIRED_LIBRARIES_FILE_PATH_PROPERTY}
//	 *             ) not installed.
//	 */
	private void start() throws R4JServerStartException {
//		String rcmd = "";
		try {
//
//
//			rcmd = startRServeProcess();
//			LOGGER.info("RServe satarted up on port " + port + ". Startup script: " + rcmd);
//
            initializeR();
//
		} catch (RequiredLibraryNotPresentException e) {
			// throw e;
			//FIXME este error no pasa más
			LOGGER.error("There were some required libraries not installed in R");
		} catch (Exception e) {
			final String msg = "Failed to start the Rserve (bridge between Java and R) on port " + port + ".";
			final String possibleCauses = " 4 possible reasons: 1) you don't have permission to open the free port " + port + " for starting RServe on this machine. Ask the system admin. \n2) the operating system asked you to start RServe.exe (for connecting Bioplat and R) and you said no. In this case, restart Bioplat and accept to establish the connection with R. \n3) you don't have R installed on this folder: " + OSDependentConstants.PATH_TO_R + " \n4) the R doesnt have the RServe package installed (it should not happen if you are using the R coming on Bioplat distribution). In this last case check if you have " + OSDependentConstants.PATH_TO_R + "\\library\\Rserve" + " folder. If not, install RServe using the following R script: install.packages (Rserve)";
			//LOGGER.error("Startup command failed: " + rcmd + ". " + msg + possibleCauses);
			throw new R4JServerStartException(msg, e, possibleCauses, "nop");
		}
}

    private void initializeR() throws R4JCreatConnectionException, RequiredLibraryNotPresentException {
        createDefaultConnection();
//FIXME determinar si es necesario ahora o se puede levantar el server ya configurado
        setTheRLibraryInsideR4JDistributionAsTheOnlyRLibFolder();
//FIXME ya no es necesario esto con el server remoto, ya que ya está configurado!
        //calculateRequiredRLibrariesNotInstalledAndLoadInstalled();
    }

    /**
	 * It creates the default connections.
	 * 
	 * @throws R4JCreatConnectionException
	 */
	private void createDefaultConnection() throws R4JCreatConnectionException {
		this.createConnectionToRServe();
	}

	public R4JConnection getDefaultConnection() {
		if (this.connections.size() > 0)
			return this.connections.get(0);
		else
			return null;
	}

	/**
	 * It shutdowns the server (it does through the default connection. Is the
	 * only way). Then it closes every connection to this server.
	 * 
	 * @throws R4JServerShutDownException
	 */
	public void shutDown() throws R4JServerShutDownException{
	//ahora no se baja el server, es compartido y remoto!!
	LOGGER.warn("Intento de bajar el server! ahora ya no se baja más");
//        if (this.getDefaultConnection() != null) {
//			// It uses the default connection to shutdown the server. The only
//			// way i found to shutdown the process cleanly.
//			try {
//
//				this.getDefaultConnection().getConnection().shutdown();
//				rServeOSProcess.destroy();
//
//			} catch (RserveException e) {
//				throw new R4JServerShutDownException("Error shtting down the server. The process will be alive. Kill him from OS.", e);
//			}
//
//			for (R4JConnection connection : this.connections) {
//				connection.close();
//			}
//
//			connections = new ArrayList<R4JConnection>();
//
//			LOGGER.info("Rserve on port " + port + " was shut down");
//		}
	}

	// //////////////////////////////////////////////////
	// //////////////////////FUTURE API///////////////////
	// //////////////////////////////////////////////////

	/**
	 * It creates and returns a connection with the RServer server. It is
	 * private because it is not working in Windows RServe the creation of more
	 * than one connection to the same server. When this works, this method will
	 * be public to allow you create new connections to the same server.
	 *
	 * @return the R4JSession. Every script will be executed using this session.
	 */
	private IR4JConnection createConnectionToRServe() throws R4JCreatConnectionException {
		try {
			R4JConnection connection = new R4JConnection(this);
			LOGGER.info("A connection to Rserve at " + rserveLocation() + " was created");
			this.connections.add(connection);
			return connection;
		} catch (R4JCreatConnectionException e) {
			LOGGER.error("Error trying to get a conection to the R server" + e);
			throw e;
		}
	}

	private String rserveLocation() {
		return getRserveHost()+":"+getPort();
	}

	// //////////////////////////////////////////////////
	// //////////////////////PRIVATE/////////////////////
	// //////////////////////////////////////////////////

//	/**
//	 * It starts the R process in the OS.
//	 *
//	 * @return
//	 * @throws IOException
//	 * @throws InterruptedException
//	 */
//    @Deprecated //no se usa
//	private String startRServeProcess() throws IOException, InterruptedException {
//		try {
//			// FIXME parametrizar que el puerto de Rserve
//			// ATENCIóN: en linux es fijo, ya que Rserve
//			// se levanta por fuera de la aplicación
//
//			port = RServeConfigurator.getInstance().getFreePort();
//			if (SystemUtils.IS_OS_LINUX)
//				port = 6311;
//			String rcmd = OSDependentConstants.PATH_TO_R + " --save --restore -q -e library('Rserve');Rserve(port=" + port + ")";
//			rServeOSProcess = Runtime.getRuntime().exec(rcmd);
//			LOGGER.debug("Starting Rserve with command: " + rcmd);
//			rServeOSProcess.waitFor();
//			LOGGER.debug("Command to start rserve terminated");
//			return rcmd;
//		} catch (Exception e) {
//			LOGGER.error("Error starting the rserve process" + e);
//			return "nop";
//		}
//
//	}

	/**
	 * Checks if the server is running.
	 * 
	 * @return true if server is running, false otherwise
	 */
	public boolean isTheRserveRunning() {
		try {
			R4JConnection connection = this.getDefaultConnection();
			connection.eval("R.version.string");
			return true;

		} catch (R4JScriptExecutionException e) {
			return false;
		}
	}

	/**
	 * 
	 * @return the port in which the server is running.
	 */
	public int getPort() {
		return RServeConfigurator.getInstance().getPort();
	}

	private String getRserveHost(){ return RServeConfigurator.getInstance().getHost();}

	private void setTheRLibraryInsideR4JDistributionAsTheOnlyRLibFolder() throws R4JCreatConnectionException {
		try {
			// Limpio estas dos variables de entorno para garantizar que los
			// paquetes se van a buscar solamente en R_HOME\library
			R4JConnection connection = this.getDefaultConnection();
			connection.eval("Sys.unsetenv(\"R_LIBS_SITE\")");
			connection.eval("Sys.unsetenv(\"R_LIBS_USER\")");
		} catch (R4JScriptExecutionException e1) {
			LOGGER.error("Trying to unset enviroment variables", e1);
		} catch (Exception e) {
			LOGGER.error("Trying to unset enviroment variables", e);
		}

	}

	/**
	 * It checks if every required library (specified in the
	 * R_REQUIRED_LIBRARIES_FILE_PATH) is installed. THe fill will be look in
	 * relative path, if it is not present it will look it in classpath.
	 * 
	 * @throws R4JCreatConnectionException
	 * 
	 * 
	 */
	private void calculateRequiredRLibrariesNotInstalledAndLoadInstalled() throws R4JCreatConnectionException, RequiredLibraryNotPresentException {

		Iterator<RLibrary> requiredLibraries;

		if (getPath() == null) {
			LOGGER.warn("The file with the R required rlibs: " + R4JSystemPropertiesExpected.R_REQUIRED_LIBRARIES_FILE_PATH_PROPERTY + " was not found");
		} else {

			requiredLibraries = getReadRequiredLibraries();
			if (requiredLibraries == null) {
				LOGGER.warn("The file with the R required rlibs: " + R4JSystemPropertiesExpected.R_REQUIRED_LIBRARIES_FILE_PATH_PROPERTY + " was not found");
			} else {
				doCalculateRequiredRLibrariesNotInstalled(requiredLibraries);
			}

		}
	}

	private void doCalculateRequiredRLibrariesNotInstalled(Iterator<RLibrary> requiredLibraries) throws R4JCreatConnectionException, RequiredLibraryNotPresentException {
		RLibraryManager rlibraryManager = new RLibraryManager(this);
		this.setRequiredRLibrariesNotInstalled(rlibraryManager.getLibrariesNotInstalled(requiredLibraries));
		List<RLibrary> requiredRLibrariesNotInstalled = this.getRequiredRLibrariesNotInstalled();
		for (RLibrary requiredLibrary : requiredRLibrariesNotInstalled) {
			LOGGER.warn("The library " + requiredLibrary.getName() + " seems not to be present in R. Try to install it by running: " + requiredLibrary.getInstallation());
		}
		if (requiredRLibrariesNotInstalled.size() > 0)
			throw new RequiredLibraryNotPresentException(requiredRLibrariesNotInstalled);

	}

	public List<RLibrary> getRequiredRLibrariesNotInstalled() {
		return requiredRLibrariesNotInstalled;
	}

	public void setRequiredRLibrariesNotInstalled(List<RLibrary> requiredRLibrariesNotInstalled) {
		this.requiredRLibrariesNotInstalled = requiredRLibrariesNotInstalled;
	}

	private Iterator<RLibrary> getReadRequiredLibraries() {
		RLibraryScanner scanner = new RLibraryScanner();
		BufferedReader reader = lookInRelativePath();
		if (reader != null) {
			return scanner.processLineByLine(reader).iterator();
		} else {
			InputStream inputStream = lookInClasspath();

			if (inputStream != null) {
				return scanner.processLineByLine(inputStream).iterator();
			} else
				return null;

		}

	}

	private InputStream lookInClasspath() {
		return this.getClass().getResourceAsStream(getPath());

	}

	private String getPath() {
		final String result = System.getProperty(R4JSystemPropertiesExpected.R_REQUIRED_LIBRARIES_FILE_PATH_PROPERTY);
		LOGGER.debug("The parameter " + R4JSystemPropertiesExpected.R_REQUIRED_LIBRARIES_FILE_PATH_PROPERTY + " resulted to " + result);
		return result;
	}

	private BufferedReader lookInRelativePath() {
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(getPath()));

		} catch (FileNotFoundException e) {
			return null;
		}

		return reader;
	}

}
