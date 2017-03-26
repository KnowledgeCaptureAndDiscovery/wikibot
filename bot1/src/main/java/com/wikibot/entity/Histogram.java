package com.wikibot.entity;

import com.wikibot.app.Constants;
import com.wikibot.app.Utils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLEncoder;

import static com.google.common.net.HttpHeaders.USER_AGENT;
import static com.wikibot.app.Constants.CATEGORY_DATASET;
import static com.wikibot.app.Constants.FORMAT_AND_API;
import static com.wikibot.app.Constants.WIKI_NAME;

/**
 * Created by neha on 3/25/17.
 */

//provides histogram for the semantic wiki
public class Histogram {
    //Get all the datasets in the JSON format
    public void listAllDataSets() {
        JSONObject datasets = Utils.doGETJSON(Constants.QUERY_ALL_DATASET);
        processDatasets(datasets);

    }

    //Extract each dataset from the json object
    public void processDatasets(JSONObject datasets) {
        JSONArray dataArray = datasets.getJSONObject("query").getJSONArray("allpages");
        for (int i=0;i<dataArray.length(); i++) {
            JSONObject currDataset = dataArray.getJSONObject(i);
            getLatestEdits(currDataset);
        }
    }

    //Get the revision edits for a particular dataset
    public void getLatestEdits(JSONObject data) {
        int limit = 5;
        String rvLimit = String.valueOf(limit);
        String pageId = data.get("pageid").toString();
        String editQuery = "http://wiki.linked.earth/wiki/" + FORMAT_AND_API + "&prop=revisions&pageids=" + pageId + "&rvlimit=" + rvLimit;
        JSONArray array;
        try{
            //String encodedURL = URLEncoder.encode(editQuery, "UTF-8");
            JSONObject latestEdits = Utils.doGETJSON(editQuery);
            array = latestEdits.getJSONObject("query").getJSONObject("pages").getJSONObject(pageId).getJSONArray("revisions");
            System.out.println(pageId + ":" + array.toString());
        }
        catch(Exception e){
            e.printStackTrace();
        }



    }
}