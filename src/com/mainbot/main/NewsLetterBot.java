package com.mainbot.main;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.mainbot.components.CategoryDefinition;
import com.mainbot.components.Edit;
import com.mainbot.components.HTMLVisualization;
import com.mainbot.dataobjects.Article;
import com.mainbot.dataobjects.Revision;
import com.mainbot.utility.ConnectionRequests;
import com.mainbot.utility.Constants;
import com.mainbot.utility.Utils;

public class NewsLetterBot extends Bot{
	
	String sessionID;
	String username;
	String password;

	/*Bot for creating a newsletter from last week on the wiki.
	 * On the page /newsletter
	 * */
	public static final Logger logger = Logger.getLogger(NewsLetterBot.class);
	NewsLetterBot(String username) 
	{
		super(username);
	}
	
	NewsLetterBot(String username, String password) 
	{
		super(username, password);
	}
	

	
	public static void createNewsletter(Bot mainbot, String whichPage) throws JSONException, IOException, ParseException
	{
		System.out.println("inside news letter function");
		int numOfDays = 100;
		CategoryDefinition catDef = new CategoryDefinition();
		
/*		int dataset = catDef.countArticlesOfCategoryNDays("Category:Dataset_(L)", numOfDays);
		int user = catDef.countArticlesOfCategoryNDays("Category:Person_(L)", numOfDays);
*/		
		
		
		
		HashMap<String, String> datasetLinks = catDef.getChangesInCategory("Category:Dataset_(L)", numOfDays);
		/*int dataset = datasetLinks.size();
		for(Entry<String, String> entry : datasetLinks.entrySet()){
			System.out.println(entry.getKey()+ " " + entry.getValue());
		}*/
		
		HashMap<String, String> userLinks = catDef.getChangesInCategory("Category:Person_(L)", numOfDays);
		/*int user = userLinks.size();
		for(Entry<String, String> entry : userLinks.entrySet()){
			System.out.println(entry.getKey()+ " " + entry.getValue());
		}*/
		
		HashMap<String, String> workingGroupLinks = catDef.getChangesInCategory("Category:Working_Group", numOfDays);
		/*int workingGroup = workingGroupLinks.size();
		for(Entry<String, String> entry : workingGroupLinks.entrySet()){
			System.out.println(entry.getKey()+ " " + entry.getValue());
		}*/
		
		HashMap<Article, Integer> subWorkingGroupLinks = catDef.getWGContributions(numOfDays);
		/*int subWorkingGroup = subWorkingGroupLinks.size();
		for(Entry<Article, Integer> entry : subWorkingGroupLinks.entrySet()){
			System.out.println(entry.getKey().getName()+ " " + entry.getKey().getUrl()+ " - " + entry.getValue());
		}*/
		
		catDef.getMaxContibutors(numOfDays);
		
		
		//int publication = catDef.countArticlesOfCategoryNDays("Category:Publication_(L)", numOfdays);
//		int workingGroup = catDef.countArticlesOfCategoryNDays("Category:Working_Group", numOfDays);
		catDef.countArticlesOfCategory("Category:Working_Group");//this checks all updated working groups
		
//		ArrayList<String> datasetLinks = catDef.datasetLinks;
		//ArrayList<String> publicationLinks = catDef.publicationLinks;
		ArrayList<String> otherPageLinks = catDef.otherLinks;
		
		ArrayList<String> datasetLinksRaw = catDef.datasetLinksRaw;
		//ArrayList<String> publicationLinksRaw = catDef.publicationLinksRaw;
		ArrayList<String> otherPageLinksRaw = catDef.otherLinksRaw;
		
		ArrayList<String> mostActiveUserAndHisContribNum = catDef.getMostActiveUserAndHisContribs();
		
		ArrayList<String> revisedWorkingGroupLinks = catDef.revisedWorkingGroupLinks;
		ArrayList<String> revisedWorkingGroupLinksRaw = catDef.revisedWorkingGroupLinksRaw;
		ArrayList<Integer> revisedWorkingGroupLinksNum = catDef.revisedWorkingGroupLinksNum;
		
		System.out.println(revisedWorkingGroupLinks.size());
		
		HTMLVisualization view = new HTMLVisualization();
		Edit edit = new Edit();
		
		int deleteLastRevId = catDef.getLastRevisionId(whichPage);
		edit.undoRevisions(deleteLastRevId, false, mainbot, whichPage);//remove previous newsletter
		
		view.newsletter_2(numOfDays, datasetLinks, userLinks, workingGroupLinks, subWorkingGroupLinks);
		int revid = edit.edit(view, mainbot, whichPage );
		/*view.newsletter(dataset, user, workingGroup, datasetLinks, datasetLinksRaw, 
				otherPageLinks, otherPageLinksRaw, 
				revisedWorkingGroupLinks, revisedWorkingGroupLinksRaw, revisedWorkingGroupLinksNum, mostActiveUserAndHisContribNum, numOfDays);
		
		int revid = edit.edit(view, mainbot, whichPage );
	*/
		
		//undoRevisions for testing use
/*		System.out.println("THE REVID IS: " + revid);
		Scanner scanner = new Scanner(System.in);
		String erase = scanner.next();
		if(!erase.isEmpty())
		{
			System.out.println("ENTERING IF ERASE CLAUSE!!");
			edit.undoRevisions(revid, false, mainbot, whichPage); //testing purpose to undo the edits
			System.out.println("ERASING FINISHED!!");
		}
*/		
		
	}
	
    public static int getPageId(String whichPage) throws JSONException
    {
    	Constants.params.put("action", "query");
    	Constants.params.put("titles", whichPage);
    	Utils util = new Utils();
		String url = util.queryFormulation();
    	JSONObject getPageinfo = ConnectionRequests.doGETJSON(url);
    	JSONObject pages = getPageinfo.getJSONObject("query").getJSONObject("pages");
    	String pageid = (String) pages.keys().next();//get the first key

		return Integer.parseInt(pageid);
    }

	public static void main(String[] args) throws JSONException, IOException, ParseException
	{
		//input format: username, password, target page		
				
//		if(args.length != 3)
//		{
//			logger.info("Input format: username, password, target page.");
//			System.exit(0);
//		}
//		else
//		{
			Bot mainbot = new NewsLetterBot("testBot", "testBot123");
			Utils util = new Utils();
			util.login(mainbot);
			
			if(util.login(mainbot) == true)
			{
				String whichPage = "Test";
				if(getPageId(whichPage) == -1 )//accessing a nonexistent page
				{
					logger.info("This page does not exist. Please try again.");
					System.exit(0);
				}
				else
				{
					System.out.println("EDITING " + whichPage + "...");
					createNewsletter(mainbot, whichPage);
				}
			}
			else
			{
				logger.info("Exiting program due to login failure.");
				System.exit(0);
			}
			
			


		//}
		
		
	}
	
}
