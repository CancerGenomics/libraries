package edu.unlp.medicine.r4j.utils;
import edu.unlp.medicine.r4j.constants.OSDependentConstants;

public class StringUtils {
	
	public static String addQuotes(String string){
		return OSDependentConstants.DOUBLE_QUOTE + string + OSDependentConstants.DOUBLE_QUOTE;
	}

}
