package edu.unlp.medicine.r4j.test.transformers;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.Rserve.RConnection;

import edu.unlp.medicine.r4j.transformers.R4JTransformerUtils;

/**
 * This class represents the transformation test cases. Requires that the server
 * (Rserve) is running to perform the tests.
 * 
 * @author Diego Garc�a
 * 
 */
public class BasicTest extends TestCase {
	
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


	public void testEvaluationOfBooleanExpression() {

		try {
			
			REXP resultOfTrueExpression = connection.eval("5 > 3");
			Object value = R4JTransformerUtils.transform(resultOfTrueExpression).asNativeJavaObject();
			Assert.assertTrue(((Boolean)value).booleanValue());

			REXP resultOfFalseExpression = connection.eval("5 < 3");
			value = R4JTransformerUtils.transform(resultOfFalseExpression).asNativeJavaObject();
			Assert.assertFalse(((Boolean)value).booleanValue());
			
			

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testEvaluationOfDoubleExpression() {

		try {
			
			REXP result = connection.eval("5 + 3"); 	
			Object value = R4JTransformerUtils.transform(result).asNativeJavaObject();
			Assert.assertTrue((Double)value == 8);

			result = connection.eval("5 - 3");
			value = R4JTransformerUtils.transform(result).asNativeJavaObject();
			Assert.assertTrue((Double)value == 2);

			result = connection.eval("5 * 3");
			value = R4JTransformerUtils.transform(result).asNativeJavaObject();
			Assert.assertTrue((Double) value == 15);
			
			

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
