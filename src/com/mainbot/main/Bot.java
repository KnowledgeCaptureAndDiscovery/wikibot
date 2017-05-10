package com.mainbot.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import org.json.JSONException;

import com.mainbot.components.Access;
import com.mainbot.components.Edit;
import com.mainbot.components.Visualization;
import com.mainbot.dataobjects.Revision;
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
	
	public static void createHistogram(Bot mainbot) throws JSONException, IOException{
		ArrayList<Revision> revisionList;
		Edit edit = new Edit();
		Visualization view = new Visualization();
		Visualization view2 = new Visualization();
		revisionList = Access.getRecentChanges(10); //get the last 10 changes
		view.recentChangeView(revisionList);
		int revid = edit.edit(view,mainbot); //edit the wiki
		
		
		/*Scanner scanner = new Scanner(System.in);
		String erase = scanner.next();
		if(!erase.isEmpty())
			edit.undoRevisions(revid, false, mainbot); //testing purpose to undo the edits
*/
		revisionList.clear();
		revisionList = Access.getChangesPastNDays(30, "Category:Working_Group"); //get the revision list for past 30 days
		view2.changesPastNDaysView(revisionList,30);
		int revid2 = edit.edit(view2,mainbot); //edit the wiki
		
		
/*		erase = scanner.next();
		if(!erase.isEmpty())
			edit.undoRevisions(revid, false, mainbot); //testing purpose to undo the edits
*/
	}
	
	public static void main(String[] args) throws JSONException, IOException {
		// TODO Auto-generated method stub
		Bot mainbot = new Bot("testBot", "testBot123");
		Utils util = new Utils();
		util.login(mainbot);
		createHistogram(mainbot);
		
	}

}
