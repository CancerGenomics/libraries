package edu.unlp.medicine.r4j.exceptions;

import java.util.List;

import edu.unlp.medicine.r4j.rLibrary.RLibrary;

@SuppressWarnings("serial")
public class RequiredLibraryNotPresentException extends Exception {

	List<RLibrary> requiredRLibrariesNotInstalled;

	public RequiredLibraryNotPresentException(
			List<RLibrary> requiredRLibrariesNotInstalled) {
		super();
		this.requiredRLibrariesNotInstalled = requiredRLibrariesNotInstalled;
	}

	public List<RLibrary> getRequiredRLibrariesNotInstalled() {
		return requiredRLibrariesNotInstalled;
	}

	public void setRequiredRLibrariesNotInstalled(
			List<RLibrary> requiredRLibrariesNotInstalled) {
		this.requiredRLibrariesNotInstalled = requiredRLibrariesNotInstalled;
	}
	
	public String getRequiredRLibrariesNotInstalledAsString(){
		StringBuilder res = new StringBuilder("");
		for (RLibrary rlib : this.getRequiredRLibrariesNotInstalled()) {
			res.append(rlib.getName() + ", ");
		}
		return res.toString();
	}
	

}
