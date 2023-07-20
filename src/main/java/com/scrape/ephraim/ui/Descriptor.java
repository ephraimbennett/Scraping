package com.scrape.ephraim.ui;

import com.scrape.ephraim.crawler.Scraper;
import com.scrape.ephraim.data.ExternalSite;
import com.scrape.ephraim.data.Issue;
import com.scrape.ephraim.data.Keyword;
import com.scrape.ephraim.data.Page;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

import java.util.*;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;

public class Descriptor
{

    ///the descriptor box we are controlling
    private TitledPane descriptorBox;

    ///the copy controller so stuff the descriptor makes can be copied
    private CopyController copyController;

    public Descriptor(TitledPane descriptorBox, CopyController copyController)
    {
        this.descriptorBox = descriptorBox;
        this.copyController = copyController;
        descriptorBox.setPrefHeight(USE_COMPUTED_SIZE);
        descriptorBox.setPrefWidth(USE_COMPUTED_SIZE);
        descriptorBox.setMaxHeight(450);
    }


    /**
     * Populates description for a page object
     * @param page
     */
    public void populateDescriptorPage(Page page, Scraper scraper) {
        descriptorBox.setText("Viewing " + page.getUrl());

        //create the inlinks list
        ListView<String> inLinksView = new ListView<>();
        ObservableList<String> inLinksObservable = FXCollections.observableArrayList(page.getInLinks());
        inLinksView.setItems(inLinksObservable);
        copyController.addList(inLinksView);

        //create the outlinks list
        ListView<String> outLinksView = new ListView<>();
        ObservableList<String> x = FXCollections.observableArrayList(page.getOutLinks());
        outLinksView.setItems(x);
        copyController.addList(outLinksView);

        //create the external links list
        ListView<String> externalLinksView = new ListView<>();
        ObservableList<String> ext = FXCollections.observableArrayList(page.getExternalLinks());
        externalLinksView.setItems(ext);
        copyController.addList(externalLinksView);

        //display the headers
        TableView headersView = new TableView();
        TableColumn<HeaderWrapper, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(header -> new ReadOnlyStringWrapper(header.getValue().getName()));
        TableColumn<HeaderWrapper, String> valueCol = new TableColumn<>("Value");
        valueCol.setCellValueFactory(header -> new ReadOnlyStringWrapper(header.getValue().getValue()));
        headersView.getColumns().add(nameCol);
        headersView.getColumns().add(valueCol);
        var headers = scraper.getHeaders().get(page.getUrl());
        if (headers != null)
        {
            //populate the table
            for (Map.Entry<String, String> entry : headers.entrySet())
            {
                headersView.getItems().add(new HeaderWrapper(entry.getKey(), entry.getValue()));
            }
        }
        headersView.getSelectionModel().setCellSelectionEnabled(true);
        copyController.addTable(headersView);


        //display the titles
        VBox titleSection = new VBox(new Label("Title"), new Label(page.getDocumentInfo().getTitle()));
        titleSection.getChildren().add(new Label("\n Meta Description"));
        Label metaDescription = new Label(page.getDocumentInfo().getMetaDescription());
        metaDescription.setWrapText(true);
        metaDescription.setTextAlignment(TextAlignment.LEFT);
        metaDescription.setMaxWidth(200);
        metaDescription.setPrefHeight(USE_COMPUTED_SIZE);
        titleSection.getChildren().add(metaDescription);
        titleSection.setPrefHeight(500);

        //new row
        HBox nodes2 = new HBox();

        //deal with the h1
        ListView h1View = new ListView();
        var observableH1 = FXCollections.observableArrayList(page.getDocumentInfo().getH1());
        h1View.setItems(observableH1);

        //deal with the h2
        ListView h2View = new ListView();
        var observableH2 = FXCollections.observableArrayList(page.getDocumentInfo().getH2());
        h2View.setItems(observableH2);
        nodes2.getChildren().add(new VBox(new Label("H1"), h1View));
        nodes2.getChildren().add(new VBox(new Label("H2"), h2View));

        //list the issues associated
        TableView issues = new TableView();
        TableColumn<Issue, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(issue -> new ReadOnlyStringWrapper(issue.getValue().getCategory()));
        TableColumn<Issue, String> summaryCol = new TableColumn<>("Summary");
        summaryCol.setCellValueFactory(issue -> new ReadOnlyStringWrapper(issue.getValue().getSummary()));
        TableColumn<Issue, String> descriptionCol = new TableColumn<>("Description");
        descriptionCol.setCellValueFactory(issue -> new ReadOnlyStringWrapper(issue.getValue().getDescription()));
        issues.getColumns().addAll(categoryCol, summaryCol, descriptionCol);
        nodes2.getChildren().add(new VBox(new Label("Issues"), issues));//add it to the row

        issues.getSelectionModel().setCellSelectionEnabled(true);
        copyController.addTable(issues);

        //fill out the issues table
        List<Issue> associatedIssues = scraper.getIssues().findIssues(page.getUrl());
        for (var issue : associatedIssues)
        {
            issues.getItems().add(issue);
        }

        HBox nodes = new HBox();
        nodes.getChildren().add(new VBox(new Label("In Links"), inLinksView));
        nodes.getChildren().add(new VBox(new Label("Out Links (internal)"), outLinksView));
        nodes.getChildren().add(new VBox(new Label("External Links"), externalLinksView));
        nodes.getChildren().add(new VBox(new Label("Headers"), headersView));
        nodes.getChildren().add(titleSection);

        //add the type
        nodes2.getChildren().add(new VBox(new Label("Type"), new Label(page.getType())));

        descriptorBox.setContent(new VBox(nodes, nodes2));
    }

