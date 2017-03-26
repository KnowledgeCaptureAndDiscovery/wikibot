package com.wikibot.app;

import com.google.common.collect.ImmutableList;

import com.wikibot.entity.Histogram;
import net.sourceforge.jwbf.core.contentRep.Article;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

/**
 * Hello world!
 *
 */
public class App 
{
    String wikiName = "http://wiki.linked.earth/wiki/";
    MediaWikiBot mediaWikiBot = new MediaWikiBot(wikiName);
//    HttpBot httpBot = new HttpBot(wikiName);


    public void getSampleArticle(String articleName){
        Article mediaArticle = this.mediaWikiBot.getArticle(articleName);
        System.out.println(mediaArticle.getText());
        System.out.println(mediaArticle.getEditor());
        System.out.println(mediaArticle.getEditSummary());
        System.out.println(mediaArticle.getRevisionId());
        
//        mediaWikiBot.getPerformedAction(new GetRevision(MediaWiki.Version.MW1_14, articleName, FIRST));
//        System.out.println(mediaArticle.getEditSummary());
        
        ImmutableList<String> expected = ImmutableList.of(articleName);
        
       // ReviewedPagesTitle 
//        mediaWikiBot.
        
//        mediaArticle.getRevisionId()
//        mediaWikiBot.
//        AllPageTitles t = new AllPageTitles(mediaWikiBot);
//        CategoryMembersFull t = new CategoryMembersFull(mediaWikiBot, "Person_�");
//        Iterator<CategoryItem> it = t.iterator();
//        while(it.hasNext()){
//            CategoryItem b = it.next();
//            System.out.println(b.getTitle());
//        }
        System.out.println("-----------------------");

//        SimpleArticle simpleArticle = this.mediaWikiBot.readData(articleName);
//        System.out.println(simpleArticle.getText().toString());
//        System.out.println("-----------------------");
    }
    public static void main(String[] args) {

        App obj = new App();
        //obj.getSampleArticle("MD982181.Khider.2014");
//        obj.getSampleArticle("D._Khider");
//        obj.getSampleArticle("Category:Marine_Sediment_Working_Group");
        Histogram test = new Histogram();
        test.listAllDataSets();
    }

}
