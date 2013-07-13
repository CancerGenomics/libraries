package edu.unlp.medicine.r4j.test.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import junit.framework.Assert;
import edu.unlp.medicine.r4j.exceptions.R4JException;
import edu.unlp.medicine.r4j.exceptions.R4JScriptExecutionException;
import edu.unlp.medicine.r4j.exceptions.R4JServerShutDownException;
import edu.unlp.medicine.r4j.exceptions.R4JServerStartException;
import edu.unlp.medicine.r4j.exceptions.RequiredLibraryNotPresentException;
import edu.unlp.medicine.r4j.server.IR4JConnection;
import edu.unlp.medicine.r4j.server.R4JServer;
import edu.unlp.medicine.r4j.systemProperties.R4JSystemPropertiesExpected;
import edu.unlp.medicine.r4j.test.R4JTestCase;
import edu.unlp.medicine.r4j.values.R4JValue;

public class ServerTest extends R4JTestCase{
	
	/**
	 * Tests that it is possible to startup a server and execute a basic script.
	 */
	public void testStartup(){
		R4JServer server=null;
		try {
			server = new R4JServer();
			R4JValue result = server.getDefaultConnection().eval("3+5");
			Assert.assertTrue(result.asInteger() == 8);
			server.shutDown();
		} catch (R4JServerStartException e) {
			fail("The server could not startup." + e.toString());
		} catch (R4JScriptExecutionException e) {
			fail("It is not possible to evaluate a basic script: " + e.getScript());
		} catch (R4JServerShutDownException e) {
			fail("There was a problem closing the connection to rseve on port" + server.getPort());
		}
	}

	/**
	 * It tests that different servers starts on different ports.
	 */
	public void testThatTwoServesStartsOnDifferentPorts(){
		R4JServer server=null;
		R4JServer server2=null;
		try {
			server = new R4JServer();
			R4JValue result = server.getDefaultConnection().eval("3+5");
			Assert.assertTrue(result.asInteger() == 8);
			
			
			server2 = new R4JServer();
			R4JValue result2 = server.getDefaultConnection().eval("3+5");
			Assert.assertTrue(result2.asInteger() == 8);
			
			server.shutDown();
			server2.shutDown();
			
			Assert.assertTrue(server.getPort() != server2.getPort());
			
		} catch (R4JServerStartException e) {
			fail("The server could not startup." + e.toString());
		} catch (R4JScriptExecutionException e) {
			fail("It is not possible to evaluate a basic script: " + e.getScript());
		} 
		catch (R4JServerShutDownException e) {
			fail("There was a problem closing the connection to rseve on port" + server.getPort());
		}
 
	}
	

	public void testTHeLogIsWrittern(){
		try {
			System.setProperty(R4JSystemPropertiesExpected.SCRIPT_LOG_FILE_PATH_PROPERTY, "/rserve.log");
			
			R4JServer server = new R4JServer();
			R4JValue result = server.getDefaultConnection().eval("3+5");
			Assert.assertTrue(result.asInteger() == 8);
			server.shutDown();
			BufferedReader reader = new BufferedReader(new FileReader("/rserve.log"));
			
		} catch (R4JException e) {
			fail("The server could not startup." + e.toString());
		} catch (FileNotFoundException e) {
			
			fail("The file was not generated");
		}
	}
	
	public void testRequiredLibraryNotInsalled(){
		
			System.setProperty(R4JSystemPropertiesExpected.R_REQUIRED_LIBRARIES_FILE_PATH_PROPERTY, "c:\\Req.libs.4test");
			BufferedWriter bw;
			try {
				
				bw = new BufferedWriter(new FileWriter("c:\\Req.libs.4test"));
				bw.write("smida=install.packages(\"aaa\")");
				bw.flush();
				bw.close();
				R4JServer server = new R4JServer();
				assertTrue(server.getRequiredRLibrariesNotInstalled().size()==1);

			} catch (IOException e) {
				fail("Error writing required libs file");

			} catch (R4JServerStartException e) {
				fail("Error starting the R4Jserver");
			}
			
			
		
	}
	
	
	
}
