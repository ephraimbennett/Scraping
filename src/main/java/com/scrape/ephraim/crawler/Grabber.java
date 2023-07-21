package com.scrape.ephraim.crawler;

import com.scrape.ephraim.data.FollowupIssue;
import com.scrape.ephraim.data.StatusIssue;
import com.scrape.ephraim.data.TimeoutIssue;
import com.scrape.ephraim.ui.FetcherObserver;
import javafx.application.Platform;
import javafx.scene.control.Label;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;

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

    ///association to fetcher observer
    private ArrayList<FetcherObserver> mFetcherObservers;

    ///the label that we are updating with the queue size
    private Label mQueueSize;

    ///the label that we are updating with the requesting size
    private Label mRequestSize;

    private boolean mFirstTime;


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
        mFetcherObservers = new ArrayList<>();
        mFirstTime = true;
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
                    //check if we are done
                    if (!mFirstTime) {
                        if (mToVisit.isEmpty() && mSpider.getRequesting() == 0) {
                            mSpider.closeAll();
                            break;
                        }
                    } else mFirstTime = false;

                    //update the ui for the queue
                    Platform.runLater(() -> {
                        mQueueSize.setText(String.valueOf(mToVisit.size()));
                    });

                    //before we get the link we are waiting
                    String url = mToVisit.take();

                    //after we have a link, update that we are requesting it
                    mSpider.addRequesting();
                    Platform.runLater(() -> {
                        mRequestSize.setText(String.valueOf(mSpider.getRequesting()));
                    });

                    Fetcher fetcher = new Fetcher(url);
                    ResponseWrapper response = fetcher.ok();

                    int r = Integer.valueOf(mRequestSize.getText()) - 1;
                    if (mToVisit.isEmpty() && r == 0 && !mFirstTime)
                        System.out.println("d");

                    //after receiving the request, update the ui
                    mSpider.removeRequesting();
                    Platform.runLater(() -> {
                        mRequestSize.setText(String.valueOf(mSpider.getRequesting()));
                    });

                    boolean newLinks = false;

                    synchronized (mScraper) {
                        //scrape the response
                        //first check for response code issue
                        if (response.getResponseCode() > 299) {
                            mScraper.getIssues().addIssue(new StatusIssue(response.getResponseCode(),
                                    response.getUrl()));
                        }
                        if (response.getResponseCode() == -1)//socket timeout
                            mScraper.getIssues().addIssue(new TimeoutIssue(response.getUrl()));
                        if (response.getResponseCode() == -5)
                            mScraper.getIssues().addIssue(new FollowupIssue(response.getUrl()));

                        //now check if this url was external
                        if (mScraper.getSiteMap().getExternals().containsKey(url))
                        {
                            mScraper.getSiteMap().getExternals().get(url).processResponse(response,
                                    mScraper.getKeywords());
                        } else {

                            //grab the other stuff
                            mScraper.setParentUrl(url);
                            mScraper.scrapePage(response);
                            //store the headers
                            mScraper.getHeaders().addResponseHeader(response.getUrl(), response.getHeaders());

                            //get the links from the scraper
                            for (String crawlLink : mScraper.getCrawlLinks())
                            {
                                if (!mVisitedLinks.containsKey(crawlLink))
                                {
                                    mVisitedLinks.put(crawlLink, true);
                                    mToVisit.put(crawlLink);
                                    newLinks = true;
                                }
                            }
                        }


                    }

                    //update fetcher observers
                    for (var observer : mFetcherObservers)
                        observer.update(url);

//                    if (!newLinks && mToVisit.isEmpty())
//                        break;
                } catch (InterruptedException e) {
                    if (mRunning)
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

    /**
     * Getter for the thread
     * @return thread
     */
    public Thread getThread() {return mThread;}

    /**
     * Sets association to fetcher observer
     */
    public void setFetcherObservers(ArrayList<FetcherObserver> observers) {mFetcherObservers = observers;}

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

    /**
     * Stops the thread gracefully
     */
    public void stop() {mRunning = false;}


}
