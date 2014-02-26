package edu.unlp.medicine.r4j.server;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unlp.medicine.r4j.utils.SocketUtils;

public class RServeConfigurator {
	public static final int DEFAULT_PORT = 6311;
	// Logger Object
	private static final Logger LOGGER = LoggerFactory.getLogger(RServeConfigurator.class);
	private static RServeConfigurator instance = null;
	//private int port = 6311;
	private String host = "127.0.0.1";


	
	private RServeConfigurator() {
	}

	public static final RServeConfigurator getInstance() {
		if (instance == null) {
			instance = new RServeConfigurator();
		}
		return instance;
	}

	public int getFreePort() throws IOException {
		int port=6311;
		try {
			port = SocketUtils.findFreePort();
		} catch (IOException e) {
			LOGGER.error("Find Free Port", e);
			throw e;
		}
		return port;

	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

}
