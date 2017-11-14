package com.mainbot.components;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.mainbot.dataobjects.Article;
import com.mainbot.dataobjects.Revision;
import com.mainbot.utility.Utils;

public class Visualization {
	String section;
	String viewText;
	String graphName;
	
	public Visualization() {
		
	}
	public Visualization(String section, String viewText) {
		super();
		this.section = section;
		this.viewText = viewText;
	}
	public static final Logger logger = Logger.getLogger(Visualization.class);

	public void recentChangeView(ArrayList<Revision> revisionList) throws IOException {
		// TODO Auto-generated method stub
		HashMap<String, Integer> histogramMap = new HashMap<>();
		int count = 1;
		String start = "", end = "";
		String result = "";
		//result += "<h1>Recent " +revisionList.size() +" changes in the wiki: </h1>\n<p>\n";
		for(Revision obj : revisionList){	
			String date = obj.getTimestampDate();
			if(count == 1)
				start = date;
			else if(count == revisionList.size()) 
				end = date;
			histogramMap.put(date, histogramMap.getOrDefault(date, 0)+1);
			result += String.valueOf(count++) + ". ";
			result += "Article " + obj.getArticle().getName();
			result += " on " + date;
			result += " at " + obj.getTimestampTime();
			result += " by " + obj.getUser().getName() + "<br/>\n";


		}
		result += "<br/><br/>";     
		this.viewText = result;
		this.section = "Recent changes in the wiki";
		System.out.println(result);
	}

