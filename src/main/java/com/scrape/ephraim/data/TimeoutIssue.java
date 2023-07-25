package com.scrape.ephraim.data;

public class TimeoutIssue extends Issue
{
    public TimeoutIssue(String url, int timeout) {
        super("Socket took longer than allocated time.", 1, url);
        setCategory("Socket Timeout Issue");

        StringBuilder summary = new StringBuilder();
        summary.append("> ").append(timeout).append("s");
        setSummary(summary.toString());
    }
}
