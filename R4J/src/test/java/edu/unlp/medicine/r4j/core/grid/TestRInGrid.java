package edu.unlp.medicine.r4j.core.grid;

import edu.unlp.medicine.r4j.core.TestR4JCore;

public class TestRInGrid {
	
	public void testSencillo(){
		TestR4JCore test = new TestR4JCore();
		for (int i = 0; i < 10; i++) {
			test.testBasico();
		}
	}
	
	public void testCluster(){
		TestR4JCore test = new TestR4JCore();
		for (int i = 0; i < 10; i++) {
			test.test4ExecuteFile();
		}
	}

}
