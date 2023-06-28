package com.scrape.ephraim.data;

import java.util.*;
import java.util.regex.Matcher;

import com.scrape.ephraim.crawler.Patterns;
import org.jsoup.nodes.Document;

public class Page
{
    ///this page's url
    private final String mUrl;

    ///this page's type
    private String mType;

    ///this page's path (url - domain name)
    private ArrayList<String> mPath;

    ///list of in link
    private HashSet<String> mInLinks;

    ///list of out links
    private HashSet<String> mOutLinks;

    ///list of the external links
    private HashSet<String> mExternalLinks;

    ///association to the document info
    private DocumentInfo mDocumentInfo;


    /**
     * Constructor
     * @param url
     */
    public Page(String url)
    {
        mUrl = url;

        init();
    }

    /**
     * Constructor
     * @param url
     */
    public Page(String url, String type)
    {
        mUrl = url;
        mType = type;

        init();
    }

    /**
     * Sets up member variables and whatnot
     */
    private void init()
    {
        mInLinks = new HashSet<>();
        mOutLinks = new HashSet<>();
        mExternalLinks = new HashSet<>();
        mDocumentInfo = new DocumentInfo();

        //create the path
        Matcher matcher = Patterns.pathPattern.matcher(mUrl);
        if (matcher.find())
        {
            String totalPath = matcher.group(2);
            mPath = new ArrayList<String>(List.of(totalPath.split("/")));
        }
    }


    /**
     * adds an inlink to the page
     * @param inLink
     */
    public void addInLink(String inLink)
    {
        mInLinks.add(inLink);
    }

    /**
     * Sets the outlinks
     * converts it from a list to a hash set
     * @param links
     */
    public void setOutLinks(List<String> links) {mOutLinks = new HashSet<>(links);}

    /**
     * Sets the outlinks
     * @param links
     */
    public void setOutLinks(HashSet<String> links) {mOutLinks = links;}

    /**
     * getter for the outlinks
     * @return
     */
    public HashSet<String> getOutLinks() {return mOutLinks;}

    /**
     * Getter for the inlinks
     * @return
     */
    public HashSet<String> getInLinks() {return mInLinks;}

    /**
     * Setter for the inlinks
     * @param inLinks
     */
    public void setInLinks(HashSet<String> inLinks) {mInLinks = inLinks;}

    /**
     * getter for the url
     * @return hmm...
     */
    public String getUrl() {return mUrl;}

    /**
     * Getter for the path
     * @return
     */
    public ArrayList<String> getPath() {return mPath;}

    /**
     * Setter for the external links
     * converts into hashset
     * @param externalLinks
     */
    public void setExternalLinks(List<String> externalLinks) {mExternalLinks = new HashSet<>(externalLinks);}

    /**
     * Setter for the external links
     * @param externalLinks
     */
    public void setExternalLinks(HashSet<String> externalLinks) {mExternalLinks = externalLinks;}

    /**
     * Setter for type
     * @param type
     */
    public void setType(String type) {mType = type;}

    /**
     * Getter for type
     * @return
     */
    public String getType() {return mType;}

    /**
     * Getter for the external links
     * @return
     */
    public HashSet<String> getExternalLinks() {return mExternalLinks;}

    /**
     * Indicates if this page is a child of the supplied path
     * @param path
     * @return
     */
    public boolean belongsToPath(String path)
    {
        return mUrl.contains(path);
    }

    /**
     * Gets the whole domain (which could be different than overall site domain b/c subdomain) from this page
     * @return
     */
    public String getWholeDomain()
    {
        Matcher matcher = Patterns.domainPattern.matcher(mUrl);
        if (matcher.find())
        {
            return matcher.group(1);
        }
        return "none";
    }

    /**
     * Composite function to document info
     * @param document
     */
    public void processDocument(Document document)
    {
        mDocumentInfo.processDocument(document);
    }

    /**
     * Getter for the document info
     * @return
     */
    public DocumentInfo getDocumentInfo() {return mDocumentInfo;}

    /**
     * Returns an array of strings that represent this page object
     * @return
     */
    public List<String> saveCSV()
    {
        ArrayList<String> line = new ArrayList<>();

        //add the url
        line.add(mUrl);

        //add the inlinks
        line.add(listToString(mInLinks));

        //add the outlinks
        line.add(listToString(mOutLinks));

        //add the external links
        line.add(listToString(mExternalLinks));

        //add the document info
        line.addAll(mDocumentInfo.saveCSV());

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
}
