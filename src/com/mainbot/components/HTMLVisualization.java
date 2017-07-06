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

import com.mainbot.dataobjects.Revision;

public class HTMLVisualization {
	String section;
	String viewText;
	String graphName;

	public void recentChangeView(ArrayList<Revision> revisionList) throws IOException 
	{
		// TODO Auto-generated method stub
		HashMap<String, Integer> histogramMap = new HashMap<>();
		int count = 1;
		String start = "", end = "";
		String result = "";
		//result += "<h1>Recent " +revisionList.size() +" changes in the wiki: </h1>\n<p>\n";
		for(Revision obj : revisionList){	
			String date = obj.getTimestampDate();
			//System.out.println("The date is: " + date);
			if(count == 1)
			{
				start = date;
			}
			else if(count == revisionList.size()) 
			{
				end = date;
			}
			histogramMap.put(date, histogramMap.getOrDefault(date, 0)+1);
			result += String.valueOf(count++) + ". ";
			result += "Article " + obj.getArticle().getName();
			result += " on " + date;
			result += " at " + obj.getTimestampTime();
			result += " by " + obj.getUser().getName() + "<br/>\n";


		}
		
		//5.23 UPDATE: changing view for changes-pastnchanges.html///////
		String htmlResult ="";
		htmlResult += "<!DOCTYPE html>";
		htmlResult += "\n<html>\n<head>";
		htmlResult += "\n<title>Histogram-Past N Days</title>";
		htmlResult += "\n<script type=\"text/javascript\" src=\"js/vis.js\"></script>";
		htmlResult += "\n<link href=\"https://cdnjs.cloudflare.com/ajax/libs/vis/4.19.1/vis.min.css\" rel=\"stylesheet\" type=\"text/css\" />";
		htmlResult += "\n</head>\n<body>\n";
		
		htmlResult += "\n<div id=\"visualization\"></div>";
		htmlResult += "\n<script type=\"text/javascript\">";
		htmlResult += "\nvar container = document.getElementById('visualization');";
		htmlResult += "\nvar items = [";
		for(String key : histogramMap.keySet())
		{
			htmlResult += "{x: '"+key+"', y: "+histogramMap.get(key)+"},";
		}
		htmlResult += "];";
		htmlResult += "\nvar dataset = new vis.DataSet(items);";
		htmlResult += "\nvar options = {start: '"+end+"',end: '"+start+"',";
		htmlResult += "style:'bar',barChart: {width:50, align:'center'},drawPoints: false};";
		htmlResult += "\nvar graph2d = new vis.Graph2d(container, dataset, options);";
		htmlResult += "\n</script>\n</body>\n</html>";
		
		String filename = "changes-pastnchanges.html";
		
		BufferedWriter writer= new BufferedWriter(new FileWriter(new File("\\Users\\mtoley\\Desktop\\ResearchBackup\\wikibot\\visualizations\\"+filename)));		
		writer.write(htmlResult);
		writer.close();
		/////////////////////////////////////////////////////////////////
		
		
		
		
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
			{
				start = date;
			}
			else if(count == revisionList.size()) 
			{
				end = date;
			}
			histogramMap.put(date, histogramMap.getOrDefault(date, 0)+1);
			count++;

		}

		String htmlResult = "";
		htmlResult += "<!DOCTYPE html>";
		htmlResult += "\n<html>\n<head>";
		htmlResult += "\n<title>Histogram-Past N Days</title>";
		htmlResult += "\n<script type=\"text/javascript\" src=\"js/vis.js\"></script>";
		htmlResult += "\n<link href=\"https://cdnjs.cloudflare.com/ajax/libs/vis/4.19.1/vis.min.css\" rel=\"stylesheet\" type=\"text/css\" />";
		htmlResult += "\n</head>\n<body>\n";

		htmlResult += "\n<div id=\"visualization\"></div>";
		htmlResult += "\n<script type=\"text/javascript\">";
		htmlResult += "\nvar container = document.getElementById('visualization');";
		htmlResult += "\nvar items = [";

		for(String key : histogramMap.keySet())
		{
			htmlResult += "{x: '"+key+"', y: "+histogramMap.get(key)+"},";
		}
		htmlResult += "];";
		htmlResult += "\nvar dataset = new vis.DataSet(items);";
		htmlResult += "\nvar options = {start: '"+end+"',end: '"+start+"',";
		htmlResult += "style:'bar',barChart: {width:50, align:'center'},drawPoints: false};";
		htmlResult += "\nvar graph2d = new vis.Graph2d(container, dataset, options);";
		htmlResult += "\n</script>\n</body>\n</html>";

