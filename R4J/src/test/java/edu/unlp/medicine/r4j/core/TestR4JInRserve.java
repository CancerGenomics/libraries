package edu.unlp.medicine.r4j.core;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPLogical;
import org.rosuda.REngine.Rserve.RConnection;

/**
 * This class represents the Rserve test cases. Requires that the server
 * (Rserve) is running to perform the tests.
 * 
 * @author Diego García
 * 
 */
public class TestR4JInRserve extends TestCase {

	public void testRVersion() {
		try {
			RConnection c = new RConnection();
			Assert.assertNotNull(c.eval("R.version.string"));
			c.close();
		} catch (Exception e) {

			e.printStackTrace();

		}
	}

	public void testConnection() {
		try {
			RConnection c = new RConnection();
			Assert.assertTrue(c.isConnected());
			c.close();
		} catch (Exception e) {

			e.printStackTrace();

		}
	}

	public void testEvaluationOfBooleanExpression() {

		try {
			RConnection c = new RConnection();
			REXP resultOfTrueExpression = c.eval("5 > 3");
			Assert.assertTrue(REXPLogical.TRUE == resultOfTrueExpression
					.asInteger());
			Assert.assertTrue(resultOfTrueExpression.isLogical());

			REXP resultOfFalseExpression = c.eval("5 < 3");
			Assert.assertTrue(REXPLogical.FALSE == resultOfFalseExpression
					.asInteger());
			Assert.assertTrue(resultOfFalseExpression.isLogical());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testEvaluationOfIntegerExpression() {

		try {
			RConnection c = new RConnection();
			REXP result = c.eval("5 + 3");
			Assert.assertTrue(result.isInteger());
			Assert.assertTrue(result.asInteger() == 8);

			result = c.eval("5 - 3");
			Assert.assertTrue(result.isInteger());
			Assert.assertTrue(result.asInteger() == 2);

			result = c.eval("5 * 3");
			Assert.assertTrue(result.isInteger());
			Assert.assertTrue(result.asInteger() == 15);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
