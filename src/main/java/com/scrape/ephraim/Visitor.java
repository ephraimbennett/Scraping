package com.scrape.ephraim;

import com.scrape.ephraim.Fetcher;
import com.scrape.ephraim.LinkParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Visitor {
    ///document we are returning
    Document mDocument;

    ///composite link parser
    LinkParser mLinkParser;

    ///composite fetcher
    Fetcher mFetcher;

    //association to the crawler
    List<Crawler> mCrawler;

    ///the different urls we have to visit
    List<String> mUrls;

    /**
     * Constructor
     */
    public Visitor()
    {
        mLinkParser = new LinkParser();
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

    public void addUrl(String url)
    {
        mUrls.add(url);
    }

    /**
     * Visits the url page
     */
    public List<List<Link>> visit()
    {
        //res
        List<List<Link>> res = new ArrayList<>();
        Executor executor = Executors.newFixedThreadPool(64);
        List<CompletableFuture<List<Link>>> futures = new ArrayList<>();
        for (String url : mUrls) {
            CompletableFuture<Document> futureDoc = CompletableFuture.supplyAsync(()-> {
                Fetcher fetcher = new Fetcher(url);
                return fetcher.fetch();
            });
            //second future that provides parsing
            CompletableFuture<List<Link>> futureLinks = futureDoc.thenApplyAsync((document) -> {
                mLinkParser.reset();
                mLinkParser.setDomainName(mCrawler.get(0).getDomain());
                mLinkParser.parse(document);
                mLinkParser.setParentUrl(url);
                List<Link> returnList = new ArrayList<>(mLinkParser.getInternalLinks());
                return returnList;
            }, executor);
            futures.add(futureLinks);
            System.out.println("Fetching " + url + " ... ");
        }
        CompletableFuture<Void> combinedFutureVoid = CompletableFuture.allOf(futures.toArray(
                new CompletableFuture[futures.size()]));
        CompletableFuture<List<List<Link>>> combinedFuture = combinedFutureVoid.thenApply(
                t -> futures.stream().map(CompletableFuture::join).collect(Collectors.toList()));
        try {
            res = combinedFuture.get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } finally {
            return res;
        }
    }
}
