package com.scrape.ephraim.ui;

import com.opencsv.CSVWriter;
import com.scrape.ephraim.crawler.Scraper;
import com.scrape.ephraim.data.ExternalSite;
import com.scrape.ephraim.data.Issue;
import com.scrape.ephraim.data.Page;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public class ExporterCSV
{
    ///the file to exportPage to
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
     * Creates a csv file to exportPage there
     * @param scraper
     */
    public void exportPage(Scraper scraper)
    {
        try
        {
            FileWriter outputFile = new FileWriter(mFile);
            CSVWriter writer = new CSVWriter(outputFile);

            //head for the internal links section
            String[] internalLinksHead = {"URL", "Status Code", "Content-Type", "Size", "InLinks", "Internal OutLinks",
                    "External Links", "Title", "Meta Description", "H1(s)", "H2(s)", "Keywords", "Headers"};
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

    /**
     * Exports the external sites to a csv file
     * @param scraper to grab from
     */
    public void exportExternal(Scraper scraper)
    {
        try
        {
            FileWriter outputFile = new FileWriter(mFile);
            CSVWriter writer = new CSVWriter(outputFile);

            //head for the internal links section
            String[] internalLinksHead = {"URL", "Status Code", "Content-Type", "Size", "InLinks",
                    "Title", "Meta Description", "H1(s)", "H2(s)", "Keywords", "Headers"};
            writer.writeNext(internalLinksHead);
            //populate it with the internal links
            for (ExternalSite site : scraper.getSiteMap().getExternals().values())
            {
                if (site.getResponseCode() == 0)
                {
                    System.out.println("huh " + site.getUrl());
                }

                //grab the list from the page
                List<String> pageLine = site.saveCSV();

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

    /**
     * Exports an issue
     * @param scraper to grab from
     */
    public void exportIssues(Scraper scraper)
    {
        try
        {
            FileWriter outputFile = new FileWriter(mFile);
            CSVWriter writer = new CSVWriter(outputFile);

            //head for the internal links section
            String[] internalLinksHead = {"Category", "URL", "Summary", "Description", "Severity", "InLinks"};
            writer.writeNext(internalLinksHead);
            //populate it with the internal links
            for (Issue issue : scraper.getIssues())
            {
                //grab the list from the page
                List<String> pageLine = issue.saveCSV();

                //grab the inlinks
                Set<String> inLinksSet;
                var issuePage = scraper.getSiteMap().getMap().get(issue.getUrl());
                if (issuePage == null) {//if the url can't be found in the internal links, check the external
                    inLinksSet = scraper.getSiteMap().getExternals().get(issue.getUrl()).getInLinks().keySet();
                } else {
                    inLinksSet = issuePage.getInLinks();
                }
                pageLine.add(listToString(inLinksSet));

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


    /**
     * Converts a list of strings into a csv item
     * @param list
     * @return
     */
    private String listToString(Iterable<String> list)
    {
        StringBuilder links = new StringBuilder();
        for (String link : list)
        {
            links.append(link).append(",");
        }
        if (links.length() > 0)//same as above
            links.deleteCharAt(links.length() - 1);
        return links.toString();
    }

}
