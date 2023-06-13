package com.scrape.ephraim.data;

import java.util.ArrayList;
import java.util.HashMap;

public class Headers {
    ///map url:headers
    private HashMap<String, HashMap<String, String>> mResponseHeaders;

    /**
     * Default constructor
     */
    public Headers() {
        mResponseHeaders = new HashMap<>();
    }

    /**
     * Adds a response header
     *
     * @param url
     * @param headers
     */
    public void addResponseHeader(String url, HashMap<String, String> headers) {
        mResponseHeaders.put(url, headers);
    }

    /**
     * literally calls map.get
     * @param url
     * @return the respective headers
     */
    public HashMap<String, String> get(String url)
    {
        return mResponseHeaders.get(url);
    }



}
