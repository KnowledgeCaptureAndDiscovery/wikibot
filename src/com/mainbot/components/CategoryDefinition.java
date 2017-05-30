package com.mainbot.components;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mainbot.utility.ConnectionRequests;

public class CategoryDefinition {
    public static final String queryCategories = "http://wiki.linked.earth/wiki/api.php?action=query&cmtype=subcat&list=categorymembers&cmlimit=100&format=json&cmtitle=";
    public static final String queryCategoryDef = "http://wiki.linked.earth/wiki/api.php?&action=parse&prop=text&section=1&format=json&page=";
    public static final String queryCategoryDefAllText = "http://wiki.linked.earth/wiki/api.php?&action=parse&prop=text&format=json&page=";
    
    private static void listSubCategories(String categoryTitle) throws JSONException
    {
        //initial query
        JSONObject art = ConnectionRequests.doGETJSON(queryCategories+categoryTitle.replace(" ", "_"));
        //retrieve categories from json
        JSONArray categories = art.getJSONObject("query").getJSONArray("categorymembers");
        //for (Object cat:categories){
        for(int i = 0; i < categories.length(); i++)
        {
        	JSONObject cat = categories.getJSONObject(i);
            String t = cat.getString("title").replace(" ", "_");
            //System.out.println(queryCategoryDef+t);
            System.out.println(t);
            try{
                JSONObject def = ConnectionRequests.doGETJSON(queryCategoryDef+t);
                //System.out.println(def.getJSONObject("parse").getJSONObject("text").getString("*"));
            }catch(Exception e){
                //no section 1 available
                JSONObject def = ConnectionRequests.doGETJSON(queryCategoryDefAllText+t);
                //System.out.println(def.getJSONObject("parse").getJSONObject("text").getString("*"));
            }
            listSubCategories(t);
        }
        //recursively call method
        
    }
    
    private static boolean hasNext(JSONObject o)
    {
        try{
            o.getJSONObject("query-continue");
            return true;
        }catch(JSONException e){
            return false;
        }
    }
    
    private static int countMembers(String category) throws UnsupportedEncodingException, JSONException
    {
        int count = 0;
                
        String c = category.replace(" ", "_");
        System.out.println(c);
        JSONObject art = ConnectionRequests.doGETJSON("http://wiki.linked.earth/wiki/api.php?action=query&list=categorymembers&cmlimit=500&format=json&rawcontinue&cmtitle="+c);
        while(hasNext(art))
        {
        	System.out.println("ENTERING HASNEXT WHILE LOOP");
            //prepare next batch
            count+= 500;
            String cont = URLEncoder.encode(art.getJSONObject("query-continue").getJSONObject("categorymembers").getString("cmcontinue"), "UTF-8");
            //System.out.println(cont);
            art = ConnectionRequests.doGETJSON("http://wiki.linked.earth/wiki/api.php?action=query&list=categorymembers&cmlimit=500&format=json&cmtitle="+c+"&rawcontinue&cmcontinue="+cont);
            //System.out.println("Pringting art: " + art.toString());

        }
        //count the rest
        count+=art.getJSONObject("query").getJSONArray("categorymembers").length();
        return count;
    }
    
    
    private static int countArticlesOfCategory(String categoryTitle) throws UnsupportedEncodingException, JSONException
    {
        //initial query
        JSONObject art = ConnectionRequests.doGETJSON(queryCategories+categoryTitle.replace(" ", "_"));
        //retrieve categories from json
        int total = countMembers(categoryTitle);
        JSONArray categories = art.getJSONObject("query").getJSONArray("categorymembers");
        //for (Object cat:categories){
        for(int i = 0; i < categories.length(); i++)
        {
        	JSONObject cat = categories.getJSONObject(i);
            String t = cat.getString("title").replace(" ", "_");
            //System.out.println(queryCategoryDef+t);
            //System.out.println(t);
            total+= countArticlesOfCategory(t);
        }
        return total;
    }
    
