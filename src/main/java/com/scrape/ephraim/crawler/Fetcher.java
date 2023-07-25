package com.scrape.ephraim.crawler;

import com.scrape.ephraim.ui.FetcherObserver;
import com.squareup.okhttp.*;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.helper.ValidationException;
import org.jsoup.nodes.Document;

import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
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

    /// the timeout limit
    private int mTimeout;

    /// client object
    OkHttpClient mClient;

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
        mTimeout = 15;
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
     * Setter for the timeout
     * @param timeout
     */
    public void setTimeout(int timeout) {mTimeout = timeout;}

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
        int responseCode = 2000;
        String type = "";
        int size = 0;

        try {
            var connection = Jsoup.connect(mUrl);
            connection.timeout(20 * 1000);
            connection.ignoreContentType(true);
            Connection.Response response = connection.execute();

            //update the info variables
            type = response.contentType();
            responseHeaders = new HashMap<>(response.headers());
            responseCode = response.statusCode();
            document = response.parse();

            //try to get the size in bytes
            try {
                size = response.bodyAsBytes().length;
            } catch (ValidationException e)
            {
            }

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
            responseCode = -1;
        }
        catch (ConnectException e)
        {
            System.out.println("Connection timed out! " + mUrl);
        }
        catch (SSLHandshakeException e)
        {
            System.out.println("Handshake exception! " + mUrl);
            responseCode = 525;//this is a 525
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally {
            ResponseWrapper wrapper = new ResponseWrapper(document, responseHeaders, mUrl, responseCode, type, size);
            return wrapper;
        }
    }

    public ResponseWrapper ok()
    {
        //set up shop
        Document document = null;
        HashMap<String, String> responseHeaders = new HashMap<>();
        int responseCode = 2000;
        String type = "";
        int size = 0;

        //make a basic get request
        Request request = new Request.Builder()
                .url(mUrl)
                .build();

        mClient = new OkHttpClient();
        mClient.setConnectTimeout(mTimeout, TimeUnit.SECONDS);

        Call call = mClient.newCall(request);
        try {
            Response response = call.execute();

            //store necessary data
            var body = response.body();
            document = Jsoup.parse(body.string());
            for (String name : response.headers().names())
            {
                responseHeaders.put(name, response.header(name));
            }
            responseCode = response.code();
            size = (int)body.contentLength();
            if (responseHeaders.containsKey("Content-Type")) {
                type = responseHeaders.get("Content-Type");
            }

            System.out.println(mUrl + " done!");
        }
        catch (SSLHandshakeException e)
        {
            System.out.println("Handshake exception! " + mUrl);
            responseCode = 525;
        }
        catch (ProtocolException e)
        {
            e.printStackTrace();
            responseCode = -5;
        }
        catch (SocketTimeoutException e)
        {
            responseCode = -1;
        }
        catch (ConnectException e)
        {
            responseCode = -7;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            updateObservers();
            return new ResponseWrapper(document, responseHeaders, mUrl, responseCode, type, size);
        }
    }

    /**
     * Force cancels the call
     */
    public void cancel()
    {
        if (mClient != null)
            mClient.getDispatcher().getExecutorService().shutdown();
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
