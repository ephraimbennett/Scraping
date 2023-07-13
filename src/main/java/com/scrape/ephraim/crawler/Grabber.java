package com.scrape.ephraim.crawler;

import java.util.HashSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SynchronousQueue;

public class Grabber
{
    ///thread safe que that we will be grabbing stuff from
    private BlockingQueue<String> mToVisit;

    ///the set of all links we've visited
    private ConcurrentHashMap<String, Boolean> mVisitedLinks;

    ///the thread this grabber is going to use
    private Thread mThread;

    ///indicates if we should be running
    private boolean mRunning;

    ///association to the scraper
    private Scraper mScraper;

    ///association to the spider
    private Spider mSpider;


    /**
     * Constructor
     * @param toVisit
     */
    public Grabber(BlockingQueue<String> toVisit, Scraper scraper, ConcurrentHashMap<String, Boolean> visitedLinks)
    {
        mToVisit = toVisit;
        mScraper = scraper;
        mVisitedLinks = visitedLinks;
        mRunning = true;
    }

    /**
     * Sets the association to the spider
     * @param spider
     */
    public void setSpider(Spider spider) {mSpider = spider;}

    /**
     * Start handling visiting urls
     */
    public void start()
    {
        mThread = new Thread(() -> {
            while (mRunning)
            {
                try {
                    //before we get the link we are waiting
                    String url = mToVisit.take();


                    Fetcher fetcher = new Fetcher(url);
                    ResponseWrapper response = fetcher.ok();

                    synchronized (mScraper) {
                        //scrape the response
                        mScraper.setParentUrl(url);
                        mScraper.scrapePage(response);
                        //store the headers
                        mScraper.getHeaders().addResponseHeader(response.getUrl(), response.getHeaders());
                    }

                    boolean newLinks = false;
                    for (String internalLink : mScraper.getInternalLinks())
                    {
                        if (!mVisitedLinks.containsKey(internalLink))
                        {
                            mVisitedLinks.put(internalLink, true);
                            mToVisit.put(internalLink);
                            newLinks = true;
                        }
                    }

                    if (mToVisit.isEmpty() && !newLinks)
                        break;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        mThread.start();
    }

    /**
     * Composite join method
     */
    public void join() throws InterruptedException
    {
        mThread.join();
    }


}
