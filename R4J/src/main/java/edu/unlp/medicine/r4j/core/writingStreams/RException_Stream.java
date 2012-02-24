package edu.unlp.medicine.r4j.core.writingStreams;

public class RException_Stream extends Exception {

	public RException_Stream(String msg) {
		super(msg+ " .The sccript will not be executed");
	}

}
