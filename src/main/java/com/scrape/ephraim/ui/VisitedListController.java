package com.scrape.ephraim.ui;

import javafx.application.Platform;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;

public class VisitedListController extends FetcherObserver
{
    ///the list
    ListView visitedLinks;

    /**
     * Constructor
     * @param visitedLinks
     */
    public VisitedListController(ListView visitedLinks)
    {
        this.visitedLinks = visitedLinks;
    }

    @Override
    public void update(String url)
    {
        Platform.runLater(()->{
            try {
                visitedLinks.getItems().add(url);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        });
    }


}
