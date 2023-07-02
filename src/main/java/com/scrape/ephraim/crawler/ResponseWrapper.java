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

    ///the response code we're given
    private int mResponseCode;

    ///the type of the content
    private String mType;

    ///the size in bytes
    private int mSize;

    public ResponseWrapper(Document doc, HashMap<String, String> headers, String url, int code, String type, int size)
    {
        mDocument = doc;
        mHeaders = headers;
        mUrl = url;
        mResponseCode = code;
        mType = type;
        mSize = size;
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

    /**
     * Getter for the response code
     * @return
     */
    public int getResponseCode() {return mResponseCode;}

    /**
     * Getter for the type
     * @return
     */
    public String getType() {return mType;}

    /**
     * Getter for the size
     * @return
     */
    public int getSize() {return mSize;}
}
