//add license
package com.wikibot.app;

/**
 * General constants used in the project
 * @author dgarijo
 */
public class Constants {
    public final static String WIKI_NAME = "http://wiki.linked.earth/wiki/";
    public static final String FORMAT_AND_API = "api.php?action=query&format=json";
    
    public final static String QUERY_GET_ALL_PAGES_AND_CONTINUE = WIKI_NAME+FORMAT_AND_API+"&list=allpages&rawcontinue&aplimit=1000";
    
}
