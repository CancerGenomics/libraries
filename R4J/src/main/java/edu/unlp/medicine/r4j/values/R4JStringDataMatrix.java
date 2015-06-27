package edu.unlp.medicine.r4j.values;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unlp.medicine.r4j.exceptions.R4JException;
import edu.unlp.medicine.r4j.server.R4JServer;

public class R4JStringDataMatrix {

	private static final Logger LOGGER = LoggerFactory.getLogger(R4JStringDataMatrix.class);
	
	List<String> colnames;
	List<String> rownames;
	/**
	 * Rows is the first dimension and columns is the second dimention. Indexe
	 * starts in 0.
	 */
	String[][] data;

	public R4JStringDataMatrix(List<String> colnames, List<String> rownames,
			String[][] data) {
		super();
		this.colnames = colnames;
		this.rownames = rownames;
		this.data = data;
	}

	public List<String> getColnames() {
		return colnames;
	}

	public void setColnames(List<String> colnames) {
		this.colnames = colnames;
	}

	public List<String> getRownames() {
		return rownames;
	}

	public void setRownames(List<String> rownames) {
		this.rownames = rownames;
	}

	public String[][] getData() {
		return data;
	}

	public void setData(String[][] data) {
		this.data = data;
	}

	public String getCellValue(int rowIndex, int colIndex) {
		try {
			return data[rowIndex][colIndex];
		} catch (IndexOutOfBoundsException e) {
			
			LOGGER.error("Error in col and/or row. You tried to access: row="+ rowIndex + "   col=" + colIndex);
		}
		return "error";

	}

	public String getCellValue(String rowName, int colIndex) {
		try {
			return data[rownames.indexOf(rowName)][colIndex];
		} catch (IndexOutOfBoundsException e) {
			LOGGER.error("Error in col and/or row. You tried to access: row="+ rowName + "   col=" + colIndex);
		}
		return "error";
	}

	public String getCellValue(String rowName, String colName) {
		try {
			return data[colnames.indexOf(rowName)][colnames.indexOf(colName)];
		} catch (IndexOutOfBoundsException e) {
			LOGGER.error("Error in col and/or row. You tried to access: row="+ rowName + "   col=" + colName);
		}
		return "error";
	}

	public String getCellValue(int rowIndex, String colName) {
		try {
			return data[rowIndex][colnames.indexOf(colName)];
		} catch (IndexOutOfBoundsException e) {
			LOGGER.error("Error in col and/or row. You tried to access: row="+ rowIndex + "   col=" + colName);
		}
		return "error";
	}

	public Map<String, String> getRowDataAsMap(String rowName) {
		return getRowDataAsMap(rownames.indexOf(rowName));
	}

	public Map<String, String> getRowDataAsMap(int rowId) {
		try {
			String[] rowData = data[rowId];
			HashMap<String, String> result = new HashMap<String, String>();
			for (int i = 0; i < rowData.length; i++) {
				result.put(colnames.get(i), rowData[i]);
			}

			return result;

		} catch (IndexOutOfBoundsException e) {
			LOGGER.error("Error in row. You tried to access: row="+ rowId, e);		}
		
		Map<String, String> errorMap = new HashMap<String, String>();
		for (int i = 0; i < data[0].length; i++) {
			errorMap.put(colnames.get(i), "error");
		
		}
		return errorMap;
		
	}
	
	public int getNumberOfRows(){
		return this.rownames.size();
	}
	
	public int getNumberOfCols(){
		return this.colnames.size();
	}
	
		
	
}

