package com.mainbot.utility;

import java.util.LinkedHashMap;

public class Utils {
	
	public static LinkedHashMap<String, String> params = new LinkedHashMap<>();
	
	public static String queryFormulation(){
		StringBuilder queryString = new StringBuilder();
		for(String key : params.keySet())
			queryString.append('&').append(key).append('=').append(params.get(key));
		params.clear();
		return queryString.toString();
	}
}
