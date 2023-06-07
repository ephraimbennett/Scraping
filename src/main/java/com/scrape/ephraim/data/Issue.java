package com.scrape.ephraim.data;

public class Issue
{
    ///the description of the issue
    private String mDescription;

    ///the summary of the issue
    private String mSummary;

    ///the category of the issue
    private String mCategory;

    ///the severity of the issue
    private int mSeverity;

    ///the url which gave this issue
    private String mUrl;

    public Issue(String description, int severity, String url)
    {
        mDescription = description;
        mSeverity = severity;
        mUrl = url;
        mSummary = "";
    }

    public void setCategory(String category)
    {
        mCategory = category;
    }

    /**
     * getter for the description
     * @return
     */
    public String getDescription() {return mDescription;}

    /**
     * Getter for the category
     * @return
     */
    public String getCategory() {return mCategory;}

    /**
     * Getter for the url
     * @return
     */
    public String getUrl() {return mUrl;}

    /**
     * Setter for the summary
     * @param summary
     */
    public void setSummary(String summary) {mSummary = summary;}

    /**
     * getter for the summary
     * @return
     */
    public String getSummary() {return mSummary;}
}
