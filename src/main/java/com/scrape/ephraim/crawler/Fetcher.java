package com.scrape.ephraim.crawler;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.OkHttpClient;
import okhttp3.Response;

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
import java.util.concurrent.*;

public class Fetcher {
    /// the url this fetcher is assigned to
    private String mUrl;

    /// the document this fetcher should return
    private Document mDocument = null;

    /**
     * Default constructor
     */
    public Fetcher()
    {

    }

    /**
     * Constructor that supplies url
     * @param url
     */
    public Fetcher(String url)
    {
        mUrl = url;
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
    public Document fetch()
    {
        Document document = null;
        try {
            var connection = Jsoup.connect(mUrl);
            connection.timeout(10 * 1000);
            document = connection.get();
            System.out.println(mUrl + " done!");
            return document;

        } catch (HttpStatusException e)
        {
            System.out.println("HTTP Status Error! " + e.getStatusCode() + " " + e.getUrl());
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
            return document;
        }
    }

}
