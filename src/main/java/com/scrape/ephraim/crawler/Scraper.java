package com.scrape.ephraim.crawler;

import com.scrape.ephraim.data.*;
import javafx.scene.control.TableView;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Scraper
{
    ///composite link parser
    private LinkParser mLinkParser;

    ///association to the site map
    private SiteMap mSiteMap;

    ///association to the headers storage
    private Headers mHeaders;

    ///association to the issues storage
    private Issues mIssues;

    ///association to the configurations class
    private Configuration mConfiguration;

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
        mHeaders = new Headers();
        mIssues = new Issues();
    }

    /**
     * Scrapes the webpage
     * @param wrapper
     */
    public void scrapePage(ResponseWrapper wrapper)
    {
        String url = wrapper.getUrl();
        Document document = wrapper.getDocument();

        //parse the links
        mLinkParser.clear();
        mLinkParser.setDomainName(url, mDomain);
        mLinkParser.parse(document);

        //make a page object for this specific url
        Page page = new Page(url, wrapper.getType());
        page.setOutLinks(mLinkParser.getInternalLinks());
        page.setExternalLinks(mLinkParser.getExternalLinks());
        page.setHeaders(wrapper.getHeaders());
        page.setResponseCode(wrapper.getResponseCode());
        page.setSize(wrapper.getSize());

        mSiteMap.addPage(page);

        if (document != null)//if this was a parse-able document, then add the html data
        {
            try {
                page.getDocumentInfo().processDocument(document);
            } catch (IndexOutOfBoundsException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * Determines if there was an issue associated with this response.
     * If so, add an issue to the issues list.
     * @param response
     * @return true if there was an issue generated
     */
    public boolean generateIssue(ResponseWrapper response)
    {
        boolean isIssue = false;
        if (response.getResponseCode() < 200 || response.getResponseCode() > 299)
        {
            mIssues.addIssue(new StatusIssue(response.getResponseCode(), response.getUrl()));
            isIssue = true;
        }
        return isIssue;
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

    /**
     * Getter for the external links
     * @return the external links
     */
    public List<String> getExternalLinks() {return mLinkParser.getExternalLinks();}

    /**
     * Getter for the site map
     * @return
     */
    public SiteMap getSiteMap() {return mSiteMap;}

    /**
     * gets the domain name
     * @return
     */
    public String getDomain() {return mDomain;}

    /**
     * Getter for the headers association
     * @return
     */
    public Headers getHeaders() {return mHeaders;}

    /**
     * Getter for the issues association
     * @return
     */
    public Issues getIssues() {return mIssues;}

    /**
     * Gets the links that the spider should be crawling
     * @return a list of strings that should be added to the visit queue
     */
    public List<String> getCrawlLinks() {return mLinkParser.getCrawlLinks();}

    /**
     * Sets the configuration for the scraper and its link parser
     * @param configuration
     */
    public void setConfiguration(Configuration configuration) {
        mConfiguration = configuration;
        mLinkParser.setConfiguration(configuration);
    }


}
