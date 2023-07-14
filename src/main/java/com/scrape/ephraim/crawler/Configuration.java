package com.scrape.ephraim.crawler;

public class Configuration
{
    ///whether or not we should test external links
    private boolean testExternals;

    ///whether or not we should test images
    private boolean testImages;

    ///the number of threads
    private int threadCount;

    public Configuration(int threadCount, boolean testImages, boolean testExternals)
    {
        this.threadCount = threadCount;
        this.testImages = testImages;
        this.testExternals = testExternals;
    }

    /**
     * Getter for testing images
     * @return
     */
    public boolean testImages() {return testImages;}

    /**
     * Getter for testing external links
     * @return
     */
    public boolean testExternals() {return testExternals;}

    /**
     * Getter for the thread count
     * @return number of threads this crawl should use.
     */
    public int getThreadCount() {return threadCount;}
}
