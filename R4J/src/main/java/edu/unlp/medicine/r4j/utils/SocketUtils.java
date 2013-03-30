package edu.unlp.medicine.r4j.utils;

import java.io.IOException;
import java.net.ServerSocket;

public class SocketUtils {

	public static int findFreePort() throws IOException {
		ServerSocket server = new ServerSocket(0);
		int port = server.getLocalPort();
		server.close();
		return port;

	}

}
