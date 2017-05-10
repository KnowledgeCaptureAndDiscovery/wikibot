package com.mainbot.components;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
	static Utils util = new Utils();
	
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

	public static ArrayList getRecentChanges(int limit) throws UnsupportedEncodingException{
		ArrayList<Revision> revList = new ArrayList<>();

		Constants.params.put("action", "query");
		Constants.params.put("list", "recentchanges");
		Constants.params.put("rcprop", URLEncoder.encode("title|ids|sizes|flags|user|timestamp", "UTF-8"));
		if(limit > 0)
			Constants.params.put("rclimit", String.valueOf(limit));


		String listChangesQuery = Utils.queryFormulation();
		JSONArray array;
		try {
			JSONObject latestEdits = conn.doGETJSON(listChangesQuery);
			array = latestEdits.getJSONObject("query").getJSONArray("recentchanges");

			for(int i=0; i< array.length(); i++){
				JSONObject obj = array.getJSONObject(i);
				int pageid = obj.getInt("pageid");
				int revid = obj.getInt("revid");
				int old_revid = obj.getInt("old_revid");
				String title = obj.getString("title");
				String user = obj.getString("user");
				String timestamp = obj.getString("timestamp");
				String type = obj.getString("type");	


				Revision rev = new Revision(pageid, title, user, timestamp, revid, old_revid, type);
				revList.add(rev);
			}
			return revList;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public static ArrayList getChangesPastNDays(int numOfDays, String categoryName) throws JSONException, UnsupportedEncodingException{
		ArrayList<Revision> revList = new ArrayList<>();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal = new GregorianCalendar();
		Date today = new Date();
		String start = dateFormat.format(today).replace(' ', 'T')+'Z';
		cal.setTime(today);
		cal.add(Calendar.DAY_OF_MONTH, -1*numOfDays);
		Date from = cal.getTime();
		String end = dateFormat.format(from).replace(' ', 'T')+'Z';

		Constants.params.put("action", "query");
		Constants.params.put("list", "recentchanges");
		Constants.params.put("rcprop", URLEncoder.encode("title|ids|sizes|flags|user|timestamp","UTF-8"));
		Constants.params.put("rcstart", start);
		Constants.params.put("rcend", end);



		String listChangesQuery = Utils.queryFormulation();
		System.out.println(listChangesQuery);
		JSONArray array;
		JSONObject latestEdits = conn.doGETJSON(listChangesQuery);
		ArrayList<Integer> articleIDList = new ArrayList<>(); 
		if(categoryName != ""){
			articleIDList = getArticlesFromCategory(categoryName);
		}
		
		while(util.hasNext(latestEdits)){
			array = latestEdits.getJSONObject("query").getJSONArray("recentchanges");
			for(int i=0; i< array.length(); i++){
				JSONObject obj = array.getJSONObject(i);
				int pageid = obj.getInt("pageid");
				if(articleIDList.size() > 0 && !articleIDList.contains(pageid)) continue;
				int revid = obj.getInt("revid");
				int old_revid = obj.getInt("old_revid");
				String title = obj.getString("title");
				String user = obj.getString("user");
				String timestamp = obj.getString("timestamp");
				String type = obj.getString("type");	


				Revision rev = new Revision(pageid, title, user, timestamp, revid, old_revid, type);
				System.out.println(rev.getArticle().getName());
				revList.add(rev);
			}
			//prepare next batch
			String cont = URLEncoder.encode(latestEdits.getJSONObject("continue").getString("rccontinue"), "UTF-8");
			latestEdits = conn.doGETJSON(listChangesQuery+"&rccontinue="+cont);
		}
		System.out.println(revList.size());

		return revList;

	}

	public static ArrayList getArticlesFromCategory(String categoryName){
		ArrayList<Integer> articleIDList = new ArrayList<>();
		Constants.params.put("action", "query");
		Constants.params.put("list", "categorymembers");
		Constants.params.put("cmtitle", categoryName);
		String articlesFromCatQuery = Utils.queryFormulation();
		JSONObject articlesFromCat = conn.doGETJSON(articlesFromCatQuery);
		JSONArray array;
		try {
			array = articlesFromCat.getJSONObject("query").getJSONArray("categorymembers");
			for(int i=0; i< array.length(); i++){
				JSONObject obj = array.getJSONObject(i);
				articleIDList.add(obj.getInt("pageid"));
			}
			return articleIDList;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	
	}


}
