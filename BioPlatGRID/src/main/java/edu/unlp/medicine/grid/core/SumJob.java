package edu.unlp.medicine.grid.core;

import java.util.List;

import org.gridgain.grid.GridException;
import org.gridgain.grid.GridJobAdapter;

import edu.unlp.medicine.r4j.core.R4JFactory;
import edu.unlp.medicine.r4j.core.R4JSession;
import edu.unlp.medicine.r4j.core.RException;

public class SumJob extends GridJobAdapter<Integer> {
	private int number;
	
	public SumJob(final int i) {
		this.number = i;
	}

	@Override
	public Object execute() throws GridException {
		String recta="recta";
		
		try {
			R4JSession rj4Session = R4JFactory.getR4JInstance().getRSession("testGetValue" + number );
			rj4Session.assign(recta, "c(1,2,3,4)");
			List<String> result = rj4Session.getArrayValue(recta);
			rj4Session.close();
			return Integer.valueOf(result.get(0));
		} catch (RException e) {
			return 0;
		}
	}
}
