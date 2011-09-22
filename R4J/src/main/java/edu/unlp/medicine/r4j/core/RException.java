package edu.unlp.medicine.r4j.core;

public class RException extends Exception {

	public RException(String msg) {
		super(msg+ " .The sccript will not be executed");
	}

}
