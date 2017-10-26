package com.mainbot.main;

import java.io.IOException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.json.JSONException;

import com.mainbot.bots.HistogramBot;
import com.mainbot.utility.Utils;

public class Histogram {
	public static final Logger logger = Logger.getLogger(Histogram.class);

	public static void main(String[] args) throws JSONException, IOException {
		String username = "testBot";
		String password = "testBot123";
		HistogramBot histobot = new HistogramBot(username, password);
		Utils util = new Utils();

		if (util.login(histobot)) {
			logger.info("Histogram Bot Created");
			histobot.createHistogram();

		}
	}

}