	public void changesPastNDaysView(ArrayList<Revision> revisionList, int numOfDays) throws IOException {
		HashMap<String, Integer> histogramMap = new HashMap<>();
		int count = 1;
		String start = "", end = "";
		String result = "";
		//result += "<h1> Changes in the wiki for the past "+String.valueOf(numOfDays)+" days</h1>\n<p>\n";
		for(Revision obj : revisionList){	
			String date = obj.getTimestampDate();
			if(count == 1)
				start = date;
			else if(count == revisionList.size()) 
				end = date;
			histogramMap.put(date, histogramMap.getOrDefault(date, 0)+1);
			count++;

		}

		String htmlResult = "";
		htmlResult += "<!DOCTYPE html>";
		htmlResult += "\n<html>\n<head>";
		htmlResult += "\n<title>Histogram</title>";
		htmlResult += "\n<script type=\"text/javascript\" src=\"js/vis.js\"></script>";
		htmlResult += "\n<link href=\"https://cdnjs.cloudflare.com/ajax/libs/vis/4.19.1/vis.min.css\" rel=\"stylesheet\" type=\"text/css\" />";
		htmlResult += "\n</head>\n<body>\n";

		htmlResult += "\n<div id=\"visualization\"></div>";
		htmlResult += "\n<script type=\"text/javascript\">";
		htmlResult += "\nvar container = document.getElementById('visualization');";
		htmlResult += "\nvar items = [";

		for(String key : histogramMap.keySet()){
			htmlResult += "{x: '"+key+"', y: "+histogramMap.get(key)+"},";
		}
		htmlResult += "];";
		htmlResult += "\nvar dataset = new vis.DataSet(items);";
		htmlResult += "\nvar options = {start: '"+end+"',end: '"+start+"',";
		htmlResult += "style:'bar',barChart: {width:50, align:'center'},drawPoints: false};";
		htmlResult += "\nvar graph2d = new vis.Graph2d(container, dataset, options);";
		htmlResult += "\n</script>\n</body>\n</html>";

		String filename = "changes-pastndays.html";
		BufferedWriter writer= new BufferedWriter(new FileWriter(new File("/home/neha/git/wikibot/visualizations/"+filename)));
		writer.write(htmlResult);
		writer.close();

		result += "{{#widget:Iframe";
		result += "|url=https://KnowledgeCaptureAndDiscovery.github.io/wikibot/visualizations/"+filename;
		result += "|width=610";
		result += "|height=342";
		result += "|border=0}}";


		this.viewText = result;
		this.section = "Changes in the past 30 days";
		System.out.println(result);

	}
	public void display_newsletter(int numOfDays, HashMap<String, String> datasetLinks, HashMap<String, String> userLinks,
			HashMap<String, String> workingGroupLinks, HashMap<Article, Integer> subWorkingGroupLinks,
			HashMap<String, Integer> maxContributorsList) {
		
		DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
		Calendar cal = new GregorianCalendar();
		Date today = new Date();
		String start = dateFormat.format(today);

		cal.setTime(today);
		cal.add(Calendar.DAY_OF_MONTH, -1 * numOfDays);
		Date from = cal.getTime();
		String end = dateFormat.format(from);
		
		
		String result = "";
		result += "<strong>Activity from " + end + " - "+ start + "</strong><br/> " + "\n";
		result += "<strong>" + datasetLinks.size() + " </strong>new datasets. <br/> \n";
		result += "<strong>" + userLinks.size() + " </strong>new contributors. <br/>" + " \n";
		result += "<strong>" + workingGroupLinks.size() + " </strong>new working groups were created.  <br/> \n";
		result += "<br/>";

		
		
		int index = 0;
		for (Entry<String, Integer> entry : maxContributorsList.entrySet()) {
			if (entry.getValue() == 0 || index == 5) {
				break;
			}
			String url = Utils.generateUrl("User:"+entry.getKey());

			if (++index == 1) {
				result += "<strong>Congratulations to " + "<span class='plainlinks'>" + "[" + url + " " + entry.getKey()
						+ "]" + "</span>" + " for being the top contributor with " + entry.getValue()
						+ " contributions!</strong><br/><br/>";
			} else {
				if(index == 2){
					result +=  "<strong>Other top contributors: </strong><br/>";
				}
				result += "* <strong>" + "<span class='plainlinks'>" + "[" + url + " "
						+ entry.getKey() + "]" + "</span>" + "</strong> " + " has <strong>" + entry.getValue()
						+ "</strong> contributions." + " <br/>";
			}
		}
		result += "<br/>";
		
		
		if (datasetLinks.size() > 0) {
			result += "<strong>Newly added datasets:</strong><br/>";
			for (Entry<String, String> entry : datasetLinks.entrySet()) {
				result += "<span class='plainlinks'>" + "[" + entry.getValue() + " " + entry.getKey() + "]" + "</span>";
				result += "<br/>";
			}
			result += "<br/>";
		}

		if (userLinks.size() > 0) {
			result += "<strong>Newly added users:</strong><br/>";
			for (Entry<String, String> entry : userLinks.entrySet()) {
				result += "<span class='plainlinks'>" + "[" + entry.getValue() + " " + entry.getKey() + "]" + "</span>";
				result += "<br/>";
			}
			result += "<br/>";
		}

		if (workingGroupLinks.size() > 0) {
			result += "<strong>Newly added working groups:</strong><br/>";
			for (Entry<String, String> entry : workingGroupLinks.entrySet()) {
				result += "<span class='plainlinks'>" + "[" + entry.getValue() + " " + entry.getKey() + "]" + "</span>";
				result += "<br/>";
			}
			result += "<br/>";
		}

		boolean printHeader = false;
		if (subWorkingGroupLinks.size() > 0) {
			for (Entry<Article, Integer> entry : subWorkingGroupLinks.entrySet()) {
				if (entry.getValue() > 0) {
					if (!printHeader) {
						result += "<strong>Updated working groups:</strong><br/>";
						printHeader = true;
					}
					result += "<span class='plainlinks'>" + "[" + entry.getKey().getUrl() + " "
							+ entry.getKey().getName() + "]";
					result += " has " + entry.getValue() + " new contributions.</span>";
					result += "<br/>";
				}
			}
			result += "<br/>";
		}

		this.viewText = result;		

		this.section = "Weekly Digest (" + end + " - " + start + ")";

		logger.info("Newsletter view object created.");
	}
	public void displayFigureText(HashMap<String, Integer> figureMap) {
		// TODO Auto-generated method stub
		
		StringBuilder result = new StringBuilder();
		for (Entry<String, Integer> entry : figureMap.entrySet()){
			result.append("'''" + entry.getKey() + "'''").append(" : ");
			result.append(entry.getValue()).append(" || ");
		}
		
		System.out.println(result.toString());
		
		this.viewText = result.toString();
		this.section = "'''Linked Earth Totals'''";
		logger.info("Figure section data created");
	}

}
