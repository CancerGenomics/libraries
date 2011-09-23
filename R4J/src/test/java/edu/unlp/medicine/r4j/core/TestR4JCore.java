package edu.unlp.medicine.r4j.core;

import java.util.List;
import java.util.Random;

import junit.framework.TestCase;

public class TestR4JCore extends TestCase{
	
	/**
	 * It tests rj4session, the assign statement, the plotInFile and the execute.
	 * Check that this test generates the file LineaSencilla.png in the user.home folder.
	 */
	public void testBasico() {
		//Variables
		String linea="linea";
		
		try {
			R4JSession rj4Session = R4JFactory.getR4JInstance().getRSession("PruebaBasica.txt", "resultadoTestBasico.txt");
			rj4Session.assign(linea, "c(1,2,3,4)");
			rj4Session.plotInFile("LineaSencilla.png", linea);
			
			rj4Session.execute();
		} catch (RException e) {
			
		}
	}
	
	
	public double test4ExecuteFile() throws RException{
		R4JSession r4jssession;
		try {
			r4jssession = R4JFactory.getR4JInstance().getRSession("script.txt", "ResultadoClustering.txt", "files4test\\ClusteringAndLogRankTestScript.txt");
			List<String> result = r4jssession.execute();
			System.out.println(result.get(0));
			return Double.valueOf(result.get(0));
		} catch (RException e) {
			fail();
			throw e;
		}
		
	}
	
	
}
