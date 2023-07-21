package com.scrape.ephraim.data;

import com.scrape.ephraim.crawler.ResponseWrapper;
import javafx.scene.control.TableView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExternalSite implements DocumentHolder
{
    ///the url of the external site
    private String mUrl;

    ///the pages which link to this external site
    private HashMap<String, Integer> mInLinks;

    ///this external site's response code
    private int mResponseCode;

    ///this external site's http headers
    private HashMap<String, String> mHeaders;

    ///the content type of this site
    private String mContentType;

    ///size of the site
    private int mSize;

    ///the content type
    private String mType;

    ///document info to hold stuff about this site
    private DocumentInfo mDocumentInfo;

    ///association to the external sites observer
    private TableView<ExternalSite> mObserverExternals;

    /**
     * Constructor
     * @param url the url
     */
    public ExternalSite(String url)
    {
        mUrl = url;
        mInLinks = new HashMap<>();
        mDocumentInfo = new DocumentInfo(this);

        mSize = -1;
        mHeaders = null;
        mResponseCode = -11;
        mType = "";
    }

    public void addOccurrence(String url)
    {
        if (mInLinks.containsKey(url))
        {
            mInLinks.put(url, mInLinks.get(url) + 1);
        }
        else
        {
            mInLinks.put(url, 1);
        }
    }

    /**
     * Store the data after this page is visited.
     * @param response
     */
    public void processResponse(ResponseWrapper response, List<Keyword> keywords)
    {
        mResponseCode = response.getResponseCode();
        mHeaders = response.getHeaders();
        mContentType = response.getType();
        mSize = response.getSize();
        mObserverExternals.getItems().add(this);

        if (response.getDocument() != null)
            mDocumentInfo.processDocument(response.getDocument(), keywords);
    }

    /**
     * Getter for the url
     * @return
     */
    public String getUrl()
    {
        return mUrl;
    }

    /**
     * Getter for inlinks size
     * @return
     */
    public int getOccurrences() {return mInLinks.size();}

    /**
     * Getter for the inlinks
     * @return
     */
    public HashMap<String, Integer> getInLinks() {return mInLinks;}

    /**
     * Getter for the headers
     * @return the headers
     */
    public HashMap<String, String> getHeaders() {return mHeaders;}

    /**
     * Getter for the response code
     * @return the response code
     */
    public int getResponseCode() {return mResponseCode;}

    /**
     * Getter for the content type
     * @return type
     */
    public String getType() {return mContentType;}

    /**
     * Getter for the size
     * @return the size
     */
    public int getSize() {return mSize;}

    /**
     * Setter for the external observer association
     * @param externals a table
     */
    public void setObserverExternals(TableView<ExternalSite> externals) {mObserverExternals = externals;}

    /**
     * Returns an array of strings that represent this page object
     * @return
     */
    public List<String> saveCSV()
    {
        ArrayList<String> line = new ArrayList<>();

        //add the url
        line.add(mUrl);

        //add the response code
        if (mResponseCode != -11)
            line.add(String.valueOf(mResponseCode));
        else
            line.add(" ");

        //add the content type
        line.add(mType);

        //add the size
        line.add(String.valueOf(mSize));

        //add the inlinks
        line.add(listToString(mInLinks.keySet()));

        //add the document info
        line.addAll(mDocumentInfo.saveCSV());

        //add the headers
        if (mHeaders != null)
            line.add(mapToString(mHeaders));

        return line;
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

    /**
     * Converts a map into a csv item
     * @param map
     * @return
     */
    public String mapToString(Map<String, String> map)
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
