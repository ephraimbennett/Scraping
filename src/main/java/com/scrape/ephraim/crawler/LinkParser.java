package com.scrape.ephraim.crawler;

import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LinkParser {
    ///the document to examine
    private Document mDocument = null;

    ///list of the internal links we find
    private List<String> mInternalLinks;

    ///list of the external links we find
    private List<String> mExternalLinks;

    ///the domain name
    private String mDomainName;

    ///parent url
    private String mParentUrl;

    ///regex pattern for the domain name
    private Pattern mDomainPattern;

    /**
     * only constructor cause fuck that
     */
    public LinkParser()
    {
        mInternalLinks = new ArrayList<String>();
        mExternalLinks = new ArrayList<String>();
        mDomainName = "%";
    }

    /**
     * Setter for the parent url
     * @param parent
     */
    public void setParentUrl(String parent) {mParentUrl = parent;}

    /**
     * Setter for domain name
     * Also creates the regex pattern for the domain name
     * @param domainName
     */
    public void setDomainName(String domainName){
        mDomainName = domainName;
        mDomainPattern = Pattern.compile("^https?://(www.)?([\\w]+\\.)?" + domainName);
    }

    /**
     * Getter for domain name
     * @return
     */
    public String getDomainName(){return mDomainName;}


    /**
     * Setter for document
     * @param doc
     */
    public void setDocument(Document doc)
    {
        mDocument = doc;
    }

    /**
     * Getter for internal links
     * @return
     */
    public List<String> getInternalLinks() {return mInternalLinks;}

    /**
     * Getter for th external links
     * @return
     */
    public List<String> getExternalLinks() {return mExternalLinks;}

    /**
     * Resets the list of internal and external links
     */
    public void reset()
    {
        mInternalLinks.clear();
        mExternalLinks.clear();
    }

    /**
     * Clears this parser, so it can parse another web page
     * Clears the link lists and the document.
     */
    public void clear()
    {
        mInternalLinks = new ArrayList<>();
        mExternalLinks = new ArrayList<>();
        mDocument = null;
    }

    /**
     * Looks for the links
     */
    public void parse(Document document)
    {
        mDocument = document;
        if (mDocument == null) {
            return;
        }

        CheckA();
    }

    //private functions

    /**
     * Grabs A elements specifically.
     */
    private void CheckA()
    {
        var elementsA = mDocument.getElementsByTag("a");
//        System.out.println(mDocument);
        for (var a : elementsA)
        {
            String url = a.attr("href");
            processUrl(url);
        }
    }

    /**
     * Processes a valid url into a link object
     * @param url
     */
    private void processUrl(String url)
    {
        String res = "";

        //remove the on page jump from the url
        Matcher mJump = Patterns.onPagePattern.matcher(url);
        if (mJump.find())
        {
           url = mJump.group(1);
//           System.out.println(url + " removed: " + mJump.group(2));
        }

        //if it begins with a slash it's gotta be internal & relative
        Matcher m1 = Patterns.slashPattern.matcher(url);
        if (m1.find())
        {
            res = "https://" + mDomainName + url;

            mInternalLinks.add(res);
            return;
        }

        //if it starts with the domain name, then it's internal but not relative
        Matcher m2 = mDomainPattern.matcher(url);
        if (m2.find())
        {
            res = url;

            mInternalLinks.add(res);
            return;
        }

        //at this point it's external (and therefore not relative)
        mExternalLinks.add(url);
    }

}
