package com.mainbot.main;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Scanner;

import org.json.JSONException;

import com.mainbot.components.CategoryDefinition;
import com.mainbot.components.Edit;
import com.mainbot.components.HTMLVisualization;
import com.mainbot.dataobjects.Revision;
import com.mainbot.utility.Constants;
import com.mainbot.utility.Utils;

public class NewsLetterBot extends Bot{
	
	String sessionID;
	String username;
	String password;

	/*Bot for creating a newsletter from last week on the wiki.
	 * On the page /newsletter
	 * */
	NewsLetterBot(String username) 
	{
		super(username);
	}
	
	NewsLetterBot(String username, String password) 
	{
		super(username, password);
	}
	

	
	public static void createNewsletter(NewsLetterBot mainbot) throws JSONException, IOException, ParseException
	{
		CategoryDefinition catDef = new CategoryDefinition();
		
		int dataset = catDef.countArticlesOfCategoryNDays("Category:Dataset_(L)", 7);
		int user = catDef.countArticlesOfCategoryNDays("Category:Person_(L)", 7);
		//int publication = catDef.countArticlesOfCategoryNDays("Category:Publication_(L)", 7);
		int workingGroup = catDef.countArticlesOfCategoryNDays("Category:Working_Group", 7);
		catDef.countArticlesOfCategory("Category:Working_Group");//this checks all updated wrking groups
		
		ArrayList<String> datasetLinks = catDef.datasetLinks;
		//ArrayList<String> publicationLinks = catDef.publicationLinks;
		ArrayList<String> otherPageLinks = catDef.otherLinks;
		
		ArrayList<String> datasetLinksRaw = catDef.datasetLinksRaw;
		//ArrayList<String> publicationLinksRaw = catDef.publicationLinksRaw;
		ArrayList<String> otherPageLinksRaw = catDef.otherLinksRaw;
		
		ArrayList<String> mostActiveUserAndHisContribNum = catDef.getMostActiveUserAndHisContribs();
		
		ArrayList<String> revisedWorkingGroupLinks = catDef.revisedWorkingGroupLinks;
		ArrayList<String> revisedWorkingGroupLinksRaw = catDef.revisedWorkingGroupLinksRaw;
		ArrayList<Integer> revisedWorkingGroupLinksNum = catDef.revisedWorkingGroupLinksNum;
		
		HTMLVisualization view = new HTMLVisualization();
		Edit edit = new Edit();
		
		int deleteLastRevId = catDef.getLastRevisionId();
		//System.out.println("Last REVID is: " + deleteLastRevId);
		edit.undoRevisions(deleteLastRevId, false, mainbot);//remove previous newsletter
		
		
		view.newsletter(dataset, user, workingGroup, datasetLinks, datasetLinksRaw, 
				otherPageLinks, otherPageLinksRaw, 
				revisedWorkingGroupLinks, revisedWorkingGroupLinksRaw, revisedWorkingGroupLinksNum, mostActiveUserAndHisContribNum, 7);
		
		int revid = edit.edit(view, mainbot);
		//undoRevisions for testing use
//		System.out.println("THE REVID IS: " + revid);
//		Scanner scanner = new Scanner(System.in);
//		String erase = scanner.next();
//		if(!erase.isEmpty())
//		{
//			System.out.println("ENTERING IF ERASE CLAUSE!!");
//			edit.undoRevisions(revid, false, mainbot); //testing purpose to undo the edits
//			System.out.println("ERASING FINISHED!!");
//		}
		
		
	}
	

	public static void main(String[] args) throws JSONException, IOException, ParseException
	{
		NewsLetterBot mainbot = new NewsLetterBot("testBot", "testBot123");
		Utils util = new Utils();
		util.login(mainbot);
		createNewsletter(mainbot);
		
		
	}
	
}
