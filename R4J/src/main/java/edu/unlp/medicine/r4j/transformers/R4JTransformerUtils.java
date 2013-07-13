package edu.unlp.medicine.r4j.transformers;

import java.io.InputStream;
import java.util.Properties;

import org.rosuda.REngine.REXP;

import edu.unlp.medicine.r4j.exceptions.R4JTransformerNotFoundException;
import edu.unlp.medicine.r4j.values.R4JValue;

public class R4JTransformerUtils {
	
	private static Properties properties = new Properties();
	
	public static R4JValue transform(final REXP source) throws R4JTransformerNotFoundException {
		IR4JvalueTransformer transformer;
		try {
			transformer = (IR4JvalueTransformer) Class.forName(properties.getProperty(getKeyToDirectory(source))).newInstance(); //$NON-NLS-1$
		} catch (Exception e) {
			throw new R4JTransformerNotFoundException(e.getMessage(), e, source.getClass());
		} 
		return transformer.transform(source);
	}

	private static String getKeyToDirectory(final REXP source) {
		return source.getClass().getSimpleName().replaceAll("REXP", "").toLowerCase();    
	}
	
	static {		
		try {
			InputStream input = R4JTransformerUtils.class.getResourceAsStream("/transformationsDirectory.properties");
			properties.load(input);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
