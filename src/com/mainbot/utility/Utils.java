package com.mainbot.utility;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.mainbot.main.Bot;
import com.mainbot.main.HistogramBot;


public class Utils{
	
	public static final Logger logger = Logger.getLogger(Utils.class);
	public static String queryFormulation()
	{
		StringBuilder queryString = new StringBuilder();
		for(String key: Constants.params.keySet()){
			queryString.append('&').append(key);
			if(Constants.params.get(key) != "")
			{
				queryString.append('=').append(Constants.params.get(key));
			}
		}
		Constants.params.clear();
		//System.out.println("Now printing querystring: " + queryString.toString());
		return Constants.WIKI_NAME_API_FORMAT + queryString.toString();
	}
	
	public static boolean login(Bot mainbot) throws JSONException{
		
		Constants.params.put("action", "login");
		Constants.params.put("lgname", mainbot.getUsername());
		
		String tokenQuery = queryFormulation();
		JSONObject getToken = ConnectionRequests.doPOSTJSON(tokenQuery, "");
		//System.out.println("This is getToken: " + getToken);
		
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
		//System.out.println("This is login: " + login);
		
		
		/* Assertion to check if the login is successful */
		checkLogin(mainbot, "user");
		checkLogin(mainbot, "bot");
		
		//check login successful or not
		if(login.getJSONObject("login").getString("result").equals("Success"))
		{
			logger.info(mainbot.getUsername() + ": LOGIN SUCCEEDED");
			return true;
		}
		else
		{
			logger.info(mainbot.getUsername() + ": LOGIN FAILED");
			return false;
		}


	}
	
	public static void checkLogin(Bot mainbot, String userType) throws JSONException{
		Constants.params.put("action", "query");
		Constants.params.put("assert", userType);
		String user = queryFormulation();
		
		//System.out.println(ConnectionRequests.doPOSTJSON(user, mainbot.getSessionID()));		
		
		JSONObject post = ConnectionRequests.doPOSTJSON(user, mainbot.getSessionID());
		System.out.println(post);
		System.out.println("-----------------------------------");
		//System.out.println("This is checkLogin, a postJSON: " + post);
		
		
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
