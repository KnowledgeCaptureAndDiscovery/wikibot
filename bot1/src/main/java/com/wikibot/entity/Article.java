//add proper license here!
package com.wikibot.entity;

import java.util.ArrayList;

/**
 * Class that stores the metadata associated to a wiki article
 * @author dgarijo
 */
public class Article {
    private String name;
    private int pageID; //easier for retrieving page stuff
    private ArrayList<String> categories;
    private Agent creator;
    private ArrayList<Agent> contributors;
    private String creationTime;
    private ArrayList<Change> editHistory;
    private int numberOfEdits; //easier for counting

    public Article() {
    }

    public String getCreationTime() {
        return creationTime;
    }

    public Agent getCreator() {
        return creator;
    }
    
    public ArrayList<Agent> getContributors(){
        return contributors;
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getCategories() {
        if(this.categories == null){
            categories = new ArrayList<>();
        }
        return categories;
    }

    public ArrayList<Change> getEditHistory() {
        return editHistory;
    }

    public int getPageID() {
        return pageID;
    }

    public void setPageID(int pageID) {
        this.pageID = pageID;
    }

    
    public void setContributors(ArrayList<Agent> contributors) {
        this.contributors = contributors;
    }

    public void setCategories(ArrayList<String> categories) {
        this.categories = categories;
    }

    public void setEditHistory(ArrayList<Change> editHistory) {
        this.editHistory = editHistory;
    }
    
    

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public void setCreator(Agent creator) {
        this.creator = creator;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    
//    public ArrayList<Agent> getContributors(){
//        ArrayList<Agent> contr= new ArrayList();
//        for(Change c:this.editHistory){
//            contr.add(c.getChangeContributor());
//        }
//        return contr;
//    }
    
//    public String getLastModificationTime{
//    
//    }
}
