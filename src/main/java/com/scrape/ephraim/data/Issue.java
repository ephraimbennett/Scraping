package com.scrape.ephraim.data;

import java.util.ArrayList;
import java.util.List;

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

    /**
     * Setter for the description
     * @param description description
     */
    public void setDescription(String description) {mDescription = description;}

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

    /**
     * Returns an array of strings that represent this page object
     * @return a list
     */
    public List<String> saveCSV()
    {
        ArrayList<String> line = new ArrayList<>();

        //add the category
        line.add(mCategory);

        //add the url
        line.add(mUrl);

        //add the summary
        line.add(mSummary);

        //add the description
        line.add(mDescription);

        //add the severity
        line.add(String.valueOf(mSeverity));

        return line;
    }
}
