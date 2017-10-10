package com.mainbot.components;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mainbot.dataobjects.Article;
import com.mainbot.utility.ConnectionRequests;
import com.mainbot.utility.Utils;
import com.sun.javafx.collections.MappingChange.Map;

/**
 * @author neha
 *
 */
public class CategoryDefinition {
	public static final String mainUrl = "http://wiki.linked.earth/";
	public static final String queryCategories = "http://wiki.linked.earth/wiki/api.php?action=query&cmtype=subcat&list=categorymembers&cmlimit=100&format=json&cmtitle=";
	public static final String queryCategoryDef = "http://wiki.linked.earth/wiki/api.php?&action=parse&prop=text&section=1&format=json&page=";
	public static final String queryCategoryDefAllText = "http://wiki.linked.earth/wiki/api.php?&action=parse&prop=text&format=json&page=";

	public static ArrayList<String> datasetLinks = new ArrayList<String>();// these
																			// three
																			// arrays
																			// store
																			// the
																			// whole
																			// link
	public static ArrayList<String> publicationLinks = new ArrayList<String>();// Ex:
																				// http://wiki.linked.earth/M35003-4.Ruehlemann.1999
	public static ArrayList<String> otherLinks = new ArrayList<String>();

	public static ArrayList<String> datasetLinksRaw = new ArrayList<String>();// these
																				// three
																				// arrays
																				// just
																				// store
																				// the
																				// name
																				// of
																				// a
																				// specific
																				// webpage
	public static ArrayList<String> publicationLinksRaw = new ArrayList<String>();// Ex:M35003-4.Ruehlemann.1999
	public static ArrayList<String> otherLinksRaw = new ArrayList<String>();

	public static ArrayList<String> revisedWorkingGroupLinks = new ArrayList<String>();
	public static ArrayList<String> revisedWorkingGroupLinksRaw = new ArrayList<String>();
	public static ArrayList<Integer> revisedWorkingGroupLinksNum = new ArrayList<Integer>();

	private static void listSubCategories(String categoryTitle) throws JSONException {
		// initial query
		JSONObject art = ConnectionRequests.doGETJSON(queryCategories + categoryTitle.replace(" ", "_"));
		// retrieve categories from json
		JSONArray categories = art.getJSONObject("query").getJSONArray("categorymembers");
		// for (Object cat:categories){
		for (int i = 0; i < categories.length(); i++) {
			JSONObject cat = categories.getJSONObject(i);
			String t = cat.getString("title").replace(" ", "_");
			// System.out.println(queryCategoryDef+t);
			// System.out.println(t);
			try {
				JSONObject def = ConnectionRequests.doGETJSON(queryCategoryDef + t);
				// System.out.println(def.getJSONObject("parse").getJSONObject("text").getString("*"));
			} catch (Exception e) {
				// no section 1 available
				JSONObject def = ConnectionRequests.doGETJSON(queryCategoryDefAllText + t);
				// System.out.println(def.getJSONObject("parse").getJSONObject("text").getString("*"));
			}
			listSubCategories(t);
		}
		// recursively call method

	}

	private static boolean hasNext(JSONObject o) {
		try {
			o.getJSONObject("query-continue");
			return true;
		} catch (JSONException e) {
			return false;
		}
	}

	private static int countMembers(String category) throws UnsupportedEncodingException, JSONException {
		int count = 0;

		String c = category.replace(" ", "_");
		// System.out.println(c);
		JSONObject art = ConnectionRequests
				.doGETJSON("http://wiki.linked.earth/wiki/api.php?action=query&list=categorymembers" + "&cmprop="
						+ URLEncoder.encode("ids|title|timestamp|type", "UTF-8")
						+ "&cmlimit=500&format=json&rawcontinue&cmtitle=" + c);
		// System.out.println(art);
		while (hasNext(art)) {
			// prepare next batch
			count += 500;
			String cont = URLEncoder.encode(
					art.getJSONObject("query-continue").getJSONObject("categorymembers").getString("cmcontinue"),
					"UTF-8");
			// System.out.println(cont);
			art = ConnectionRequests.doGETJSON(
					"http://wiki.linked.earth/wiki/api.php?action=query&list=categorymembers&cmlimit=500&format=json"
							+ "&cmprop=" + URLEncoder.encode("ids|title|timestamp|type", "UTF-8") + "&cmtitle=" + c
							+ "&rawcontinue&cmcontinue=" + cont);
			// System.out.println("Pringting art: " + art.toString());

		}
		// count the rest
		count += art.getJSONObject("query").getJSONArray("categorymembers").length();
		return count;
	}

	

