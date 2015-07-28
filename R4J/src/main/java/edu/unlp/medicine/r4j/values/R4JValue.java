/**
 * 
 */
package edu.unlp.medicine.r4j.values;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.RList;

import edu.unlp.medicine.r4j.exceptions.R4JValueMismatchException;
import edu.unlp.medicine.r4j.server.R4JConnection;
import edu.unlp.medicine.r4j.transformers.R4JDataMatrixTransformer;

/**
 * This abstract class tops the hierarchy representing the possible values.
 * 
 * @author Diego García
 * 
 */
public abstract class R4JValue {

	public R4JValue(final REXP anAdaptee) {
		this.adaptee = anAdaptee;
		
	}

	public REXP getNativeValue() {
		return this.getAdaptee();
	}

	/**
	 * Represents an adaptation of REXP.
	 */
	private REXP adaptee;

	/**
	 * This method converts the object into a native java object.
	 * 
	 * @return
	 */
	public abstract Object asNativeJavaObject() throws R4JValueMismatchException;

	/**
	 * GETTER
	 * 
	 * @return The adaptation of REXP.
	 */
	protected REXP getAdaptee() {
		return this.adaptee;
	}

	public List<String> asStringArrayList() {
		List<String> strings = new ArrayList<String>();
		String[] stringsArray = this.asStrings();
		for (String string: stringsArray) {
			strings.add(string);
		}
		return strings;
	}

	public List<Integer> asIntegerArrayList() {
		List<Integer> ints = new ArrayList<Integer>();
		int[] intsArray = this.asIntegers();
		for (int anInteger : intsArray) {
			ints.add(anInteger );
		}
		return ints;

	}

	/**
	 * Just to convert r Dataframes into StringDataMatrix.
	 * If there is no colnames set in the R dataframe, it will set default column names (column1, column2, ...)
	 * If there is no rownames set in the R dataframe, it will set default column names (Row1, Row2, ...) 
	 */
	public R4JStringDataMatrix asStringDataMatrix(){
		RList rList;
		
		try {
			rList = this.getAdaptee().asList();
			int ncols = rList.size();
			int nrows = rList.at(0).length();
			String[][] matrix = new String[nrows][ncols];
			String[] actualCol; 
			for (int i=0; i<ncols; i++) {
				actualCol = rList.at(i).asStrings();
				for (int j = 0; j < actualCol.length; j++) {
					matrix[j][i]=actualCol[j];
				}
				
			}
			
			//for (int i=0; i<ncols; i++) matrix[i]=rList.at(i).asStrings();
			
			//cols
			REXP colnamesREXP = this.getAdaptee().getAttribute("names");
			List<String> colNames=null;
			if (colnamesREXP!=null){
				colNames = Arrays.asList(colnamesREXP.asStrings());
			}
			else{
				colNames=new ArrayList<String>();
				for (int i = 0; i < ncols; i++) {
					colNames.add("Column" + i);
				}
			}

			
			//rows
			REXP rownamesREXP = this.getAdaptee().getAttribute("row.names");
			List<String> rowNames=null;
			if (rownamesREXP!=null){
				rowNames = Arrays.asList(rownamesREXP.asStrings());
			}
			else{
				rowNames=new ArrayList<String>();
				for (int i = 0; i < matrix[1].length; i++) {
					rowNames.add("row" + i);
				}
			}
			
			
			return new R4JStringDataMatrix(colNames, rowNames, matrix);
		} catch (REXPMismatchException e) {
			e.printStackTrace();
		}
		return null;
		
		
	}
	
	public String[] asStrings() {
		String[] result = null;
		try {
			if (this.getAdaptee().length() > 0)
				result = this.getAdaptee().asStrings();
		} catch (REXPMismatchException e) {
			// TODO log
			e.printStackTrace();
		}
		return result;
	}

	public double[] asDoubles() {
		double[] result = null;
		try {
			if (this.getAdaptee().length() > 0)
				result = this.getAdaptee().asDoubles();
		} catch (REXPMismatchException e) {
			// TODO log
			e.printStackTrace();
		}
		return result;
	}

	public double asDouble() {
		double result = 0;
		try {
			if (this.getAdaptee().length() > 0)
				result = this.getAdaptee().asDouble();
		} catch (REXPMismatchException e) {
			// TODO log
			e.printStackTrace();
		}
		return result;
	}

	public int asInteger() {
		int result = 0;
		try {
			if (this.getAdaptee().length() > 0)
				result = this.getAdaptee().asInteger();
		} catch (REXPMismatchException e) {
			// TODO log
			e.printStackTrace();
		}
		return result;
	}

	public String asString() {
		String result = "N/A";
		try {
			if (this.getAdaptee().length() > 0)
				result = this.getAdaptee().asString();
		} catch (REXPMismatchException e) {
			// TODO log
			e.printStackTrace();
		}
		return result;
	}

	public int[] asIntegers() {
		int[] result = null;
		try {
			if (this.getAdaptee().length() > 0)
				result = this.getAdaptee().asIntegers();
		} catch (REXPMismatchException e) {
			// TODO log
			e.printStackTrace();
		}
		return result;
	}

	public boolean isLogical() {
		return this.getAdaptee().isLogical();
		
	}

	public boolean isNumeric() {
		return this.getAdaptee().isNumeric();
		
	}

}
