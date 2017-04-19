package com.mainbot.components;

import org.json.JSONObject;

import com.mainbot.main.Bot;
import com.mainbot.utility.*;


public class Access {

	static ConnectionRequests conn = new ConnectionRequests();
	/*
	 * @params query Query string for the api
	 * @params connType Type of Connection. Get, POST or POST with params*/
	
	public static JSONObject makeQuery(String query, int connType){
		JSONObject result;
		switch(connType){
			case '1': return conn.doGETJSON(query);
			case '2': return conn.doPOSTJSON(query,sessionID);
			case '3': return conn.postFuncWithParams(query, sessionID, "token");
		}
		return null;
	}
}
