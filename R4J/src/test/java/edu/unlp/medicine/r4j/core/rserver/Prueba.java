package edu.unlp.medicine.r4j.core.rserver;


public class Prueba {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		long tiempoInicio = System.currentTimeMillis();
	    
	    for (int i = 0; i<50; i++) {
	    	TestGSE24185InRserve test = new TestGSE24185InRserve();
	    	System.out.println("Iteracion:" + i);
	    	test.setUp();
	    	test.testEval();
	    	test.tearDown();
	    }
	    long totalTiempo = System.currentTimeMillis() - tiempoInicio;
	    System.out.println("El tiempo de demora es :" + (totalTiempo * 0.001)  + " seg");
	    System.out.println("El tiempo de demora es :" + (totalTiempo)  + " ms");
	    
	}

}

