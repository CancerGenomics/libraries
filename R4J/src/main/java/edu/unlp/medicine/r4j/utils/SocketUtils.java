package edu.unlp.medicine.r4j.utils;

import java.io.IOException;
import java.net.ServerSocket;

import edu.unlp.medicine.r4j.server.RServeConfigurator;

public class SocketUtils {

	public static int findFreePort() throws IOException {
		try{
			ServerSocket server = new ServerSocket(RServeConfigurator.DEFAULT_PORT);
			server.close();
			return RServeConfigurator.DEFAULT_PORT;
		}catch(IOException e){
			ServerSocket server = new ServerSocket(0);
			int port = server.getLocalPort();
			server.close();
			return port;
		}
		
		
		
		

	}

}
