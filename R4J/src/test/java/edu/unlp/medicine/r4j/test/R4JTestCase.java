package edu.unlp.medicine.r4j.test;

import edu.unlp.medicine.r4j.systemProperties.R4JSystemPropertiesExpected;
import junit.framework.TestCase;

public class R4JTestCase extends TestCase {
	
	@Override
	protected void setUp()  {
		
		System.setProperty(R4JSystemPropertiesExpected.R_HOME_BIOPLAT_PROPERTY, "D:\\Dropbox\\BioPlat\\3-Desarrollo\\r\\R-3.1.0");
		System.setProperty(R4JSystemPropertiesExpected.R_REQUIRED_LIBRARIES_FILE_PATH_PROPERTY, "/librariesToBioplat.r");
		

	}

	
	
	
	
}
