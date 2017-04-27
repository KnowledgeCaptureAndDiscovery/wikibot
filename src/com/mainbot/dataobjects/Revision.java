package com.mainbot.dataobjects;

public class Revision {
	String pageid;
	String timestamp;
	String revid;
	String old_revid;	
	String type;
	
	public Revision(String pageid, String timestamp, String revid,
			String old_revid, String type) {
		super();
		this.pageid = pageid;
		this.timestamp = timestamp;
		this.revid = revid;
		this.old_revid = old_revid;
		this.type = type;
	}
	
	public String getPageid() {
		return pageid;
	}

	public String getTimestampDate() {
		
		return timestamp.substring(0, 10);
	}
	
	public String getTimestampTime() {
		
		return timestamp.substring(11,19);
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "\nPage ID: " + this.pageid + "\t Timestamp: " + this.timestamp + "\t Revid: " + this.revid; 
	}	
	
	
}
