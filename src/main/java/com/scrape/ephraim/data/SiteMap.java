package com.scrape.ephraim.data;

import java.util.HashMap;
import java.util.Iterator;

import javafx.scene.control.TableView;

public class SiteMap implements Iterable<Page>
{
    ///the hashtable for the site map
    private HashMap<String, Page> mMap;

    ///the map of the external sites
    private HashMap<String, ExternalSite> mExternals;

    ///association to the inLinks observer
    private TableView<Page> mObserverInternalLinks;

    ///association to the external sites observer
    private TableView<ExternalSite> mObserverExternals;

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

        mObserverInternalLinks.getItems().add(page);

        //now we have to keep the running total of the external links
        for (String externalLink : page.getExternalLinks())
        {
            if (mExternals.containsKey(externalLink))
            {
                mExternals.get(externalLink).addOccurrence(url);
            }
            else
            {
                ExternalSite obj = new ExternalSite(externalLink);
                obj.setObserverExternals(mObserverExternals);
                obj.addOccurrence(page.getUrl());
                mExternals.put(externalLink, obj);
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

    /**
     * Setter for the internal links observer association
     * @param inLinks a table
     */
    public void setObserverInternalLinks(TableView<Page> inLinks) {mObserverInternalLinks = inLinks;}

    /**
     * Setter for the external observer association
     * @param externals a table
     */
    public void setObserverExternals(TableView<ExternalSite> externals) {mObserverExternals = externals;}

    /**
     * Gets the association
     * @return a table
     */
    public TableView<ExternalSite> getObserverExternals() {return mObserverExternals;}


}
