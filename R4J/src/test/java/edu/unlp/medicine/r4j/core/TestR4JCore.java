package edu.unlp.medicine.r4j.core;

import edu.unlp.medicine.r4j.utils.FileSystemUtils;
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
			R4JSession rj4Session = R4JFactory.getR4JInstance().getRSession(FileSystemUtils.completePathToUserFolder("PruebaBasica.txt"), "resultado.txt");
			rj4Session.assign(linea, "c(1,2,3,4)");
			rj4Session.plotInFile("LineaSencilla.png", linea);
			
			rj4Session.execute();
		} catch (RException e) {
			
		}
	}
	
	public void test4ExecuteFile(){
		R4JFactory.getR4JInstance().executeFile("files4test\\ClusteringAndLogRankTestScript.txt");
	}
	
	
}
