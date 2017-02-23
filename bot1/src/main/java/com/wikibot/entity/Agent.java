//add proper license here
package com.wikibot.entity;

import java.util.ArrayList;

/**
 * Class that stores the metadata of a contributor/creator.
 * @author dgarijo
 */
public class Agent {
    private String name;
    private ArrayList<Article> contributedArticles;
    private ArrayList<String> workingGroups;

    public Agent() {
    }

    public Agent(String name, ArrayList<Article> contributedArticles) {
        this.name = name;
        this.contributedArticles = contributedArticles;
    }

    public ArrayList<Article> getContributedArticles() {
        return contributedArticles;
    }

    public String getName() {
        return name;
    }

    public void setContributedArticles(ArrayList<Article> contributedArticles) {
        this.contributedArticles = contributedArticles;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    
}
