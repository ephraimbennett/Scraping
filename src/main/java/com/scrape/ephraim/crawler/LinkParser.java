package com.scrape.ephraim.crawler;

import com.squareup.okhttp.HttpUrl;
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

    ///list of the links we are going to crawl
    private List<String> mCrawlLinks;

    ///the domain name
    private String mDomainName;

    ///parent url
    private String mParentUrl;

    ///regex pattern for the domain name
    private Pattern mDomainPattern;

    ///regex pattern to check if the default domain name exists in the url
    private Pattern mDefaultPattern;

    ///association to the crawl's configuration
    private Configuration mConfiguration;

    /**
     * only constructor cause fuck that
     */
    public LinkParser()
    {
        mInternalLinks = new ArrayList<String>();
        mExternalLinks = new ArrayList<String>();
        mCrawlLinks = new ArrayList<>();
        mDomainName = "%";
    }

    /**
     * Setter for the config association
     * @param configuration
     */
    public void setConfiguration(Configuration configuration) {mConfiguration = configuration;}

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

        //do all this work to account for subdomains
        if (mConfiguration.crawlSubdomains()) {
            Matcher matcher = Patterns.domainPattern.matcher(url);
            if (matcher.find()) {
                mDomainName = matcher.group(1);
            } else {
                mDomainName = defaultDomain;
            }

            //check if the domain name has a www.
            Matcher wwwMatcher = Patterns.noWWWPattern.matcher(mDomainName);
            if (wwwMatcher.find()) {
                mDomainName = wwwMatcher.group(2);
            }

            mDomainPattern = Pattern.compile("^https?://(www.)?([\\w]+\\.)?" + mDomainName);
        } else {
            mDomainPattern = Pattern.compile("^" + defaultDomain);
        }

        mDefaultPattern = Pattern.compile("^" + defaultDomain);
        mParentUrl = url;
    }

    /**
     * Getter for domain name
     * @return
     */
    public String getDomainName(){return mDomainName;}


    /**i
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
     * Getter for the external links
     * @return
     */
    public List<String> getExternalLinks() {return mExternalLinks;}

    /**
     * Getter for the crawl links
     * @return links we are going to visit
     */
    public List<String> getCrawlLinks() {return mCrawlLinks;}

    /**
     * Clears this parser, so it can parse another web page
     * Clears the link lists and the document.
     */
    public void clear()
    {
        mInternalLinks = new ArrayList<>();
        mExternalLinks = new ArrayList<>();
        mCrawlLinks = new ArrayList<>();
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
            processUrl(url, mConfiguration.testImages());
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
            processUrl(url, true);
        }
    }

    /**
     * Processes a valid url into a link object
     * @param url
     * @param crawl - indicates if this url should be added to the crawl list
     */
    private void processUrl(String url, boolean crawl)
    {
        String res = "";

        //remove the on page jump from the url
        Matcher mJump = Patterns.onPagePattern.matcher(url);
        if (mJump.find())
        {
           url = mJump.group(1);
//           System.out.println(url + " removed: " + mJump.group(2));
        }

        if (url.equals("//facebook.com/sharer/sharer.php?u=https://www.spartanblockchain.org/challenge-page/938af51f-d589-4dd5-9589-ee8ae8603786&quote=Come join Analyst!"))
            System.out.println(" ");

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
//        Matcher m1 = Patterns.slashPattern.matcher(url);
//        if (m1.find())
//        {
//            res = "https://" + mDomainName + url;
//
//            mInternalLinks.add(res);
//            if (crawl) mCrawlLinks.add(res);
//            return;
//        }

        //if it matches the parentUrl's domain name
        Matcher m2 = mDomainPattern.matcher(url);
        if (m2.find())
        {
            res = url;

            mInternalLinks.add(res);
            if (crawl) mCrawlLinks.add(res);
            return;
        }

        //if it matches the default domain name
        Matcher defaultMatcher = mDefaultPattern.matcher(url);
        if (defaultMatcher.find())
        {
            res = url;

            mInternalLinks.add(res);
            if (crawl) mCrawlLinks.add(res);
            return;
        }

        Matcher httpMatcher = Patterns.httpPattern.matcher(url);
        if (httpMatcher.find()) //make sure it isn't a mail link or something
        {
            //at this point it's external (and therefore not relative)
            mExternalLinks.add(url);
            if (mConfiguration.testExternals())
                mCrawlLinks.add(url);

        }

    }


}
