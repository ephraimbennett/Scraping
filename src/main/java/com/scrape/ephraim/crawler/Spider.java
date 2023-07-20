package com.scrape.ephraim.crawler;

import com.scrape.ephraim.ui.FetcherObserver;
import javafx.scene.control.Label;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

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

    ///association to fetcher observer
    private ArrayList<FetcherObserver> mFetcherObservers;

    ///association to the configuration settings
    private Configuration mConfiguration;

    ///the label that we are updating with the queue size
    private Label mQueueSize;

    ///the label that we are updating with the requesting size
    private Label mRequestSize;

    ///the integer value of requests
    private AtomicInteger mRequestsAtomic;

    /**
     * Default constructor
     */
    public Spider(Scraper scraper, Configuration configuration)
    {
        mConfiguration = configuration;
        mThreadCount = configuration.getThreadCount();
        mScraper = scraper;
        mToVisit = new LinkedBlockingQueue<>();
        mWorkers = new ArrayList<>();
        mVisitedLinks = new ConcurrentHashMap<>();
        mNoTasks = 0;
        mFetcherObservers = new ArrayList<>();
        mRequestsAtomic = new AtomicInteger(0);

        //set the configuration for the scraper
        mScraper.setConfiguration(configuration);
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
            grabber.setFetcherObservers(mFetcherObservers);
            grabber.setLabelObservers(mQueueSize, mRequestSize);
            mWorkers.add(grabber);
            grabber.start();
        }

        System.out.println("Waiting...");

        try {
            for (Grabber worker : mWorkers)
                worker.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void closeAll()
    {
        for (Grabber worker : mWorkers)
        {
            worker.stop();
            worker.getThread().interrupt();
        }
   }


    /**
     * Getter for  the visited links
     * @return the concurrent hashmap
     */
    public ConcurrentHashMap<String, Boolean> getVisitedLinks() {return mVisitedLinks;}

    /**
     * Creates association for a fetcher observer
     */
    public void addFetcherObserver(FetcherObserver observer) {mFetcherObservers.add(observer);}

    /**
     * Setter for the label observers
     * @param queueLabel queue size
     * @param requestLabel request size
     */
    public void setLabelObservers(Label queueLabel, Label requestLabel)
    {
        mQueueSize = queueLabel;
        mRequestSize = requestLabel;
    }

    public int addRequesting()
    {
        return mRequestsAtomic.incrementAndGet();
    }

    public int removeRequesting()
    {
        return mRequestsAtomic.decrementAndGet();
    }

    public int getRequesting()
    {
        return mRequestsAtomic.get();
    }


}
