
package com.wikibot.entity;

import com.wikibot.app.Constants;
import com.wikibot.app.Utils;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Given a category, this class will explore all subcategories and list their definition from the wiki.
 * Only categories, not properties.
 * @author dgarijo
 */
public class CategoryDefinition {
    public static final String queryCategories = "http://wiki.linked.earth/wiki/api.php?action=query&cmtype=subcat&list=categorymembers&cmlimit=100&format=json&cmtitle=";
    public static final String queryCategoryDef = "http://wiki.linked.earth/wiki/api.php?&action=parse&prop=text&section=1&format=json&page=";
    public static final String queryCategoryDefAllText = "http://wiki.linked.earth/wiki/api.php?&action=parse&prop=text&format=json&page=";
    
    private static void listSubCategories(String categoryTitle){
        //initial query
        JSONObject art = Utils.doGETJSON(queryCategories+categoryTitle.replace(" ", "_"));
        //retrieve categories from json
        JSONArray categories = art.getJSONObject("query").getJSONArray("categorymembers");
        for (Object cat:categories){
            String t = ((JSONObject)cat).getString("title").replace(" ", "_");
            //System.out.println(queryCategoryDef+t);
            System.out.println(t);
            try{
                JSONObject def = Utils.doGETJSON(queryCategoryDef+t);
                //System.out.println(def.getJSONObject("parse").getJSONObject("text").getString("*"));
            }catch(Exception e){
                //no section 1 available
                JSONObject def = Utils.doGETJSON(queryCategoryDefAllText+t);
                //System.out.println(def.getJSONObject("parse").getJSONObject("text").getString("*"));
            }
            listSubCategories(t);
        }
        //recursively call method
        
    }
    
    private static boolean hasNext(JSONObject o){
        try{
            o.getJSONObject("query-continue");
            return true;
        }catch(JSONException e){
            return false;
        }
    }
    
    private static int countMembers(String category) throws UnsupportedEncodingException{
        int count = 0;
        String c = category.replace(" ", "_");
        JSONObject art = Utils.doGETJSON("http://wiki.linked.earth/wiki/api.php?action=query&list=categorymembers&cmlimit=500&format=json&rawcontinue&cmtitle="+c);
        while(hasNext(art)){
            //prepare next batch
            count+= 500;
            String cont = URLEncoder.encode(art.getJSONObject("query-continue").getJSONObject("categorymembers").getString("cmcontinue"), "UTF-8");
            //System.out.println(cont);
            art = Utils.doGETJSON("http://wiki.linked.earth/wiki/api.php?action=query&list=categorymembers&cmlimit=500&format=json&cmtitle="+c+"&rawcontinue&cmcontinue="+cont);
        }
        //count the rest
        count+=art.getJSONObject("query").getJSONArray("categorymembers").length();
        return count;
    }
    
    private static int countArticlesOfCategory(String categoryTitle) throws UnsupportedEncodingException{
        //initial query
        JSONObject art = Utils.doGETJSON(queryCategories+categoryTitle.replace(" ", "_"));
        //retrieve categories from json
        int total = countMembers(categoryTitle);
        JSONArray categories = art.getJSONObject("query").getJSONArray("categorymembers");
        for (Object cat:categories){
            String t = ((JSONObject)cat).getString("title").replace(" ", "_");
            //System.out.println(queryCategoryDef+t);
            //System.out.println(t);
            total+= countArticlesOfCategory(t);
        }
        return total;
    }
    
