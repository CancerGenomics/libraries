package edu.unlp.medicine.r4j.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

public class TestR4JCore extends TestCase{

	public void testRefreshVariableValues(){
		R4JSession rj4SessionFaster = R4JFactory.getR4JInstance().getRSessionFaster("testGetRVariablesUsingFaster2");
		
		ListOfRVariablesToRefresh listOfRVariablesToRefresh = new ListOfRVariablesToRefresh();
		listOfRVariablesToRefresh.addVariableToQuery("singleNumericValue", R_VARIABLE_TYPE.NUMERIC, "c(7)");
		listOfRVariablesToRefresh.addVariableToQuery("multipleNumericValue", R_VARIABLE_TYPE.NUMERIC,"c(8,1,2)");
		listOfRVariablesToRefresh.addVariableToQuery("singleValueString", R_VARIABLE_TYPE.STRING,"c('hola')");
		listOfRVariablesToRefresh.addVariableToQuery("multipleValueString", R_VARIABLE_TYPE.STRING,"c('hola','como','te','va')");
		rj4SessionFaster.getValuesFromR(listOfRVariablesToRefresh);
				
		assertTrue(rj4SessionFaster.getSingleValueFromCache("singleNumericValue", R_VARIABLE_TYPE.NUMERIC).equals("7"));
		assertTrue(rj4SessionFaster.getArrayValueFromCache("multipleNumericValue", R_VARIABLE_TYPE.NUMERIC).size()==3);
		
		assertTrue(rj4SessionFaster.getSingleValueFromCache("singleValueString", R_VARIABLE_TYPE.STRING).equals("\"hola\""));
		assertTrue(rj4SessionFaster.getArrayValueFromCache("multipleValueString", R_VARIABLE_TYPE.STRING).size()==4);
		rj4SessionFaster.close();		
		
	}
	
	public void testGetValuesFromR(){
		R4JSession rj4SessionFaster = R4JFactory.getR4JInstance().getRSessionFaster("testGetRVariablesUsingFaster2");
		
		rj4SessionFaster.addStatement("singleNumericValue<-c(7)");
		rj4SessionFaster.addStatement("multipleNumericValue<-c(8,1,2)");
		rj4SessionFaster.addStatement("singleValueString<-c('hola')");
		rj4SessionFaster.addStatement("multipleValueString<-c('hola','como','te','va')");
		
		assertTrue(rj4SessionFaster.getSingleValueFromR("singleNumericValue", R_VARIABLE_TYPE.NUMERIC).equals("7"));
		assertTrue(rj4SessionFaster.getArrayValueFromR("multipleNumericValue", R_VARIABLE_TYPE.NUMERIC).size()==3);
		
		assertTrue(rj4SessionFaster.getSingleValueFromR("singleValueString", R_VARIABLE_TYPE.STRING).equals("\"hola\""));
		assertTrue(rj4SessionFaster.getArrayValueFromR("multipleValueString", R_VARIABLE_TYPE.STRING).size()==4);
		rj4SessionFaster.close();		
		
	}


	public void testGetValuesFromRUsingExpression(){
		R4JSession rj4SessionFaster = R4JFactory.getR4JInstance().getRSessionFaster("testGetRVariablesUsingFaster2");
		
		assertTrue(rj4SessionFaster.getSingleValueFromR("singleNumericValue", R_VARIABLE_TYPE.NUMERIC, "c(7)").equals("7"));
		assertTrue(rj4SessionFaster.getArrayValueFromR("multipleNumericValue", R_VARIABLE_TYPE.NUMERIC, "c(8,1,2)").size()==3);
		
		assertTrue(rj4SessionFaster.getSingleValueFromR("singleValueString", R_VARIABLE_TYPE.STRING, "c('hola')").equals("\"hola\""));
		assertTrue(rj4SessionFaster.getArrayValueFromR("multipleValueString", R_VARIABLE_TYPE.STRING, "c('hola','como','te','va')").size()==4);
		rj4SessionFaster.close();		
		
	}

	
	public void testBasicoConPathAbsoluto() {
		//Variables
		String linea="linea";
		
		try {
			R4JSession rj4Session = R4JFactory.getR4JInstance().getRSessionFaster("testBasicoDiego2", "c:\\matias");
			rj4Session.assign(linea, "c(1,2,2,2,1)");
			rj4Session.plotInFile("LineaSencilla.png", linea);
			rj4Session.flush();
			rj4Session.close();
		} catch (RException e) {
			
		}
	}

	
	/**
	 * It tests rj4session, the assign statement, the plotInFile and the flush.
	 * Check that this test generates the file LineaSencilla.png in the user.home folder.
	 */
	public void testBasico() {
		//Variables
		String linea="linea";
		
		try {
			R4JSession rj4Session = R4JFactory.getR4JInstance().getRSessionFaster("testBasicoDiego2");
			rj4Session.assign(linea, "c(1,2,2,2,1)");
			rj4Session.plotInFile("LineaSencilla.png", linea);
			rj4Session.flush();
			rj4Session.close();
		} catch (RException e) {
			
		}
	}
	
	public void testGetSingleNumericValue(){
		R4JSession rj4Session = R4JFactory.getR4JInstance().getRSessionFaster("testGetSingleNumericValue2");
		rj4Session.addStatement("valor<-c(7)");
		String valor = rj4Session.getSingleValueFromR("valor", R_VARIABLE_TYPE.NUMERIC);
		
		rj4Session.addStatement("valor2<-c(8)");
		String valor2 = rj4Session.getSingleValueFromR("valor2", R_VARIABLE_TYPE.NUMERIC);
		
		
		assertTrue(valor.equals("7"));
		
		rj4Session.close();
		
		
	}
	
	
	public int testGetValue(String id){
		//Variables
		String recta="recta";
		
		try {
			R4JSession rj4Session = R4JFactory.getR4JInstance().getRSessionFaster("testGetValue" + id);
			rj4Session.assign(recta, "c(1,2,3,4)");
			List<String> result = rj4Session.getArrayValueFromR(recta, R_VARIABLE_TYPE.NUMERIC);
			
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
			R4JSession rj4Session = R4JFactory.getR4JInstance().getRSessionFaster("testOpenCloseOpen");
			rj4Session.assign(recta, "c(1,2,3,4)");
			rj4Session.flush();
			
			List<String> result = rj4Session.getArrayValueFromR(recta, R_VARIABLE_TYPE.NUMERIC);
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
