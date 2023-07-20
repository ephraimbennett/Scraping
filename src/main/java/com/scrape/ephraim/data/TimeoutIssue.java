package com.scrape.ephraim.data;

public class TimeoutIssue extends Issue
{
    public TimeoutIssue(String url) {
        super("Socket took longer than allocated time.", 1, url);
        setCategory("Socket Timeout Issue");
        setSummary("> 15s");
    }
}
