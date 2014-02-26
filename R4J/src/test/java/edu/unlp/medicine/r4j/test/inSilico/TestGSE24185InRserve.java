package edu.unlp.medicine.r4j.test.inSilico;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.rosuda.REngine.REXPS4;

import edu.unlp.medicine.r4j.server.R4JConnection;
import edu.unlp.medicine.r4j.server.R4JServer;

/**
 * This class is used to test the Rserve connection to the inSilicoDb
 * @author Diego Garcia
 *
 */
public class TestGSE24185InRserve extends TestCase {
	/**
	 * Represents the connection to Rserve
	 */
	private R4JServer server;

	
	@Override
	protected void setUp(){
		try { 
		server=new R4JServer();
		R4JConnection connection = server.getDefaultConnection();
		connection.eval("library(inSilicoDb)");
		connection.eval("library(Biobase)");
		connection.assign("GSE12237", "\"GSE12237\"");
		connection.assign("FRMA", "\"FRMA\"");
		connection.assign("platforms", "getPlatforms(GSE12237)");
		REXPS4 list = ((REXPS4)connection.eval("getDataset(GSE12237, platforms[[1]],norm=FRMA, genes=TRUE)").getNativeValue());	
		
		// Que retorna (getDataset(GSE24185, platforms[[1]],norm=FRMA, genes=TRUE)?????????????????????????????????????? expressionSet
		connection.assign("expressionMatrix", "exprs(getDataset(GSE12237, platforms[[1]],norm=FRMA, genes=TRUE))");		
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	@Override
	protected void tearDown(){ 
		try {
			super.tearDown();
			server.shutDown();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void testEval() {
		try {
			R4JConnection conn = server.getDefaultConnection();
			Assert.assertEquals(12701, conn.eval("dim(expressionMatrix)[1]").asInteger());
			Assert.assertEquals(13, conn.eval("dim(expressionMatrix)[2]").asInteger());
			String[] sampleNames = conn.eval("names(expressionMatrix[1,])").asStrings();
			Assert.assertEquals(13, sampleNames.length);
			
			//valoresDelSample1
			Assert.assertTrue(conn.eval("as.numeric(expressionMatrix[,1])").getNativeValue().isVector());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	
	}	
	
	
		
		
	
	}



