package edu.unlp.medicine.r4j.test.utils.RUtils;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import edu.unlp.medicine.r4j.utils.RUtils;

public class RUtilsTest extends TestCase{
	
	public void testJavaStringListAsRStringList(){
		List<String> aList = new ArrayList<String>();
		aList.add("string1");aList.add("string2");aList.add("string3");
		String rStringList = RUtils.javaStringListAsRStringList(aList);
		assertEquals(rStringList, "c(\"string1\",\"string2\",\"string3\")");
	}

}
