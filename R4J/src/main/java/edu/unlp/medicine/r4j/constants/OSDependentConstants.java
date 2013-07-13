package edu.unlp.medicine.r4j.constants;

import edu.unlp.medicine.r4j.exceptions.R4JServerStartException;
import edu.unlp.medicine.r4j.systemProperties.R4JSystemPropertiesExpected;

public class OSDependentConstants {

	// "\" in windows  "/" in linux
	public static String FILE_SEPARATOR = System.getProperty("file.separator");

	//
	public static String PATH_TO_R = resolveRHome();


	public static String USER_HOME = System.getProperty("user.home");

	public static String DOUBLE_QUOTE = "\"";

	public static String LINE_SEPARATOR = "\n";

	public static String BLANK_CHAR = " ";

	private static String resolveRHome() {
		// /* TODO throw exception 
		String result = System.getProperty(R4JSystemPropertiesExpected.R_HOME_BIOPLAT_PROPERTY);
		
		
		result = result.substring(0, result.length());
		return result + FILE_SEPARATOR + "bin" + System.getProperty("file.separator") + "R";

	}
}
