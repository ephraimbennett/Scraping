package com.scrape.ephraim.data;

public class StatusIssue extends Issue
{
    ///the status code
    private int mStatusCode;

    /**
     * Default constructor
     */
    public StatusIssue(int code, String url)
    {
        super("Non 200 status code", 1);
        setCategory("Status Issue");
        mStatusCode = code;
    }

    /**
     * Getter for status code
     * @return
     */
    public int getStatusCode() {return mStatusCode;}



}
