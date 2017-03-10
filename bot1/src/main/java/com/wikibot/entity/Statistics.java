//add proper license here!!
package com.wikibot.entity;

import com.wikibot.app.Constants;
import com.wikibot.app.Utils;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class that retrieves a series of articles and their metadata.
 * Maybe it could be made more efficient.
 * @author dgarijo
 */
public class Statistics {
    //The key is the id because the name could be the same in different contributors (unlikely but possible)
    private final HashMap<Integer,Article> articles;//key:id, value article object
    private final HashMap<Integer, Agent> contributors;//key:agent id, value: Agent. This makes it easy to retrieve.

    public Statistics() {
        articles = new HashMap<>();
        contributors = new HashMap<>();
        
        //parsing the whole Wiki
        try{
        //while there are remaining articles, process them.
        JSONObject art = Utils.doGETJSON(Constants.QUERY_GET_ALL_PAGES_AND_CONTINUE);
        while(hasNext(art)){
//            processArticles(art);
            //prepare next batch
            String cont = URLEncoder.encode(art.getJSONObject("query-continue").getJSONObject("allpages").getString("apcontinue"), "UTF-8");
            art = Utils.doGETJSON(Constants.QUERY_GET_ALL_PAGES_AND_CONTINUE+"&apcontinue="+cont);
            System.out.println(cont);
        }
        //the last batch (no "next")
        processArticles(art);
        System.out.println("Number of articles:"+articles.size());
        System.out.println("Number of contributors:"+contributors.size());
        }catch(Exception e){
            System.err.println("Error: "+e.getMessage());
        }
    }
   
    private void processArticles(JSONObject art){
        
            //process articles. This can be optimized by retrieving qeuries of 50 max instead of 1
            JSONArray pages = art.getJSONObject("query").getJSONArray("allpages");
            for(Object currPage:pages){
                //hacer: si es mod 30, entonces hacer query. si no, iterar.
                //hacer esta mejora cuando termine uno a uno, aunque si hay que recuperar el historial puede no valer
                
                int articleID = ((JSONObject)currPage).getInt("pageid");
                String title = ((JSONObject)currPage).getString("title");
                Article a = new Article();
                a.setName(title);
                a.setPageID(articleID);
                
                ArrayList<String> pageCategories = null;
                ArrayList<Agent> pageContributors = null;
                //get article metadata. This could be optimized by getting 50 at a time.
                JSONObject artMetadata = Utils.doGETJSON(Constants.WIKI_NAME+Constants.FORMAT_AND_API+"&prop=categories%7Ccontributors&pageids="+articleID);
                artMetadata = artMetadata.getJSONObject("query").getJSONObject("pages").getJSONObject(""+articleID);
                System.out.println("ARTICLE "+this.articles.size()+" :" +title+"\n");
                try{
                   JSONArray cat = ((JSONObject)artMetadata).getJSONArray("categories");
                   pageCategories = new ArrayList<String>();
                   for (Object aux:cat){
                       pageCategories.add(((JSONObject)aux).getString("title"));
                   }
                }catch(Exception e){
                    System.out.println("\tNo categories for article "+title);
                }
                try{
                    JSONArray contr = ((JSONObject)artMetadata).getJSONArray("contributors");
                    pageContributors = new ArrayList<Agent>();
                    for (Object aux:contr){
                       String contribName = ((JSONObject)aux).getString("name");
                       int contribId = ((JSONObject)aux).getInt("userid");
                       //if it is already part of the contributors, reuse it. Otherwise, create it.
                       Agent currentContrib;
                       if(contributors.containsKey(contribId)){
                           currentContrib = contributors.get(contribId);
                       }else{
                           currentContrib = new Agent();
                           currentContrib.setGetAgentId(contribId);
                           currentContrib.setName(contribName);
                           contributors.put(contribId, currentContrib);
                       }
                       //add this article to the list of contributed articles of the contributor.
                       currentContrib.addContributedArticle(a);
                       pageContributors.add(currentContrib);
                   }
                }catch(Exception e){
                    System.out.println("\tNo contributors for article "+title);
                }
                a.setCategories(pageCategories);
                a.setContributors(pageContributors);
                this.articles.put(articleID, a);
                
                //for doing tests. remove!!
//                if(articles.size()>100){
//                    return;
//                }
            }
    }
    
    /**
     * there are many articles, so we get the min batches of 500. This function
     * evaluates if we are done, or we need more.
     * @param o
     * @return 
     */
    private boolean hasNext(JSONObject o){
        try{
            o.getJSONObject("query-continue");
            return true;
        }catch(JSONException e){
            return false;
        }
    }

    public HashMap<Integer, Article> getArticles() {
        return articles;
    }

    public HashMap<Integer, Agent> getContributors() {
        return contributors;
    }
    
    
    /**
     * Functions for performing different stats.
     * These should be out of this class.
     */
    /**
     * This function returns the top N contributors of all articles
     * @param n
     * @return 
     */
    public ArrayList<Agent> topNContributors(int n){
        return topNContributors(this.contributors.values().iterator(),n);
    }
    
