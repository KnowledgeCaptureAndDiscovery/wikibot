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
public class WikiStatistics {
    //The key is the id because the name could be the same in different contributors (unlikely but possible)
    private final HashMap<Integer,Article> articles;//key:id, value article object
    private final HashMap<Integer, Agent> contributors;//key:agent id, value: Agent. This makes it easy to retrieve.

    public WikiStatistics() {
        articles = new HashMap<>();
        contributors = new HashMap<>();
        
        //parsing the whole Wiki
        try{
        //while there are remaining articles, process them.
        JSONObject art = Utils.doGETJSON(Constants.QUERY_GET_ALL_PAGES_AND_CONTINUE);
        while(hasNext(art)){
            processArticles(art);
            //prepare next batch
            String cont = URLEncoder.encode(art.getJSONObject("query-continue").getJSONObject("allpages").getString("apcontinue"), "UTF-8");
            art = Utils.doGETJSON(Constants.QUERY_GET_ALL_PAGES_AND_CONTINUE+"&apcontinue="+cont);
        }
        //the last batch (no "next")
        processArticles(art);
        //process categories (for some reason they are not listed among the pages)
        //there are less than 200 categories.
        art = Utils.doGETJSON(Constants.QUERY_GET_ALL_CATEGORIES);
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
                //OPTIMIZATION: DO 20 BY 20, IT SHOULD BE WAY FASTER
                
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
     * This method will retrieve the contributors that have contributed to an article of a given category
     * @param category the category we want to get the contributors from.
     * @return 
     */
    public List<Agent> getCategoryContributors(String category){
        return this.contributors.values().stream().filter((contributor)-> contributor.getContributedArticlesFromCategory(category).size()>0).collect(Collectors.toList());
    }
    
    /**
     * Function that retrieves all articles from a given category.
     * @param category
     * @return 
     */
    public List<Article> getArticlesFromCategory (String category){
        return articles.values().stream().filter(a -> a.getCategories().contains(category)).collect(Collectors.toList());
    }
    
    /**
     * Returns all category articles
     * @return 
     */
    private List<Article> getNumCategories(){
        return articles.values().stream().filter(a-> a.getName().contains("Category:")).collect(Collectors.toList());
    }
    
    
    /**
     * This method should be on a separate class
     * @param category 
     * @param name title of the div
     * @return 
     */
    public String printCollaborationGraph(String category, String name){
        //the input should be the contributor arraylist (filtered)
        String s = "<div id=\""+name+"\" class =\"mynetwork\"></div>\n" +
        "<script type=\"text/javascript\">\n";
        //for all contributors, get their list and add the nodes.
        String nodes = "var nodes = new vis.DataSet([\n";
        String edges = "var edges = new vis.DataSet([\n";
        ArrayList<Integer> addedNodes = new ArrayList<>();
        for(Agent currentAgent:contributors.values()){
            //get collaborators and fequency from author
            HashMap<Integer,Integer> freqs = currentAgent.getCollaboratorsAndFrequencies(category);
            if(freqs.size()>0){
                //add the node into the graph:
                nodes+="{id: "+currentAgent.getAgentId()+", label: '"+currentAgent.getName()+"'},\n";
                addedNodes.add(currentAgent.getAgentId());
                for(Integer contribArticle: freqs.keySet()){
                    if(!addedNodes.contains(contribArticle)){
                    edges+="{from: "+currentAgent.getAgentId()+", to: "+contribArticle+
                            ", value: "+freqs.get(contribArticle)+", title: '"+freqs.get(contribArticle)+" joint articles'},\n";
                    }
                }
            }
        }
        //remove the last comma and break line
        nodes= nodes.substring(0, nodes.length()-2) + "\n]);\n";
        edges= edges.substring(0, edges.length()-2)+ "\n]);\n";
        if(!addedNodes.isEmpty()){
            s+=nodes+edges+"  var container = document.getElementById('"+name+"');\n" +
            "  var data = {\n" +
            "    nodes: nodes,\n" +
            "    edges: edges\n" +
            "  };\n" +
            "  var options = {};\n" +
            "  var network = new vis.Network(container, data, options);\n" ;
        }
        s+="</script>";
        return s;
    }

    /**
     * Given an article id, this method will print out the contributors.
     * @param id
     * @return 
     */
    public static String printAuthorContributorsForArticle(String id){
        JSONObject artMetadata = Utils.doGETJSON(Constants.WIKI_NAME+Constants.FORMAT_AND_API+"&prop=categories%7Ccontributors&pageids="+id);
        ArrayList<Agent> pageContributors=null;
        try{
            artMetadata = artMetadata.getJSONObject("query").getJSONObject("pages").getJSONObject(""+id);
            JSONArray contr = ((JSONObject)artMetadata).getJSONArray("contributors");
            pageContributors = new ArrayList<>();
            for (Object aux:contr){
               String contribName = ((JSONObject)aux).getString("name");
               pageContributors.add(new Agent(contribName, null));
           }
        }catch(Exception e){
            System.out.println("\tNo contributors for article ");
        }
        String toreturn="";
        return pageContributors.stream().map(a->(a.getName()+"-")).reduce(toreturn, String::concat);
    }
    
    public String getWikiArticleDistributionSummary(){
        int numArticles = this.getArticles().size();
        int numContribs = this.getContributors().size();
        int numWG = this.getArticlesFromCategory(Constants.CATEGORY_WORKING_GROUP).size();
        int numCategories = this.getNumCategories().size();
        int numCategoriesNotWG = getNumCategories().stream().filter(a->!a.getCategories().contains(Constants.CATEGORY_WORKING_GROUP)).collect(Collectors.toList()).size();
//        for(Article a: getArticlesFromCategory(Constants.CATEGORY_WORKING_GROUP)){
//            System.out.println(a.getName());
//        }
        int numPerson = this.getArticlesFromCategory(Constants.CATEGORY_PERSON).size();
        int numDataset = this.getArticlesFromCategory(Constants.CATEGORY_DATASET).size();
        String s = "<h1>Summary of the articles in the wiki: </h1>\n";
        s+="<p>The wiki contains "+numArticles +" articles, edited by "+ numContribs +" contributors.<br/>\n";
        s+="Working group articles: "+numWG+".<br/>\n";
        s+="Person articles: "+numPerson+".<br/>\n";
        s+="Dataset articles: "+numDataset+".<br/>\n";
        s+="Category articles: "+numCategories+".<br/></p>\n";
        s+="<div id=\"distribution\"></div>";
        s+="<script>\n" +
"	var pie = new d3pie(\"distribution\", {\n" +
"		header: {\n" +
"			title: {\n" +
"				text: \"Article distribution\",\n" +
"				fontSize: 20\n" +
"			}\n" +
"		},\n" +
"		size: {\n" +
"			canvasHeight: 300,\n" +
"			canvasWidth: 300\n" +
"		  },\n" +
"		data: {\n" +
"			content: [\n" +
"				{ label: \"Dataset\", value: "+numDataset+" },\n" +
"				{ label: \"Person\", value: "+numPerson+" },\n" +
"				{ label: \"Working Group\", value: "+numWG+"},\n" +
"				{ label: \"Categories\", value: "+numCategories+"},\n" +                
"				{ label: \"Other\", value: "+(numArticles - numDataset - numPerson - numWG - numCategoriesNotWG)+"}\n" +
"			]\n" +
"		}\n" +
"	});\n" +
"</script>";
        return s;
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
    
    public ArrayList<Article> topNContributedArticles(int n){
        //to do
        return null;
    }
    
    public ArrayList<Article> topNContributedArticles(int n, String Category){
        //to do
        return null;
    }
    
    /**
     * Method to retrieve the total number of articles being contributed to by users.
     * Each person is counted only once per article (it's not edits)
     * @param category null if no category to be applied
     * @return 
     */
    public int totalContributions(String category){
        if(category==null){
            return contributors.values().stream().map((contributor)->contributor.getContributedArticles().size()).reduce(0,(a,b)->a+b);
        }else{
            return this.getCategoryContributors(category).stream().map((contributor)->contributor.getContributedArticlesFromCategory(category).size()).reduce(0,(a,b)->a+b);
        } 
            
    }
    
    public String getWikiContributionSummary(){
        int total = totalContributions(null);
        int totalDataset = totalContributions(Constants.CATEGORY_DATASET);
        int totalPerson = totalContributions(Constants.CATEGORY_PERSON);
        int totalWG = totalContributions(Constants.CATEGORY_WORKING_GROUP);
        String s = "<h1>Contribution summary</h1>"
                + "<p>There are "+contributors.size() + " contributors in the wiki with "+ total+" contributions<br/>\n";
        s+= "Number of contributions to working group articles: "+totalWG+ "<br/>\n";
        s+= "Number of contributions to person articles: "+totalPerson+ "<br/>\n";
        s+= "Number of contributions to dataset articles: "+totalDataset+"<br/>\n";
        s+= "Note: a contribution summarizes all the edits done by a user to an article</p>\n";
        s+="<div id=\"contrib\"></div>\n";
        s+="<script>\n" +
"	var pie = new d3pie(\"contrib\", {\n" +
"		header: {\n" +
"			title: {\n" +
"				text: \"Article contribution\",\n" +
"				fontSize: 20\n" +
"			}\n" +
"		},\n" +
"		size: {\n" +
"			canvasHeight: 300,\n" +
"			canvasWidth: 300\n" +
"		  },\n" +
"		data: {\n" +
"			content: [\n" +
"				{ label: \"Dataset\", value: "+totalDataset+" },\n" +
"				{ label: \"Person\", value: "+totalPerson+" },\n" +
"				{ label: \"Working Group\", value: "+totalWG+"},\n" +
"				{ label: \"Other\", value: "+(total - totalDataset - totalPerson - totalWG)+"}\n" +
"			]\n" +
"		}\n" +
"	});\n" +
"</script>";
        s+= "<h2>Top 10 contributors</h2><p>\n";
        ArrayList<Agent>top5 = topNContributors(10);
        Collections.sort(top5, (Agent o1, Agent o2) -> o2.getContributedArticles().size() - o1.getContributedArticles().size());
        for(Agent a:top5){
            s+=("<a href=\"http://wiki.linked.earth/User:"+a.getName().replace(" ", "_")+"\">"+a.getName()+"</a>: contributed to "+a.getContributedArticles().size() + " articles from " + a.getContributedCategories().size() + " categories </br>\n");
//            System.out.println(a.getContributedArticlesFromCategory(Constants.CATEGORY_DATASET).size() +" Datasets" );            
        }
        s+="</p>";
        return s;
    }
    
    public String getWikiCollaborationSummary(){
        String s ="<h1>Collaboration summary</h1>";
        s+="<h2>User collaboration in the wiki</h2>";
        s+=printCollaborationGraph(null,"collaboration");
        s+="<h2>User collaboration in working groups</h2>";
        s+=printCollaborationGraph(Constants.CATEGORY_WORKING_GROUP,"collaborationWG");
        s+="<h2>User collaboration in datasets</h2>";
        s+=printCollaborationGraph(Constants.CATEGORY_DATASET, "collaborationDataset");
        return s;
    }
    
    public static void main(String[] args){
//        System.out.println(WikiStatistics.printAuthorContributorsForArticle("1824"));// this is the working group on marine sediment
        
        WikiStatistics s = new WikiStatistics();
        
        //tests
        System.out.println(s.getWikiArticleDistributionSummary());
        
        System.out.println(s.getWikiContributionSummary());
        
        System.out.println(s.getWikiCollaborationSummary());
        
//        do the json serialization and produce the stats.
        
//        Number of uploads per user (in terms of datasets.) -this is an individual stat
//        Contributions of users broken down in categories. done
//        do article distribution by category and contributions. 
//        including working groups        
//        
//        you can even get the top ten collaborative users.
//        
    }
    
}
