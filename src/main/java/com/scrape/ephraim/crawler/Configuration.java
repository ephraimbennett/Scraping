package com.scrape.ephraim.crawler;

public class Configuration
{
    ///whether or not we should test external links
    private boolean testExternals;

    ///whether or not we should test images
    private boolean testImages;

    ///whether or not we should crawl subdomains
    private boolean crawlSubdomains;

    ///the number of threads
    private int threadCount;

    ///timeout limit
    private int timeout;

    public Configuration(int threadCount, int timeout, boolean testImages, boolean testExternals, boolean crawlSubdomains)
    {
        this.threadCount = threadCount;
        this.timeout = timeout;
        this.testImages = testImages;
        this.testExternals = testExternals;
        this.crawlSubdomains = crawlSubdomains;
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

    /**
     * Setter for crawling subdomains
     * @param crawlSubdomains bool
     */
    public void setCrawlSubdomains(boolean crawlSubdomains) {this.crawlSubdomains = crawlSubdomains;}

    /**
     * Getter for crawling subdomains
     * @return bool
     */
    public boolean crawlSubdomains() {return crawlSubdomains;}

    /**
     * Getter for the timeout
     * @return seconds
     */
    public int getTimeout() {return timeout;}

    /**
     * Setter for the timeout
     * @param timeout seconds
     */
    public void setTimeout(int timeout) {this.timeout = timeout;}

}
