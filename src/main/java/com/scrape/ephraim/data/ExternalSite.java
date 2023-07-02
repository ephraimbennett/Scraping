package com.scrape.ephraim.data;

import java.util.HashMap;
import java.util.HashSet;

public class ExternalSite
{
    ///the url of the external site
    private String mUrl;

    ///the pages which link to this external site
    private HashMap<String, Integer> mInLinks;

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
}
