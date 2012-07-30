package edu.unlp.medicine.r4j.constants;

public interface OSDependentConstants {
	
	//  "\" in windows  "/" in linux
	public String FILE_SEPARATOR = System.getProperty("file.separator");
	
	//
	public String PATH_TO_R = System.getenv("R_HOME") + OSDependentConstants.FILE_SEPARATOR + "bin" + System.getProperty("file.separator") + "R";
	
	public String USER_HOME = System.getProperty("user.home");
	
	public String DOUBLE_QUOTE = "\"";
	
	public String LINE_SEPARATOR = "\n";
	
	public String BLANK_CHAR = " ";
}
