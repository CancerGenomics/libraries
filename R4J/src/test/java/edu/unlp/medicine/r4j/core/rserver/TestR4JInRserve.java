package edu.unlp.medicine.r4j.core.rserver;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPLogical;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

/**
 * This class represents the Rserve test cases. Requires that the server
 * (Rserve) is running to perform the tests.
 * 
 * @author Diego García
 * 
 */
public class TestR4JInRserve extends TestCase {
	
	private RConnection connection;
	
	@Override
	protected void tearDown(){ 
		try {
			super.tearDown();
			connection.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	protected void setUp() {
		try {
			super.setUp();
			connection = new RConnection();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void testRVersion() {
		try {
			Assert.assertNotNull(connection.eval("R.version.string"));
			
		} catch (Exception e) {

			e.printStackTrace();

		}
	}

	public void testConnection() {
		try {
			Assert.assertTrue(connection.isConnected());
			
		} catch (Exception e) {

			e.printStackTrace();

		}
	}

	public void testEvaluationOfBooleanExpression() {

		try {
			
			REXP resultOfTrueExpression = connection.eval("5 > 3");
			Assert.assertTrue(REXPLogical.TRUE == resultOfTrueExpression
					.asInteger());
			Assert.assertTrue(resultOfTrueExpression.isLogical());

			REXP resultOfFalseExpression = connection.eval("5 < 3");
			Assert.assertTrue(REXPLogical.FALSE == resultOfFalseExpression
					.asInteger());
			Assert.assertTrue(resultOfFalseExpression.isLogical());
			
			

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testEvaluationOfIntegerExpression() {

		try {
			
			REXP result = connection.eval("5 + 3"); 	
			Assert.assertTrue(result.isNumeric());
			Assert.assertTrue(result.asInteger() == 8);

			result = connection.eval("5 - 3");
			Assert.assertTrue(result.isNumeric());
			Assert.assertTrue(result.asInteger() == 2);

			result = connection.eval("5 * 3");
			Assert.assertTrue(result.isNumeric());
			Assert.assertTrue(result.asInteger() == 15);
			
			

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
