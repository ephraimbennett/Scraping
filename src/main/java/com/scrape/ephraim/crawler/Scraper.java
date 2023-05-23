package com.scrape.ephraim.crawler;

import com.scrape.ephraim.data.Page;
import com.scrape.ephraim.data.SiteMap;
import org.jsoup.nodes.Document;

import java.util.HashMap;
import java.util.List;

public class Scraper
{
    ///composite link parser
    private LinkParser mLinkParser;

    ///association to the site map
    private SiteMap mSiteMap;

    ///the domain name
    private String mDomain;


    /**
     * Constructor
     */
    public Scraper(String domain)
    {
        mLinkParser = new LinkParser();
        mDomain = domain;
        mSiteMap = new SiteMap();
    }

    /**
     * Scrapes the webpage
     * @param document
     */
    public void scrapePage(Document document, String url)
    {
        //parse the links
        mLinkParser.clear();
        mLinkParser.setDomainName(mDomain);
        mLinkParser.parse(document);

        //make a page object for this specific url
        Page page = new Page(url);
        page.setOutLinks(mLinkParser.getInternalLinks());

        mSiteMap.addPage(page);

    }

    /**
     * Composite setter for link parser
     * @param parentUrl
     */
    public void setParentUrl(String parentUrl)
    {
        mLinkParser.setParentUrl(parentUrl);
    }

    /**
     * getter for the internal links
     * @return hmmmm
     */
    public List<String> getInternalLinks()
    {
        return mLinkParser.getInternalLinks();
    }



}
