package com.mainbot.main;

import java.io.UnsupportedEncodingException;

import org.apache.log4j.Logger;
import org.json.JSONException;

import com.mainbot.bots.FigureBot;
import com.mainbot.bots.NewsLetterBot;
import com.mainbot.utility.Utils;

public class Figure {
	public static final Logger logger = Logger.getLogger(Figure.class);

	public static void main(String[] args) throws JSONException, UnsupportedEncodingException {
		int revid = 0;
		String username = "testBot";
		String password = "testBot123";
		String figPage = "Main_Page"; // TODO: Main page
		
		FigureBot figBot = new FigureBot(username, password, figPage);
		Utils util = new Utils();

		if (util.login(figBot)) {
			logger.info("Figbot - Login Success!");
			if (util.getPageId(figPage) == -1) {
				logger.info(figPage + " does not exist. Please try again.");
			} else {
				logger.info("Updating " + figPage + " with latest wiki figures...");
				revid = figBot.getAllFigures();
				logger.info("Revision ID for the lates figure update: " + revid);
			}
		} else {
			logger.info("Figbot - Login Failure!");
		}
	}
}
