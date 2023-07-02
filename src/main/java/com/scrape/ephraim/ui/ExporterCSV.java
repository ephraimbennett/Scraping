package com.scrape.ephraim.ui;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.scrape.ephraim.crawler.Scraper;
import com.scrape.ephraim.data.Page;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class ExporterCSV
{
    ///the file to export to
    private File mFile;

    /**
     * Constructor
     * @param file
     */
    public ExporterCSV(File file)
    {
        mFile = file;
    }

    /**
     * Creates a csv file to export there
     * @param scraper
     */
    public void export(Scraper scraper)
    {
        try
        {
            FileWriter outputFile = new FileWriter(mFile);
            CSVWriter writer = new CSVWriter(outputFile);

            //head for the internal links section
            String[] internalLinksHead = {"url", "response code", "content type", "inlinks", "internal outlinks", "external links",
                    "title", "meta description", "h1's", "h2's", "headers"};
            writer.writeNext(internalLinksHead);
            //populate it with the internal links
            for (Page page : scraper.getSiteMap())
            {
                if (page.getResponseCode() == 0)
                {
                    System.out.println("huh " + page.getUrl());
                }

                //grab the list from the page
                List<String> pageLine = page.saveCSV();

                //convert to basic array and then write it
                writer.writeNext(pageLine.toArray(new String[0]));
            }
            writer.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
}
