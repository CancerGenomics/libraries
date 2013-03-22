package edu.unlp.medicine.r4j.environments;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unlp.medicine.r4j.utils.SocketUtils;

public class RServeConfigurator {
	// Logger Object
	private static Logger logger = LoggerFactory.getLogger(RServeConfigurator.class);
	private static RServeConfigurator instance = null;
	private int port = 6311;
	private String host = "127.0.0.1";

	private RServeConfigurator() {
		try {
			this.port = SocketUtils.findFreePort();
		} catch (IOException e) {
			logger.error("Find Free Port", e);
		}
	}

	public static final RServeConfigurator getInstance() {
		if (instance == null) {
			instance = new RServeConfigurator();
		}
		return instance;
	}

	public int getPort() {
		return this.port;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

}