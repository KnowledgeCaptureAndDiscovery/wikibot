package com.mainbot.utility;

import org.json.JSONException;
import org.json.JSONObject;

import com.mainbot.main.Bot;


public class Utils{
	
	public static String queryFormulation(){
		StringBuilder queryString = new StringBuilder();
		for(String key: Constants.params.keySet()){
			queryString.append('&').append(key);
			if(Constants.params.get(key) != "")
				queryString.append('=').append(Constants.params.get(key));
		}
		Constants.params.clear();
		return Constants.WIKI_NAME_API_FORMAT + queryString.toString();
	}
	
	public static void login(Bot mainbot) throws JSONException{
		
		Constants.params.put("action", "login");
		Constants.params.put("lgname", mainbot.getUsername());
		
		String tokenQuery = queryFormulation();
		JSONObject getToken = ConnectionRequests.doPOSTJSON(tokenQuery, "");
		System.out.println(getToken);
		
		String token = getToken.getJSONObject("login").get("token").toString();
		String sessionid = getToken.getJSONObject("login").get("sessionid").toString();
		String cookieprefix = getToken.getJSONObject("login").get("cookieprefix").toString();
		
		mainbot.setSessionID(cookieprefix + "=" + sessionid);
		
		Constants.params.put("action", "login");
		Constants.params.put("lgname", mainbot.getUsername());
		Constants.params.put("lgpassword", mainbot.getPassword());
		Constants.params.put("lgtoken", token);
		
		String loginQuery = queryFormulation();
		JSONObject login = ConnectionRequests.doPOSTJSON(loginQuery, mainbot.getSessionID());
		System.out.println(login);

		/* Assertion to check if the login is successful */
		checkLogin(mainbot, "user");
		checkLogin(mainbot, "bot");
	}
	
	public static void checkLogin(Bot mainbot, String userType){
		Constants.params.put("action", "query");
		Constants.params.put("assert", userType);
		String user = queryFormulation();
		
		System.out.println(ConnectionRequests.doPOSTJSON(user, mainbot.getSessionID()).toString());		
	}
	
	public static boolean hasNext(JSONObject o){
        try{
            o.getJSONObject("continue");
            return true;
        }catch(JSONException e){
            return false;
        }
    }
}