	// extended from the above countMembers function
	// this function can count all the category members which has been active
	// during a given time period
	private static int countMembersNDays(String category, int numOfDays)
			throws UnsupportedEncodingException, JSONException {
		// get start/end date//////////////////////////////
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal = new GregorianCalendar();
		Date today = new Date();
		String start = dateFormat.format(today).replace(' ', 'T') + 'Z';

		// System.out.println("START DATE:" + start);

		cal.setTime(today);
		cal.add(Calendar.DAY_OF_MONTH, -1 * numOfDays);
		Date from = cal.getTime();
		String end = dateFormat.format(from).replace(' ', 'T') + 'Z';

		// System.out.println("END DATE:" + end);
		//////////////////////////////////////////////////

		int count = 0;

		String c = category.replace(" ", "_");
		JSONObject art = ConnectionRequests.doGETJSON(
				"http://wiki.linked.earth/wiki/api.php?action=query&list=categorymembers&cmlimit=500&cmsort=timestamp&cmdir=desc&cmprop="
						+ URLEncoder.encode("ids|title|timestamp|type", "UTF-8") + "&cmstart=" + start + "&cmend=" + end
						+ "&format=json&cmtitle=" + c);

		// System.out.println(art);

		while (hasNext(art)) {
			// prepare next batch
			count += 500;
			String cont = URLEncoder.encode(
					art.getJSONObject("query-continue").getJSONObject("categorymembers").getString("cmcontinue"),
					"UTF-8");
			// System.out.println(cont);

			art = ConnectionRequests.doGETJSON(
					"http://wiki.linked.earth/wiki/api.php?action=query&list=categorymembers&cmlimit=500&cmsort=timestamp&cmdir=desc&cmprop="
							+ URLEncoder.encode("ids|title|timestamp|type", "UTF-8") + "&cmstart=" + start + "&cmend="
							+ end + "&format=json&cmtitle=" + c + "&rawcontinue&cmcontinue=" + cont);

			// System.out.println(art);

		}
		// count the rest

		count += art.getJSONObject("query").getJSONArray("categorymembers").length();

		// adding all the links to corresponding arraylist
		for (int i = 0; i < art.getJSONObject("query").getJSONArray("categorymembers").length(); i++) {
			// System.out.println("This array includes: TITLE = " +
			// art.getJSONObject("query").getJSONArray("categorymembers").getJSONObject(i).getString("title")
			// + " TIMESTAMP = " +
			// art.getJSONObject("query").getJSONArray("categorymembers").getJSONObject(i).getString("timestamp")
			// + " LINK = " + "http://wiki.linked.earth/" +
			// art.getJSONObject("query").getJSONArray("categorymembers").getJSONObject(i).getString("title").replace("
			// ", "_"));

			if (category.equals("Category:Dataset_(L)")) {
				datasetLinksRaw.add(art.getJSONObject("query").getJSONArray("categorymembers").getJSONObject(i)
						.getString("title").replace(" ", "_"));
				datasetLinks.add("http://wiki.linked.earth/" + art.getJSONObject("query")
						.getJSONArray("categorymembers").getJSONObject(i).getString("title").replace(" ", "_"));
			} else if (category.equals("Category:Publication_(L)")) {
				publicationLinksRaw.add(art.getJSONObject("query").getJSONArray("categorymembers").getJSONObject(i)
						.getString("title").replace(" ", "_"));
				publicationLinks.add("http://wiki.linked.earth/" + art.getJSONObject("query")
						.getJSONArray("categorymembers").getJSONObject(i).getString("title").replace(" ", "_"));
			} else {
				otherLinksRaw.add(art.getJSONObject("query").getJSONArray("categorymembers").getJSONObject(i)
						.getString("title").replace(" ", "_"));
				otherLinks.add("http://wiki.linked.earth/" + art.getJSONObject("query").getJSONArray("categorymembers")
						.getJSONObject(i).getString("title").replace(" ", "_"));
			}

		}

		return count;
	}

