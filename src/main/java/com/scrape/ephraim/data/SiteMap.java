package com.scrape.ephraim.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class SiteMap implements Iterable<Page>
{
    ///the hashtable for the site map
    private HashMap<String, Page> mMap;

    ///the map of the external sites
    private HashMap<String, ExternalSite> mExternals;

    /**
     * Default constructor
     */
    public SiteMap()
    {
        mMap = new HashMap<>();
        mExternals = new HashMap<>();
    }

    @Override
    public Iterator<Page> iterator() {
        return this.mMap.values().iterator();
    }

    /**
     * Adds a new url to the site map
     * Handles all the logic for not double counting.
     * @param page
     */
    public void addPage(Page page)  {
        //the url of the page we're adding
        String url = page.getUrl();

        //this loop will create the corresponding inlinks for each outlink of the page
        //unfortunately, O(N)
        var outLinks = page.getOutLinks();
        for (var link : outLinks) {//go through this page's outlinks
            var targetPage = mMap.get(link);//grab the target page from the map
            //if this page's out link hasn't been indexed yet, index it!
            if (targetPage == null)
            {
                targetPage = new Page(link);
                targetPage.addInLink(url);
                mMap.put(link, targetPage);
            } else {//if the target has already been indexed, simply add an inlink to it
                targetPage.addInLink(url);
            }
        }

        //update the map with the page
        if (mMap.containsKey(url))
        {
            page.setInLinks(mMap.get(url).getInLinks());
        }
        mMap.put(url, page);

        //now we have to keep the running total of the external links
        for (String externalLink : page.getExternalLinks())
        {
            if (mExternals.containsKey(externalLink))
            {
                mExternals.get(externalLink).addOccurrance(url);
            }
            else
            {
                mExternals.put(externalLink, new ExternalSite(externalLink));
            }
        }
    }

    /**
     * Getter for the actual map
     * @return
     */
    public HashMap<String, Page> getMap() {return mMap;}

    /**
     * Getter for the external sites
     * @return
     */
    public HashMap<String, ExternalSite> getExternals() {return mExternals;}

}
