package edu.unlp.medicine.r4j.constants;

import edu.unlp.medicine.r4j.systemProperties.R4JSystemPropertiesExpected;

public class OSDependentConstants {

	// "\" in windows  "/" in linux
	public static final String FILE_SEPARATOR = System.getProperty("file.separator");

	//
	public static final String PATH_TO_R = resolveRHome();


	public static final String USER_HOME = System.getProperty("user.home");

	public static final String DOUBLE_QUOTE = "\"";

	public static final String LINE_SEPARATOR = "\n";

	public static final String BLANK_CHAR = " ";

	private static String resolveRHome() {
		try {
			// /* TODO throw exception
			String result = System.getProperty(R4JSystemPropertiesExpected.R_HOME_BIOPLAT_PROPERTY);


			result = result.substring(0, result.length());
			return result + FILE_SEPARATOR + "bin" + System.getProperty("file.separator") + "R";
		}catch (Exception e ){
			//FIXME
			return "/home/R/"; //no sirve para nada ahora...
		}

	}
}
