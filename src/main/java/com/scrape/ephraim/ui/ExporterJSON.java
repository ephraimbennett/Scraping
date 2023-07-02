package com.scrape.ephraim.ui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.scrape.ephraim.crawler.Scraper;
import com.scrape.ephraim.data.Page;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

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
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            FileWriter outputWriter = new FileWriter(mFile);

            //get the hasmap that contains all the pages
            HashMap<String, Page> siteMap = scraper.getSiteMap().getMap();

            //convert into an array
            Page[] siteList = siteMap.values().toArray(new Page[0]);

            //write it
            gson.toJson(siteList, outputWriter);

            //close the writer
            outputWriter.close();

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}