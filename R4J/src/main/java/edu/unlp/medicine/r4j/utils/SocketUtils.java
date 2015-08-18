package edu.unlp.medicine.r4j.utils;

import java.io.IOException;
import java.net.ServerSocket;

import edu.unlp.medicine.r4j.server.RServeConfigurator;

public class SocketUtils {

	public static int findFreePort() throws IOException {
		try{
			ServerSocket server = new ServerSocket(RServeConfigurator.getInstance().getPort());
			server.close();
			return RServeConfigurator.getInstance().getPort();
		}catch(IOException e){
			ServerSocket server = new ServerSocket(0);
			int port = server.getLocalPort();
			server.close();
			return port;
		}
		
		
		
		

	}

}
