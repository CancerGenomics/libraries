package edu.unlp.medicine.r4j.test;

import edu.unlp.medicine.r4j.systemProperties.R4JSystemPropertiesExpected;
import junit.framework.TestCase;

public class R4JTestCase extends TestCase {
	
	@Override
	protected void setUp()  {
		
		System.setProperty(R4JSystemPropertiesExpected.R_HOME_BIOPLAT_PROPERTY, "C:\\desarrollo\\R-3.0.1");
		System.setProperty(R4JSystemPropertiesExpected.R_REQUIRED_LIBRARIES_FILE_PATH_PROPERTY, "/librariesToBioplat.r");
		

	}

	
	
	
	
}
