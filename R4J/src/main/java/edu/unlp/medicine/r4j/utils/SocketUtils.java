package edu.unlp.medicine.r4j.utils;

import java.io.IOException;
import java.net.ServerSocket;

import edu.unlp.medicine.r4j.server.RServeConfigurator;

public class SocketUtils {

	public static int findFreePort() {
		int port = 0;
		ServerSocket server = null;
		while (server == null) {
			try {
				server = new ServerSocket(0);
				server.close();
				port = server.getLocalPort();
			} catch (IOException e1) {
				continue;
			}
		}
		return port;
	}

	public static boolean isFreePort(int port) {
		boolean isFree = true;
		try {
			ServerSocket server = new ServerSocket(port);
			server.close();
		} catch (IOException e1) {
			isFree = false;
		}
		return isFree;
	}

}
