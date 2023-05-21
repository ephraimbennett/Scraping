package com.scrape.ephraim;

import org.jsoup.nodes.Document;

import java.util.*;

import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Crawler
{
    ///the base url
    private String mBaseUrl;

    ///the domain name
    private String mDomain;

    ///visited links
    private Set<Link> mVisitedLinks = new HashSet<>();

    ///association to the visitor

    /**
     * Constructor with base url
     */
    public Crawler(String url)
    {

        mBaseUrl = url;
        generateDomain();
    }

    /**
     * Getter for
     * @return url
     */
    public String getUrl() {return mBaseUrl;}

    /**
     * Setter for base
     * @param url
     */
    public void setUrl(String url){mBaseUrl = url;}

    /**
     * Getter for the domain name
     * @return string of the domain
     */
    public String getDomain() {return mDomain;}

    /**
     * Grabs the set of visited links
     * @return
     */
    public Set<Link> getVisitedLinks() {return mVisitedLinks;}

    /**
     * recursive crawl
     */
    public void crawl(List<Link> urlList)
    {
        System.out.println("NEW VISITOR\n\n\n\n\n\n");
        Visitor visitor = new Visitor();
        int index = 0;
        for (Link link : urlList) {
            try {
                index++;
                if (visitedLink(link))
                {
                    continue;
                }
                if (link.isRelative())
                {
                    visitor.addUrl("https://" + mDomain + link.getUrl());
                } else {
                    visitor.addUrl(link.getUrl());
                }
                visitor.setCrawler(this);
                addLink(link);
            } catch (NullPointerException e)
            {
                var x = urlList.subList(index, index + 20);
                e.printStackTrace();
            }
        }
        var listOfLinks = visitor.visit();
        List<Link> finalList = new ArrayList<>();
        for (var list : listOfLinks)
        {
            finalList.addAll(list);
        }
        if (finalList.size() > 0)
        {
            crawl(finalList);
        }
    }

    /**
     * Adds a singular link to the list of visited links
     * @param link
     */
    public void addLink(Link link)
    {
        mVisitedLinks.add(link);
    }

    /**
     * Adds links from another list onto the list of visted links
     * ignores duplicates
     * @param links
     */
    public void addLinks(List<Link> links)
    {
        mVisitedLinks.addAll(links);
    }

    /**
     * determines if we've visited a link or not
     * @param link
     * @return
     */
    public boolean visitedLink(Link link)
    {
        for (var checkLink : mVisitedLinks)
        {
            if (link.equals(checkLink))
            {
                return true;
            }
        }
        return false;
    }


    /**
     * Generates the domain name
     * must be called before the crawl.
     * @return the domain name or a percent sign if it was not found
     */
    private String generateDomain() {
        Matcher domainMatcher = Patterns.domainPattern.matcher(mBaseUrl);
        if (domainMatcher.find())
        {
            mDomain = domainMatcher.group(1);
            return domainMatcher.group(1);
        }
        return "%";
    }
}
