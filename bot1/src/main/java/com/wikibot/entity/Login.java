package com.wikibot.entity;

import static com.wikibot.app.Constants.FORMAT_AND_API;
import static com.wikibot.app.Constants.WIKI_NAME;

import org.json.JSONObject;

import com.wikibot.app.Utils;

public class Login {
	String lgname;
	String lgpassword;
	String lgcookie;
	
	
	public Login(String lgname, String lgpassword){
		this.lgname = lgname;
		this.lgpassword = lgpassword;
	}


	public String getLgcookie() {
		return lgcookie;
	}


	public void setLgcookie(String lgcookie) {
		this.lgcookie = lgcookie;
	}
	
	public void login(){
		String tokenQuery = WIKI_NAME
				+ "api.php?action=login&format=json&lgname=" + this.lgname;
		/* Cookie parameter is empty because no session is set */
		JSONObject getToken = Utils.doPOSTJSON(tokenQuery, "");

		String token = getToken.getJSONObject("login").get("token").toString();
		String sessionid = getToken.getJSONObject("login").get("sessionid").toString();
		String cookieprefix = getToken.getJSONObject("login").get("cookieprefix").toString();

		/* Set the cookie string to be sent in the subsequent post requests */
		setLgcookie(cookieprefix + "=" + sessionid);

		/* Login along with the token id */
		String loginQuery = tokenQuery + "&lgpassword=" + this.lgpassword + "&lgtoken=" + token;
		JSONObject login = Utils.doPOSTJSON(loginQuery, this.lgcookie);
		System.out.println(login.toString());

		/* Assertion to check if the login is successful */
		String user = WIKI_NAME + FORMAT_AND_API + "&assert=user";
		System.out.println(Utils.doPOSTJSON(user, this.lgcookie).toString());
	}	
	
	public void edit(String edittext){
		String tokenQuery2 = WIKI_NAME + FORMAT_AND_API
				+ "&format=json&meta=tokens";
		JSONObject getToken2 = Utils.doPOSTJSON(tokenQuery2, this.getLgcookie());
		String token2 = getToken2.getJSONObject("query")
				.getJSONObject("tokens").get("csrftoken").toString();
		System.out.println(token2);

		String editQuery = WIKI_NAME
				+ "api.php?action=edit&title=Test&section=new&sectiontitle=EditAPITest&text="+edittext+"&format=json";
		JSONObject edit = Utils.postFuncWithParams(editQuery, this.getLgcookie(), token2);
		System.out.println(edit);
	}

	public void undoRevisions(int revid, boolean undoafter){
		String tokenQuery2 = WIKI_NAME + FORMAT_AND_API
				+ "&format=json&meta=tokens";
		JSONObject getToken2 = Utils.doPOSTJSON(tokenQuery2, this.getLgcookie());
		String token2 = getToken2.getJSONObject("query")
				.getJSONObject("tokens").get("csrftoken").toString();
		System.out.println(token2);
		
		String undoQuery = WIKI_NAME + "api.php?action=edit&title=Test&format=json";
		
		if(undoafter)
			undoQuery += "&undoafter="+revid+"&text=Hello";
		else
			undoQuery += "&undo="+revid;
		JSONObject edit = Utils.postFuncWithParams(undoQuery, this.getLgcookie(), token2);
		System.out.println(edit);
	}
}
