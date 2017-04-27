package com.mainbot.main;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Scanner;

import org.json.JSONException;
import org.json.JSONObject;

import com.mainbot.components.Access;
import com.mainbot.components.Edit;
import com.mainbot.components.Visualization;
import com.mainbot.dataobjects.Revision;
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
	
	public static void createHistogram(Bot mainbot) throws JSONException, UnsupportedEncodingException{
		ArrayList<Revision> revisionList = Access.getRecentChanges(10); //get the revision list
		Visualization view = new Visualization();
		Edit edit = new Edit();
		view.recentChangeView(revisionList); // create view for the recent revisions
		int revid = edit.edit(view,mainbot); //edit the wiki
		
		
		Scanner scanner = new Scanner(System.in);
		String erase = scanner.next();
		if(!erase.isEmpty())
			edit.undoRevisions(revid, false, mainbot); //testing purpose to undo the edits
	}
	
	public static void main(String[] args) throws JSONException, UnsupportedEncodingException {
		// TODO Auto-generated method stub
		Bot mainbot = new Bot("testBot", "testBot123");
		Utils util = new Utils();
		util.login(mainbot);
		createHistogram(mainbot);
		//check the working of ssh keys
	}

}
