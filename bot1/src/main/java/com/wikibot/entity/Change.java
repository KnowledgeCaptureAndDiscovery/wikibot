//add license here
package com.wikibot.entity;

/**
 * A change is an edit done to an article.
 * 
 * Take into account that depending on the wiki size, it may be unwise to store
 * all edit history in memory.
 * @author dgarijo
 */
public class Change {
    private Agent changeContributor;
    private String timeOfChange;

    public Change() {
    }

    public Change(Agent changeContributor, String timeOfChange) {
        this.changeContributor = changeContributor;
        this.timeOfChange = timeOfChange;
    }
    
    

    public Agent getChangeContributor() {
        return changeContributor;
    }

    public String getTimeOfChange() {
        return timeOfChange;
    }

    public void setChangeContributor(Agent changeContributor) {
        this.changeContributor = changeContributor;
    }

    public void setTimeOfChange(String timeOfChange) {
        this.timeOfChange = timeOfChange;
    }
    
    
    
}