	public static int countArticlesOfCategory(String categoryTitle)
			throws UnsupportedEncodingException, JSONException, ParseException {
		// initial query
		String query = queryCategories + categoryTitle.replace(" ", "_");
		JSONObject art = ConnectionRequests.doGETJSON(query);
		// retrieve categories from json
		int total = countMembers(categoryTitle);
		JSONArray categories = art.getJSONObject("query").getJSONArray("categorymembers");

		System.out.println(categories);

		if (categoryTitle.equals("Category:Working_Group")) {
			for (int i = 0; i < categories.length(); i++) {
				JSONObject cat = categories.getJSONObject(i);
				String t = cat.getString("title").replace(" ", "_");
				int pageid = cat.getInt("pageid");
				// System.out.println("title is: " + t + " pageid is " +
				// pageid);

				if (checkIfOneWorkingGroupIsUpdated(t, pageid) > 0) {
					revisedWorkingGroupLinks.add("http://wiki.linked.earth/" + t);
					revisedWorkingGroupLinksRaw.add(t.substring(9, t.length()));// cut
																				// out
																				// the
																				// initial
																				// "Category:"
					revisedWorkingGroupLinksNum.add(checkIfOneWorkingGroupIsUpdated(t, pageid));
				}

			}

			int n = revisedWorkingGroupLinks.size();// total # of working groups
			int temp = 0;
			String buf, bufRaw;
			for (int i = 0; i < n; i++)// rank working groups by updates
			{
				for (int j = 1; j < (n - i); j++) {
					if (revisedWorkingGroupLinksNum.get(j - 1) < revisedWorkingGroupLinksNum.get(j)) {
						temp = revisedWorkingGroupLinksNum.get(j - 1);
						revisedWorkingGroupLinksNum.set(j - 1, revisedWorkingGroupLinksNum.get(j));
						revisedWorkingGroupLinksNum.set(j, temp);

						buf = revisedWorkingGroupLinks.get(j - 1);
						revisedWorkingGroupLinks.set(j - 1, revisedWorkingGroupLinks.get(j));
						revisedWorkingGroupLinks.set(j, buf);

						bufRaw = revisedWorkingGroupLinksRaw.get(j - 1);
						revisedWorkingGroupLinksRaw.set(j - 1, revisedWorkingGroupLinksRaw.get(j));
						revisedWorkingGroupLinksRaw.set(j, bufRaw);
					}
				}
			}
			System.out.println("links-->" + revisedWorkingGroupLinks.toString());
			System.out.println("n" + revisedWorkingGroupLinks.size());

		}

		// have to stop recursion for working groups since some of the working
		// groups are subgroup of itself
		// Ex. floods working group has a subgroup called floods working group
		// this will cause infinite loop
		if (categoryTitle.equals("Category:Working_Group") == false) {

			for (int i = 0; i < categories.length(); i++) {
				JSONObject cat = categories.getJSONObject(i);
				String t = cat.getString("title").replace(" ", "_");
				total += countArticlesOfCategory(t);
			}

		}
		return total;
	}
	
	// extended from the above countArticlesOfCategory function
	// this function can count all the articles which has been updated during a
	// given time period
	public static int countArticlesOfCategoryNDays(String categoryTitle, int numOfDays)
			throws UnsupportedEncodingException, JSONException {
		// initial query
		String querypages = "http://wiki.linked.earth/wiki/api.php?action=query&cmtype=subcat&list=categorymembers&cmlimit=100&format=json&cmtitle=";
		String query = querypages + categoryTitle.replace(" ", "_");
		System.out.println(query);
		System.out.println();
		JSONObject art = ConnectionRequests.doGETJSON(query);
		// retrieve categories from json

		int total = countMembersNDays(categoryTitle, numOfDays);
		JSONArray categories = art.getJSONObject("query").getJSONArray("categorymembers");
		System.out.println(categories.toString());

		if (categoryTitle.equals("Category:Working_Group") == false) {

			for (int i = 0; i < categories.length(); i++) {
				JSONObject cat = categories.getJSONObject(i);
				String t = cat.getString("title").replace(" ", "_");
				// System.out.println(queryCategoryDef+t);
				// System.out.println(t);
				total += countArticlesOfCategoryNDays(t, numOfDays);
			}

		}
		return total;
	}