    /**
     * Given a number n, this function will return the top n contributors
     * to all articles in the wiki. The contributions are in form of the number of articles
     * contributed to, not the number of edits.
     * @param it iterator over the contributors we want to handle
     * @param n
     * @return top n contributors
     */
    private ArrayList<Agent> topNContributors(Iterator<Agent> it, int n){
        ArrayList<Agent> topN = new ArrayList<>();
        Agent minimumTopContributor = null; 
        //if any of the rest in the list have more, replace them
//        Iterator<Agent> it = contrib.values().iterator();
        while (it.hasNext()){
            Agent currAgent = it.next();
            if(topN.size()<n){
                topN.add(currAgent);
                if(minimumTopContributor == null ||
                        currAgent.getContributedArticles().size()<minimumTopContributor.getContributedArticles().size()){
                    minimumTopContributor = currAgent;
                    //we store the contrubutions of the minimum from the array.
                }
            }else{
                //compare with the rest and add (if appropriate).
                if(currAgent.getContributedArticles().size()>minimumTopContributor.getContributedArticles().size()){
                   topN.add(currAgent);
                   topN.remove(minimumTopContributor);
                   //find new minimum
                   minimumTopContributor = currAgent;
                   for(Agent a:topN){
                       if (a.getContributedArticles().size()<minimumTopContributor.getContributedArticles().size()){
                           minimumTopContributor = a;
                       }
                   }
                }
            }
        }        
        return topN;
    }
   
    
    /**
     * Given a category, this function will return the top n contributors
     * that have contributed to articles of that category.
     * @param category
     * @return 
     */
    public ArrayList<Agent> topNContributors(int n, String category){
        //filter all those contributors that belong to a categroy.
        return null;
    }
    
    public ArrayList<Article> topNEditedArticles(int n){
        //maybe this should not be part of this class. The input arument should be the arraylist
        //or getting the articles should not be part of this class.
        return null;
    }
    
    public ArrayList<Article> topNEditedArticles(int n, String Category){
        return null;
    }
    
    /**
     * Function that retrieves all articles from a given category.
     * @param category
     * @return 
     */
    public List<Article> getArticlesFromCategory (String category){
        return articles.values().stream().filter(a -> a.getCategories().contains(category)).collect(Collectors.toList());
    }
    
    public String getPageSummary(){
        //in terms of working groups, users, datasets, other.
        //page summary: number of pages in the wiki from that category.
        return null;
    }
    
    public String getCollaborationGraph(List<Article> articles){
        //the input should be the contributor arraylist (filtered)
        String toreturn = "";
//        while (articles.hasNext()){
//            Article currArt = articles.next();
//            if(currArt.getContributors().size()>1){
//                toreturn+=("These agents will be sharing an edge in article: "+ currArt.getName()+"\n\t");
//                toreturn = currArt.getContributors().stream().map((a) -> (a.getName()+"-")).reduce(toreturn, String::concat);
//                toreturn+=("\n");
//                //maybe we can serialize weight better afterwards.
//            }
//        }
        //all articles with contributors
        List<Article> aux = articles.stream().filter(art ->art.getContributors().size()>1).collect(Collectors.toList());
        for(Article currArt:aux){
            toreturn+=("These agents will be sharing an edge in article: "+ currArt.getName()+"\n\t");
            //concatenate all the contributors of an article. In the future we might need just to state weight
            toreturn = currArt.getContributors().stream().map((a) -> (a.getName()+"-")).reduce(toreturn, String::concat);
            toreturn+=("\n");
                
        }
        return toreturn;
    }
    
    public String getCollaborationGraph(String category){
        //if category is null, then for all articles.
        if (category == null){
            return getCollaborationGraph((List<Article>)articles.values());
        }
        else{
            //filter articles according to their category
            return null;
        }
        
    }
    
    
    
//    public String getOntologyClasses(){
//        //all classes
//    }
//    
//    public String getOntologyProperties(){
//        //same. This can be done with a query.
//    }
    
    public static void main(String[] args){
        //String json = Utils.doGETJSON("http://wiki.linked.earth/wiki/api.php?action=query&format=json&list=allpages&rawcontinue&aplimit=500&apcontinue=Publication.10.1029/1999PA000428");//"http://wiki.linked.earth/wiki/api.php?action=query&format=json&list=allpages&rawcontinue&aplimit=100000");//apcontinue=A._Timmermann&
        //JSONObject obj = new JSONObject(json);
        Statistics s = new Statistics();
        ArrayList<Agent>top5 = s.topNContributors(5);
        Collections.sort(top5, (Agent o1, Agent o2) -> o2.getContributedArticles().size() - o1.getContributedArticles().size());
        for(Agent a:top5){
            System.out.println(a.getName()+": contributed to "+a.getContributedArticles().size() + " articles");
            //calculate categories of those articles
            HashMap<String, Integer> categoryFrequencies = new HashMap();
            a.getContributedArticles().stream().forEach((art) -> {
                art.getCategories().stream().forEach((currCat) -> {
                    if(!categoryFrequencies.containsKey(currCat)){
                        categoryFrequencies.put(currCat,1);
                    }else{
                        categoryFrequencies.put(currCat,categoryFrequencies.get(currCat)+1);
                    }
                });
            });
            System.out.println("with "+categoryFrequencies.size()+" categories:");
            categoryFrequencies.forEach((String c, Integer nu)->{
                System.out.println("\t"+c+", "+nu+" articles");
            });
        }
        //tests
        System.out.println(s.getCollaborationGraph(new ArrayList(s.getArticles().values())));
        System.out.println("Person articles "+s.getArticlesFromCategory("Category:Person ©").size());
        System.out.println("Dataset articles "+s.getArticlesFromCategory("Category:Dataset ©").size());
        System.out.println("Working group articles "+s.getArticlesFromCategory("Category:Working_Group").size());
        //print here Other.
//        do the json serialization and produce the stats.
        
//        Number of uploads per user (in terms of datasets.) -this is an individual stat
//        Contributions of users broken down in categories. done
//        do article distribution by category and contributions. 
//        including working groups        
//        
//        you can even get the top ten collaborative users.
//        
//        still have to finish the serialization in JSON
//        String cont = obj.getJSONObject("query-continue").getJSONObject("allpages").getString("apcontinue");
//   System.out.println(cont);
//articles with collaborations
    }
    
}
