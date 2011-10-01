package edu.unlp.medicine.r4j.core;

import java.util.List;

import junit.framework.TestCase;

public class TestR4JCore extends TestCase{
	
	/**
	 * It tests rj4session, the assign statement, the plotInFile and the flush.
	 * Check that this test generates the file LineaSencilla.png in the user.home folder.
	 */
	public void testBasico() {
		//Variables
		String linea="linea";
		
		try {
			R4JSession rj4Session = R4JFactory.getR4JInstance().getRSession("testBasico");
			rj4Session.assign(linea, "c(1,2,3,4)");
			rj4Session.plotInFile("LineaSencilla.png", linea);
			rj4Session.flush();
			rj4Session.close();
		} catch (RException e) {
			
		}
	}
	
	public int testGetValue(String id){
		//Variables
		String recta="recta";
		
		try {
			R4JSession rj4Session = R4JFactory.getR4JInstance().getRSession("testGetValue" + id);
			rj4Session.assign(recta, "c(1,2,3,4)");
			List<String> result = rj4Session.getArrayValue(recta);
			
			assertTrue(result.get(0).equals("1"));
			
			rj4Session.close();
			return new Integer(result.get(0));
		} catch (RException e) {
			return 0;
		}
	}
	
	
	public void testOpenCloseOpen(){
		//Variables
		String recta="recta";
		try {
			R4JSession rj4Session = R4JFactory.getR4JInstance().getRSession("testOpenCloseOpen");
			rj4Session.assign(recta, "c(1,2,3,4)");
			rj4Session.flush();
			
			List<String> result = rj4Session.getArrayValue(recta);
			assertTrue(result.get(0).equals("1"));

			rj4Session.close();
		} catch (RException e) {
		}
	}
	
	
	
//	public void test4ExecuteASecondFile(){
//		R4JSession r4jssession;
//		try {
//			r4jssession = R4JFactory.getR4JInstance().getRSession("files4test\\testPlotInFile.txt");
//			r4jssession.addStatementsOfTheFile("test4ExecuteFile_TwoFiles1");
//			r4jssession.flush();
//		
//			r4jssession.addStatementsOfTheFile("files4test\\test2.txt");
//			r4jssession.flush();
//			r4jssession.close();
//			
//		} catch (RException e) {
//			fail();
//		
//		}
//		
//	}
//	
//	public void test4ExecuteTwoFiles(){
//		R4JSession r4jssession;
//		try {
//			r4jssession = R4JFactory.getR4JInstance().getRSession("test4ExecuteFile_TwoFiles1");
//			r4jssession.addStatementsOfTheFile("files4test\\testPlotInFile.txt");
//			r4jssession.flush();
//			r4jssession.close();
//		
//			r4jssession = R4JFactory.getR4JInstance().getRSession("test4ExecuteFile_TwoFiles2");
//			r4jssession.addStatementsOfTheFile("files4test\\test2.txt");
//			
//			r4jssession.flush();
//			r4jssession.close();
//			
//		} catch (RException e) {
//			fail();
//		
//		}
//	}
//	
//	/**
//	 * Checks if there is a png file in userhome/testPlotInFile
//	 * @return
//	 * @throws RException
//	 */
//	public double test4ExecuteFile_testPlotFile() throws RException{
//		R4JSession r4jssession;
//		try {
//			r4jssession = R4JFactory.getR4JInstance().getRSession("test4ExecuteFile_plot");
//			r4jssession.addStatementsOfTheFile("files4test\\testPlotInFile.txt");
//			r4jssession.flush();
//			r4jssession.close();
//			return 0;
//		} catch (RException e) {
//			fail();
//			throw e;
//		}
//	}
//	
//	
//	
//	
//	private void printStream(InputStream inputStream) {
//		InputStreamReader isr = new InputStreamReader(inputStream);
//		try {
//			if (isr.ready()){
//		
//					BufferedReader errorStream = new BufferedReader(isr);
//		
//					StringBuffer buffer = new StringBuffer();
//					int ch;
//		
//			
//					while ((ch = errorStream.read()) > -1) {
//						buffer.append((char)ch);
//					}
//					System.out.println(buffer.toString());
//					
//			}
//		} catch (IOException e) {
//			
//			e.printStackTrace();
//		}
//		}
//	
//	public void testHard() {
//		try {
//			
//			Process rProcess = Runtime.getRuntime().exec(OSDependentConstants.DOUBLE_QUOTE + OSDependentConstants.PATH_TO_R + OSDependentConstants.DOUBLE_QUOTE + " --save -f c:/a10.txt");
//			printStream(rProcess.getInputStream());
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
	
}
