package com.scrape.ephraim;

import org.jsoup.nodes.Document;

import java.util.List;

public class Scraper
{
    ///composite link parser
    private LinkParser mLinkParser;

    ///the domain name
    private String mDomain;

    ///the parent url

    /**
     * Constructor
     */
    public Scraper(String domain)
    {
        mLinkParser = new LinkParser();
        mDomain = domain;
    }

    /**
     * Scrapes the webpage
     * @param document
     */
    public void scrapePage(Document document)
    {
        mLinkParser.clear();
        mLinkParser.setDomainName(mDomain);
        mLinkParser.parse(document);
    }

    /**
     * Composite setter for link parser
     * @param parentUrl
     */
    public void setParentUrl(String parentUrl)
    {
        mLinkParser.setParentUrl(parentUrl);
    }

    public List<Link> getInternalLinks()
    {
        return mLinkParser.getInternalLinks();
    }



}
