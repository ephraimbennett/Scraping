package com.scrape.ephraim.ui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.scrape.ephraim.crawler.Scraper;
import com.scrape.ephraim.data.ExternalSite;
import com.scrape.ephraim.data.Issue;
import com.scrape.ephraim.data.Page;
import javafx.stage.FileChooser;

import java.io.*;
import java.lang.reflect.InaccessibleObjectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExporterJSON
{
    ///the file we are writing to
    File mFile;

    public ExporterJSON(File file)
    {
        mFile = file;
    }

    public void export(Scraper scraper)
    {
        try {
            Gson gson = new GsonBuilder().create();
            FileWriter outputWriter = new FileWriter(mFile);

            //convert into gson scraper wrapper object
            GsonScraper gsonScraper = new GsonScraper(scraper);

            //write it
            gson.toJson(gsonScraper, outputWriter);

            //close the writer
            outputWriter.close();

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void open(Scraper scraper)
    {

        Gson gson = new GsonBuilder().create();

        //process the file
        try {
            FileReader reader = new FileReader(mFile);
            GsonScraper gsonScraper = gson.fromJson(reader, GsonScraper.class);

            scraper.setDomain(gsonScraper.domain);

            for (Page page : gsonScraper.pageList)
            {
                scraper.getSiteMap().addPage(page);
            }
            for (ExternalSite site : gsonScraper.externalsList)
            {
                site.setObserverExternals(scraper.getSiteMap().getObserverExternals());
                site.updateObserver();
                scraper.getSiteMap().getExternals().put(site.getUrl(), site);
            }
            for (Issue issue : gsonScraper.issues)
            {
                scraper.getIssues().addIssue(issue);
            }
        } catch (FileNotFoundException e)
        {

        }
    }

    public class GsonScraper
    {
        public String domain;

        public Page[] pageList;

        public ExternalSite[] externalsList;

        public Issue[] issues;

        public GsonScraper(Scraper scraper)
        {
            domain = scraper.getDomain();
            pageList = scraper.getSiteMap().getMap().values().toArray(new Page[0]);
            externalsList = scraper.getSiteMap().getExternals().values().toArray(new ExternalSite[0]);
            issues = scraper.getIssues().toArray();
        }
    }
}