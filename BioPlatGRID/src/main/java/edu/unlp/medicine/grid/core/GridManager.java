package edu.unlp.medicine.grid.core;

import org.gridgain.grid.GridException;
import org.gridgain.grid.GridFactory;
import org.gridgain.grid.gridify.Gridify;

public class GridManager {

	 /**
     * Method grid-enabled with {@link Gridify} annotation. 
     * Note that in this case instead of
     * using default task, we provide our own task which will sum the values (using R) on remote nodes,
     */
	@Gridify(taskClass = SumTask.class)
	public static int sumValues(int value) {
		System.out.println(">>>");
		System.out.println(">>> Printing '" + "******************" + value
				+ "*****************");
		System.out.println(">>>");
		return value;
	}

	/**
     * Execute <tt>SumValues</tt> example grid-enabled with
     * <tt>Gridify</tt> annotation.
     */
	public static void main(String[] args) throws GridException {
		GridFactory.start();
		try {
			int value = sumValues(0);
			System.out.println(">>>");
			System.out.println(">>> Total is '" + value + "'.");
			System.out.println(">>>");
		} finally {
			GridFactory.stop(true);
		}
	}
}