	// getting number of a user's contribution during a period
	public static int userContrib(String username) throws JSONException, UnsupportedEncodingException {
		// get start/end date//////////////////////////////
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal = new GregorianCalendar();
		Date today = new Date();
		String start = dateFormat.format(today).replace(' ', 'T') + 'Z';

		// System.out.println("START DATE:" + start);

		cal.setTime(today);
		cal.add(Calendar.DAY_OF_MONTH, -1 * 7);
		Date from = cal.getTime();
		String end = dateFormat.format(from).replace(' ', 'T') + 'Z';

		// System.out.println("END DATE:" + end);
		//////////////////////////////////////////////////

		int counter = 0;

		String url = "http://wiki.linked.earth/wiki/api.php?action=query&list=usercontribs&uclimit=500&ucuser="
				+ username + "&ucstart=" + start + "&ucend=" + end + "&format=json" + "&rawcontinue";
//		System.out.println(url);
		JSONObject art = ConnectionRequests.doGETJSON(url);
		// System.out.println(art);;

		while (hasNext(art)) {
			counter += 500;
			String cont = URLEncoder.encode(
					art.getJSONObject("query-continue").getJSONObject("usercontribs").getString("uccontinue"), "UTF-8");
			art = ConnectionRequests.doGETJSON(
					"http://wiki.linked.earth/wiki/api.php?action=query&list=usercontribs&uclimit=500&ucuser="
							+ username + "&ucstart=" + start + "&ucend=" + end + "&format=json"
							+ "&rawcontinue&uccontinue=" + cont);
			// System.out.println("NEXT: " + art);
		}

		counter += art.getJSONObject("query").getJSONArray("usercontribs").length();

		// System.out.println(counter);

		return counter;
	}

	// get the most active user during last 7 days, also gives his/her total
	// number of contributions during that time period
	public static ArrayList<String> getMostActiveUserAndHisContribs()
			throws JSONException, UnsupportedEncodingException {
		String url = "http://wiki.linked.earth/wiki/api.php?action=query&list=allusers&aulimit=500&rawcontinue&auprop="
				+ URLEncoder.encode("title|name|editcount", "UTF-8") + "&format=json";
//		System.out.println(url);
		JSONObject art = ConnectionRequests.doGETJSON(url);

		ArrayList<String> res = new ArrayList<String>();

		JSONArray buf = art.getJSONObject("query").getJSONArray("allusers");
		// System.out.println(buf);

		String mostProductiveLast7 = buf.getJSONObject(0).get("name").toString();
		int howManyContrib1 = 0;

		String secondProductive = "";
		int howManyContrib2 = 0;

		String thirdProductive = "";
		int howManyContrib3 = 0;

		String fourthProductive = "";
		int howManyContrib4 = 0;

		String fifthProductive = "";
		int howManyContrib5 = 0;

		// getting the most productive user during last 7 days
		for (int i = 0; i < buf.length(); i++) {
			String temp = buf.getJSONObject(i).get("name").toString();

			if (temp.contains(" ")) {
				continue;// skipping those names with white space
							// media wiki can't read a username with white
							// space, so here is a compromise
			} else if (temp.equals("TestBot")) {
				continue;// need to filter testbot
			} else {
				int store = userContrib(temp);

				if (store > howManyContrib1) {
					fifthProductive = fourthProductive;// update the fifth
					howManyContrib5 = howManyContrib4;

					fourthProductive = thirdProductive;// update the fourth
					howManyContrib4 = howManyContrib3;

					thirdProductive = secondProductive;// update the third
					howManyContrib3 = howManyContrib2;

					secondProductive = mostProductiveLast7;// update the second
					howManyContrib2 = howManyContrib1;

					mostProductiveLast7 = temp;// update the most active user
					howManyContrib1 = store;
				} else if (store > howManyContrib2) {
					fifthProductive = fourthProductive;// update the fifth
					howManyContrib5 = howManyContrib4;

					fourthProductive = thirdProductive;// update the fourth
					howManyContrib4 = howManyContrib3;

					thirdProductive = secondProductive;// update the third
					howManyContrib3 = howManyContrib2;

					secondProductive = temp;// update the second
					howManyContrib2 = store;
				} else if (store > howManyContrib3) {
					fifthProductive = fourthProductive;// update the fifth
					howManyContrib5 = howManyContrib4;

					fourthProductive = thirdProductive;// update the fourth
					howManyContrib4 = howManyContrib3;

					thirdProductive = temp;// update the third
					howManyContrib3 = store;
				} else if (store > howManyContrib4) {
					fifthProductive = fourthProductive;// update the fifth
					howManyContrib5 = howManyContrib4;

					fourthProductive = temp;// update the fourth
					howManyContrib4 = store;
				} else if (store > howManyContrib5) {
					fifthProductive = temp;// update the fifth
					howManyContrib5 = store;
				}
			}

		}
		// return an arraylist, with index 0 being the most active user, index 1
		// being number of his/her contribudtions
		res.add(mostProductiveLast7);
		res.add(Integer.toString(howManyContrib1));

		res.add(secondProductive);
		res.add(Integer.toString(howManyContrib2));

		res.add(thirdProductive);
		res.add(Integer.toString(howManyContrib3));

		res.add(fourthProductive);
		res.add(Integer.toString(howManyContrib4));

		res.add(fifthProductive);
		res.add(Integer.toString(howManyContrib5));

		return res;
	}

