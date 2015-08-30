package edu.unlp.medicine.r4j.server;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unlp.medicine.r4j.utils.SocketUtils;

public class RServeConfigurator {
	private static final Logger LOGGER = LoggerFactory.getLogger(RServeConfigurator.class);
	public static final int DEFAULT_PORT = 6311;
	public static final String DEFAULT_HOST = "127.0.0.1";
	private int port;
	private String host; 
	

	private static RServeConfigurator INSTANCE = new RServeConfigurator(); 

	private RServeConfigurator() {}
	
	public static  RServeConfigurator getInstance() {
		return INSTANCE;
	}

	public String getHost() {
		return host;
	}

	public int getPort(){ return port;}

	void setHost(String host) {
		this.host = host;
	}

	void setPort(int port){ this.port = port;}
}
