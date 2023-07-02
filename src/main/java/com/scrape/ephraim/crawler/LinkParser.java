package com.scrape.ephraim.crawler;

import org.jsoup.nodes.Document;

import java.net.MalformedURLException;
import java.net.URL;
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

    ///regex pattern to check if the default domain name exists in the url
    private Pattern mDefaultPattern;

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
     * @params url, defaultDomain
     */
    public void setDomainName(String url, String defaultDomain) {
        Matcher matcher = Patterns.domainPattern.matcher(url);
        if (matcher.find())
        {
            mDomainName = matcher.group(1);
        } else {
            mDomainName = defaultDomain;
        }
        mDomainPattern = Pattern.compile("^https?://(www.)?([\\w]+\\.)?" + mDomainName);
        mDefaultPattern = Pattern.compile("^https?://(www.)?([\\w]+\\.)?" + defaultDomain);
        mParentUrl = url;
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
        mDomainName = "";
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
        CheckImg();
    }

    //private functions

    /**
     * Grabs img elements
     */
    private void CheckImg()
    {
        var imgElements = mDocument.getElementsByTag("img");
        for (var img : imgElements)
        {
            String url = img.attr("src");
            processUrl(url);
        }
    }

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

        //determine if it's a relative path
        Matcher relative = Patterns.relativePattern.matcher(url);
        if (!relative.find())
        {
            try {
                URL base = new URL(mParentUrl);
                URL resolvedUrl = new URL(base, url);
                url = resolvedUrl.toString();
            } catch (MalformedURLException e)
            {
                return;
            }
        }

        //if it begins with a slash it's gotta be internal & relative
        Matcher m1 = Patterns.slashPattern.matcher(url);
        if (m1.find())
        {
            res = "https://" + mDomainName + url;

            mInternalLinks.add(res);
            return;
        }

        //if it matches the parentUrl's domain name
        Matcher m2 = mDomainPattern.matcher(url);
        if (m2.find())
        {
            res = url;

            mInternalLinks.add(res);
            return;
        }

        //if it matches the default domain name
        Matcher defaultMatcher = mDefaultPattern.matcher(url);
        if (defaultMatcher.find())
        {
            res = url;

            mInternalLinks.add(res);
            return;
        }

        //at this point it's external (and therefore not relative)
//        if (url.contains("https://order.unclejulios.com"))
//        {
//            System.out.println(url);
//        }
        mExternalLinks.add(url);
    }


}
