package com.scrape.ephraim.crawler;

import com.scrape.ephraim.data.StatusIssue;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class Visitor {

    ///composite fetcher
    Fetcher mFetcher;

    //association to the crawler
    List<Crawler> mCrawler;

    ///association to the scraper
    Scraper mScraper;

    ///the different urls we have to visit
    List<String> mUrls;

    ///lists the origin

    /**
     * Constructor
     */
    public Visitor()
    {
        mFetcher = new Fetcher();
        mCrawler = new ArrayList<>();
        mUrls = new ArrayList<>();
    }

    /**
     * Sets the association to the crawler
     * @param crawler
     */
    public void setCrawler(Crawler crawler) {
        mCrawler.add(crawler);
    }

    /**
     * Sets the association to the scraper
     * @param scraper
     */
    public void setScraper(Scraper scraper)
    {
        mScraper = scraper;
    }

    public void addUrl(String url)
    {
        mUrls.add(url);
    }

    /**
     * Visits the url page
     */
    public List<List<String>> visit()
    {
        //res
        List<List<String>> res = new ArrayList<>();
        //the executor
        Executor executor = Executors.newFixedThreadPool(256);

        //list of futures that we are going to combine into one later
        //the loop will populate this list with a request to each url
        List<CompletableFuture<ResponseWrapper>> futures = new ArrayList<>();
        for (String url : mUrls) {
            CompletableFuture<ResponseWrapper> futureDoc = CompletableFuture.supplyAsync(()-> {
                Fetcher fetcher = new Fetcher(url);
                return fetcher.fetch();
            }, executor);
            futures.add(futureDoc);
        }

        //make one giant combined future
        CompletableFuture<Void> combinedFutureVoid = CompletableFuture.allOf(futures.toArray(
                new CompletableFuture[futures.size()]));
        CompletableFuture<List<ResponseWrapper>> combinedFuture = combinedFutureVoid.thenApply(
                t -> futures.stream().map(CompletableFuture::join).collect(Collectors.toList()));
        try {
            var responses = combinedFuture.get();
            System.out.println("Fetching done!");
            for (int i = 0; i < responses.size(); i++)
            {
                var response = responses.get(i);
                //check if this is a 400 error
                if (response.getResponseCode() > 399)
                {
                    mScraper.getIssues().addIssue(new StatusIssue(response.getResponseCode(), response.getUrl()));
                    var x = response.getHeaders();
                }
                else
                {
                    //scrape the document
                    var document = response.getDocument();
                    var url = responses.get(i).getUrl();
                    mScraper.setParentUrl(url);
                    mScraper.scrapePage(document, url);
                    res.add(mScraper.getInternalLinks());
                }

                //store the headers
                mScraper.getHeaders().addResponseHeader(response.getUrl(), responses.get(i).getHeaders());
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } finally {
            return res;
        }
    }
}
