package com.mainbot.bots;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mainbot.components.Edit;
import com.mainbot.components.Visualization;
import com.mainbot.utility.ConnectionRequests;
import com.mainbot.utility.Utils;

public class FigureBot extends Bot {
	String figPage;
	String section;
	HashMap<String, Integer> figureMap = new HashMap<>();

	ConnectionRequests conn = new ConnectionRequests();
	Visualization view = new Visualization();
	Edit edit = new Edit();

	public FigureBot(String username, String password, String figPage, String section) {
		super(username, password);
		this.figPage = figPage;
		this.section = section;
		// TODO Auto-generated constructor stub
	}

	public int getAllFigures() throws JSONException, UnsupportedEncodingException {
		int revid = 0;
		figureMap.put("Authors", getFigureOf("Person_(L)"));
		figureMap.put("Compilation", getFigureOf("Compilation_(L)"));
		figureMap.put("Publication", getFigureOf("Publication_(L)"));
		figureMap.put("Observation", getFigureOf("Observation_(L)"));
		figureMap.put("Datasets", getFigureOf("Dataset_(L)"));
		figureMap.put("Working Groups", getFigureOf("Working_Group"));
		figureMap.put("Contributors", getRegisteredUserCount());
		figureMap.put("Institutions", getInstitutionCount());

		view.displayFigureText(figureMap);
		revid = edit.edit(view, this, this.figPage, this.section);
		return revid;

	}

	public int getFigureOf(String title) throws JSONException {
		int personCount = 0;
		String personQuery = "http://wiki.linked.earth/wiki/api.php?action=query&format=json&prop=categoryinfo&titles=Category:‏‎"
				+ title;
		JSONObject personJson = conn.doGETJSON(personQuery);
		String tempJSONKey = JSONObject.getNames(personJson.getJSONObject("query").getJSONObject("pages"))[0];
		personCount = personJson.getJSONObject("query").getJSONObject("pages").getJSONObject(tempJSONKey)
				.getJSONObject("categoryinfo").getInt("size");
		return personCount;
	}
	
	public int getRegisteredUserCount() throws JSONException{
		int users = 0, authors = 0;
		String allUsers = "http://wiki.linked.earth/wiki/api.php?action=query&list=allusers&format=json&auprop=editcount&aulimit=500";
		JSONObject allUserObject = ConnectionRequests.doGETJSON(allUsers);
		JSONArray userList = allUserObject.getJSONObject("query").getJSONArray("allusers");
		return userList.length();
	}
	
	public int getInstitutionCount() throws JSONException{
		String allUni = "http://wiki.linked.earth/store/ds/query?format=json&query=PREFIX+core%3A+%3Chttp%3A%2F%2Flinked.earth%2Fontology%23%3E%0ASELECT+distinct+%3Funi+%0AWHERE+%7B%0A++%3Fp+a+core%3APerson+.%0A++%3Fp+%3Chttp%3A%2F%2Fwiki.linked.earth%2FSpecial%3AURIResolver%2FProperty-3AUniversity%3E+%3Funi%0A%7D";
		JSONObject uniObject = ConnectionRequests.doGETJSON(allUni);
		JSONArray uniList = uniObject.getJSONObject("results").getJSONArray("bindings");
		return uniList.length();
	}

}