    /**
     * Provides description for the issue selected
     * @param issue
     */
    public void populateDescriptorIssue(Issue issue, Scraper scraper)
    {
        //reset the title
        descriptorBox.setText("Issue: " + issue.getCategory());

        HBox nodes = new HBox();
        nodes.setSpacing(10);
        //get the summary
        nodes.getChildren().add(new VBox(new Label("Summary"), new Label(issue.getSummary())));
        //get the description
        nodes.getChildren().add(new VBox(new Label("Description"), new Label(issue.getDescription())));
        //get the url
        nodes.getChildren().add(new VBox(new Label("Url Associated"), new Label(issue.getUrl())));
        //get the inlinks
        ListView<String> inLinks = new ListView<>();

        Set<String> inLinksSet;
        var issuePage = scraper.getSiteMap().getMap().get(issue.getUrl());
        if (issuePage == null) {//if the url can't be found in the internal links, check the external
            inLinksSet = scraper.getSiteMap().getExternals().get(issue.getUrl()).getInLinks().keySet();
        } else {
            inLinksSet = issuePage.getInLinks();
        }
        ObservableList<String> observableInLinks = FXCollections.observableArrayList(inLinksSet);
        inLinks.setItems(observableInLinks);
        nodes.getChildren().add(new VBox(new Label("In Links"), inLinks));
        copyController.addList(inLinks);

        descriptorBox.setContent(nodes);
    }

    public void populateExternalSite(ExternalSite site, Scraper scraper) {
        descriptorBox.setText("Viewing: " + site.getUrl());

        HBox nodes = new HBox();

        //display the in links
        //
        TableView inLinks = new TableView();//will be displaying two columns, so need to use tableview

        //column for the url
        TableColumn<Map.Entry<String, Integer>, String> urlCol = new TableColumn<>("url");
        urlCol.setCellValueFactory(entry -> new ReadOnlyStringWrapper(entry.getValue().getKey()));
        urlCol.setPrefWidth(200);
        inLinks.getColumns().add(urlCol);

        //column for the number of appearances on that url
        TableColumn<Map.Entry<String, Integer>, String> occCol = new TableColumn<>("Occurrences");
        occCol.setCellValueFactory(entry -> new ReadOnlyStringWrapper(String.valueOf(entry.getValue().getValue())));
        occCol.setPrefWidth(100);
        inLinks.getColumns().add(occCol);

        for (Map.Entry<String, Integer> entry : site.getInLinks().entrySet())
        {
            inLinks.getItems().add(entry);
        }
        inLinks.getSelectionModel().setCellSelectionEnabled(true);
        copyController.addTable(inLinks);

        //display the http headers
        //display the headers
        TableView headersView = new TableView();
        TableColumn<HeaderWrapper, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(header -> new ReadOnlyStringWrapper(header.getValue().getName()));
        TableColumn<HeaderWrapper, String> valueCol = new TableColumn<>("Value");
        valueCol.setCellValueFactory(header -> new ReadOnlyStringWrapper(header.getValue().getValue()));
        headersView.getColumns().add(nameCol);
        headersView.getColumns().add(valueCol);
        var headers = site.getHeaders();
        if (headers != null)
        {
            //populate the table
            for (Map.Entry<String, String> entry : headers.entrySet())
            {
                headersView.getItems().add(new HeaderWrapper(entry.getKey(), entry.getValue()));
            }
        }
        headersView.getSelectionModel().setCellSelectionEnabled(true);
        copyController.addTable(headersView);

        nodes.getChildren().add(inLinks);
        nodes.getChildren().add(headersView);
        descriptorBox.setContent(nodes);
    }

    /**
     * Provides description for a keyword
     * @param keyword the keyword we are displaying
     * @param scraper just because
     */
    public void populateDescriptorKeyword(Keyword keyword, Scraper scraper)
    {
        descriptorBox.setText("Viewing Keyword: " + keyword.getWord());

        //set up shop
        HBox nodes = new HBox();
        TableView locationsTable = new TableView();
        locationsTable.setPrefWidth(500);

        //the location column
        TableColumn<Map.Entry<String, Integer>, String> locationCol = new TableColumn<>("location");
        locationCol.setCellValueFactory(entry -> new ReadOnlyStringWrapper(entry.getValue().getKey()));
        locationCol.setPrefWidth(350);
        locationsTable.getColumns().add(locationCol);

        //the occurrences per page column
        TableColumn<Map.Entry<String, Integer>, Integer> occurrencesCol = new TableColumn<>("occurrences");
        occurrencesCol.setCellValueFactory(entry -> new ReadOnlyObjectWrapper<>(entry.getValue().getValue()));
        occurrencesCol.setPrefWidth(90);
        locationsTable.getColumns().add(occurrencesCol);

        for (Map.Entry<String, Integer> entry : keyword.getLocations().entrySet())
        {
            locationsTable.getItems().add(entry);
        }
        locationsTable.getSelectionModel().setCellSelectionEnabled(true);
        copyController.addTable(locationsTable);

        //add all to nodes and then set that as the box's content
        nodes.getChildren().add(locationsTable);
        descriptorBox.setContent(nodes);
    }

    /**
     * Resets the descriptor
     */
    public void clear()
    {
        descriptorBox.setText("(Empty Selection)");
        descriptorBox.setContent(new HBox());
    }

    /**
     * Used to help display the headers in a tableview easier
     */
    private class HeaderWrapper
    {
        ///the name
        private String mName;

        ///the value
        private String mValue;

        public HeaderWrapper(String name, String value)
        {
            mName = name;
            mValue = value;
        }

        public String getName() {return mName;}

        public String getValue() {return mValue;}
    }
}
