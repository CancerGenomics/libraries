package edu.unlp.medicine.r4j.core.rserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RSession;

public class PruebasVector {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			long tiempoInicio = System.currentTimeMillis();
			
			int[] arreglo = new int[] {1,2,3};
			
			//Process process = Runtime.getRuntime().exec("C:\\Program Files\\R\\R-2.14.1\\bin\\i386\\R.exe --save --restore -q -f c:\\diego.txt");
			//Process process = Runtime.getRuntime().exec("C:\\Program Files\\R\\R-2.14.1\\bin\\i386\\a.bat", null,new File("C:\\Program Files\\R\\R-2.14.1\\bin\\i386"));
			Process process = Runtime.getRuntime().exec("C:\\Program Files\\R\\R-2.14.1\\bin\\i386\\R.exe --save --restore -q -e library('Rserve');Rserve()");
			
			
			
			process.getInputStream();
			
			

			for (int iteracion = 0; iteracion < 500; iteracion++) {
				System.out.println ("Iteración: " + iteracion);
				RConnection connection = new RConnection();
				
				connection
						.assign("aNumerico1",
								"c(1,2,3,4,5,6,7,8,7,6,5,4,3,2,2,3,3,4,5,6,7,5,4,3,2,2,4,5,56,7,8,7,6,5,4,3,3,4,5,6,7,8,8,7,6,5,45,4,43,3,3,3,3,3,3,3,3,4,5,6,6,6,6,5,5,4,4,3,3,3,2,2,3,3,4,5,6,67,9)");
				connection
						.assign("aNumerico2",
								"c(1,2,3,4,5,6,7,8,7,6,5,4,3,2,2,3,3,4,5,6,7,5,4,3,2,2,4,5,56,7,8,7,6,5,4,3,3,4,5,6,7,8,8,7,6,5,45,4,43,3,3,3,3,3,3,3,3,4,5,6,6,6,6,5,5,4,4,3,3,3,2,2,3,3,4,5,6,67,9,1,2,3,4,5,6,7,8,7,6,5,4,3,2,2,3,3,4,5,6,7,5,4,3,2,2,4,5,56,7,8,7,6,5,4,3,3,4,5,6,7,8,8,7,6,5,45,4,43,3,3,3,3,3,3,3,3,4,5,6,6,6,6,5,5,4,4,3,3,3,2,2,3,3,4,5,6,67,9)");

				String[] vector1 = connection.eval("aNumerico1").asStrings();
				String[] vector2 = connection.eval("aNumerico2").asStrings();

				for (int i = 0; i < vector1.length; i++) {
					System.out.println(vector1[i]);
				}

				for (int i = 0; i < vector2.length; i++) {
					System.out.println(vector2[i]);
				}
				connection.close();
			}

			long totalTiempo = System.currentTimeMillis() - tiempoInicio;
			System.out.println("El tiempo de demora es :"
					+ (totalTiempo * 0.001) + " seg");
			System.out.println("El tiempo de demora es :" + (totalTiempo)
					+ " ms");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
