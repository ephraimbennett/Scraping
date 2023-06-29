package com.scrape.ephraim.crawler;



import com.scrape.ephraim.ui.FetcherObserver;

import java.util.*;

import java.util.regex.Matcher;

public class Crawler
{
    ///the base url
    private String mBaseUrl;

    ///the domain name
    private String mDomain;

    ///visited links
    private HashSet<String> mVisitedLinks = new HashSet<>();

    ///association to the scraper
    private Scraper mScraper;

    ///list of observers for the fetcher
    private ArrayList<FetcherObserver> mObservers;

    /**
     * Constructor with base url
     */
    public Crawler(String url)
    {

        mBaseUrl = url;
        mObservers = new ArrayList<>();
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
    public HashSet<String> getVisitedLinks() {return mVisitedLinks;}

    /**
     * recursive crawl
     */
    public void crawl(List<String> urlList)
    {
        System.out.println("NEW VISITOR\n\n\n\n\n\n");

        //set up the visitor
        Visitor visitor = new Visitor();
        visitor.setCrawler(this);
        visitor.setScraper(mScraper);
        visitor.setObservers(mObservers);

        int index = 0;
        //iterate through each list from the last visitor
        for (String link : urlList) {
            try {
                if (visitedLink(link))//if we've visited this link before, skip it
                {
                    continue;
                }
                //otherwise add it as a destination to the visitor
                visitor.addUrl(link);
                addLink(link);//also add it to the set of visited links
            } catch (NullPointerException e)
            {
                var x = urlList.subList(index, index + 20);
                e.printStackTrace();
            }
        }
        var listOfLinks = visitor.visit();//all the links the visitor found. This is a 2d list

        //this bit of code here makes it one big 1d list
        List<String> finalList = new ArrayList<>();
        for (var list : listOfLinks)
        {
            finalList.addAll(list);
        }
        if (finalList.size() > 0)//if there are any links found crawl through them again.
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

    /**
     * adds an observer
     * @param observer
     */
    public void addObserver(FetcherObserver observer) {mObservers.add(observer);}
}
