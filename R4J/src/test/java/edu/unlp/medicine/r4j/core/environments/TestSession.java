package edu.unlp.medicine.r4j.core.environments;

import org.rosuda.REngine.REXP;

import edu.unlp.medicine.r4j.environments.R4JSession;
import edu.unlp.medicine.r4j.exceptions.R4JConnectionException;
import edu.unlp.medicine.r4j.values.R4JObjectValue;
import edu.unlp.medicine.r4j.values.R4JValue;
import junit.framework.Assert;
import junit.framework.TestCase;

public class TestSession extends TestCase {
	private R4JSession session = new R4JSession("test");

	protected void setUp() throws Exception {
		super.setUp();
		session.open();
		
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		session.close();
	}
	
	public void testEval() {
		try {
			session.loadLibrary("inSilicoDb");
			session.loadLibrary("Biobase");
			session.assign("GSE12237", "GSE12237");
			session.assign("FRMA", "FRMA");
			session.loadPlatforms("GSE12237");
			R4JValue value = (session.evaluate("getDataset(GSE12237, platforms[[1]],norm=FRMA, genes=TRUE)"));	
			//session.assign("eset", value);
			R4JValue exprs = session.evaluate("exprs(getDataset(GSE12237, platforms[[1]],norm=FRMA, genes=TRUE))");
			//session.assign("expressionMatrix", "exprs(getDataset(GSE12237, platforms[[1]],norm=FRMA, genes=TRUE))");
			session.assign("expressionMatrix", exprs);
			R4JValue cantGenes = session.evaluate("dim(expressionMatrix)[1]");
			R4JValue cantSamples = session.evaluate("dim(expressionMatrix)[2]");
			R4JValue sampleNames = session.evaluate("names(expressionMatrix[1,])");
			R4JValue clinicalLabesls = session.evaluate("varLabels(getDataset(GSE12237, platforms[[1]],norm=FRMA, genes=TRUE)@phenoData)");
			
			R4JValue gen = session.evaluate("as.character(unlist(((getDataset(GSE12237, platforms[[1]],norm=FRMA, genes=TRUE))@featureData@data)$ENTREZID))");

			R4JValue valoresDeExpressionForSample1 = session.evaluate("as.numeric(expressionMatrix[,1])");
			R4JValue valoresClinicosParaSample1 =  session.evaluate("as.character(unlist(getDataset(GSE12237, platforms[[1]],norm=FRMA, genes=TRUE)@phenoData@data[1,]))");
			Assert.assertTrue(value instanceof R4JObjectValue);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
