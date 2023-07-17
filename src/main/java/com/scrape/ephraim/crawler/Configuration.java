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
     * Setter for testing images
     * @param testImages bool
     */
    public void setTestImages(boolean testImages) {this.testImages = testImages;}

    /**
     * Getter for testing external links
     * @return bool
     */
    public boolean testExternals() {return testExternals;}

    /**
     * Setter for testing the externals
     * @param testExternals bool
     */
    public void setTestExternals(boolean testExternals) {this.testExternals = testExternals;}

    /**
     * Getter for the thread count
     * @return number of threads this crawl should use.
     */
    public int getThreadCount() {return threadCount;}

    /**
     * Setter for the thread count
     * @param threadCount int
     */
    public void setThreadCount(int threadCount) {this.threadCount = threadCount;}
}
