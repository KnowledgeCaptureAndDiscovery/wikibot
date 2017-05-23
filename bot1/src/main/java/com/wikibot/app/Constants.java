//add license
package com.wikibot.app;

/**
 * General constants used in the project
 * @author dgarijo
 */
public class Constants {
    public final static String WIKI_NAME = "http://wiki.linked.earth/wiki/";//"http://wiki.linked.earth/snapshots/2017-04-15/";//
    public static final String FORMAT_AND_API = "api.php?action=query&format=json";
    
    public final static String QUERY_GET_ALL_PAGES_AND_CONTINUE = WIKI_NAME+FORMAT_AND_API+"&list=allpages&rawcontinue&aplimit=1000";
    public final static String QUERY_GET_ALL_CATEGORIES = WIKI_NAME+FORMAT_AND_API+"&list=allpages&apnamespace=14&rawcontinue&aplimit=1000";
    
    public final static String CATEGORY_PERSON = "Category:Person (L)";
    public final static String CATEGORY_WORKING_GROUP = "Category:Working Group";
    public final static String CATEGORY_DATASET = "Category:Dataset (L)";
    public final static String QUERY_ALL_DATASET = WIKI_NAME+FORMAT_AND_API + "&list=allpages&aplimit=1000&title="+CATEGORY_DATASET;
    
}
