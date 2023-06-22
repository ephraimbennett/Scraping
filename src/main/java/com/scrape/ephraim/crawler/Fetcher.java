package com.scrape.ephraim.crawler;

import com.scrape.ephraim.ui.FetcherObserver;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.nio.charset.MalformedInputException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.*;

public class Fetcher {
    /// the url this fetcher is assigned to
    private String mUrl;

    /// the document this fetcher should return
    private Document mDocument = null;

    /// list of observers
    private ArrayList<FetcherObserver> mObservers;

    /**
     * Default constructor
     */
    public Fetcher()
    {
        mObservers = new ArrayList<>();
    }

    /**
     * Constructor that supplies url
     * @param url
     */
    public Fetcher(String url)
    {
        mUrl = url;
        mObservers = new ArrayList<>();
    }

    /**
     * Getter for URL
     * @return string for url
     */
    public String getUrl() {return mUrl;}

    /**
     * Setter for URL
     * @param url
     */
    public void setUrl(String url) {mUrl = url;}

    /**
     * Getter for the document
     * @return the document
     */
    public Document getDocument() {return mDocument;}


    ///returns a future document
    public ResponseWrapper fetch()
    {
        Document document = null;
        HashMap<String, String> responseHeaders = new HashMap<>();
        int responseCode = 0;
        try {
            var connection = Jsoup.connect(mUrl);
            connection.timeout(10 * 1000);
            connection.ignoreContentType(true);
            Connection.Response response = connection.execute();
            responseHeaders = new HashMap<>(response.headers());
            responseCode = response.statusCode();
            document = response.parse();
//            System.out.println(mUrl + " done!");
            updateObservers();
        } catch (HttpStatusException e)
        {
//            System.out.println("HTTP Status Error! " + e.getStatusCode() + " " + e.getUrl());
            responseCode = e.getStatusCode();
        } catch (UnsupportedMimeTypeException e)
        {
            System.out.println("Unsuported Mime Type! " + e.getMimeType());
        }
        catch (MalformedURLException e)
        {
            System.out.println("Malformed Url! " + mUrl);
        }
        catch (SocketTimeoutException e)
        {
            System.out.println("the socket timed out! " + mUrl);
        }
        catch (ConnectException e)
        {
            System.out.println("Connection timed out! " + mUrl);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally {
            ResponseWrapper wrapper = new ResponseWrapper(document, responseHeaders, mUrl, responseCode);
            return wrapper;
        }
    }

    /**
     * Adds an observer
     * @param observer
     */
    public void addObserver(FetcherObserver observer)
    {
        mObservers.add(observer);
    }

    /**
     * Update the oberservers
     */
    public void updateObservers()
    {
        for (var observer : mObservers)
        {
            observer.update(mUrl);
        }
    }

}
