package com.scrape.ephraim.crawler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.*;

public class Spider
{
    ///the number of threads we are using
    private int mThreadCount;

    ///the number of threads that have no tasks to do
    private int mNoTasks;

    ///the links that we are waiting to visit
    private BlockingQueue<String> mToVisit;

    ///this concurrent set will have all the visited links
    private ConcurrentHashMap<String, Boolean> mVisitedLinks;

    ///the fetchers that will be doing our work
    private ArrayList<Grabber> mWorkers;

    ///association to the scraper
    private Scraper mScraper;

    /**
     * Default constructor
     */
    public Spider(Scraper scraper, int threadCount)
    {
        mThreadCount = threadCount;
        mScraper = scraper;
        mToVisit = new LinkedBlockingQueue<>();
        mWorkers = new ArrayList<>();
        mVisitedLinks = new ConcurrentHashMap<>();
        mNoTasks = 0;
    }

    public void crawl(String url)
    {
        //add the initial url to the queue
        try {
            mToVisit.put(url);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //populate our fetchers list
        for (int i = 0; i < mThreadCount; i++)
        {
            Grabber grabber = new Grabber(mToVisit, mScraper, mVisitedLinks);
            grabber.setSpider(this);
            mWorkers.add(grabber);
            grabber.start();
        }

        try {
            for (Grabber worker : mWorkers)
                worker.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    /**
     * Getter for  the visited links
     * @return the concurrent hashmap
     */
    public ConcurrentHashMap<String, Boolean> getVisitedLinks() {return mVisitedLinks;}
}
