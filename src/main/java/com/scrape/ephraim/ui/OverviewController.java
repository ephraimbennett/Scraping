package com.scrape.ephraim.ui;

import com.scrape.ephraim.crawler.Scraper;
import com.scrape.ephraim.data.Issue;
import com.scrape.ephraim.data.Page;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.util.HashMap;
import java.util.Map;

public class OverviewController
{
    ///the grid
    GridPane gridPane;

    /**
     * Constructor
     * @param grid
     */
    public OverviewController(GridPane grid)
    {
        gridPane = grid;
    }

    public void createOverview(Scraper scraper)
    {
        //the labels we will be updating
        Label totalUrls = (Label) gridPane.getChildren().get(5);
        Label totalInternals = (Label) gridPane.getChildren().get(6);
        Label totalExternals = (Label) gridPane.getChildren().get(7);
        Label totalBroken = (Label) gridPane.getChildren().get(8);

        //update the info
        //
        int totalUrlsNum = scraper.getSiteMap().getExternals().size() + scraper.getSiteMap().getMap().size();
        totalUrls.setText(String.valueOf(totalUrlsNum));

        totalInternals.setText(String.valueOf(scraper.getSiteMap().getMap().size()));
        totalExternals.setText(String.valueOf(scraper.getSiteMap().getExternals().size()));

        //if it is a status issue, then add it to the total of broken links
        int broken = 0;
        for (Issue issue : scraper.getIssues())
        {
            if (issue.getCategory().equals("Status Issue"))
                broken++;
        }
        totalBroken.setText(String.valueOf(broken));

        //display all the different content types we have
        //store this info in a map
        HashMap<String, Integer> typesMap = new HashMap<>();

        //iterate through each page. If the page's content is on the map, increase the count
        for (Page page : scraper.getSiteMap())
        {
            //check for illegal values
            if (page.getType() == null || page.getType().length() == 0) continue;

            if (typesMap.containsKey(page.getType()))
            {
                typesMap.put(page.getType(), typesMap.get(page.getType()) + 1);
            } else {
                typesMap.put(page.getType(), 1);//set to one.
            }
        }

        //now iterate through the map and add each row
        for (Map.Entry<String, Integer> entry : typesMap.entrySet())
        {
            gridPane.addRow(gridPane.getRowCount(), new Label(entry.getKey()),
                    new Label(String.valueOf(entry.getValue())));
        }
    }

    /**
     * Clears the overview
     */
    public void clear()
    {
        //for the rows describing the site data, go through and remove them
        int totalRows = gridPane.getRowCount() - 1;
        int stopRow = 4;

        for (int i = totalRows; i > stopRow; i--)
        {
            gridPane.getChildren().remove(i * 2 + 1);
            gridPane.getChildren().remove(i * 2);
        }

        //repopulate the grid with empty data
        createOverview(new Scraper(""));

    }
}
