package edu.unlp.medicine.r4j.core;

import org.rosuda.REngine.Rserve.RConnection;

import junit.framework.TestCase;

public class TestGSE24185InRserve extends TestCase {
	
	private RConnection connection;
	
	@Override
	protected void setUp(){
		try { 
		connection = new RConnection();
		connection.eval("library(inSilicoDb)");
		connection.eval("library(Biobase)");
		connection.assign("platforms", "getPlatforms('GSE24185')");
		connection.assign("eset", "getDataset('GSE24185', platforms[[1]],norm='FRMA', genes=TRUE)");
		connection.assign("expressionMatrix", "exprs(eset)");
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	

}
