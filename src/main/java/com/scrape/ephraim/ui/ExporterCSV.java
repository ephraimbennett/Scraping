package com.scrape.ephraim.ui;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.scrape.ephraim.crawler.Scraper;
import com.scrape.ephraim.data.Page;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExporterCSV
{
    ///the file to export to
    File mFile;

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
            String[] internalLinksHead = {"url", "inlinks", "internal outlinks", "external links", "title",
            "meta description", "h1's", "h2's", "headers"};
            writer.writeNext(internalLinksHead);
            //populate it with the internal links
            for (Page page : scraper.getSiteMap())
            {
                //grab the list from the page
                List<String> pageLine = page.saveCSV();

                //grab the headers and add it
                var headers = scraper.getHeaders().get(page.getUrl());
                if (headers != null)
                    pageLine.add(mapToString(headers));

                //convert to basic array and then add
                writer.writeNext(pageLine.toArray(new String[0]));
            }
            writer.close();
        }
        catch(IOException e)
        {

        }
    }

    /**
     * Converts a map into a csv item
     * @param map
     * @return
     */
    private String mapToString(Map<String, String> map)
    {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> item : map.entrySet())
        {
            builder.append(item.getKey()).append("=").append(item.getValue()).append(",");
        }
        if (builder.length() > 0)
            builder.deleteCharAt(builder.length() - 1);

        return builder.toString();
    }
}
