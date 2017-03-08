/**
 * License goes here
 */
package com.wikibot.app;

import static com.google.common.net.HttpHeaders.USER_AGENT;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;

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
            System.err.println("Error while getting query:" +query);
            return null;
        }
        
    }   
}
