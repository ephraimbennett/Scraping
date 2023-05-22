package com.scrape.ephraim;

import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LinkParser {
    ///the document to examine
    private Document mDocument = null;

    ///list of the internal links we find
    private List<Link> mInternalLinks;

    ///list of the external links we find
    private List<Link> mExternalLinks;

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
        mInternalLinks = new ArrayList<Link>();
        mExternalLinks = new ArrayList<Link>();
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
        mDomainPattern = Pattern.compile("^https?://(www.)?" + domainName);
    }

    /**
     * Getter for domain name
     * @return
     */
    public String getDomainName(){return mDomainName;}


    /**
     * Getter for document
     * @param doc
     */
    public void setDocument(Document doc)
    {
        mDocument = doc;
    }

    /**
     * Setter for document
     * @return
     */
    public List<Link> getInternalLinks() {return mInternalLinks;}

    public List<Link> getExternalLinks() {return mExternalLinks;}

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
        if (mDocument == null) return;

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
            Link link = processUrl(url);
            if (link == null) continue;
            if (link.isInternal())
            {
                mInternalLinks.add(link);
            }
            else
            {
                mExternalLinks.add(link);
            }
        }
    }

    /**
     * Processes a valid url into a link object
     * @param url
     * @return the link object
     */
    private Link processUrl(String url)
    {
        //first determine if it's an on sight page


        //if it begins with a slash it's gotta be internal & relative
        Matcher m1 = Patterns.slashPattern.matcher(url);
        if (m1.find())
        {
            return new Link(url, true, true);
        }

        //if it starts with the domain name, then it's internal but not relative
        Matcher m2 = mDomainPattern.matcher(url);
        if (m2.find())
        {
            return new Link(url, true, false);
        }

        //at this point it's external (and therefore not relative)
        return new Link(url, false, false);
    }

}