	public static int checkIfOneWorkingGroupIsUpdated(String s, int pageid)
			throws UnsupportedEncodingException, JSONException, ParseException {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		// get start/end date//////////////////////////////
		Calendar cal = new GregorianCalendar();
		Date today = new Date();
		String start = dateFormat.format(today).replace(' ', 'T') + 'Z';

		// System.out.println("START DATE:" + start);

		cal.setTime(today);
		cal.add(Calendar.DAY_OF_MONTH, -1 * 7);
		Date from = cal.getTime();
		String end = dateFormat.format(from).replace(' ', 'T') + 'Z';

		// System.out.println("END DATE:" + end);
		//////////////////////////////////////////////////

		String url = "http://wiki.linked.earth/wiki/api.php?action=query&prop=revisions" + "&titles=" + s + "&rvprop="
				+ URLEncoder.encode("timestamp|user", "UTF-8") + "&rvlimit=50&rvstart=" + start + "&rvend=" + end
				+ "&format=json";

		JSONObject art = ConnectionRequests.doGETJSON(url);

		// get the timestamp of the last revision of a certain working group
		// and parse this timestamp into Date format, so that we can check if
		// this timestamp is within last seven days
		// String thisRevisionDate =
		// art.getJSONObject("query").getJSONObject("pages").getJSONObject(Integer.toString(pageid)).getJSONArray("revisions").getJSONObject(0).getString("timestamp");
		// thisRevisionDate = thisRevisionDate.substring(0,
		// thisRevisionDate.length()-1).replace("T", " ");
		// System.out.println(thisRevisionDate);
		// Date checkedDate = dateFormat.parse(thisRevisionDate);

		int totalRevisions;

		if (art.getJSONObject("query").getJSONObject("pages").getJSONObject(Integer.toString(pageid))
				.has("revisions")) {
			totalRevisions = art.getJSONObject("query").getJSONObject("pages").getJSONObject(Integer.toString(pageid))
					.getJSONArray("revisions").length();
		} else {
			totalRevisions = 0;
		}

		return totalRevisions;
	}

