package com.scrape.ephraim.crawler;

import com.scrape.ephraim.data.StatusIssue;
import com.scrape.ephraim.ui.FetcherObserver;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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

    ///list of the observers
    List<FetcherObserver> mObservers;

    ArrayList<ResponseWrapper> codes;

    /**
     * Constructor
     */
    public Visitor()
    {
        mFetcher = new Fetcher();
        mCrawler = new ArrayList<>();
        mUrls = new ArrayList<>();
        mObservers = new ArrayList<>();
        codes = new ArrayList<>();
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
        codes = new ArrayList<>();
        //the executor
        ExecutorService executor = Executors.newCachedThreadPool();

        //list of futures that we are going to combine into one later
        //the loop will populate this list with a request to each url
        List<CompletableFuture<ResponseWrapper>> futures = new ArrayList<>();
        for (String url : mUrls) {
            CompletableFuture<ResponseWrapper> futureDoc = CompletableFuture.supplyAsync(()-> {
                Fetcher fetcher = new Fetcher(url);
                for (FetcherObserver observer : mObservers)
                {
                    observer.setFetcher(fetcher);
                }
                ResponseWrapper response = fetcher.ok();
                codes.add(response);
                return response;
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


            HashSet<ResponseWrapper> total = new HashSet<>();
            total.addAll(codes);
            total.addAll(responses);

            try {

                for (var response : total) {
                    //check if this is a 400 error
                    if (response.getResponseCode() > 299) {
                        mScraper.getIssues().addIssue(new StatusIssue(response.getResponseCode(), response.getUrl()));
                    }
                    //scrape the document
                    var url = response.getUrl();

                    mScraper.setParentUrl(url);
                    mScraper.scrapePage(response);
                    res.add(mScraper.getInternalLinks());


                    //store the headers
                    mScraper.getHeaders().addResponseHeader(response.getUrl(), response.getHeaders());
                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } finally {
            executor.shutdown();
            return res;
        }
    }

    /**
     * Setter for the observers
     * @param observers
     */
    public void setObservers(ArrayList<FetcherObserver> observers) {mObservers = observers;}
}
