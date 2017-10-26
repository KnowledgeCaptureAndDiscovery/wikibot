package com.mainbot.bots;

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

/**
 * @author neha
 *
 *         NewsLetterBot is a child class for Bot to update wiki newsletter in a
 *         scheduled time.
 *
 * @member numOfDays Number of days the newsletter has to read contents for.
 * @member newsLetterPage Title of the page to be updated with the newsletter.
 */
public class NewsLetterBot extends Bot {

	int numOfDays;
	String newsLetterPage;
	public final Logger logger = Logger.getLogger(NewsLetterBot.class);

	public NewsLetterBot(String username, String password, String newsLetterPage, int numOfDays) {
		super(username, password);
		this.numOfDays = numOfDays;
		this.newsLetterPage = newsLetterPage;
	}

	NewsLetterBot(String username, String password) {
		super(username, password);
	}

	/**
	 * @throws JSONException
	 * @throws IOException
	 * 
	 * @return Revision ID for the newsletter update.
	 */
	public int generateNewsLetter() throws JSONException, IOException {
		CategoryDefinition catDef = new CategoryDefinition();
		HTMLVisualization view = new HTMLVisualization();
		Edit edit = new Edit();
		int deleteLastRevId, revid = 0;

		HashMap<String, String> datasetLinks = catDef.getAdditionToCategory("Category:Dataset_(L)", this.numOfDays);
		HashMap<String, String> userLinks = catDef.getAdditionToCategory("Category:Person_(L)", this.numOfDays);
		HashMap<String, String> workingGroupLinks = catDef.getAdditionToCategory("Category:Working_Group",this.numOfDays);
		HashMap<Article, Integer> subWorkingGroupLinks = catDef.getWGContributions(this.numOfDays);
		HashMap<String, Integer> maxContributorsList = catDef.getMaxContibutors(this.numOfDays);

		/*-----Remove previous Newsletter----*/
//		deleteLastRevId = catDef.getLastRevisionId(this.newsLetterPage);
//		edit.undoRevisions(deleteLastRevId, false, this, this.newsLetterPage);
//
//		/*-----Write newsletter in the newsletter page----*/
//		view.display_newsletter(this.numOfDays, datasetLinks, userLinks, workingGroupLinks, subWorkingGroupLinks,maxContributorsList);
//		revid = edit.edit(view, this, this.newsLetterPage);
//		
		return revid;
	}

	public static void createNewsletter(NewsLetterBot mainbot, String whichPage)
			throws JSONException, IOException, ParseException {
		CategoryDefinition catDef = new CategoryDefinition();

		int dataset = catDef.countArticlesOfCategoryNDays("Category:Dataset_(L)", 7);
		int user = catDef.countArticlesOfCategoryNDays("Category:Person_(L)", 7);
		// int publication =
		// catDef.countArticlesOfCategoryNDays("Category:Publication_(L)", 7);
		int workingGroup = catDef.countArticlesOfCategoryNDays("Category:Working_Group", 7);
		catDef.countArticlesOfCategory("Category:Working_Group");// this checks
		// all
		// updated
		// wrking
		// groups

		ArrayList<String> datasetLinks = catDef.datasetLinks;
		// ArrayList<String> publicationLinks = catDef.publicationLinks;
		ArrayList<String> otherPageLinks = catDef.otherLinks;

		ArrayList<String> datasetLinksRaw = catDef.datasetLinksRaw;
		// ArrayList<String> publicationLinksRaw = catDef.publicationLinksRaw;
		ArrayList<String> otherPageLinksRaw = catDef.otherLinksRaw;

		ArrayList<String> mostActiveUserAndHisContribNum = catDef.getMostActiveUserAndHisContribs();

		ArrayList<String> revisedWorkingGroupLinks = catDef.revisedWorkingGroupLinks;
		ArrayList<String> revisedWorkingGroupLinksRaw = catDef.revisedWorkingGroupLinksRaw;
		ArrayList<Integer> revisedWorkingGroupLinksNum = catDef.revisedWorkingGroupLinksNum;

		HTMLVisualization view = new HTMLVisualization();
		Edit edit = new Edit();

		int deleteLastRevId = catDef.getLastRevisionId(whichPage);
		// System.out.println("Last REVID is: " + deleteLastRevId);
		edit.undoRevisions(deleteLastRevId, false, mainbot, whichPage);// remove
		// previous
		// newsletter

		view.newsletter(dataset, user, workingGroup, datasetLinks, datasetLinksRaw, otherPageLinks, otherPageLinksRaw,
				revisedWorkingGroupLinks, revisedWorkingGroupLinksRaw, revisedWorkingGroupLinksNum,
				mostActiveUserAndHisContribNum, 7);

		int revid = edit.edit(view, mainbot, whichPage);
		// undoRevisions for testing use
		// System.out.println("THE REVID IS: " + revid);
		// Scanner scanner = new Scanner(System.in);
		// String erase = scanner.next();
		// if(!erase.isEmpty())
		// {
		// System.out.println("ENTERING IF ERASE CLAUSE!!");
		// edit.undoRevisions(revid, false, mainbot); //testing purpose to undo
		// the edits
		// System.out.println("ERASING FINISHED!!");
		// }

	}

}