    private static int countMembersNDays(String category, int numOfDays) throws UnsupportedEncodingException, JSONException
    {
    	//get start/end date//////////////////////////////
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal = new GregorianCalendar();
		Date today = new Date();
		String start = dateFormat.format(today).replace(' ', 'T')+'Z';
		
		//System.out.println("START DATE:" + start);
		
		cal.setTime(today);
		cal.add(Calendar.DAY_OF_MONTH, -1*numOfDays);
		Date from = cal.getTime();
		String end = dateFormat.format(from).replace(' ', 'T')+'Z';
		
		//System.out.println("END DATE:" + end);
		//////////////////////////////////////////////////

    	
        int count = 0;
        
        
        String c = category.replace(" ", "_");
        JSONObject art = ConnectionRequests.doGETJSON("http://wiki.linked.earth/wiki/api.php?action=query&list=categorymembers&cmlimit=500&cmsort=timestamp&cmdir=desc&cmprop="+ URLEncoder.encode("ids|title|timestamp", "UTF-8")+"&cmstart="+start+"&cmend="+end  +"&format=json&cmtitle="+c);

        while(hasNext(art))
        {        		
            //prepare next batch
            count+= 500;
            String cont = URLEncoder.encode(art.getJSONObject("query-continue").getJSONObject("categorymembers").getString("cmcontinue"), "UTF-8");
            //System.out.println(cont);
            
            art = ConnectionRequests.doGETJSON("http://wiki.linked.earth/wiki/api.php?action=query&list=categorymembers&cmlimit=500&cmsort=timestamp&cmdir=desc&cmprop="+ URLEncoder.encode("ids|title|timestamp", "UTF-8")+"&cmstart="+start+"&cmend="+end  +"&format=json&cmtitle="+c+"&rawcontinue&cmcontinue="+cont);                   
        }
        //count the rest
        
        
        count+=art.getJSONObject("query").getJSONArray("categorymembers").length();
        
        for(int i = 0; i < art.getJSONObject("query").getJSONArray("categorymembers").length(); i++)
        {
        	System.out.println("This array includes: TITLE = " + art.getJSONObject("query").getJSONArray("categorymembers").getJSONObject(i).getString("title")
        			+ "     TIMESTAMP = " + art.getJSONObject("query").getJSONArray("categorymembers").getJSONObject(i).getString("timestamp")
        			+ "     LINK = " + "http://wiki.linked.earth/" + art.getJSONObject("query").getJSONArray("categorymembers").getJSONObject(i).getString("title").replace(" ", "_"));
        }
        
        
        System.out.println("Printing array length of " + category + ":  " + count);
        
        
        return count;
    }
    
    
    
    
    public static int countArticlesOfCategoryNDays(String categoryTitle, int numOfDays) throws UnsupportedEncodingException, JSONException
    {
        //initial query
        JSONObject art = ConnectionRequests.doGETJSON(queryCategories+categoryTitle.replace(" ", "_"));
        //retrieve categories from json
        //int total = countMembers(categoryTitle);
        int total = countMembersNDays(categoryTitle, numOfDays);
        JSONArray categories = art.getJSONObject("query").getJSONArray("categorymembers");
        //for (Object cat:categories){
        for(int i = 0; i < categories.length(); i++)
        {
        	JSONObject cat = categories.getJSONObject(i);
            String t = cat.getString("title").replace(" ", "_");
            //System.out.println(queryCategoryDef+t);
            //System.out.println(t);
            total+= countArticlesOfCategoryNDays(t, numOfDays);
        }
        return total;
    }
    
    public static void main(String[] args)
    {
        try{
            //System.out.println("Datasets: "+countArticlesOfCategory("Category:Dataset_(L)"));
            
            System.out.println("Datasets in last 7 days: "+countArticlesOfCategoryNDays("Category:Dataset_(L)", 7));
            
//            System.out.println("Proxy Acrhive: "+countArticlesOfCategory("Category:ProxyArchive_(L)"));
                        
//            System.out.println("Proxy Observation: "+countArticlesOfCategory("Category:ProxyObservation_(L)"));
//            System.out.println("Proxy Observation in last 90 days: "+countArticlesOfCategoryNDays("Category:ProxyObservation_(L)", 90));

//            System.out.println("Proxy Sensor: "+countArticlesOfCategory("Category:ProxySensor_(L)"));
//            System.out.println("Instrument: "+countArticlesOfCategory("Category:Instrument_(L)"));
//            System.out.println("InferredVariable: "+countArticlesOfCategory("Category:InferredVariable_(L)"));
//            System.out.println("MeasuredVariable: "+countArticlesOfCategory("Category:MeasuredVariable_(L)"));
//            System.out.println("Locations: "+countArticlesOfCategory("Category:Location_(L)"));
            //System.out.println("Person: "+countArticlesOfCategory("Category:Person_(L)"));
            System.out.println("Person in last 7 days: "+countArticlesOfCategoryNDays("Category:Person_(L)", 7));
            
            //System.out.println("Publication: "+countArticlesOfCategory("Category:Publication_(L)"));
            System.out.println("Publication in last 7 days: "+countArticlesOfCategoryNDays("Category:Publication_(L)", 7));
            
            //System.out.println("Working Group: "+countArticlesOfCategory("Category:Working_Group"));

        }catch(Exception e){
            System.out.println("Error "+e.getMessage()); 
        }
    }
}