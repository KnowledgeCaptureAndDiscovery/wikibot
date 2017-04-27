package com.mainbot.components;

import java.util.ArrayList;

import com.mainbot.dataobjects.Revision;

public class Visualization {
	String section;
	String viewText;
	
	public void recentChangeView(ArrayList<Revision> revisionList) {
		// TODO Auto-generated method stub
		int count = 1;
        String result = "<h1>Recent " +revisionList.size() +" changes in the wiki: </h1>\n<p>\n";
        for(Revision obj : revisionList){	        
	        result += String.valueOf(count++) + ". ";
	        result += "Article " + obj.getPageid();
	        result += " on " + obj.getTimestampDate();
	        result += " at " + obj.getTimestampTime() + "<br/>\n";
        }
        result += "</p>";
        this.viewText = result;
        this.section = "histogram";
        System.out.println(result);
	}

}
