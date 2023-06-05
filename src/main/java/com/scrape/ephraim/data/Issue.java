package com.scrape.ephraim.data;

public class Issue
{
    ///the description of the issue
    private String mDescription;

    ///the category of the issue
    private String mCategory;

    ///the severity of the issue
    private int mSeverity;

    ///the url which gave this issue
    private String mUrl;

    public Issue(String description, int severity)
    {
        mDescription = description;
        mSeverity = severity;
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
}