		String filename = "changes-pastndays.html";
		//BufferedWriter writer= new BufferedWriter(new FileWriter(new File("/home/neha/git/wikibot/visualizations/"+filename)));
		BufferedWriter writer= new BufferedWriter(new FileWriter(new File("\\Users\\mtoley\\Desktop\\ResearchBackup\\wikibot\\visualizations\\"+filename)));		
		writer.write(htmlResult);
		writer.close();

		result += "{{#widget:Iframe";
		result += "|url=https://nehasuvarna.github.io/wikibot/visualizations/"+filename;
		//result += "|url=file:///Users/jieji/Desktop/ResearchBackup/visualizations/changes-pastndays.html";
		result += "|width=610";
		result += "|height=342";
		result += "|border=0}}";


		this.viewText = result;
		this.section = "Changes in the past 30 days";
		System.out.println(result);

	}
	
	
	
	
	//5.23 Update: Newsletter///////////////////////////////
	public void newsletter(int dataset, int user, int workingGroup, ArrayList<String> datasetLinks, 
			ArrayList<String> datasetLinksRaw,  ArrayList<String> otherLinks, ArrayList<String> otherLinksRaw, 
			ArrayList<String> revisedWorkingGroupLinks, ArrayList<String> revisedWorkingGroupLinksRaw, 
			ArrayList<Integer> revisedWorkingGroupLinksNum,ArrayList<String> mostActiveUserAndHisContribNum, int numOfDays) throws IOException
	{	
		String result = "";
		
		result += "<strong>During last 7 days: </strong><br/> " + "\n";
		result += "<strong>" + dataset + " </strong>new datasets; <br/> \n";
		result += "<strong>" + user + " </strong>new contributors; <br/>" + " \n";
		//result += "<strong>" + publication + " </strong>new publications; <br/> \n";
		result += "<strong>" + workingGroup + " </strong>new working groups were created.  <br/> \n"  ;
		
		result += "<br/>";
		
		
		String user1url, user2url, user3url, user4url, user5url;
		user1url = "http://wiki.linked.earth/User:" + mostActiveUserAndHisContribNum.get(0);
		user2url = "http://wiki.linked.earth/User:" + mostActiveUserAndHisContribNum.get(2);
		user3url = "http://wiki.linked.earth/User:" + mostActiveUserAndHisContribNum.get(4);
		user4url = "http://wiki.linked.earth/User:" + mostActiveUserAndHisContribNum.get(6);
		user5url = "http://wiki.linked.earth/User:" + mostActiveUserAndHisContribNum.get(8);
		
		result += "Congratulations to <strong>" + "<span class='plainlinks'>" + "[" + user1url + " " + mostActiveUserAndHisContribNum.get(0) + "]" + "</span>" + " </strong> for being the top contributor with <strong>" + mostActiveUserAndHisContribNum.get(1) +"</strong> contributions this week!  Congratulations also to our top contributors: <br/>";
		result += "2. <strong>" + "<span class='plainlinks'>" + "[" + user2url + " " + mostActiveUserAndHisContribNum.get(2) + "]" + "</span>"  + "</strong> " + " has <strong>" + mostActiveUserAndHisContribNum.get(3) + "</strong> contributions." + " <br/>";
		
		
		//"<span class='plainlinks'>" + "[" + updatedwg.get(i) + " " + updatedwgRaw.get(i) + "]" + "</span>" 
		
		
		result += "3. <strong>" + "<span class='plainlinks'>" + "[" + user3url + " " + mostActiveUserAndHisContribNum.get(4) + "]" + "</span>"  + "</strong> " + " has <strong>" + mostActiveUserAndHisContribNum.get(5) + "</strong> contributions." + " <br/>";
		result += "4. <strong>" + "<span class='plainlinks'>" + "[" + user4url + " " + mostActiveUserAndHisContribNum.get(6) + "]" + "</span>"  + "</strong> " + " has <strong>" + mostActiveUserAndHisContribNum.get(7) + "</strong> contributions." + " <br/>";
		result += "5. <strong>" + "<span class='plainlinks'>" + "[" + user5url + " " + mostActiveUserAndHisContribNum.get(8) + "]" + "</span>"  + "</strong> " + " has <strong>" + mostActiveUserAndHisContribNum.get(9) + "</strong> contributions." + " <br/>";
		
//		result += "The most active user during last 7 days is <strong> " + mostActiveUserAndHisContribNum.get(0) + "</strong>, who has <strong>" + mostActiveUserAndHisContribNum.get(1) + "</strong> contributions. <br/> ";
//		result += "The second most active user during last 7 days is <strong> " + mostActiveUserAndHisContribNum.get(2) + "</strong>, who has <strong>" + mostActiveUserAndHisContribNum.get(3) + "</strong> contributions. <br/> ";
//		result += "The third most active user during last 7 days is <strong> " + mostActiveUserAndHisContribNum.get(4) + "</strong>, who has <strong>" + mostActiveUserAndHisContribNum.get(5) + "</strong> contributions. <br/> ";
//		result += "The fourth most active user during last 7 days is <strong> " + mostActiveUserAndHisContribNum.get(6) + "</strong>, who has <strong>" + mostActiveUserAndHisContribNum.get(7) + "</strong> contributions. <br/> ";
//		result += "The fourth most active user during last 7 days is <strong> " + mostActiveUserAndHisContribNum.get(8) + "</strong>, who has <strong>" + mostActiveUserAndHisContribNum.get(9) + "</strong> contributions. <br/> ";

		
		result += "<br/>";
		
		
		
		ArrayList<String> updatedwg = revisedWorkingGroupLinks;
		ArrayList<String> updatedwgRaw = revisedWorkingGroupLinksRaw;
		ArrayList<Integer> updatedwgNum = revisedWorkingGroupLinksNum;
		
//WORKING GROUP NEW PAGE LINKS////////////////////////////////////////
		if(updatedwg.size() != 0 )
		{
			result += "<strong> Here are the updated working groups: </strong><br/>";
			result += "<div>";
			if(updatedwg.size() > 5)//make the page less messy
			{
				for(int i = 0; i < 5; i++)
				{	
					//<span class="plainlinks">[https://www.mediawiki.org/w/index.php?title=Help:Links&action=edit Edit this page]</span>
			
					result += "<span class='plainlinks'>" + "[" + updatedwg.get(i) + " " + updatedwgRaw.get(i) + "]" + "</span>" 
								+ " has <strong>" + revisedWorkingGroupLinksNum.get(i) + "</strong> new contributions;" +"<br/>";
				}
				result += "<strong>[[#more-wg-links|More updated working group links are at the bottom.]]</strong>";
			}
			else
			{
				for(int i = 0; i < updatedwg.size(); i++)
				{	
					//<span class="plainlinks">[https://www.mediawiki.org/w/index.php?title=Help:Links&action=edit Edit this page]</span>
			
					result += "<span class='plainlinks'>" + "[" + updatedwg.get(i) + " " + updatedwgRaw.get(i) + "]" + "</span>" 
								+ " has <strong>" + revisedWorkingGroupLinksNum.get(i) + "</strong> new contributions;" +"<br/>";
				}
			}
				
			result += "</div>";
			result += "<br/>";
		}
		
////////////////////////////////////////////////////////
		
		
		
		
		ArrayList<String> buf = datasetLinks;
		ArrayList<String> rawbuf = datasetLinksRaw;

//DATASET LINKS////////////////////////////////////////
		if(datasetLinks.size() != 0)
		{
			result += "<strong> Here are newly added datasets: </strong><br/>";
			result += "<div>";
			if(buf.size() > 5)//make the page less messy
			{
				for(int i = 0; i < 5; i++)
				{	
					//<span class="plainlinks">[https://www.mediawiki.org/w/index.php?title=Help:Links&action=edit Edit this page]</span>
			
					result += "<span class='plainlinks'>" + "[" + buf.get(i) + " " + rawbuf.get(i) + "]" + "</span>" + "<br/>";
				}
				result += "<strong>[[#more-dataset-links|More updated working group links are at the bottom.]]</strong>";
			}
			else
			{
				for(int i = 0; i < buf.size(); i++)
				{	
					//<span class="plainlinks">[https://www.mediawiki.org/w/index.php?title=Help:Links&action=edit Edit this page]</span>
			
					result += "<span class='plainlinks'>" + "[" + buf.get(i) + " " + rawbuf.get(i) + "]" + "</span>" + "<br/>";
				}
			}
			
			
			result += "</div>";
			result += "<br/>";

		}
		
////////////////////////////////////////////////////////		
		
//NEW PUBLICATION LINKS(NOT NEEDED ANYMORE)		
//		result += "<strong> Here are the links to the new publications: </strong><br/>";
//		
//		result += "<div>";
//		ArrayList<String> buf1 = publicationLinks;
//		ArrayList<String> rawbuf1 = publicationLinksRaw;
//
//		if(buf1.size() > 5)
//		{
//			for(int i = 0; i < 5; i++)
//			{
//				result += "<span class='plainlinks'>" + "[" + buf1.get(i) + " " + rawbuf1.get(i) + "]" + "</span>" + "<br/>";
//			}
//			result += "<strong>More publication links are at the bottom.</strong>";
//		}
//		else
//		{
//			for(int i = 0; i < buf1.size(); i++)
//			{
//				result += "<span class='plainlinks'>" + "[" + buf1.get(i) + " " + rawbuf1.get(i) + "]" + "</span>" + "<br/>";
//			}
//		}
//		
//
//		result += "</div>";
//		result += "<br/>";
		
		
//NEW OTHER MODIFIED PAGES/////////////////////////////////////////////	
		
		

		ArrayList<String> temp = otherLinks;
		ArrayList<String> rawtemp = otherLinksRaw;
		
		if(otherLinks.size() != 0)
		{
			result += "<strong>Here are other modified pages: </strong><br/>";
			result += "<div>";
			if(temp.size() > 5)
			{
				for(int i = 0; i < 5; i++)
				{
					result += "<span class='plainlinks'>" + "[" + temp.get(i) + " " + rawtemp.get(i) + "]" + "</span>" + "<br/>";
				}
				result += "<strong>[[#more-other-links|More modified links are at the bottom.]]</strong>";

			}
			else
			{
				for(int i = 0; i < temp.size(); i++)
				{
					result += "<span class='plainlinks'>" + "[" + temp.get(i) + " " + rawtemp.get(i) + "]" + "</span>" + "<br/>";
				}
			}

			result += "</div>";
			result += "<br/>";
		}
		

		

//////////////////////////////////////////////////////////////////
		result += "<br/>";
		result += "<br/>";
		result += "<br/>";
		result += "<br/>";
		result += "<br/>";
		result += "<br/>";
		

		if(updatedwg.size() > 5)//other working group links
		{
			result += "<strong id='more-wg-links'> More updated working group links: </strong> <br/>";
			for(int i = 5; i < updatedwg.size(); i++)
			{	
				//<span class="plainlinks">[https://www.mediawiki.org/w/index.php?title=Help:Links&action=edit Edit this page]</span>
		
				result += "<span class='plainlinks'>" + "[" + updatedwg.get(i) + " " + updatedwgRaw.get(i) + "]" + "</span>" 
						+ " has <strong>" + revisedWorkingGroupLinksNum.get(i) + "</strong> new contributions;" +"<br/>";
			}
			result += "<br/>";
		}
		
		
		if(buf.size() > 5)//other dataset links
		{
			result += "<strong id='more-dataset-links'> More dataset links: </strong> <br/>";
			for(int i = 5; i < buf.size(); i++)
			{	
				//<span class="plainlinks">[https://www.mediawiki.org/w/index.php?title=Help:Links&action=edit Edit this page]</span>
		
				result += "<span class='plainlinks'>" + "[" + buf.get(i) + " " + rawbuf.get(i) + "]" + "</span>" + "<br/>";
			}
			result += "<br/>";
		}

		
//		if(buf1.size() > 5)//PUBLICATION LINKS NOT NEEDED ANYMORE
//		{
//			result += "<strong> More pulication links:  </strong><br/>";
//			for(int i = 5; i < buf1.size(); i++)
//			{
//				result += "<span class='plainlinks'>" + "[" + buf1.get(i) + " " + rawbuf1.get(i) + "]" + "</span>" + "<br/>";
//			}
//		}


		
		if(temp.size() > 5)//other other links
		{
			result += "<strong id='more-other-links'>More modified links: </strong><br/>";
			for(int i = 5; i < temp.size(); i++)
			{
				result += "<span class='plainlinks'>" + "[" + temp.get(i) + " " + rawtemp.get(i) + "]" + "</span>" + "<br/>";
			}
			result += "<br/>";
		}
		


 
		
		this.viewText = result;
		
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy.MMM.dd HH.mmaaa");
		Calendar cal = new GregorianCalendar();
		Date today = new Date();
		String start = dateFormat.format(today);

		cal.setTime(today);
		cal.add(Calendar.DAY_OF_MONTH, -1*7);
		Date from = cal.getTime();
		String end = dateFormat.format(from);
		
		this.section = "Newsletter from <strong>" + end + "</strong> to <strong>" + start + "</strong>";

		System.out.println("Newsletter created!!");
		
	}
	////////////////////////////////////////////////////////

	
}
