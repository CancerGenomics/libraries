package edu.unlp.medicine.r4j.test.transformers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unlp.medicine.r4j.exceptions.R4JServerStartException;
import edu.unlp.medicine.r4j.server.R4JConnection;
import edu.unlp.medicine.r4j.server.R4JServer;
import edu.unlp.medicine.r4j.test.R4JTestCase;
import edu.unlp.medicine.r4j.values.R4JStringDataMatrix;

/**
 * This class is used to test the Rserve connection to the inSilicoDb
 * @author Diego Garcia
 *
 */
public class DataFrameAsStringMatrixTest extends R4JTestCase {
	
	private R4JServer server;
	private static Logger logger = LoggerFactory.getLogger(DataFrameAsStringMatrixTest.class);
														   
	@Override
	protected void setUp(){
		super.setUp();
		try {
			server=new R4JServer();
			
		} catch (R4JServerStartException e) {
			logger.error(e.getMessage() + ". " + e.getCommandToStartTheR4JServer() + ". "+ e.getPossibleCauses());
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
	
	
	
	public void testRDataFrameToStringMatrix() {
		try { 
		R4JConnection connection = server.getDefaultConnection();		
		//connection.voidEval("m = matrix(c(2, 4, 3, 1, 5, 7),  nrow=3, ncol=2)");
		R4JStringDataMatrix r4jmatrix = connection.eval("{m=data.frame(\"huhu\",c(11:20)); lapply(m,as.character)}").asStringDataMatrix();
		System.out.println(r4jmatrix.getColnames());
		System.out.println(r4jmatrix.getRownames());
		System.out.println(r4jmatrix.getCellValue(1, 1));

		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	
//	public void testRMatrixToStringMatrix() {
//		try { 
//		R4JConnection connection = server.getDefaultConnection();		
//		R4JStringDataMatrix r4jmatrix = connection.voidEval("matrix(c(2, 4, 3, 1, 5, 7),  nrow=3, ncol=2)").asStringDataMatrix();
//		System.out.println(r4jmatrix.getColnames());
//		System.out.println(r4jmatrix.getRownames());
//		System.out.println(r4jmatrix.getCellValue(1, 1));
//
//		} catch(Exception ex) {
//			ex.printStackTrace();
//		}
//	}

	
//	public void testGetValue() {
//		try {
//			
//			R4JValue value = R4JTransformerUtils.transform(s4);
//			assertTrue(value instanceof R4JObjectValue);
//			R4JObjectValue object = (R4JObjectValue) value;
//			R4JValue matrix = ((R4JObjectValue)(object.getValue("phenoData"))).getValue("data");
//			Assert.assertTrue(matrix instanceof R4JDataMatrix);
//			Assert.assertNotNull(((R4JDataMatrix)matrix).getMatrix());
//			//TODO probar que se esten obteniendo los valores correctamente.
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
	
//	}	
	

		
	
	}



