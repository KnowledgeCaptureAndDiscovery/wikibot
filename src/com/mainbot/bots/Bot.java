package com.mainbot.bots;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import org.json.JSONException;

import com.mainbot.components.RetrieveData;
import com.mainbot.components.Edit;
import com.mainbot.components.HTMLVisualization;
import com.mainbot.dataobjects.Revision;
import com.mainbot.utility.Utils;


/*
 * Parent for all the specialized bots.
 * */
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
	
	


}
