package com.scrape.ephraim.crawler;

import org.jsoup.nodes.Document;

import java.util.HashMap;

public class ResponseWrapper
{
    ///the document
    Document mDocument;

    ///the headers
    HashMap<String, String> mHeaders;

    ///the url for safekeeping
    private String mUrl;

    public ResponseWrapper(Document doc, HashMap<String, String> headers, String url)
    {
        mDocument = doc;
        mHeaders = headers;
        mUrl = url;
    }

    /**
     * Getter for the document
     * @return
     */
    public Document getDocument() {return mDocument;}

    /**
     * Getter for the headers
     * @return
     */
    public HashMap<String, String> getHeaders() {return mHeaders;}

    /**
     * Getter for the url
     * @return
     */
    public String getUrl() {return mUrl;}
}
