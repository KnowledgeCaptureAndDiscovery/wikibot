package com.mainbot.main;

import java.io.IOException;
import java.text.ParseException;

import org.apache.log4j.Logger;
import org.json.JSONException;

import com.mainbot.bots.NewsLetterBot;
import com.mainbot.utility.Utils;


/**
 * @author neha
 *
 *Main Class to be called for news letter generation
 */
public class NewsLetter {
	public static final Logger logger = Logger.getLogger(NewsLetter.class);
	public static void main(String[] args) throws JSONException, IOException, ParseException {
		String username = "testBot";
		String password = "testBot123";
		String newsLetterPage = "Weekly_Digest";
		int numOfDays = 7;
		int revid;

		NewsLetterBot digestBot = new NewsLetterBot(username, password, newsLetterPage, numOfDays);
		Utils util = new Utils();
		
		if (util.login(digestBot)) {
			logger.info("LOGIN SUCCESS!");
			if (util.getPageId(newsLetterPage) == -1)// accessing a nonexistent page
			{
				logger.info(newsLetterPage + " does not exist. Please try again.");
			} else {
				logger.info("Updating " + newsLetterPage + "...");
				revid = digestBot.generateNewsLetter();
				logger.info("Revision ID for the newsletter: " + revid);
			}
		} else {
			logger.info("LOGIN FAILURE!");
		}

	}


}
