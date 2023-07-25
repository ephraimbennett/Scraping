package com.scrape.ephraim.data;

import javafx.scene.control.TableView;

import java.util.ArrayList;
import java.util.Iterator;

public class Issues implements Iterable<Issue>
{
    ///the list of issues
    private ArrayList<Issue> mIssues;

    ///association to the issues observer
    private TableView<Issue> mObserverIssues;

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
     * Adds an issue and updates the observer
     * @param issue
     */
    public void addIssue(Issue issue)
    {
        var list = mObserverIssues.getItems();
        list.add(issue);
        mIssues.add(issue);
    }

    /**
     * grabs issues based on the url
     * @param url
     * @return arraylist of issues
     */
    public ArrayList<Issue> findIssues(String url)
    {
        ArrayList<Issue> res = new ArrayList<>();
        for (var issue : mIssues)
        {
            if (issue.getUrl().equals(url))
            {
                res.add(issue);
            }
        }
        return res;
    }

    /**
     * Setter for the observer issues association
     * @param issues a table
     */
    public void setObserverIssues(TableView<Issue> issues) {mObserverIssues = issues;}

    /**
     * Converts this into a basic array
     * @return basic
     */
    public Issue[] toArray() {return mIssues.toArray(new Issue[0]);}

    /**
     * Empties out the list
     */
    public void clear()
    {
        mIssues.clear();
    }

}