    public static void main(String[] args){
        try{
            System.out.println("Datasets: "+countArticlesOfCategory("Category:Dataset_(L)"));
            System.out.println("Proxy Acrhive: "+countArticlesOfCategory("Category:ProxyArchive_(L)"));
            System.out.println("Working Group: "+countArticlesOfCategory("Category:Working_Group"));
            System.out.println("Proxy Observation: "+countArticlesOfCategory("Category:ProxyObservation_(L)"));
            System.out.println("Proxy Sensor: "+countArticlesOfCategory("Category:ProxySensor_(L)"));
            System.out.println("Instrument: "+countArticlesOfCategory("Category:Instrument_(L)"));
            System.out.println("InferredVariable: "+countArticlesOfCategory("Category:InferredVariable_(L)"));
            System.out.println("MeasuredVariable: "+countArticlesOfCategory("Category:MeasuredVariable_(L)"));
            System.out.println("Locations: "+countArticlesOfCategory("Category:Location_(L)"));
            System.out.println("Person: "+countArticlesOfCategory("Category:Person_(L)"));
            System.out.println("Publication: "+countArticlesOfCategory("Category:Publication_(L)"));

        }catch(Exception e){
            System.out.println("Error "+e.getMessage()); 
        }
//        listSubCategories("Category:ProxyObservation_%C2%A9");
//        listSubCategories("Category:InferredVariable_(L)");
//System.out.println("<h3><span class=\"mw-headline\" id=\"d18O\">d18O</span><span class=\"mw-editsection\"><span class=\"mw-editsection-bracket\">[</span><a href=\"/wiki/index.php?title=D18O&amp;action=edit&amp;section=1\" title=\"Edit section: d18O\">edit</a><span class=\"mw-editsection-bracket\">]</span></span></h3>\n<p>Oxygen has three naturally-occuring stable <a href=\"/Category:Isotope\" title=\"Category:Isotope\"> isotopes</a>: <sup>16</sup>O, <sup>17</sup>O, <sup>18</sup>O, with <sup>16</sup>O being the most abundant (99.762%). \n</p><p>Two international reference <a href=\"/wiki/index.php?title=Standard&amp;action=edit&amp;redlink=1\" class=\"new\" title=\"Standard (page does not exist)\"> standards</a> are used to report variations in oxygen isotope standards: <a href=\"/wiki/index.php?title=PDB&amp;action=edit&amp;redlink=1\" class=\"new\" title=\"PDB (page does not exist)\">PDB</a> and <a href=\"/wiki/index.php?title=SMOW&amp;action=edit&amp;redlink=1\" class=\"new\" title=\"SMOW (page does not exist)\">SMOW</a>. The use of the <a href=\"/wiki/index.php?title=PDB&amp;action=edit&amp;redlink=1\" class=\"new\" title=\"PDB (page does not exist)\">PDB</a> standard in reporting oxygen isotope composition is restricted to carbonates of low-temperature origins (e.g., <a href=\"/wiki/index.php?title=Marine_sediment&amp;action=edit&amp;redlink=1\" class=\"new\" title=\"Marine sediment (page does not exist)\"> oceanic</a>, <a href=\"/wiki/index.php?title=Lake_sediment&amp;action=edit&amp;redlink=1\" class=\"new\" title=\"Lake sediment (page does not exist)\"> lacustrine </a>). The conversion between SMOW and PDB scales is given by: \n</p>\n<div style=\"text-align: center;\"> <img class=\"mwe-math-fallback-image-inline tex\" alt=\" \\delta^{18}O_{SMOW} = 1.03091 (\\delta^{18}O_{PDB}) +30.91 \" src=\"/wiki/images/math/d/0/4/d04245002041592a6bb80a9adfc4e62d.png\" /> </div>\n<p>&#948;<sup>18</sup>O may be measured on:\n</p>\n<ul><li>the <a href=\"/Stable_oxygen_isotopes_in_foraminifera\" title=\"Stable oxygen isotopes in foraminifera\"> shells</a> of <a href=\"/Category:Foraminifera\" title=\"Category:Foraminifera\"> foraminifera</a></li></ul>\n\n<!-- \nNewPP limit report\nCached time: 20170405035940\nCache expiry: 86400\nDynamic content: false\nCPU time usage: 0.021 seconds\nReal time usage: 0.025 seconds\nPreprocessor visited node count: 6/1000000\nPreprocessor generated node count: 34/1000000\nPost\u2010expand include size: 0/2097152 bytes\nTemplate argument size: 0/2097152 bytes\nHighest expansion depth: 2/40\nExpensive parser function count: 0/100\n-->\n\n<!-- \nTransclusion expansion time report (%,ms,calls,template)\n100.00%    0.000      1 - -total\n-->\n");
//        String categoryToExplore = "ProxyArchive";//MUST BELONG TO CORE. OTHERWISE MUST REMOVE THE %C2%A9
//        String command = "http://wiki.linked.earth/wiki/api.php?action=query&cmtype=subcat&list=categorymembers&cmlimit=100&cmtitle=Category:"+categoryToExplore+"_%C2%A9";
        
//        System.out.println("<h3><span class=\"mw-headline\" id=\"Category:_Coral_.5B1.5D\">Category: Coral <a rel=\"nofollow\" class=\"external autonumber\" href=\"http://linked.earth/ontology#Coral\">[1]</a></span><span class=\"mw-editsection\"><span class=\"mw-editsection-bracket\">[</span><a href=\"/wiki/index.php?title=Category:Coral&amp;action=edit&amp;section=1\" title=\"Edit section: Category: Coral [1]\">edit</a><span class=\"mw-editsection-bracket\">]</span></span></h3>\n<p>Imported from: <a rel=\"nofollow\" class=\"external text\" href=\"http://linked.earth/ontology#\">core:Coral</a> (<a rel=\"nofollow\" class=\"external text\" href=\"http://linked.earth/ontology#\">Linked Earth Core</a>)\n</p><p>The geochemical tracers contain in the skeletons of <a rel=\"nofollow\" class=\"external text\" href=\"https://en.wikipedia.org/wiki/Coral\">corals</a> provide an unaltered record of the chemical and physical conditions that existed in the surrounding seawater at the time of accretion of its calcium carbonate skeleton <sup id=\"cite_ref-druffel1997_1-0\" class=\"reference\"><a href=\"#cite_note-druffel1997-1\">[1]</a></sup>. Corals are useful oceanic recorders because they are widely distributed, can be accurately dated, provide an  enhanced time resolution (monthly) available from the high growth rate, and are nor subjected to the mixing processes that are present in all toxic sediments (i.e., bioturbation) <sup id=\"cite_ref-druffel1997_1-1\" class=\"reference\"><a href=\"#cite_note-druffel1997-1\">[1]</a></sup> <sup id=\"cite_ref-2\" class=\"reference\"><a href=\"#cite_note-2\">[2]</a></sup>.\n</p><p>Corals are from the order Scleractinian, a group in the subclass Zoantharia. Scleractinians include solitary and colonial species of corals. may of which secrete external skeletons of <a rel=\"nofollow\" class=\"external text\" href=\"https://en.wikipedia.org/wiki/Aragonite\">aragonite</a> <sup id=\"cite_ref-druffel1997_1-2\" class=\"reference\"><a href=\"#cite_note-druffel1997-1\">[1]</a></sup>. The oldest known scleractinians are shallow water corals from the Middle Triassic <sup id=\"cite_ref-3\" class=\"reference\"><a href=\"#cite_note-3\">[3]</a></sup>.\n</p>\nThe polyp portion of the coral secretes calcium carbonate (CaCO<sub>3</sub>) as the mineral aragonite <sup id=\"cite_ref-druffel1997_1-3\" class=\"reference\"><a href=\"#cite_note-druffel1997-1\">[1]</a></sup>. Massive <a rel=\"nofollow\" class=\"external text\" href=\"https://en.wikipedia.org/wiki/Hermatypic_coral\">hermatypic corals</a> (i.e., reef-building corals) are more desirable than the branching varieties for paleoreconstructions. First, massive corals form round, wave-resistant structures that can include hundreds of years of uninterrupted growth. Second, the accretion rate of calcium carbonate is much higher for hermatypic corals  that contain symbiotic <a rel=\"nofollow\" class=\"external text\" href=\"https://en.wikipedia.org/wiki/Zooxanthellae\">zooxanthellae</a> than for deep species <sup id=\"cite_ref-druffel1997_1-4\" class=\"reference\"><a href=\"#cite_note-druffel1997-1\">[1]</a></sup>. Most massive reef corals live at water depths of &lt;40m and grow continuously at rated of 6-20 mm yr<sup>-1</sup> <sup id=\"cite_ref-4\" class=\"reference\"><a href=\"#cite_note-4\">[4]</a></sup><ol class=\"references\">\n<li id=\"cite_note-druffel1997-1\"><span class=\"mw-cite-backlink\">\u2191 <sup><a href=\"#cite_ref-druffel1997_1-0\">1.0</a></sup> <sup><a href=\"#cite_ref-druffel1997_1-1\">1.1</a></sup> <sup><a href=\"#cite_ref-druffel1997_1-2\">1.2</a></sup> <sup><a href=\"#cite_ref-druffel1997_1-3\">1.3</a></sup> <sup><a href=\"#cite_ref-druffel1997_1-4\">1.4</a></sup></span> <span class=\"reference-text\">Druffel, E. R. M. (1997). Geochemistry of corals: Proxies of past ocean chemistry, ocean circulation, and climate. Proceeding of the National Academy of Sciences, 94, 8354-8361. </span>\n</li>\n<li id=\"cite_note-2\"><span class=\"mw-cite-backlink\"><a href=\"#cite_ref-2\">\u2191</a></span> <span class=\"reference-text\"> Gagan, M. K., Ayliffe, L. K., Beck, J. W., Cole, J. E., Druffel, E. R. M., Dunbar, R. B., &amp; Schrag, D. P. (2000). New views of tropical paleoclimates from corals. Quaternary Science Reviews, 19(1-5), 45-64. doi:10.1016/S0277-3791(99)00054-2</span>\n</li>\n<li id=\"cite_note-3\"><span class=\"mw-cite-backlink\"><a href=\"#cite_ref-3\">\u2191</a></span> <span class=\"reference-text\"> Stanley, G. (1981). Early history of scleractinian corals and its geological consequences. Geology, 9, 507-511. </span>\n</li>\n<li id=\"cite_note-4\"><span class=\"mw-cite-backlink\"><a href=\"#cite_ref-4\">\u2191</a></span> <span class=\"reference-text\">Knutson, D. W., Buddemeier, R. W., &amp; Smith, S. V. (1972). Coal chronologies: seasonal growth bands in reef corals. Science, 177, 270-272. </span>\n</li>\n</ol>\n\n<!-- \nNewPP limit report\nCached time: 20170404042940\nCache expiry: 86400\nDynamic content: false\nCPU time usage: 0.057 seconds\nReal time usage: 0.064 seconds\nPreprocessor visited node count: 100/1000000\nPreprocessor generated node count: 534/1000000\nPost\u2010expand include size: 0/2097152 bytes\nTemplate argument size: 0/2097152 bytes\nHighest expansion depth: 3/40\nExpensive parser function count: 0/100\n-->\n\n<!-- \nTransclusion expansion time report (%,ms,calls,template)\n100.00%    0.000      1 - -total\n-->\n");
    }
}
