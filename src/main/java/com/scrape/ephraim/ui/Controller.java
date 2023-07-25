package com.scrape.ephraim.ui;

import com.scrape.ephraim.crawler.Configuration;
import com.scrape.ephraim.crawler.Scraper;
import com.scrape.ephraim.crawler.Spider;
import com.scrape.ephraim.data.ExternalSite;
import com.scrape.ephraim.data.Issue;
import com.scrape.ephraim.data.Keyword;
import com.scrape.ephraim.data.Page;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class Controller implements Initializable
{
    @FXML
    public VBox page;

    @FXML
    public TreeView<String> treeView;

    @FXML
    TextField urlField;

    @FXML
    TextField urlSearch;

    @FXML
    TextField typeSearch;

    @FXML
    TableView<Page> internalLinks;

    @FXML
    TableView<ExternalSite> externalLinks;

    @FXML
    TableView<Issue> issues;

    @FXML
    ListView<String> visitedLinks;

    @FXML
    TitledPane descriptorBox;

    @FXML
    MenuBar menuBar;

    @FXML
    GridPane overviewGrid;

    @FXML
    TableView<Keyword> keywordsTable;

    @FXML
    MenuItem copyItem;

    @FXML
    Label queueLabel;

    @FXML
    Label requestLabel;

    ///controller for the descriptorBox
    Descriptor descriptorController;

    ///controller for the menu bar
    MenuBarController menuBarController;

    ///controller for the lists that contain links and errors
    VisitedListController visitedController;

    ///controller for the overview
    OverviewController overviewController;

    ///controller for the search box
    SearchController searchController;

    ///controller for copy & paste
    CopyController copyController;

    ///the scraper
    private Scraper scraper;

    ///the spider
    private Spider spider;

    ///the configuration settings
    private Configuration configuration;

    ///the keywords to look for
    private List<Keyword> keywords;

    /**
     * handler for exit on menu bar
     */
    public void onExit()
    {
        menuBarController.onExit();
    }

    /**
     * handler for hitting exportPage to csv
     */
    public void onExportPages()
    {
        menuBarController.exportPages(scraper);
    }

    /**
     * Handler
     */
    public void onExportExternals()
    {
        menuBarController.exportExternals(scraper);
    }

    /**
     * Handler
     */
    public void onExportIssues()
    {
        menuBarController.exportIssues(scraper);
    }


    /**
     * Handler for hitting export to json
     */
    public void onExportJSON()
    {
        menuBarController.saveJSON(scraper);
    }

    /**
     * Handler for opening a json file
     */
    public void onOpen()
    {
        //get the scraper ready to be filled
        scraper = new Scraper("");
        scraper.getSiteMap().setObserverInternalLinks(internalLinks);
        scraper.getSiteMap().setObserverExternals(externalLinks);
        scraper.getIssues().setObserverIssues(issues);

        menuBarController.openJSON(scraper);

        createTreeView(scraper);

        populateInternalLinks(scraper);
        populateExternalLinks(scraper);
        populateIssues(scraper);

        createOverview();


    }

    /**
     * Opens configuration window
     */
    public void onConfiguration() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/configuration.fxml"));
        Stage configurationStage = new Stage();
        configurationStage.setTitle("Crawl Configuration");
        Scene configScene = new Scene(root);
        configScene.setUserData(configuration);
        configurationStage.setScene(configScene);
        configurationStage.initModality(Modality.APPLICATION_MODAL);
        configurationStage.show();
        configurationStage.setOnCloseRequest((par) -> {
            System.out.println(configuration.getThreadCount());
        });
    }

    /**
     * Opens the keywords setting page
     * @throws IOException e
     */
    public void onKeywords() throws IOException
    {
        Parent root = FXMLLoader.load(getClass().getResource("/keywords.fxml"));
        Stage keywordsStage = new Stage();
        keywordsStage.setTitle("Add / Delete Keywords");

        Scene keywordsScene = new Scene(root);
        keywordsScene.setUserData(keywords);
        keywordsStage.setScene(keywordsScene);

        keywordsStage.initModality(Modality.APPLICATION_MODAL);
        keywordsStage.show();
        keywordsStage.setOnCloseRequest(par-> {
            keywordsTable.setItems(FXCollections.observableArrayList(keywords));
        });
    }

    /**
     * Handles clearing the scraper and tables
     */
    public void onClear()
    {
        //empty the scraper
        if (scraper == null) return;
        scraper.getSiteMap().getMap().clear();
        scraper.getIssues().clear();
        scraper.getSiteMap().getExternals().clear();
        scraper = null;

        //clear out ui
        internalLinks.setItems(FXCollections.observableArrayList());
        externalLinks.setItems(FXCollections.observableArrayList());
        visitedLinks.setItems(FXCollections.observableArrayList());
        issues.setItems(FXCollections.observableArrayList());
        overviewController.clear();
        descriptorController.clear();
        treeView.getRoot().getChildren().clear();
        treeView.refresh();

    }

    /**
     * Handler for stopping the crawl
     */
    public void onStop()
    {
        if (spider != null)
            spider.closeAll();
    }

    /**
     * Asynchronously crawl
     */
    public void onSubmit()
    {
        CompletableFuture<Scraper> futureScraper = CompletableFuture.supplyAsync(() -> {
            //set up a timer for performance
            long beginTime = System.currentTimeMillis();

            scraper = new Scraper(urlField.getText());
            spider = new Spider(scraper, configuration);


            //add keywords
            for (Keyword keyword : keywords)
            {
                keyword.setObserverKeywords(keywordsTable);
            }
            scraper.setKeywords(keywords);

            //add observers
            spider.addFetcherObserver(visitedController);
            spider.setLabelObservers(queueLabel, requestLabel);
            scraper.getSiteMap().setObserverInternalLinks(internalLinks);
            scraper.getSiteMap().setObserverExternals(externalLinks);
            scraper.getIssues().setObserverIssues(issues);

            spider.crawl(scraper.getDomain());

            //performance stuff
            System.out.println("total links visited: " + spider.getVisitedLinks().size());

            long endTime = System.currentTimeMillis();
            System.out.println("Elapsed time: " + (endTime - beginTime) / 1000);
            return scraper;
        });

        futureScraper.thenAccept((scraper)->
            Platform.runLater(() -> {
                createTreeView(scraper);

                populateInternalLinks(scraper);
                populateExternalLinks(scraper);
                populateIssues(scraper);

                createOverview();


            })
        );

    }

    /**
     * Updates info about the site
     */
    private void createOverview()
    {
        overviewController.createOverview(scraper);
    }


    /**
     * kinda obvious
     * @param scraper should be filled
     */
    private void populateIssues(Scraper scraper)
    {
        searchController.bindIssue(issues, scraper);
    }

    /**
     * Creates the table view for the internal links
     * @param scraper should be filled
     */
    private void populateInternalLinks(Scraper scraper)
    {
        searchController.bindPage(internalLinks, scraper);
    }

    /**
     * Creates the table for the external links
     * @param scraper should be filled
     */
    private void populateExternalLinks(Scraper scraper)
    {
        searchController.bindExternal(externalLinks, scraper);
    }

    /**
     * Creates the tree view that has the sitemap.
     * @param scraper should be filled
     */
    private void createTreeView(Scraper scraper)
    {
        //create most basic root
        TreeItem<String> baseRoot = new TreeItem("Site Map");
        baseRoot.setExpanded(true);
        treeView.setRoot(baseRoot);

        //map of the roots. Synonymous for the different types of domains.
        HashMap<String, TreeItem<String>> roots = new HashMap<>();

        //add each page to the map
        //first we are checking if it's domain is unique, then we are adding it into the map
        for (Page page : scraper.getSiteMap())
        {
            //grab this url's entire domain, so including subdomains
            String wholeDomain = page.getWholeDomain();
            if (!roots.containsKey(wholeDomain))//if this whole domain isn't added yet, add it.
            {
                TreeItem<String> newRoot = new TreeItem<>(wholeDomain);
                treeView.getRoot().getChildren().add(newRoot);
                roots.put(wholeDomain, newRoot);
            }

            //now if this url has a path other than just the domain, we need to recursively add each section
            if (page.getPath().size() > 1)//if we have more than just a /
            {
                populateTreeView(page, roots.get(wholeDomain), 1);
            }
        }
    }

    /**
     * If the sitemap is double-clicked, display the selected item
     * @param e the mouse event
     */
    @FXML
    public void siteMapClicked(MouseEvent e)
    {
        if (e.getClickCount() == 1)
        {
            //set up shop
            StringBuilder urlBuilder = new StringBuilder();
            TreeItem<String> currentItem = treeView.getSelectionModel().getSelectedItem();

            //if no item is selected, just stop!
            if (currentItem == null) return;
            //if the scraper is null, just stop!
            if (scraper == null) return;

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
            //if the selected page was null, try adding a slash at the end of the url
            if (selectedPage == null) {
                urlBuilder.append("/");
                selectedPage = scraper.getSiteMap().getMap().get(urlBuilder.toString());
                if (selectedPage == null) {
                    //but if that doesn't work then clear it
                    descriptorController.clear();
                    return;
                }
            }
            descriptorController.populateDescriptorPage(selectedPage, scraper);
        }
    }

    /**
     * Places the page somewhere on the tree
     * Depth first search algorithm
     * @param page the page we are trying to set
     * @param root the item we are placing it inside
     * @param level the number of steps away from the most basic root
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
    public void clickItemInternalLinks(MouseEvent ignoredEvent)
    {
        Page selectedPage = internalLinks.getSelectionModel().getSelectedItem();
        if (selectedPage == null) return;
        descriptorController.populateDescriptorPage(selectedPage, scraper);
    }

    @FXML
    public void clickItemExternalLinks(MouseEvent ignoredEvent)
    {
        ExternalSite selectedSite = externalLinks.getSelectionModel().getSelectedItem();
        if (selectedSite == null) return;
        descriptorController.populateExternalSite(selectedSite, scraper);
    }


    @FXML
    public void clickItemIssue(MouseEvent ignoredEvent)
    {
        Issue selectedIssue = issues.getSelectionModel().getSelectedItem();
        if (selectedIssue == null) return;
        descriptorController.populateDescriptorIssue(selectedIssue, scraper);
    }

    @FXML
    public void clickItemKeyword(MouseEvent ignoredEvent)
    {
        Keyword keyword = keywordsTable.getSelectionModel().getSelectedItem();
        if (keyword == null) return;
        descriptorController.populateDescriptorKeyword(keyword, scraper);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        //make an empty scraper
        scraper = null;
        spider = null;
        //set configruation
        configuration = new Configuration(10, 5, false, true, false);
        //set the keywords
        keywords = new ArrayList<>();
        keywords.add(new Keyword("Tree"));

        //set up the copy stuff
        copyController = new CopyController(copyItem);
        copyController.addList(visitedLinks);
        copyController.addTree(treeView);

        //assign the elements to the proper controllers
        descriptorController = new Descriptor(descriptorBox, copyController);
        menuBarController = new MenuBarController(menuBar);
        visitedController = new VisitedListController(visitedLinks);
        overviewController = new OverviewController(overviewGrid);
        searchController = new SearchController(urlSearch, typeSearch);

        //initialize table columns
        initInternalLinks();
        initExternalLinks();
        initIssues();
        initKeywords();

    }

    /**
     * Sets up the columns for the external links table
     */
    private void initExternalLinks()
    {
        TableColumn<ExternalSite, String> urlColumn = new TableColumn<>("URL");
        urlColumn.setCellValueFactory(externalSite -> new ReadOnlyStringWrapper(externalSite.getValue().getUrl()));
        urlColumn.setPrefWidth(200);
        externalLinks.getColumns().add(urlColumn);

        TableColumn<ExternalSite, Integer> occurrences = new TableColumn<>("# of Occurrences");
        occurrences.setCellValueFactory(ext -> new ReadOnlyObjectWrapper<>(ext.getValue().getOccurrences()));
        occurrences.setPrefWidth(70);
        externalLinks.getColumns().add(occurrences);

        TableColumn<ExternalSite, Integer> codeColumn = new TableColumn("Status Code");
        codeColumn.setCellValueFactory(site -> new ReadOnlyObjectWrapper<>(site.getValue().getResponseCode()));
        codeColumn.setPrefWidth(90);
        externalLinks.getColumns().add(codeColumn);

        TableColumn<ExternalSite, String> typeColumn = new TableColumn<>("Content Type");
        typeColumn.setCellValueFactory(site -> new ReadOnlyStringWrapper(site.getValue().getType()));
        typeColumn.setPrefWidth(90);
        externalLinks.getColumns().add(typeColumn);

        TableColumn<ExternalSite, Integer> sizeColumn = new TableColumn<>("Size");
        sizeColumn.setCellValueFactory(site -> new ReadOnlyObjectWrapper<>(site.getValue().getSize()));
        sizeColumn.setPrefWidth(70);
        externalLinks.getColumns().add(sizeColumn);

        externalLinks.getSelectionModel().setCellSelectionEnabled(true);
        copyController.addTable(externalLinks);
    }


    /**
     * Sets up the columns for the internal links
     */
    private void initInternalLinks()
    {
        TableColumn<Page, String> urlColumn = new TableColumn<>("URL");
        urlColumn.setCellValueFactory(page -> new ReadOnlyStringWrapper(page.getValue().getUrl()));
        urlColumn.setPrefWidth(200);
        internalLinks.getColumns().add(urlColumn);

        TableColumn<Page, Integer> inLinkNumColumn = new TableColumn<>("# of InLinks");
        inLinkNumColumn.setCellValueFactory(page -> new ReadOnlyObjectWrapper<>(page.getValue().getInLinks().size()));
        inLinkNumColumn.setPrefWidth(70);
        internalLinks.getColumns().add(inLinkNumColumn);

        TableColumn<Page, Integer> outLinkNumColumn = new TableColumn<>("# of OutLinks");
        outLinkNumColumn.setCellValueFactory(page -> new ReadOnlyObjectWrapper<>(page.getValue().getOutLinks().size()));
        outLinkNumColumn.setPrefWidth(80);
        internalLinks.getColumns().add(outLinkNumColumn);

        TableColumn<Page, String> typeColumn = new TableColumn<>("Content Type");
        typeColumn.setCellValueFactory(page -> new ReadOnlyStringWrapper(page.getValue().getType()));
        typeColumn.setPrefWidth(80);
        internalLinks.getColumns().add(typeColumn);

        TableColumn<Page, Integer> sizeColumn = new TableColumn<>("Size");
        sizeColumn.setCellValueFactory(page -> new ReadOnlyObjectWrapper<>(page.getValue().getSize()));
        sizeColumn.setPrefWidth(70);
        internalLinks.getColumns().add(sizeColumn);

        TableColumn<Page, String> indexColumn = new TableColumn<>("Indexable");
        indexColumn.setCellValueFactory(page -> new ReadOnlyStringWrapper(
                page.getValue().getDocumentInfo().getIndexable() ? "Indexable" : "Non-Indexable"));
        indexColumn.setPrefWidth(95);
        internalLinks.getColumns().add(indexColumn);

        internalLinks.getSelectionModel().setCellSelectionEnabled(true);
        copyController.addTable(internalLinks);
    }

    /**
     * Sets up the columns for the issues table
     */
    private void initIssues()
    {
        TableColumn<Issue, String> categoryColumn = new TableColumn<>("Category");
        categoryColumn.setCellValueFactory(statusIssue -> new ReadOnlyStringWrapper(
                statusIssue.getValue().getCategory()));
        issues.getColumns().add(categoryColumn);

        TableColumn<Issue, String> urlColumn = new TableColumn<>("URL");
        urlColumn.setCellValueFactory(issue -> new ReadOnlyStringWrapper(
                issue.getValue().getUrl()));
        issues.getColumns().add(urlColumn);

        TableColumn<Issue, String> summaryColumn = new TableColumn<>("Summary");
        summaryColumn.setCellValueFactory(issue -> new ReadOnlyStringWrapper(issue.getValue().getSummary()));
        issues.getColumns().add(summaryColumn);

        issues.getSelectionModel().setCellSelectionEnabled(true);
        copyController.addTable(issues);
    }

    private void initKeywords()
    {
        TableColumn<Keyword, String> wordCol = new TableColumn<>("Search Text");
        wordCol.setCellValueFactory(keyword -> new ReadOnlyStringWrapper(keyword.getValue().getWord()));
        wordCol.setPrefWidth(150);
        keywordsTable.getColumns().add(wordCol);

        TableColumn<Keyword, Integer> occurrenceCol = new TableColumn<>("Total Occurrences");
        occurrenceCol.setCellValueFactory(keyword -> new ReadOnlyObjectWrapper<>(keyword.getValue().getOccurrences()));
        occurrenceCol.setPrefWidth(110);
        keywordsTable.getColumns().add(occurrenceCol);

        TableColumn<Keyword, Integer> pageTotalCol = new TableColumn<>("Total Pages Found");
        pageTotalCol.setCellValueFactory(keyword -> new ReadOnlyObjectWrapper<>(
                keyword.getValue().getTotalLocations()));
        pageTotalCol.setPrefWidth(110);
        keywordsTable.getColumns().add(pageTotalCol);

        //handle copy stuff
        keywordsTable.getSelectionModel().setCellSelectionEnabled(true);
        copyController.addTable(keywordsTable);
    }
}