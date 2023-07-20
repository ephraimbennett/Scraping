package com.scrape.ephraim.data;

public class FollowupIssue extends Issue
{
    public FollowupIssue(String url) {
        super("Too many follow up requests.", 1, url);
        setCategory("Redirect Issue");
        setSummary(">21");
    }
}
