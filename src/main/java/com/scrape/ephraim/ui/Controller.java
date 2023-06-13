package com.scrape.ephraim.ui;

import com.scrape.ephraim.crawler.Crawler;
import com.scrape.ephraim.crawler.Scraper;
import com.scrape.ephraim.data.ExternalSite;
import com.scrape.ephraim.data.Issue;
import com.scrape.ephraim.data.Page;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.*;
import java.util.concurrent.CompletableFuture;

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
    TableView externalLinks;

    @FXML
    TableView issues;

    @FXML Descriptor descriptorBox;

    ///the scraper
    private Scraper scraper;

    public void onExit()
    {
        Platform.exit();
        System.out.println("Application ended.");
    }

    /**
     * Asynchronously crawl
     */
    public void onSubmit()
    {
        CompletableFuture<Scraper> futureScraper = CompletableFuture.supplyAsync(() -> {
            //set up a timer for performance
            long beginTime = System.currentTimeMillis();

            //creates a crawler
            Crawler crawler = new Crawler(urlField.getText());
            List<String> urls = new ArrayList<>();
            urls.add(crawler.getUrl());

            //now create a scraper
            scraper = new Scraper(crawler.getDomain());
            crawler.setScraper(scraper);

            crawler.crawl(urls);
            System.out.println("total links visited: " + crawler.getVisitedLinks().size());

            long endTime = System.currentTimeMillis();
            System.out.println("Elapsed time: " + (endTime - beginTime) / 1000);
            return scraper;
        });

        futureScraper.thenAccept((scraper)-> {
            Platform.runLater(() -> {
                createTreeView(scraper);

                populateInternalLinks(scraper);
                populateExternalLinks(scraper);
                populateIssues(scraper);
            });
        });



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
     * Creates the table for the external links
     * @param scraper
     */
    private void populateExternalLinks(Scraper scraper)
    {
        for (ExternalSite site : scraper.getSiteMap().getExternals().values())
        {
            externalLinks.getItems().add(site);
        }
    }

    /**
     * Creates the tree view that has the sitemap.
     * @param scraper
     */
    private void createTreeView(Scraper scraper)
    {
        //create most basic root
        treeView.setRoot(new TreeItem<String>("Site Map"));

        //map of the roots. Synonymous for the different types of domains.
        HashMap<String, TreeItem> roots = new HashMap<>();

        //the basic domain name from the user. Not including subdomains.
        String domain = scraper.getDomain() + "/";

        //add it as the first root. Add it to the map of known roots.
        TreeItem<String> basicRoot = new TreeItem(domain);
        roots.put(domain, basicRoot);
        treeView.getRoot().getChildren().add(basicRoot);

        for (Page page : scraper.getSiteMap())
        {
            String wholeDomain = page.getWholeDomain();
            if (!roots.containsKey(wholeDomain))//if this whole domain isn't added yet, add it.
            {
                TreeItem<String> newRoot = new TreeItem<>(wholeDomain);
                treeView.getRoot().getChildren().add(newRoot);
                roots.put(wholeDomain, newRoot);
            }
            if (page.getPath().size() > 1)//if we have more than just a /
            {
                populateTreeView(page, roots.get(wholeDomain), 1);
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
    public void clickItemInternalLinks(MouseEvent event)
    {
        Page selectedPage = (Page) internalLinks.getSelectionModel().getSelectedItem();
        if (selectedPage == null) return;
        descriptorBox.populateDescriptorPage(selectedPage, scraper);
    }

    @FXML
    public void clickItemExternalLinks(MouseEvent event)
    {
        Page selectedPage = (Page) externalLinks.getSelectionModel().getSelectedItem();
        if (selectedPage == null) return;
        descriptorBox.populateDescriptorPage(selectedPage, scraper);
    }


    @FXML
    public void clickItemIssue(MouseEvent event)
    {
        Issue selectedIssue = (Issue) issues.getSelectionModel().getSelectedItem();
        if (selectedIssue == null) return;
        descriptorBox.populateDescriptorIssue(selectedIssue, scraper);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        //make an empty scraper
        scraper = null;

        //initialize table columns
        initInternalLinks();
        initExternalLinks();
        initIssues();

    }

    /**
     * Sets up the columns for the external links table
     */
    private void initExternalLinks()
    {
        TableColumn<ExternalSite, String> urlColumn = new TableColumn<>("url");
        urlColumn.setCellValueFactory(externalSite -> new ReadOnlyStringWrapper(externalSite.getValue().getUrl()));
        urlColumn.setPrefWidth(200);
        externalLinks.getColumns().add(urlColumn);

        TableColumn<ExternalSite, String> occurrences = new TableColumn<>("# of occurrences");
        occurrences.setCellValueFactory(ext -> new ReadOnlyStringWrapper(
                String.valueOf(ext.getValue().getOccurrences())));
        occurrences.setPrefWidth(70);
        externalLinks.getColumns().add(occurrences);
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

        TableColumn<Issue, String> urlColumn = new TableColumn<>("url");
        urlColumn.setCellValueFactory(issue -> new ReadOnlyStringWrapper(
                issue.getValue().getUrl()));
        issues.getColumns().add(urlColumn);

        TableColumn<Issue, String> summaryColumn = new TableColumn<>("summary");
        summaryColumn.setCellValueFactory(issue -> new ReadOnlyStringWrapper(issue.getValue().getSummary()));
        issues.getColumns().add(summaryColumn);
    }
}