package com.mainbot.dataobjects;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
//import java.util.stream.Collectors;

/**
 * Class that stores the metadata of a contributor/creator.
 */
public class Agent {
    private String name;
    private int agentId;
    private ArrayList<Article> contributedArticles;

    public Agent() {
    }

    public Agent(String name, ArrayList<Article> contributedArticles) {
        this.name = name;
        this.contributedArticles = contributedArticles;
    }

    public ArrayList<Article> getContributedArticles() {
        if(contributedArticles == null){
            contributedArticles = new ArrayList<>();
        }
        return contributedArticles;
    }

    public String getName() {
        return name;
    }

    public int getAgentId() {
        return agentId;
    }
    
  /*  public List<String>getContributedCategories(){
        List<String> contrCategories = new ArrayList<>();
        getContributedArticles().stream().forEach((art) -> {
                art.getCategories().stream().forEach((currCat) -> {
                    if(!contrCategories.contains(currCat)){
                        contrCategories.add(currCat);
                    }
                });
            });
        return contrCategories;
    }*/
    
    /**
     * Given a category, this method returns the contributed articles of this person to that category.
     * @param category
     * @return 
     */
   /* public List<Article> getContributedArticlesFromCategory(String category){
        return getContributedArticles().stream().filter(a -> a.getCategories().contains(category)).collect(Collectors.toList());
    }
   */
    /**
     * Method that returns a map with the id of a collaborator plus the number of articles they have contributed to together.
     * @param category category for filtering. null by default
     * @return 
     */
  /*  public HashMap<Integer, Integer> getCollaboratorsAndFrequencies(String category){
        List<Article> collectionToBrowse = this.contributedArticles;
        if(category!=null){
           collectionToBrowse = collectionToBrowse.stream().filter((article)->article.getCategories().contains(category)).collect(Collectors.toList());
        }
        HashMap<Integer,Integer> collaboration = new HashMap();
        collectionToBrowse.stream().forEach((article) -> {
            article.getContributors().stream().forEach((ag) -> {
                //System.out.println("Agent "+this.name+ " -- "+ ag.getName() + collaboration.get(ag.getAgentId()));
                if(collaboration.containsKey(ag.getAgentId())){
                    collaboration.put(ag.getAgentId(), collaboration.get(ag.getAgentId())+1);
                }else{
                    if(ag.getAgentId()!=this.getAgentId()){//we don't want self-colaborations
                        collaboration.put(ag.getAgentId(), 1);
                    }
                }
            });
        });
        return collaboration;
    }
*/
    public void setContributedArticles(ArrayList<Article> contributedArticles) {
        this.contributedArticles = contributedArticles;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGetAgentId(int getAgentId) {
        this.agentId = getAgentId;
    }
    
    public void addContributedArticle(Article a){
        if(this.contributedArticles==null){
            contributedArticles = new ArrayList<>();
        }
        contributedArticles.add(a);
    }
    
    
}
