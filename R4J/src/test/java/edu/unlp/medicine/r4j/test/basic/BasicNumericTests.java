package edu.unlp.medicine.r4j.test.basic;

import junit.framework.Assert;

import org.rosuda.REngine.REXPLogical;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unlp.medicine.r4j.exceptions.R4JServerStartException;
import edu.unlp.medicine.r4j.exceptions.RequiredLibraryNotPresentException;
import edu.unlp.medicine.r4j.server.R4JConnection;
import edu.unlp.medicine.r4j.server.R4JServer;
import edu.unlp.medicine.r4j.test.R4JTestCase;
import edu.unlp.medicine.r4j.values.R4JValue;

/**
 * This class represents the Rserve test cases. Requires that the server
 * (Rserve) is running to perform the tests.
 * 
 * @author Matias Butti
 * @author Diego Garc�a
 * 
 * 
 */
public class BasicNumericTests extends R4JTestCase {
	
	
	private R4JServer server;
	private static Logger logger = LoggerFactory.getLogger(BasicNumericTests.class);



	
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
	


	public void testRVersion() {
		try {
			R4JConnection connection = server.getDefaultConnection();
			Assert.assertNotNull(connection.eval("R.version.string"));
		} catch (Exception e) {

			e.printStackTrace();

		}
	}


	public void testEvaluationOfBooleanExpression() {

		try {
			
			R4JConnection connection = server.getDefaultConnection();
			
			R4JValue resultOfTrueExpression = connection.eval("5 > 3");
			Assert.assertTrue(REXPLogical.TRUE == resultOfTrueExpression.asInteger());
			Assert.assertTrue(resultOfTrueExpression.isLogical());

			R4JValue resultOfFalseExpression = connection.eval("5 < 3");
			Assert.assertTrue(REXPLogical.FALSE == resultOfFalseExpression.asInteger());
			Assert.assertTrue(resultOfFalseExpression.isLogical());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testEvaluationOfIntegerExpression() {

		try {
			
			R4JConnection session = server.getDefaultConnection();
			
			R4JValue result = session.eval("5 + 3"); 	
			Assert.assertTrue(result.isNumeric());
			Assert.assertTrue(result.asInteger() == 8);

			result = session.eval("5 - 3");
			Assert.assertTrue(result.isNumeric());
			Assert.assertTrue(result.asInteger() == 2);

			result = session.eval("5 * 3");
			Assert.assertTrue(result.isNumeric());
			Assert.assertTrue(result.asInteger() == 15);
			

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
