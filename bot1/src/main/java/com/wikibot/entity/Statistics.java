//add proper license here!!
package com.wikibot.entity;

import static com.google.common.net.HttpHeaders.USER_AGENT;
import com.wikibot.app.Constants;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import net.sourceforge.jwbf.mediawiki.actions.queries.AllPageTitles;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;

/**
 * Class that retrieves a series of articles and their metadata.
 * Maybe it could be made more efficient.
 * @author dgarijo
 */
public class Statistics {
    private HashMap<String,Article> articles;//key:article Name, value article object
    private ArrayList<Agent> contributors;

    public Statistics() {
        articles = new HashMap<String, Article>();
        contributors = new ArrayList();
//        MediaWikiBot mediaWikiBot = new MediaWikiBot(Constants.WIKI_NAME);
//        AllPageTitles t = new AllPageTitles(mediaWikiBot);
//        Iterator<String> it = t.iterator();
//        while(it.hasNext()){
//            String artTitle = it.next();
//            Article a = new Article();
//            a.setName(artTitle);
//            //get contributors
//            //
////            getJSON(artTitle, "api.php?action=query&format=json&prop=categories&titles="+artTitle);
//            //get categories
//            //initialize
//            articles.put(artTitle,a);
//            //System.out.println(b.getTitle());
//        }
    }
    
    //this has to be improved
    private String getJSON(String query){
        try{
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(query);

        request.addHeader("User-Agent", USER_AGENT);
        HttpResponse response = client.execute(request);
        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        StringBuilder result = new StringBuilder();
        String line = "";
        while ((line = rd.readLine()) != null) {
                result.append(line);
        }
        return result.toString();
        }catch (Exception e){
            System.err.println("Error while getting info");
            return null;
        }
    }

    public HashMap<String, Article> getArticles() {
        return articles;
    }

    public ArrayList<Agent> getContributors() {
        return contributors;
    }
    
    public static void main(String[] args){
        String json = new Statistics().getJSON("http://wiki.linked.earth/wiki/api.php?action=query&format=json&list=allpages&rawcontinue&aplimit=1000");//apcontinue=A._Timmermann&
        JSONObject obj = new JSONObject(json);
        String cont = obj.getJSONObject("query-continue").getJSONObject("allpages").getString("apcontinue");
        System.out.println(cont);
        
    }
    
}
