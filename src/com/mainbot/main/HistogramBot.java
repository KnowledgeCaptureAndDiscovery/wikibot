package com.mainbot.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import org.json.JSONException;

import com.mainbot.components.Edit;
import com.mainbot.components.HTMLVisualization;
import com.mainbot.components.RetrieveData;
import com.mainbot.dataobjects.Revision;
import com.mainbot.utility.Utils;

/*
 * Bot that create the histogram past changes.
 * */

public class HistogramBot extends Bot{

	HistogramBot(String username) 
	{
		super(username);
	}
	
	HistogramBot(String username, String password)
	{
		super(username, password);
	}
	
	
	public static void createHistogram(HistogramBot mainbot) throws JSONException, IOException
	{
		ArrayList<Revision> revisionList;
		Edit edit = new Edit();
		HTMLVisualization view = new HTMLVisualization();
		System.out.println("1ST VIEW(for past n changes) CREATED!!!!");
		
		
		HTMLVisualization view2 = new HTMLVisualization();
		System.out.println("2ND VIEW(for past n days) CREATED!!!!");
		
		//5.23 Update: Newsletter////////////////////////
		HTMLVisualization view3 = new HTMLVisualization();
		System.out.println("3RD VIEW(for newsletter) CREATED!!!");
		/////////////////////////////////////////////////
		
		
		
		revisionList = RetrieveData.getRecentChanges(10); //get the last 10 changes
		
		for(int i = 0; i < revisionList.size(); i++)
		{
			System.out.println("Now printing last 10 changes' article name");
			System.out.println(revisionList.get(i).getArticle().getName());
		}
		
		
		view.recentChangeView(revisionList);
		int revid = edit.edit(view,mainbot); //edit the wiki
		
		
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
		//view3.firstnewsletter(revisionList, 15);
		System.out.println("VIEW3 CREATED!!");
		///////////////////////////////////////
		
		//int revid2 = edit.edit(view2,mainbot); //edit the wiki
		
		int revid3 = edit.edit(view3, mainbot);
		
		
/*		erase = scanner.next();
		if(!erase.isEmpty())
			edit.undoRevisions(revid, false, mainbot); //testing purpose to undo the edits
*/
	}
	
	public static void main(String[] args) throws JSONException, IOException 
	{
		// TODO Auto-generated method stub
		HistogramBot mainbot = new HistogramBot("testBot", "testBot123");
		System.out.println("histogram bot!!");
		Utils util = new Utils();
		util.login(mainbot);
		createHistogram(mainbot);
	}
}
