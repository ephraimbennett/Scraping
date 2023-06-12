package com.scrape.ephraim.ui;

import com.scrape.ephraim.crawler.Scraper;
import com.scrape.ephraim.data.Issue;
import com.scrape.ephraim.data.Page;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

import java.util.Map;

public class Descriptor extends TitledPane
{

    public Descriptor()
    {
        super();
    }


    /**
     * Populates description for a page object
     * @param page
     */
    public void populateDescriptorPage(Page page, Scraper scraper) {
        this.setText("Viewing " + page.getUrl());

        //create the inlinks list
        ListView<String> inLinksView = new ListView<>();
        ObservableList<String> inLinksObservable = FXCollections.observableArrayList(page.getInLinks());
        inLinksView.setItems(inLinksObservable);

        //create the outlinks list
        ListView<String> outLinksView = new ListView<>();
        ObservableList<String> x = FXCollections.observableArrayList(page.getOutLinks());
        outLinksView.setItems(x);

        //create the external links list
        ListView<String> externalLinksView = new ListView<>();
        ObservableList<String> ext = FXCollections.observableArrayList(page.getExternalLinks());
        externalLinksView.setItems(ext);

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


        //display the titles
        VBox titleSection = new VBox(new Label("Title"), new Label(page.getDocumentInfo().getTitle()));
        titleSection.getChildren().add(new Label("\n Meta Description"));
        Label metaDescription = new Label(page.getDocumentInfo().getMetaDescription());
        metaDescription.setWrapText(true);
        metaDescription.setTextAlignment(TextAlignment.LEFT);
        metaDescription.setMaxWidth(200);
        metaDescription.setPrefHeight(Region.USE_COMPUTED_SIZE);
        titleSection.getChildren().add(metaDescription);
        titleSection.setPrefHeight(500);

        HBox nodes = new HBox();
        nodes.getChildren().add(new VBox(new Label("In Links"), inLinksView));
        nodes.getChildren().add(new VBox(new Label("Out Links (internal)"), outLinksView));
        nodes.getChildren().add(new VBox(new Label("External Links"), externalLinksView));
        nodes.getChildren().add(new VBox(new Label("Headers"), headersView));
        nodes.getChildren().add(titleSection);
        this.setContent(nodes);
    }

    /**
     * Provides description for the issue selected
     * @param issue
     */
    public void populateDescriptorIssue(Issue issue, Scraper scraper)
    {
        //reset the title
        this.setText("Issue: " + issue.getCategory());

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
        var issuePage = scraper.getSiteMap().getMap().get(issue.getUrl());
        ObservableList<String> observableInLinks = FXCollections.observableArrayList(issuePage.getInLinks());
        inLinks.setItems(observableInLinks);
        nodes.getChildren().add(new VBox(new Label("In Links"), inLinks));

        this.setContent(nodes);
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
