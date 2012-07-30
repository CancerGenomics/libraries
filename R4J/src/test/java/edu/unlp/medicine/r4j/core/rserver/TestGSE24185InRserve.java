package edu.unlp.medicine.r4j.core.rserver;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPList;
import org.rosuda.REngine.REXPS4;
import org.rosuda.REngine.RList;
import org.rosuda.REngine.Rserve.RConnection;

/**
 * This class is used to test the Rserve connection to the inSilicoDb
 * @author Diego Garcia
 *
 */
public class TestGSE24185InRserve extends TestCase {
	/**
	 * Represents the connection to Rserve
	 */
	private RConnection connection;
	
	static { 
		
	}
	
	@Override
	protected void setUp(){
		try { 
		connection = new RConnection();
		connection.eval("library(inSilicoDb)");
		connection.eval("library(Biobase)");
		connection.assign("GSE12237", "GSE12237");
		connection.assign("FRMA", "FRMA");
		REXP platforms = connection.eval("getPlatforms(GSE12237)");
		connection.assign("platforms", platforms);
		REXPS4 list = ((REXPS4)connection.eval("getDataset(GSE12237, platforms[[1]],norm=FRMA, genes=TRUE)"));	
		
		// Que retorna (getDataset(GSE24185, platforms[[1]],norm=FRMA, genes=TRUE)?????????????????????????????????????? expressionSet
		REXP exprs = connection.eval("exprs(getDataset(GSE12237, platforms[[1]],norm=FRMA, genes=TRUE))");		
		connection.assign("expressionMatrix", exprs);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	@Override
	protected void tearDown(){ 
		try {
			super.tearDown();
			connection.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void testEval() {
		try {
			Assert.assertEquals(12718, connection.eval("dim(expressionMatrix)[1]").asInteger());
			Assert.assertEquals(103, connection.eval("dim(expressionMatrix)[2]").asInteger());
			String[] sampleNames = connection.eval("names(expressionMatrix[1,])").asStrings();
			Assert.assertEquals(103, sampleNames.length);
			
			//valoresDelSample1
			Assert.assertTrue(connection.eval("as.numeric(expressionMatrix[,1])").isVector());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	
	}	
	
	
		
		
	
	}



