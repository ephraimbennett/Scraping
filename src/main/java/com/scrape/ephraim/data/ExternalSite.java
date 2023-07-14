package com.scrape.ephraim.data;

import com.scrape.ephraim.crawler.ResponseWrapper;
import org.jsoup.Connection;

import java.util.HashMap;
import java.util.HashSet;

public class ExternalSite
{
    ///the url of the external site
    private String mUrl;

    ///the pages which link to this external site
    private HashMap<String, Integer> mInLinks;

    ///this external site's response code
    private int mResponseCode;

    ///this external site's http headers
    private HashMap<String, String> mHeaders;

    ///the content type of this site
    private String mContentType;

    ///size of the site
    private int mSize;

    /**
     * Constructor
     * @param url the url
     */
    public ExternalSite(String url)
    {
        mUrl = url;
        mInLinks = new HashMap<>();
    }

    public void addOccurrance(String url)
    {
        if (mInLinks.containsKey(url))
        {
            mInLinks.put(url, mInLinks.get(url) + 1);
        }
        else
        {
            mInLinks.put(url, 1);
        }
    }

    /**
     * Store the data after this page is visited.
     * @param response
     */
    public void processResponse(ResponseWrapper response)
    {
        mResponseCode = response.getResponseCode();
        mHeaders = response.getHeaders();
        mContentType = response.getType();
        mSize = response.getSize();
    }

    /**
     * Getter for the url
     * @return
     */
    public String getUrl()
    {
        return mUrl;
    }

    /**
     * Getter for inlinks size
     * @return
     */
    public int getOccurrences() {return mInLinks.size();}

    /**
     * Getter for the inlinks
     * @return
     */
    public HashMap<String, Integer> getInLinks() {return mInLinks;}

    /**
     * Getter for the headers
     * @return the headers
     */
    public HashMap<String, String> getHeaders() {return mHeaders;}

    /**
     * Getter for the response code
     * @return the response code
     */
    public int getResponseCode() {return mResponseCode;}

    /**
     * Getter for the content type
     * @return type
     */
    public String getType() {return mContentType;}

    /**
     * Getter for the size
     * @return the size
     */
    public int getSize() {return mSize;}


}
