package com.scrape.ephraim.ui;

import com.scrape.ephraim.crawler.Fetcher;

public class FetcherObserver {

    ///the subjects
    private Fetcher fetcher;

    /**
     * Default constructor
     */
    public FetcherObserver()
    {
    }

    /**
     * Sets the fetcher.
     * Sets this as an observer
     * @param fetcher
     */
    public void setFetcher(Fetcher fetcher) {
        this.fetcher = fetcher;
        fetcher.addObserver(this);
    }

    /**
     * Getter for the fetcher
     * @return
     */
    public Fetcher getFetcher() {return fetcher;}


    /**
     * To be called by the fetcher
     */
    public void update(String url) {}
}
