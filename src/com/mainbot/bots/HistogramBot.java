package com.mainbot.bots;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.json.JSONException;

import com.mainbot.components.Edit;
import com.mainbot.components.Visualization;
import com.mainbot.components.RetrieveData;
import com.mainbot.dataobjects.Revision;
import com.mainbot.utility.Utils;

/*
 * Bot that create the histogram past changes.
 * */

public class HistogramBot extends Bot{

	public static final Logger logger = Logger.getLogger(HistogramBot.class);
	HistogramBot(String username) 
	{
		super(username);
	}
	
	public HistogramBot(String username, String password)
	{
		super(username, password);
	}
	
	
	public void createHistogram() throws JSONException, IOException
	{
		ArrayList<Revision> revisionList;
		Edit edit = new Edit();
		Visualization view = new Visualization();
		System.out.println("1ST VIEW(for past n changes) CREATED!!!!");
		
		
		Visualization view2 = new Visualization();
		System.out.println("2ND VIEW(for past n days) CREATED!!!!");
		
		//5.23 Update: Newsletter////////////////////////
		Visualization view3 = new Visualization();
		System.out.println("3RD VIEW(for newsletter) CREATED!!!");
		/////////////////////////////////////////////////
		
		
		
		revisionList = RetrieveData.getRecentChanges(10); //get the last 10 changes
		
		for(int i = 0; i < revisionList.size(); i++)
		{
			System.out.println("Now printing last 10 changes' article name");
			System.out.println(revisionList.get(i).getArticle().getName());
		}
		
		
		view.recentChangeView(revisionList);
		int revid = edit.edit(view, this, "Test", "new"); //edit the wiki
		
		
//		Scanner scanner = new Scanner(System.in);
//		String erase = scanner.next();
//		if(!erase.isEmpty())
//		{
//			edit.undoRevisions(revid, false, mainbot); //testing purpose to undo the edits
//		}

		revisionList.clear();
		revisionList = RetrieveData.getChangesPastNDays(10, "Category:Working_Group"); //get the revision list for past 30 days
		view2.changesPastNDaysView(revisionList,15);
		
		//5.23 Update: Newsletter/////////////
		//view3.newsletter(revisionList, 15);
		System.out.println("VIEW3 CREATED!!");
		///////////////////////////////////////
		
		int revid2 = edit.edit(view2, this, "Test", "new"); //edit the wiki
		
		//int revid3 = edit.edit(view3, mainbot, "Test");
		
		
/*		erase = scanner.next();
		if(!erase.isEmpty())
			edit.undoRevisions(revid, false, mainbot); //testing purpose to undo the edits
*/
	}
	

}