	public static int getLastRevisionId(String whichPage) throws JSONException, UnsupportedEncodingException {
		String url = "http://wiki.linked.earth/wiki/api.php?action=query&prop=revisions&titles=" + whichPage
				+ "&rvprop=" + URLEncoder.encode("ids|timestamp", "UTF-8") + "&format=json";
		JSONObject art = ConnectionRequests.doGETJSON(url);

		int revid = 0;

		int pageid = getPageId(whichPage);

		revid = art.getJSONObject("query").getJSONObject("pages").getJSONObject(Integer.toString(pageid))
				.getJSONArray("revisions").getJSONObject(0).getInt("revid");

		return revid;
	}

	public static int getPageId(String whichPage) throws JSONException {
		String url = "http://wiki.linked.earth/wiki/api.php?action=query&prop=pageprops&titles=" + whichPage
				+ "&format=json";
		JSONObject art = ConnectionRequests.doGETJSON(url);

		System.out.println(art);

		System.out.println(art.getJSONObject("query").getJSONObject("pages"));
		JSONObject pages = art.getJSONObject("query").getJSONObject("pages");
		// pages are in this format:
		// {"33579":{"ns":0,"pageid":33579,"title":"Weekly
		// Summary","pageprops":{"smw-semanticdata-status":"1"}}}
		// 33579 is the pageid of queried page, so the iterator just need to
		// return the first thing it gets

		Iterator allKeys = pages.keys();

		String key = (String) allKeys.next();// get the first key

		return Integer.parseInt(key);
	}

	
	/*------------@author Neha---------------*/
	
	/**
	 * This function returns the number of pages added to a category.
	 * 
	 * 
	 * @param categoryTitle Category title to check for new pages
	 * @param numOfDays	Additions dating back to days
	 * @return count of changes
	 * @throws JSONException
	 */
	public static LinkedHashMap<String, String> getChangesInCategory(String categoryTitle, int numOfDays)
			throws JSONException {

		String endTime = Utils.getEndTime(numOfDays, "yyyy-MM-dd") + "T00:00:00Z";
		categoryTitle = categoryTitle.replace(' ', '_');
		String categoryQuery = "http://wiki.linked.earth/wiki/api.php?action=query&cmtype=page&list=categorymembers&cmlimit=100&format=json&cmtitle="
				+ categoryTitle + "&cmsort=timestamp&cmdir=desc&cmend=" + endTime;
		JSONObject pagesChanged = ConnectionRequests.doGETJSON(categoryQuery);
		JSONArray categorymembers = pagesChanged.getJSONObject("query").getJSONArray("categorymembers");
		LinkedHashMap<String, String> data = new LinkedHashMap<>();
		for (int i = 0; i < categorymembers.length(); i++) {
			JSONObject page = categorymembers.getJSONObject(i);
			String title = page.getString("title").replace(' ', '_');
			data.put(title, mainUrl + title);
		}		
		return data;
	}
	
	public static HashMap<String, Integer> getMaxContibutors(int numOfDays) throws JSONException{
		String endTime = Utils.getEndTime(numOfDays, "yyyy-MM-dd") + "T00:00:00Z";
		HashMap <String, Integer> contributionScore = new HashMap<>();
		String allUsers = "http://wiki.linked.earth/wiki/api.php?action=query&list=allusers&aulimit=500&format=json";
		JSONObject allUserObject = ConnectionRequests.doGETJSON(allUsers);
		JSONArray userList = allUserObject.getJSONObject("query").getJSONArray("allusers");
		for (int i = 0; i < userList.length(); i++) {
			JSONObject user = userList.getJSONObject(i);
			String username = user.getString("name").replace(" ", "_");
			if (username.equals("TestBot"))
				continue;
			String userid = String.valueOf(user.get("userid")); 
			String contributionQuery = "http://wiki.linked.earth/wiki/api.php?action=query&list=usercontribs&uclimit=500&format=json&ucuser="+username+"&ucend="+endTime;
			JSONObject contributionObject = ConnectionRequests.doGETJSON(contributionQuery);
			int contributionCount = contributionObject.getJSONObject("query").getJSONArray("usercontribs").length();
			contributionScore.put(username, contributionCount);
		}
		contributionScore = (HashMap<String, Integer>)Utils.sortMapByValues(contributionScore);
		return contributionScore;				
	}
	
