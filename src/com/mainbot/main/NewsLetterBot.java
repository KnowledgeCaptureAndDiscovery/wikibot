package com.mainbot.main;

import java.io.UnsupportedEncodingException;

import org.json.JSONException;

import com.mainbot.components.CategoryDefinition;
import com.mainbot.utility.Constants;
import com.mainbot.utility.Utils;

public class NewsLetterBot extends Bot{
	
	String sessionID;
	String username;
	String password;

	/*BOT FOR CREATING A Newsletter from the last week on the wiki.
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
	
	public static int newDatasets(NewsLetterBot mainbot) throws UnsupportedEncodingException, JSONException
	{
		CategoryDefinition catDef = new CategoryDefinition();
		int newDatasets = catDef.countArticlesOfCategoryNDays("Category:Dataset_(L)", 7);
		
		return newDatasets;
	}
	
	public static int newUsers(NewsLetterBot mainbot) throws UnsupportedEncodingException, JSONException
	{
		CategoryDefinition catDef = new CategoryDefinition();
		int newUsers = catDef.countArticlesOfCategoryNDays("Category:Person_(L)", 7);
		
		return newUsers;
	}
	
	public static void main(String[] args) throws JSONException, UnsupportedEncodingException
	{
		NewsLetterBot mainbot = new NewsLetterBot("testBot", "testBot123");
		Utils util = new Utils();
		util.login(mainbot);
		
		int dataset = newDatasets(mainbot);
		System.out.println("There were " + dataset + " new datasets created during last 7 days.");
		
		int user = newUsers(mainbot);
		System.out.println("There were " + user + " new users created during last 7 days.");
	}
	
}
