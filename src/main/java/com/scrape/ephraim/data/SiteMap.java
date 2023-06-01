package com.scrape.ephraim.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class SiteMap implements Iterable<Page>
{
    ///the hashtable for the site map
    private HashMap<String, Page> mMap;

    /**
     * Default constructor
     */
    public SiteMap()
    {
        mMap = new HashMap<>();
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
                Page newPage = new Page(link);
                newPage.addInLink(url);
                mMap.put(link, newPage);
            } else {//if the target has already been indexed, simply add an inlink to it
                targetPage.addInLink(url);
            }
        }

        //update the map with the page
        //even if this page already exists in the map, we need to update it because it doesn't have the outlinks
        mMap.put(url, page);
    }

}
