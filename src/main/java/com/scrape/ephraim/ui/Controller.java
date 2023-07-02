package com.scrape.ephraim.ui;

import com.scrape.ephraim.crawler.Crawler;
import com.scrape.ephraim.crawler.Scraper;
import com.scrape.ephraim.data.ExternalSite;
import com.scrape.ephraim.data.Issue;
import com.scrape.ephraim.data.Page;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class Controller implements Initializable
{
    @FXML
    public VBox page;

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

    @FXML
    ListView visitedLinks;

    @FXML TitledPane descriptorBox;

    @FXML MenuBar menuBar;

    ///controller for the descriptorBox
    Descriptor descriptorController;

    ///controller for the menu bar
    MenuBarController menuBarController;

    ///controller for the lists that contain links and errors
    VisitedListController visitedController;

    ///the scraper
    private Scraper scraper;

    /**
     * handler for exit on menu bar
     */
    public void onExit()
    {
        menuBarController.onExit();
    }

    /**
     * handler for hitting export to csv
     */
    public void onExportCSV()
    {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV File", ".csv"));
        File path = chooser.showSaveDialog((Stage) page.getScene().getWindow());
        ExporterCSV exporter = new ExporterCSV(path);
        exporter.export(scraper);
    }

    /**
     * Handler for hitting export to json
     */
    public void onExportJSON()
    {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save");
        chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("JSON File", ".json")
        , new FileChooser.ExtensionFilter("All file types", "."));
        File path = chooser.showSaveDialog((Stage) page.getScene().getWindow());
        ExporterJSON exporter = new ExporterJSON(path);
        exporter.export(scraper);
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

            //add observers
            crawler.addObserver(visitedController);

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
     * If the sitemap is double-clicked, display the selected item
     * @param e
     */
    @FXML
    public void siteMapClicked(MouseEvent e)
    {
        if (e.getClickCount() == 1)
        {
            //set up shop
            StringBuilder urlBuilder = new StringBuilder();
            TreeItem<String> currentItem = (TreeItem<String>) treeView.getSelectionModel().getSelectedItem();

            //if no item is selected, just stop!
            if (currentItem == null) return;

            //keep adding the item's value until we get to the root
            while (currentItem.getParent() != null)
            {
                urlBuilder.insert(0, currentItem.getValue()).insert(0, "/");
                currentItem = currentItem.getParent();
            }

            //add the https://
            //since we are always adding a trailing "/" we only need to add http:/
            urlBuilder.insert(0, "https:/");

            Page selectedPage = scraper.getSiteMap().getMap().get(urlBuilder.toString());
            if (selectedPage != null)
                descriptorController.populateDescriptorPage(selectedPage, scraper);
            else
                descriptorController.clear();
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
        descriptorController.populateDescriptorPage(selectedPage, scraper);
    }

    @FXML
    public void clickItemExternalLinks(MouseEvent event)
    {
        ExternalSite selectedSite = (ExternalSite) externalLinks.getSelectionModel().getSelectedItem();
        if (selectedSite == null) return;
        descriptorController.populateExternalSite(selectedSite, scraper);
    }


    @FXML
    public void clickItemIssue(MouseEvent event)
    {
        Issue selectedIssue = (Issue) issues.getSelectionModel().getSelectedItem();
        if (selectedIssue == null) return;
        descriptorController.populateDescriptorIssue(selectedIssue, scraper);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        //make an empty scraper
        scraper = null;

        //assign the elements to the proper controllers
        descriptorController = new Descriptor(descriptorBox);
        menuBarController = new MenuBarController(menuBar);
        visitedController = new VisitedListController(visitedLinks);

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

        TableColumn<ExternalSite, Integer> occurrences = new TableColumn<>("# of occurrences");
        occurrences.setCellValueFactory(ext -> new ReadOnlyObjectWrapper<>(ext.getValue().getOccurrences()));
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

        TableColumn<Page, Integer> inLinkNumColumn = new TableColumn<>("# of in links");
        inLinkNumColumn.setCellValueFactory(page -> new ReadOnlyObjectWrapper<>(page.getValue().getInLinks().size()));
        inLinkNumColumn.setPrefWidth(70);
        internalLinks.getColumns().add(inLinkNumColumn);

        TableColumn<Page, Integer> outLinkNumColumn = new TableColumn<>("# of out links");
        outLinkNumColumn.setCellValueFactory(page -> new ReadOnlyObjectWrapper<>(page.getValue().getOutLinks().size()));
        outLinkNumColumn.setPrefWidth(80);
        internalLinks.getColumns().add(outLinkNumColumn);

//        outLinkNumColumn.o
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