package com.mainbot.main;

public class Bot {

	/**
	 * @param args
	 */
	
	String sessionID;
	String username;
	String password;
	
	Bot(String sessionID){
		this.sessionID = sessionID;
	}
	
	Bot(String sessionID, String username, String password){
		this.sessionID = sessionID;
		this.username = username;
		this.password = password;
	}
	public void addUserData(String username, String password){
		this.username = username;
		this.password = password;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
