package com.mainbot.utility;

import java.util.LinkedHashMap;

public class Constants {
	public final static String WIKI_NAME = "http://wiki.linked.earth/wiki/";
    public static final String FORMAT_AND_API = "api.php?action=query&format=json";
    
    public final static String QUERY_GET_ALL_PAGES_AND_CONTINUE = WIKI_NAME+FORMAT_AND_API+"&list=allpages&rawcontinue&aplimit=1000";
    public final static String QUERY_GET_ALL_CATEGORIES = WIKI_NAME+FORMAT_AND_API+"&list=allpages&apnamespace=14&rawcontinue&aplimit=1000";
    
    public final static String CATEGORY_PERSON = "Category:Person �";
    public final static String CATEGORY_WORKING_GROUP = "Category:Working Group";
    public final static String CATEGORY_DATASET = "Category:Dataset";
    public final static String QUERY_ALL_DATASET = WIKI_NAME+FORMAT_AND_API + "&list=allpages&aplimit=1000&title="+CATEGORY_DATASET;
    
    
    public static LinkedHashMap<String, String> params = new LinkedHashMap<>();
    public final static String WIKI_NAME_API_FORMAT = "http://wiki.linked.earth/wiki/api.php?format=json";
}
