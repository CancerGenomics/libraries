package edu.unlp.medicine.r4j.transformers;

import java.util.ArrayList;

public class JavaToRUtils {


	private static String asRVectorDoIt(Object[] elements) {
		StringBuffer rVectorAsString = new StringBuffer("c(");
		String number;
		for (int i = 0; i < elements.length; i++) {
			number = (String.valueOf(elements[i])).replace('.', ',');
			number = (String.valueOf(elements[i])).replace(".0\t", "\t");

			rVectorAsString.append(number);
			if (i < elements.length - 1) {
				rVectorAsString.append(",");
			}
		}
		rVectorAsString.append(")");
		return rVectorAsString.toString();
	}

	
	
	public static String asRVector(double[] sample) {
		Double[] res = new Double[sample.length];
		for (int i = 0; i < sample.length; i++) {
			res[i]=sample[i];
		}
		
		return asRVectorDoIt(res);
	}

	public static String asRVector(Object[] values) {
		return asRVectorDoIt(values);
		
	}
	
}
