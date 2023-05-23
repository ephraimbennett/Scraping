package com.scrape.ephraim.data;

import java.util.HashSet;
import java.util.List;

public class Page
{
    ///this page's url
    private String mUrl;

    ///list of in link
    private HashSet<String> mInLinks;

    ///list of out links
    private HashSet<String> mOutLinks;

    /**
     * Constructor
     * @param url
     */
    public Page(String url)
    {
        mUrl = url;
        mInLinks = new HashSet<>();
        mOutLinks = new HashSet<>();
    }

    /**
     * adds an inlink to the page
     * @param inLink
     */
    public void addInLink(String inLink)
    {
        mInLinks.add(inLink);
    }

    /**
     * Sets the outlinks
     * converts it from a list to a hash set
     * @param links
     */
    public void setOutLinks(List<String> links)
    {
        mOutLinks = new HashSet<>(links);
    }

    /**
     * getter for the outlinks
     * @return
     */
    public HashSet<String> getOutLinks() {return mOutLinks;}

    /**
     * getter for the url
     * @return hmm...
     */
    public String getUrl() {return mUrl;}
}
