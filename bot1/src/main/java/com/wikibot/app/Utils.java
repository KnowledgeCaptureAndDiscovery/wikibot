/**
 * License goes here
 */
package com.wikibot.app;

import static com.google.common.net.HttpHeaders.USER_AGENT;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Class that contains several functions to facilitate retrieving or operating with data.
 * @author dgarijo
 */
public class Utils {
    
/**
 * Given a query, the method performs it and returns the resultant JSON.
 * The query is a url with the parameters to call the api.
 * This is a GET method.
 * @param query (url encoded)
 * @return JSON text
 */    
 public static JSONObject doGETJSON(String query){
        try{
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(query);

            request.addHeader("User-Agent", USER_AGENT);
            HttpResponse response = client.execute(request);
            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

            StringBuilder result = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                    result.append(line);
            }
            return new JSONObject(result.toString());
        }catch (Exception e){
            System.err.println("Error while getting query:" +query +e.getMessage());
            return null;
        }
        
    }   
 
 /**
  * Given a query, the method performs it and returns the resultant JSON.
  * The query is a url with the parameters to call the api.
  * This is a POST method.
  * @param query (url encoded)
  * @return JSON text
  */  
 
 
 	public static JSONObject doPOSTJSON(String query, String cookie){
 		
        try {
        	if(cookie.equals(""))
        		CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
        	URL url = new URL(query);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.addRequestProperty("Cookie", cookie);
            conn.addRequestProperty("User-Agent", USER_AGENT);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/x-www-urlencoded"); 
	        conn.setDoOutput(true);
	        Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
	        JSONObject array = new JSONObject(new JSONTokener(in));
	       
	        in.close();
	        return array;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			 System.err.println("Error while getting POST object:" +query +e.getMessage());
	         return null;
		}
		
 	}
 	
 	public static JSONObject postFuncWithParams(String query, String cookie, String token){
 		 URL url;
		try {
			url = new URL(query);
			Map<String,Object> params = new LinkedHashMap<>();
	         params.put("token", token);
	         StringBuilder postData = new StringBuilder();
	         for (Map.Entry<String,Object> param : params.entrySet()) {
	             if (postData.length() != 0) postData.append('&');
	             postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
	             postData.append('=');
	             postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
	         }
	         byte[] postDataBytes = postData.toString().getBytes("UTF-8");

	         HttpURLConnection conn = (HttpURLConnection)url.openConnection();
	         conn.setRequestMethod("POST");
	         conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	         conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
	         conn.setDoOutput(true);
	         conn.getOutputStream().write(postDataBytes);

	         Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
	         JSONObject array = new JSONObject(new JSONTokener(in));
	         return array;
	         
		} catch (Exception e) {
			// TODO Auto-generated catch block
			 System.err.println("Error while getting POST object with token as param:" +query +e.getMessage());
	         return null;
		}
         
 	}
 	
}
