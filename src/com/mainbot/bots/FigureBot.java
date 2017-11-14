package com.mainbot.bots;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.mainbot.components.Edit;
import com.mainbot.components.Visualization;
import com.mainbot.utility.ConnectionRequests;

public class FigureBot extends Bot {
	String figPage;
	HashMap<String, Integer> figureMap = new HashMap<>();

	ConnectionRequests conn = new ConnectionRequests();
	Visualization view = new Visualization();
	Edit edit = new Edit();

	public FigureBot(String username, String password, String figPage) {
		super(username, password);
		this.figPage = figPage;
		// TODO Auto-generated constructor stub
	}

	public int getAllFigures() throws JSONException, UnsupportedEncodingException {
		int revid = 0;
		figureMap.put("Contributors", getFigureOf("Person_(L)"));
		figureMap.put("Datasets", getFigureOf("Dataset_(L)"));
		figureMap.put("Working Groups", getFigureOf("Working_Group"));
		figureMap.put("Data Tables", getFigureOf("DataTable_(L)"));

		view.displayFigureText(figureMap);
		revid = edit.edit(view, this, this.figPage, "10");
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

}
