package com.scrape.ephraim;



import java.util.*;

import java.util.regex.Matcher;

public class Crawler
{
    ///the base url
    private String mBaseUrl;

    ///the domain name
    private String mDomain;

    ///visited links
    private Set<String> mVisitedLinks = new HashSet<>();

    ///association to the scraper
    private Scraper mScraper;

    /**
     * Constructor with base url
     */
    public Crawler(String url)
    {

        mBaseUrl = url;
        generateDomain();
    }

    /**
     * Sets the scraper
     * @param scraper
     */
    public void setScraper(Scraper scraper)
    {
        mScraper = scraper;
    }

    /**
     * Getter for
     * @return url
     */
    public String getUrl() {return mBaseUrl;}

    /**
     * Getter for the domain name
     * @return string of the domain
     */
    public String getDomain() {return mDomain;}

    /**
     * Grabs the set of visited links
     * @return
     */
    public Set<String> getVisitedLinks() {return mVisitedLinks;}

    /**
     * recursive crawl
     */
    public void crawl(List<String> urlList)
    {
        System.out.println("NEW VISITOR\n\n\n\n\n\n");
        Visitor visitor = new Visitor();
        visitor.setCrawler(this);
        visitor.setScraper(mScraper);
        int index = 0;
        for (String link : urlList) {
            try {
                if (visitedLink(link))
                {
                    continue;
                }
                visitor.addUrl(link);
                addLink(link);
            } catch (NullPointerException e)
            {
                var x = urlList.subList(index, index + 20);
                e.printStackTrace();
            }
        }
        var listOfLinks = visitor.visit();
        List<String> finalList = new ArrayList<>();
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
    public void addLink(String link)
    {
        mVisitedLinks.add(link);
    }

    /**
     * determines if we've visited a link or not
     * @param link
     * @return
     */
    public boolean visitedLink(String link)
    {
        return mVisitedLinks.contains(link);
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
