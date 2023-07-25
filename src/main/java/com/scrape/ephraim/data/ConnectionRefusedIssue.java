package com.scrape.ephraim.data;

public class ConnectionRefusedIssue extends Issue
{
    public ConnectionRefusedIssue(String url) {
        super("No service listening on the IP/port.", 1, url);
        setCategory("Connection Refused Issue");
        setSummary("Failed");
    }
}