	public static LinkedHashMap<Article, Integer> getWGContributions(int numOfDays) throws JSONException{
		
		String title = "";
		LinkedHashMap<Article, Integer> data = new LinkedHashMap<>();
		String allWGQuery = "http://wiki.linked.earth/wiki/api.php?action=query&cmtype=subcat&list=categorymembers&cmlimit=100&format=json&cmtitle=Category:Working_Group";
		JSONObject allWGObject = ConnectionRequests.doGETJSON(allWGQuery);
		JSONArray categorymembers = allWGObject.getJSONObject("query").getJSONArray("categorymembers");
		for (int i = 0; i < categorymembers.length(); i++) {
			JSONObject wg = categorymembers.getJSONObject(i);
			title = wg.getString("title");
			Article subcat = new Article();
			subcat.setName(title);
			subcat.setUrl(Utils.generateUrl(title));
			subcat.setPageID(wg.getInt("pageid"));
			LinkedHashMap<String, String> contributions = getChangesInCategory( title, numOfDays );
			/*for (Entry<String, String> entry : contributions.entrySet()){
				System.out.println(entry.getKey()+ " "+ entry.getValue());
			}
			System.out.println();*/
			data.put(subcat, contributions.size());
		}
		/*for (Entry<Article, Integer> entry : data.entrySet()){
			System.out.println(entry.getKey().getName() + " "+ entry.getValue());
		}*/
		return data;
	}
	
	
	
	/*------------@author Neha---------------*/

	// main for testing
	// public static void main(String[] args)
	// {
	// try{
	/// getPageId("Weekly_Summary");
	// checkIfOneWorkingGroupIsUpdated("Category:Floods_Working_Group", 32121);
	// checkIfOneWorkingGroupIsUpdated("Category:Chronologies_Working_Group",
	// 4050);
	// getLastRevisionId();
	//
	// int buf = userContrib("TestBot");
	// System.out.println("# CONTRIBS: " + buf);
	//
	// System.out.println(getMostActiveUserAndHisContribs());
	// getMostActiveUserAndHisContribs();
	//
	// getAllUsers();
	//
	//
	// System.out.println("Datasets:
	// "+countArticlesOfCategory("Category:Dataset_(L)"));
	//
	// System.out.println("Datasets in last 7 days:
	// "+countArticlesOfCategoryNDays("Category:Dataset_(L)", 7));
	//
	// System.out.println("Proxy Acrhive:
	// "+countArticlesOfCategory("Category:ProxyArchive_(L)"));
	//
	// System.out.println("Proxy Observation:
	// "+countArticlesOfCategory("Category:ProxyObservation_(L)"));
	// System.out.println("Proxy Observation in last 90 days:
	// "+countArticlesOfCategoryNDays("Category:ProxyObservation_(L)", 90));
	//
	// System.out.println("Proxy Sensor:
	// "+countArticlesOfCategory("Category:ProxySensor_(L)"));
	// System.out.println("Instrument:
	// "+countArticlesOfCategory("Category:Instrument_(L)"));
	// System.out.println("InferredVariable:
	// "+countArticlesOfCategory("Category:InferredVariable_(L)"));
	// System.out.println("MeasuredVariable:
	// "+countArticlesOfCategory("Category:MeasuredVariable_(L)"));
	// System.out.println("Locations:
	// "+countArticlesOfCategory("Category:Location_(L)"));
	// System.out.println("Person:
	// "+countArticlesOfCategory("Category:Person_(L)"));
	// System.out.println("Person in last 7 days:
	// "+countArticlesOfCategoryNDays("Category:Person_(L)", 7));
	//
	// System.out.println("Publication:
	// "+countArticlesOfCategory("Category:Publication_(L)"));
	// System.out.println("Publication in last 7 days:
	// "+countArticlesOfCategoryNDays("Category:Publication_(L)", 7));
	//
	// System.out.println("Working Group:
	// "+countArticlesOfCategory("Category:Working_Group"));
	// System.out.println("Working Group:
	// "+countArticlesOfCategoryNDays("Category:Working_Group", 10));
	//
	// }catch(Exception e){
	// System.out.println("Error "+e.getMessage());
	// }
	// }
}