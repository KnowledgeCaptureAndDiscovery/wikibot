package com.mainbot.components;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.mainbot.dataobjects.Revision;

public class Visualization {
	String section;
	String viewText;
	String graphName;

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
		result += "|url=https://nehasuvarna.github.io/wikibot/visualizations/"+filename;
		result += "|width=610";
		result += "|height=342";
		result += "|border=0}}";


		this.viewText = result;
		this.section = "Changes in the past 30 days";
		System.out.println(result);

	}
}
