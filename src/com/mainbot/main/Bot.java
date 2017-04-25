package com.mainbot.main;

import org.json.JSONException;
import org.json.JSONObject;

import com.mainbot.components.Access;
import com.mainbot.utility.ConnectionRequests;
import com.mainbot.utility.Constants;
import com.mainbot.utility.Utils;

public class Bot {
	/**
	 * @param args
	 */
	String sessionID;
	String username;
	String password;

	Bot(String username){
		this.username = username;
	}
	
	Bot(String username, String password){
		this.username = username;
		this.password = password;
	}	
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getSessionID() {
		return this.sessionID;
	}

	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Bot mainbot = new Bot("testBot", "testBot123");
		try {
			Utils.login(mainbot);
			/*Timeline*/
			String changes = Access.getRecentChanges(10);
			System.out.println(changes);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
