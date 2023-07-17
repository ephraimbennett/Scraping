package com.scrape.ephraim.ui;

import com.scrape.ephraim.crawler.Scraper;
import com.scrape.ephraim.data.ExternalSite;
import com.scrape.ephraim.data.Issue;
import com.scrape.ephraim.data.Page;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class SearchController
{
    ///the text where we get the search input from
    TextField urlBox;

    ///the content type search box
    TextField contentBox;

    public SearchController(TextField urlBox, TextField contentBox)
    {
        this.urlBox = urlBox;
        this.contentBox = contentBox;
    }

    /**
     * Adds listeners and set predicates for sorting a table of pages
     * @param table should be the internal links table
     * @param scraper ...
     */
    public void bindPage(TableView<Page> table, Scraper scraper)
    {
        //turn the pages into an observable list
        ObservableList<Page> dataList = FXCollections.observableArrayList();
        for (var page : scraper.getSiteMap())
        {
            dataList.add(page);
        }

        //wrap it in a filtered list, so we can filter it
        //initially, the predicate is always true
        FilteredList<Page> filteredData = new FilteredList<>(dataList, b -> true);

        //set the filter predicate when the filter text changes
        //first the url box
        urlBox.textProperty().addListener((observable, oldVal, newVal) ->
            filteredData.setPredicate((page) -> {

                //case insensitive search
                if (page.getUrl().toLowerCase().indexOf(newVal.toLowerCase()) != -1)
                {
                    return true;
                }
                return false;

            })
        );

        //second the content box
        contentBox.textProperty().addListener((observable, oldVal, newVal) ->
            filteredData.setPredicate((page) -> {

                if (page.getType() == null)
                    return false;

                //case in sensitive search
                if (page.getType().toLowerCase().indexOf(newVal.toLowerCase()) != -1)
                {
                    return true;
                }
                return false;
            })
        );

        //wrap the data in a sorted list
        //then bind this sorted list to the tableview comparator (otherwise the sort wouldn't do anything?)
        SortedList<Page> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(table.comparatorProperty());

        //add the items to the table
        table.setItems(sortedData);
    }

    /**
     * Adds listeners and set predicates for sorting a table of pages
     * @param table should be the issues table
     * @param scraper ...
     */
    public void bindIssue(TableView table, Scraper scraper)
    {
        //turn the pages into an observable list
        ObservableList<Issue> dataList = FXCollections.observableArrayList();
        for (var issue : scraper.getIssues())
        {
            dataList.add(issue);
        }

        //wrap it in a filtered list, so we can filter it
        //initially, the predicate is always true
        FilteredList<Issue> filteredData = new FilteredList<>(dataList, b -> true);

        //set the filter predicate when the filter text changes
        urlBox.textProperty().addListener((observable, oldVal, newVal) -> {
            filteredData.setPredicate((issue) -> {

                //case insensitive search
                if (issue.getUrl().toLowerCase().indexOf(newVal.toLowerCase()) != -1)
                {
                    return true;
                }
                return false;

            });
        });

        //wrap the data in a sorted list
        //then bind this sorted list to the tableview comparator (otherwise the sort wouldn't do anything?)
        SortedList<Issue> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(table.comparatorProperty());

        //add the items to the table
        table.setItems(sortedData);
    }

    /**
     * Adds listeners and set predicates for sorting a table of pages
     * @param table should be the external links table
     * @param scraper ...
     */
    public void bindExternal(TableView table, Scraper scraper)
    {
        //turn the pages into an observable list
        ObservableList<ExternalSite> dataList = FXCollections.observableArrayList();
        for (var site : scraper.getSiteMap().getExternals().values())
        {
            dataList.add(site);
        }

        //wrap it in a filtered list, so we can filter it
        //initially, the predicate is always true
        FilteredList<ExternalSite> filteredData = new FilteredList<>(dataList, b -> true);

        //set the filter predicate when the filter text changes
        urlBox.textProperty().addListener((observable, oldVal, newVal) -> {
            filteredData.setPredicate((site) -> {

                //case insensitive search
                if (site.getUrl().toLowerCase().indexOf(newVal.toLowerCase()) != -1)
                {
                    return true;
                }
                return false;

            });
        });

        //wrap the data in a sorted list
        //then bind this sorted list to the tableview comparator (otherwise the sort wouldn't do anything?)
        SortedList<ExternalSite> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(table.comparatorProperty());

        //add the items to the table
        table.setItems(sortedData);
    }


}
