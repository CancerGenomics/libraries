package edu.unlp.medicine.r4j.test.transformers;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.Rserve.RConnection;

import edu.unlp.medicine.r4j.transformers.R4JTransformerUtils;
import edu.unlp.medicine.r4j.values.R4JDataMatrix;
import edu.unlp.medicine.r4j.values.R4JObjectValue;
import edu.unlp.medicine.r4j.values.R4JValue;

/**
 * This class is used to test the Rserve connection to the inSilicoDb
 * @author Diego Garcia
 *
 */
public class DataMatrixTest extends TestCase {
	/**
	 * Represents the connection to Rserve
	 */
	private RConnection connection;
	private REXP s4;
	
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
		s4 = (connection.eval("getDataset(GSE12237, platforms[[1]],norm=FRMA, genes=TRUE)"));	
		
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
	
	public void testGetValue() {
		try {
			
			R4JValue value = R4JTransformerUtils.transform(s4);
			assertTrue(value instanceof R4JObjectValue);
			R4JObjectValue object = (R4JObjectValue) value;
			R4JValue matrix = ((R4JObjectValue)(object.getValue("phenoData"))).getValue("data");
			Assert.assertTrue(matrix instanceof R4JDataMatrix);
			Assert.assertNotNull(((R4JDataMatrix)matrix).getMatrix());
			//TODO probar que se esten obteniendo los valores correctamente.
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	
	}	
	
	
		
		
	
	}



