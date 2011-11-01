package edu.unlp.medicine.r4j.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileManager {
	
	public static BufferedWriter createFile(String path) throws IOException{
		return createFile(path, false);
	}
	
	public static BufferedWriter createFile(String path, boolean append) throws IOException{
		BufferedWriter bufferedWriter;
		try {
			bufferedWriter = new BufferedWriter(new FileWriter(path, append));
		} catch (IOException e) {
			File file = new File(path);
			file.getParentFile().mkdirs();
			try {
				bufferedWriter = new BufferedWriter(new FileWriter(path, append));
			} catch (IOException e1) {
				e.printStackTrace();
				throw e1;
			}
		}
		return bufferedWriter;
	}

}
