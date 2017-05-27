package com.mainbot.main;

import com.mainbot.components.CategoryDefinition;
import com.mainbot.utility.Constants;

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
	
	public static void newDatasets(NewsLetterBot mainbot)
	{
		CategoryDefinition catDef = new CategoryDefinition();
		
	}
	
}
