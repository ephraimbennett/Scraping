package com.scrape.ephraim.ui;

import com.scrape.ephraim.crawler.Crawler;
import com.scrape.ephraim.crawler.Scraper;
import com.scrape.ephraim.data.Page;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Controller
{

    @FXML
    public Button submitButton;

    @FXML
    public TreeView treeView;

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
        Crawler crawler = new Crawler("https://unclejulios.com/");
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
            if (page.getPath().size() > 1)
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

}