package com.scrape.ephraim.data;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class DocumentInfo
{
    ///the title
    private String mTitle;

    ///the meta description
    private String mMeta;

    ///the h1 tags
    private List<String> mH1;

    ///the h2 tags
    private List<String> mH2;

    ///the keywords found on this page
    private List<String> mKeywords;

    ///indicates if it has been processed or not
    private boolean mProcessed;

    ///association to either the page or external site being held
    private DocumentHolder mPage;

    /**
     * Default constructor
     * @param page the page this document belongs to
     */
    public DocumentInfo(DocumentHolder page)
    {
        mTitle = "";
        mMeta = "";
        mH1 = new ArrayList<>();
        mH2 = new ArrayList<>();
        mProcessed = false;
        mKeywords = new ArrayList<>();
        mPage = page;
    }

    /**
     * Processes a document
     * @param doc jsoup object
     * @param keywords the words to look for in this document
     */
    public void processDocument(Document doc, List<Keyword> keywords)
    {
        //grab the title
        Elements titles = doc.getElementsByTag("title");
        if (titles.size() > 0)
            mTitle = titles.get(0).text();

        //grab the meta description
        var metas = doc.getElementsByTag("meta");
        for (var meta : metas)
        {
            if (meta.attr("name").equals("description"))
            {
                mMeta = meta.attr("content");
            }
        }

        //grab the headers
        for (var h1 : doc.getElementsByTag("h1")) {
            mH1.add(h1.text());
        }
        for (var h2 : doc.getElementsByTag("h2"))
        {
            mH2.add(h2.text());
        }

        //check for keywords
        for (Keyword keyword : keywords)
        {
            String whole = doc.toString();
            int occurrences = whole.split(keyword.getWord(), -1).length-1;
            if (occurrences > 0) {
                keyword.addOccurrence(mPage.getUrl(), occurrences);
                mKeywords.add(keyword.getWord());
            }
        }
    }

    /**
     * Getter for the title
     * @return
     */
    public String getTitle() {return mTitle;}

    /**
     * Getter for the meta description
     * @return
     */
    public String getMetaDescription() {return mMeta;}

    /**
     * Getter for the h1
     * @return
     */
    public List<String> getH1() {return mH1;}

    /**
     * Getter for the h2
     * @return
     */
    public List<String> getH2() {return mH2;}

    /**
     * Sets the list of keywords
     * @param keywords list of strings
     */
    public void setKeywords(List<String> keywords) {mKeywords = keywords;}

    /**
     * Adds a keyword
     * @param keyword string
     */
    public void addKeyword(String keyword) {mKeywords.add(keyword);}


    /**
     * Returns csv elements for the document
     * @return list of strings
     */
    public List<String> saveCSV()
    {
        ArrayList<String> line = new ArrayList<>();
        //save the title
        line.add(mTitle);

        //save the meta
        line.add(mMeta);

        //save the h1's
        line.add(listToString(mH1));

        //save the h2's
        line.add(listToString(mH2));

        //save the keywords
        line.add(listToString(mKeywords));

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

