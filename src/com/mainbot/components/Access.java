package com.mainbot.components;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mainbot.dataobjects.Revision;
import com.mainbot.main.Bot;
import com.mainbot.utility.*;
import static com.mainbot.utility.Constants.FORMAT_AND_API;
import static com.mainbot.utility.Constants.WIKI_NAME;

public class Access {

	static ConnectionRequests conn = new ConnectionRequests();
	/*
	 * @params query Query string for the api
	 * @params connType Type of Connection. Get, POST or POST with params*/
	
	public static JSONObject makeQuery(String query, int connType){
		JSONObject result;
		switch(connType){
			case '1': return conn.doGETJSON(query);
			case '2': return conn.doPOSTJSON(query,"");
			case '3': return conn.postFuncWithParams(query, "", "token");
		}
		return null;
	}
	
	public void listAllDataSets() throws JSONException {
		JSONObject datasets = makeQuery(Constants.QUERY_ALL_DATASET, 1);
		processDatasets(datasets);
	}

	// Extract each dataset from the json object
	public void processDatasets(JSONObject datasets) throws JSONException {
		JSONArray dataArray = datasets.getJSONObject("query").getJSONArray("allpages");
		for (int i = 0; i < dataArray.length(); i++) {
			JSONObject currDataset = dataArray.getJSONObject(i);
			getLatestEdits(currDataset, 5);
		}
	}

	// Get the revision edits for a particular dataset
	public void getLatestEdits(JSONObject data, int limit) throws JSONException {
		limit = 5;
		String rvLimit = String.valueOf(limit);
		String pageId = data.get("pageid").toString();
		String editQuery = WIKI_NAME + FORMAT_AND_API
				+ "&prop=revisions&pageids=" + pageId + "&rvlimit=" + rvLimit;
		JSONArray array;
		try {
			// String encodedURL = URLEncoder.encode(editQuery, "UTF-8");
			//JSONObject latestEdits = Utils.doGETJSON(editQuery);
			JSONObject latestEdits = makeQuery(editQuery, 1);
			array = latestEdits.getJSONObject("query").getJSONObject("pages")
					.getJSONObject(pageId).getJSONArray("revisions");
			System.out.println(pageId + ":" + array.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}	
	
	public static ArrayList getRecentChanges(int limit){
		ArrayList<Revision> revList = new ArrayList<>();
		
		Constants.params.put("action", "query");
		Constants.params.put("list", "recentchanges");
		if(limit > 0)
			Constants.params.put("rclimit", String.valueOf(limit));
		Constants.params.put("continue", "");
		
		
		String listChangesQuery = WIKI_NAME + FORMAT_AND_API + "&list=recentchanges&continue&rclimit="+ limit;
		JSONArray array;
		try {
			JSONObject latestEdits = conn.doGETJSON(listChangesQuery);
			array = latestEdits.getJSONObject("query").getJSONArray("recentchanges");
			
			for(int i=0; i< array.length(); i++){
				JSONObject obj = array.getJSONObject(i);
				Revision rev = new Revision(obj.get("pageid").toString(), 
									obj.get("timestamp").toString(), 
									obj.get("revid").toString(), 
									obj.get("old_revid").toString(), 
									obj.get("type").toString());
				revList.add(rev);
			}
		return revList;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static ArrayList getChangesPastNDays(int numOfDays){
		ArrayList<Revision> revList = new ArrayList<>();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal = new GregorianCalendar();
		Date today = new Date();		
		cal.setTime(today);
		cal.add(Calendar.DAY_OF_MONTH, numOfDays*-1);
		Date from = cal.getTime();
		String end = dateFormat.format(today).replace(' ', 'T')+'Z';
		
		String start = dateFormat.format(from).replace(' ', 'T')+'Z';
		
		Constants.params.put("action", "query");
		Constants.params.put("list", "recentchanges");
		Constants.params.put("rcstart", "");
		Constants.params.put("rcend", "");
		return revList;
	}
}
