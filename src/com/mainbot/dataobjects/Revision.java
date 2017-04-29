package com.mainbot.dataobjects;

public class Revision {
	Article article;
	Agent user;
	String timestamp;
	int revid;
	int old_revid;	
	String type;
	
	public Revision(int pageID, String title, String user, String timestamp, int revid,
			int old_revid, String type) {
		
		this.user = new Agent(); // change to retrieval from the map
		this.user.setName(user);
		this.article = new Article(); // change to retrieval from the map
		this.article.setName(title);
		this.article.setPageID((pageID));
		
		this.timestamp = timestamp;
		this.revid = revid;
		this.old_revid = old_revid;
		this.type = type;
	}

	public String getTimestampDate() {
		
		return timestamp.substring(0, 10);
	}
	
	public String getTimestampTime() {
		
		return timestamp.substring(11,19);
	}

	public Article getArticle() {
		return article;
	}

	public void setArticle(Article article) {
		this.article = article;
	}

	public Agent getUser() {
		return user;
	}

	public void setUser(Agent user) {
		this.user = user;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	

}
