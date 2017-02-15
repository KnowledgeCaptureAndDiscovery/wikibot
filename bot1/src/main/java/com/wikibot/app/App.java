package com.wikibot.app;

import com.fasterxml.jackson.databind.util.JSONPObject;
import jdk.nashorn.internal.parser.JSONParser;
import net.sourceforge.jwbf.core.actions.GetPage;
import net.sourceforge.jwbf.core.actions.RequestBuilder;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.core.bots.HttpBot;
import net.sourceforge.jwbf.core.contentRep.Article;
import net.sourceforge.jwbf.core.contentRep.SimpleArticle;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import net.sourceforge.jwbf.mediawiki.contentRep.CategoryItem;

import java.util.Locale;

/**
 * Hello world!
 *
 */
public class App 
{
    String wikiName = "http://wiki.linked.earth/wiki/";
    MediaWikiBot mediaWikiBot = new MediaWikiBot(wikiName);
    HttpBot httpBot = new HttpBot(wikiName);


    public void getSampleArticle(String articleName){
        Article mediaArticle = this.mediaWikiBot.getArticle(articleName);
        System.out.println(mediaArticle.getText());
        System.out.println("-----------------------");

        SimpleArticle simpleArticle = this.mediaWikiBot.readData(articleName);
        System.out.println(simpleArticle.getText().toString());
        System.out.println("-----------------------");
    }
    public static void main(String[] args) {

        App obj = new App();
        obj.getSampleArticle("BJ8-03-13GGC.Linsley.2010");
    }

}
