package edu.unlp.medicine.r4j.server;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unlp.medicine.r4j.utils.SocketUtils;

public class RServeConfigurator {
	private static final Logger LOGGER = LoggerFactory.getLogger(RServeConfigurator.class);
	public static final String DEFAULT_PORT = "6311";
	private static final String DEFAULT_HOST = "127.0.0.1";
	private String port = parameter("bioplat.rserve.port", DEFAULT_PORT);
	//inicializa el host segun el parámetro del sistema y si no está definido usa localhost
	private String host = parameter("bioplat.rserve.host", DEFAULT_HOST);
	// /(System.getProperty("bioplat.rserve.host",null)!=null)?System.getProperty("host"):"127.0.0.1";

	private static RServeConfigurator INSTANCE = new RServeConfigurator();

	private String parameter(String varname, String def) {
		String varvalue = System.getProperty(varname, null);
		if (varvalue != null)
			return varvalue;
		else
			return def;
	}

	public static  RServeConfigurator getInstance() {
		return INSTANCE;
	}

	public String getHost() {
		return host;
	}

	public int getPort(){ return Integer.valueOf(port); 	}

	/**
	 *
	 * @return el free port...
	 * @throws IOException
	 * @deprecated no va más
	 */
	private int getFreePort() throws IOException {
		int port;
		try {
			port = SocketUtils.findFreePort();
		} catch (IOException e) {
			LOGGER.error("Find Free Port", e);
			throw e;
		}
		return port;

	}

}
