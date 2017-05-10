package com.mainbot.components;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import com.mainbot.main.Bot;
import com.mainbot.utility.ConnectionRequests;
import com.mainbot.utility.Constants;
import com.mainbot.utility.Utils;

public class Edit {
	static ConnectionRequests conn = new ConnectionRequests();
	static Utils util = new Utils();
	
	public static int edit(Visualization view , Bot mainbot) throws JSONException, UnsupportedEncodingException{
		Constants.params.put("meta", "tokens");
		Constants.params.put("action", "query");
		
		
		//String tokenQuery2 = WIKI_NAME + FORMAT_AND_API+ "&format=json&meta=tokens";
		String tokenQuery = util.queryFormulation();
		JSONObject getToken = ConnectionRequests.doPOSTJSON(tokenQuery, mainbot.getSessionID());
		String token = getToken.getJSONObject("query").getJSONObject("tokens").get("csrftoken").toString();
		System.out.println(token);

		
		Constants.params.put("action", "edit");
		Constants.params.put("text", URLEncoder.encode(view.viewText, "UTF-8"));
		Constants.params.put("sectiontitle", URLEncoder.encode(view.section, "UTF-8"));
		Constants.params.put("contentformat", "text/x-wiki");
		Constants.params.put("section", "new");
		Constants.params.put("title", "Test");
		
		String editQuery = Utils.queryFormulation();
		//String editQuery = WIKI_NAME+ "api.php?action=edit&title=Test&section=new&sectiontitle=EditAPITest&text="+edittext+"&format=json";
		JSONObject edit = conn.postFuncWithParams(editQuery, mainbot.getSessionID(), token);
		System.out.println(edit);
		return edit.getJSONObject("edit").getInt("newrevid");
	}
	
	
	public void undoRevisions(int revid, boolean undoafter, Bot mainbot) throws JSONException{
		Constants.params.put("action", "query");
		Constants.params.put("meta", "tokens");
		String tokenQuery = util.queryFormulation();
		JSONObject getToken = conn.doPOSTJSON(tokenQuery, mainbot.getSessionID());
		String token = getToken.getJSONObject("query").getJSONObject("tokens").get("csrftoken").toString();
		System.out.println(token);
		
		Constants.params.put("action", "edit");
		Constants.params.put("title", "Test");
		
		
		if(undoafter){
			Constants.params.put("undoafter", String.valueOf(revid));
			Constants.params.put("text", "Testing");			
		}else{
			Constants.params.put("undo", String.valueOf(revid));
		}
		String undoQuery = util.queryFormulation();
		JSONObject undo = conn.postFuncWithParams(undoQuery, mainbot.getSessionID(), token);
		System.out.println(undo);
	}

	
}
