package com.scrape.ephraim.data;

import java.util.ArrayList;
import java.util.Iterator;

public class Issues implements Iterable<Issue>
{
    ///the list of issues
    private ArrayList<Issue> mIssues;

    /**
     * Default constructor
     */
    public Issues()
    {
        mIssues = new ArrayList<>();
    }

    @Override
    public Iterator<Issue> iterator() {
        return this.mIssues.iterator();
    }

    /**
     * Adds an issue
     * @param issue
     */
    public void addIssue(Issue issue)
    {
        mIssues.add(issue);
    }
}
