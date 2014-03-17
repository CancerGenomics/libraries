package edu.unlp.medicine.r4j.server;

public class R4JConnectionPool {
	
	static R4JConnectionPool instance;
	
	private R4JConnectionPool(){
		
	}
	
	public static R4JConnectionPool getInstance(){
		if (instance==null){
			instance= new R4JConnectionPool();
		}
		return instance;
	}

}
