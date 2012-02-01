package edu.unlp.medicine.r4j.core.grid;

import junit.framework.TestCase;
import edu.unlp.medicine.r4j.core.RException;
import edu.unlp.medicine.r4j.core.TestR4JCore;

public class TestRInGrid extends TestCase {
	
	public void testSencillo(){
		TestR4JCore test = new TestR4JCore();
		int suma = 0;
		for (int i = 0; i < 10; i++) {
			suma = suma + test.testGetValue(String.valueOf(i));
		}
		System.out.println(suma);
	}
	
	public void testCluster() throws RException{
		TestR4JCore test = new TestR4JCore();
		for (int i = 0; i < 10; i++) {
			//System.out.println(test.test4ExecuteFile());
		}
	}

}
