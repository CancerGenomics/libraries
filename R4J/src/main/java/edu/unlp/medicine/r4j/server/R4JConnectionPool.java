package edu.unlp.medicine.r4j.server;

public class R4JConnectionPool {
	
	static R4JConnectionPool _instance;
	
	private R4JConnectionPool(){
		
	}
	
	public static R4JConnectionPool getInstance(){
		if (_instance==null){
			_instance= new R4JConnectionPool();
		}
		return _instance;
	}

}
