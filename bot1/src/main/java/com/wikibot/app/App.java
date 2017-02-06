package com.wikibot.app;

import net.sourceforge.jwbf.core.contentRep.Article;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main(String[] args) {
        MediaWikiBot wikiBot = new MediaWikiBot("https://en.wikipedia.org/w/");
        Article article = wikiBot.getArticle("1");
        System.out.println(article.getText().substring(0,20));
        // HITCHHIKER'S GUIDE TO THE GALAXY FANS
        applyChangesTo(article);
        wikiBot.login("user", "***");
       // article.save();
    }

    static void applyChangesTo(Article article) {
        // edits the article...
    }
}
