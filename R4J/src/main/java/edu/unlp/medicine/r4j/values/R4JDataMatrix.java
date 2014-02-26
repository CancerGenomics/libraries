package edu.unlp.medicine.r4j.values;

import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;

import edu.unlp.medicine.r4j.exceptions.R4JValueMismatchException;

public class R4JDataMatrix extends R4JValue {
	private R4JValue[][] matrix;
	private String[] columnNames;
	private String[] rowNames;

	public R4JDataMatrix(REXP anAdaptee) {
		super(anAdaptee);	
	}

	@Override
	public Object asNativeJavaObject() throws R4JValueMismatchException {
		try {
			return this.getAdaptee().asNativeJavaObject();
		} catch (REXPMismatchException e) {
			throw new R4JValueMismatchException(e,this,"native Java Object");		
		}		
	}

	public R4JValue getValue(final int rowIndex, final int colIndex) {
		return this.getMatrix()[rowIndex][colIndex];
	}

	public R4JValue getValue(final String rowName, final int colIndex) {
		int rowIndex = this.getRowIndex(rowName);
		return this.getValue(rowIndex, colIndex);
	}

	public R4JValue getValue(final int rowIndex, final String colName) {		
		int colIndex = this.getColumnIndex(colName);
		return this.getValue(rowIndex, colIndex);
	}

	public R4JValue getValue(final String rowName, final String colName) {
		int rowIndex = this.getRowIndex(rowName);
		int colIndex = this.getColumnIndex(colName);
		return this.getValue(rowIndex, colIndex);
	}

	public R4JValue getValuesOfRow(final String rowName) {
		return this.getValuesOfRow(this.getColumnIndex(rowName));
	}

	public R4JCollectionValue getValuesOfColumn(final String columnName) {
		return this.getValuesOfColumn(this.getColumnIndex(columnName));
	}

	public R4JValue getValuesOfRow(final int rowIndex) {
		R4JCollectionValue collection = new R4JCollectionValue(this.getAdaptee());
		for (int i = 0; i < this.columnNames.length; i++) {
				R4JValue value = this.getMatrix()[rowIndex][i];
				collection.addValue(value);
		}		
		return collection;
	}

	public R4JCollectionValue getValuesOfColumn(final int columnIndex) {
		R4JCollectionValue collection = new R4JCollectionValue(this.getAdaptee());
		for (int i = 0; i < this.rowNames.length; i++) {
				R4JValue value = this.getMatrix()[i][columnIndex];
				collection.addValue(value);
		}		
		return collection;
	}
	
	public String[] getRowNames() {
		try {
			if (rowNames == null)
			rowNames = this.getAdaptee().getAttribute("row.names").asStrings();
		} catch (REXPMismatchException e) {
			// TODO log
			e.printStackTrace();
		}
		return rowNames;
	}
	
	public String[] getColumnNames() {
		try {
			if (this.columnNames == null) {
				columnNames = this.getAdaptee().getAttribute("names").asStrings();
			}
			 
		} catch (REXPMismatchException e) {
			// TODO log
			e.printStackTrace();
		}
		return columnNames;
	}
	
	public R4JValue[][] getMatrix() {
		if (this.matrix == null) {
			this.matrix = this.buildMatrix();
		}
		return this.matrix;
		
	}

	private R4JValue[][] buildMatrix() {
		R4JValue[][] result = new R4JValue[this.getRowNames().length][this.getColumnNames().length];
		try {
			Map expressionInJava = (Map) this.getAdaptee().asNativeJavaObject();
			Iterator iterator = expressionInJava.keySet().iterator();			
			while (iterator.hasNext()) {
				
		        String[] key = ( String[])iterator.next();
		        String columnName = expressionInJava.get(key).toString();
				for (int i = 0; i< key.length; i++) {
					result[i][this.getColumnIndex(columnName)] = new R4JStringValue(key[i].trim());
					
				}				
			}
			
		} catch (REXPMismatchException e) {
			// TODO log
			e.printStackTrace();
		}		
		return result;
	}

	public int getColumnIndex(final String columnName) {
		boolean found = false;
		int index = 0;
		while (!found && index < this.columnNames.length) {
			if (this.getColumnNames()[index].equals(columnName)) {
				found = true;
			} else
			    index++;
		}		
		return (found) ? index: -1;		
	}
	
	public int getRowIndex(final String rowName) {
		boolean found = false;
		int index = 0;
		while (!found && index < this.rowNames.length) {
			if (this.getRowNames()[index].equals(rowName)) {
				found = true;
			} else
			   index++;
		}
		return (found) ? index: -1;		
	}


}
