package edu.unlp.medicine.grid.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.gridgain.grid.GridException;
import org.gridgain.grid.GridJob;
import org.gridgain.grid.GridJobAdapter;
import org.gridgain.grid.GridJobResult;
import org.gridgain.grid.gridify.GridifyArgument;
import org.gridgain.grid.gridify.GridifyTaskSplitAdapter;

public class SumTask extends GridifyTaskSplitAdapter<Integer>{

	@Override
	public Integer reduce(List<GridJobResult> results) throws GridException {
		 int totalCharCnt = 0;

	        for (GridJobResult res : results) {
	            // Every job returned a number of letters
	            // for the phrase it was responsible for.
	            Integer charCnt = res.getData();
	            totalCharCnt += charCnt;
	        }
	      
	        // Total number of characters in the phrase
	        // passed into task execution.
	        System.out.println("Total:" + totalCharCnt );
	        return totalCharCnt;
	}

	
	@Override
	protected Collection<? extends GridJob> split(int gridSize, GridifyArgument arg)
			throws GridException {		 

	        List<GridJobAdapter<Integer>> jobs = new ArrayList<GridJobAdapter<Integer>>(10);

	        for (int i = 0; i<10; i++) {
	            jobs.add(new SumJob(i));
	        }

	        return jobs;
	}

}
