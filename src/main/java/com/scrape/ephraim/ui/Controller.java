package com.scrape.ephraim.ui;

import com.scrape.ephraim.crawler.Crawler;
import com.scrape.ephraim.crawler.Scraper;
import com.scrape.ephraim.data.Issue;
import com.scrape.ephraim.data.Page;
import com.scrape.ephraim.data.StatusIssue;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.*;

public class Controller implements Initializable
{

    @FXML
    public Button submitButton;

    @FXML
    public TreeView treeView;

    @FXML
    TextField urlField;

    @FXML
    TableView internalLinks;

    @FXML
    TableView issues;

    public void onExit()
    {
        Platform.exit();
        System.out.println("Application ended.");
    }

    public void onSubmit()
    {
        //set up a timer for performance
        long beginTime = System.currentTimeMillis();

        //creates a crawler
        Crawler crawler = new Crawler(urlField.getText());
        List<String> urls = new ArrayList<>();
        urls.add(crawler.getUrl());

        //now create a scraper
        Scraper scraper = new Scraper(crawler.getDomain());
        crawler.setScraper(scraper);

        crawler.crawl(urls);
        System.out.println("total links visited: " + crawler.getVisitedLinks().size());

        long endTime = System.currentTimeMillis();
        System.out.println("Elapsed time: " + (endTime - beginTime) / 1000);

        createTreeView(scraper);

        populateInternalLinks(scraper);
        populateIssues(scraper);

    }

    private void populateIssues(Scraper scraper)
    {
        for (var issue : scraper.getIssues())
        {
            issues.getItems().add(issue);
        }
    }

    /**
     * Creates the table view for the internal links
     * @param scraper
     */
    private void populateInternalLinks(Scraper scraper)
    {
        for (var page : scraper.getSiteMap())
        {
            internalLinks.getItems().add(page);
        }
    }

    /**
     * Creates the tree view that has the sitemap.
     * @param scraper
     */
    private void createTreeView(Scraper scraper)
    {
        //create treeview
        String domain = scraper.getDomain() + "/";
        TreeItem<String> rootItem = new TreeItem(domain);
        treeView.setRoot(rootItem);

        for (Page page : scraper.getSiteMap())
        {
            if (page.getPath().size() > 1)//if we have more than just a /
            {
                populateTreeView(page, rootItem, 1);
            }
        }
    }

    /**
     * Places the page somewhere on the tree
     * Depth first search algorithm
     * @param page
     * @param root
     * @param level
     */
    void populateTreeView(Page page, TreeItem<String> root, int level)
    {
        String node = "de";
        try {
            node = page.getPath().get(level);//the node in the path we are looking to match
        } catch (IndexOutOfBoundsException e)
        {
            e.printStackTrace();
        }

        //try to match this node with some branch in this element of the tree
        for (var x : root.getChildren())
        {
            if (x.getValue().equals(node))
            {
                //check to see if this is the end of the path
                if (level < page.getPath().size() - 1)
                {
                    populateTreeView(page, x, level + 1);
                    return;
                }
            }
        }
        //if we couldn't find any, add the page to the root
        TreeItem<String> item = new TreeItem<>(page.getPath().get(level));
        root.getChildren().add(item);

        //see if there is more to do with the page
        if (level < page.getPath().size() - 1)
        {
            //if so, finish adding
            populateTreeView(page, item, level + 1);
        }
    }

    @FXML
    public void clickItem(MouseEvent event)
    {
        if (event.getClickCount() == 2) //Checking double click
        {
            System.out.println(internalLinks.getSelectionModel().getSelectedItem());
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {

        //initialize table columns
        initInternalLinks();
        initIssues();

    }

    /**
     * Sets up the columns for the internal links
     */
    private void initInternalLinks()
    {
        TableColumn<Page, String> urlColumn = new TableColumn<>("url");
        urlColumn.setCellValueFactory(page -> new ReadOnlyStringWrapper(page.getValue().getUrl()));
        urlColumn.setPrefWidth(200);
        internalLinks.getColumns().add(urlColumn);

        TableColumn<Page, String> inLinkNumColumn = new TableColumn<>("# of in links");
        inLinkNumColumn.setCellValueFactory(page -> new ReadOnlyStringWrapper(
                String.valueOf(page.getValue().getInLinks().size())));
        inLinkNumColumn.setPrefWidth(70);
        internalLinks.getColumns().add(inLinkNumColumn);

        TableColumn<Page, String> outLinkNumColumn = new TableColumn<>("# of out links");
        outLinkNumColumn.setCellValueFactory(page -> new ReadOnlyStringWrapper(
                String.valueOf(page.getValue().getOutLinks().size())));
        outLinkNumColumn.setPrefWidth(80);
        internalLinks.getColumns().add(outLinkNumColumn);
    }

    /**
     * Sets up the columns for the issues table
     */
    private void initIssues()
    {
        TableColumn<Issue, String> categoryColumn = new TableColumn<>("category");
        categoryColumn.setCellValueFactory(statusIssue -> new ReadOnlyStringWrapper(
                statusIssue.getValue().getCategory()));
        issues.getColumns().add(categoryColumn);
    }
}