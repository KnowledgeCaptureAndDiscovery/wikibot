//add proper license here
package com.wikibot.entity;

import java.util.ArrayList;

/**
 * Class that stores the metadata of a contributor/creator.
 * @author dgarijo
 */
public class Agent {
    private String name;
    private int getAgentId;
    private ArrayList<Article> contributedArticles;
    private ArrayList<String> workingGroups;

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

    public int getGetAgentId() {
        return getAgentId;
    }

    public void setContributedArticles(ArrayList<Article> contributedArticles) {
        this.contributedArticles = contributedArticles;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGetAgentId(int getAgentId) {
        this.getAgentId = getAgentId;
    }
    
    public void addContributedArticle(Article a){
        if(this.contributedArticles==null){
            contributedArticles = new ArrayList<Article>();
        }
        contributedArticles.add(a);
    }
    
    
}
