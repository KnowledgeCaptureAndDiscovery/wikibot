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
import com.mainbot.components.Visualization;
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
		Visualization view = new Visualization();
		Edit edit = new Edit();
		int deleteLastRevId, revid;

		HashMap<String, String> datasetLinks = catDef.getAdditionToCategory("Category:Dataset_(L)", this.numOfDays);
		HashMap<String, String> userLinks = catDef.getAdditionToCategory("Category:Person_(L)", this.numOfDays);
		HashMap<String, String> workingGroupLinks = catDef.getAdditionToCategory("Category:Working_Group",this.numOfDays);
		HashMap<Article, Integer> subWorkingGroupLinks = catDef.getWGContributions(this.numOfDays);
		HashMap<String, Integer> maxContributorsList = catDef.getMaxContibutors(this.numOfDays);

		/*-----Remove previous Newsletter----*/
		deleteLastRevId = catDef.getLastRevisionId(this.newsLetterPage);
		edit.undoRevisions(deleteLastRevId, false, this, this.newsLetterPage);

		/*-----Write newsletter in the newsletter page----*/
		view.display_newsletter(this.numOfDays, datasetLinks, userLinks, workingGroupLinks, subWorkingGroupLinks,maxContributorsList);
		revid = edit.edit(view, this, this.newsLetterPage);
		
		return revid;
	}

}
