package com.scrape.ephraim.data;

import javafx.application.Platform;
import javafx.scene.control.TableView;
import org.jsoup.nodes.Document;

import java.util.HashMap;

public class Keyword
{
    ///the word to look for
    private String mWord;

    ///the locations and number of occurrences this word is found on
    private HashMap<String, Integer> mLocations;

    ///association to the keywords observer
    private TableView mObserverKeywords;

    ///running total of occurrences
    private int mOccurrences;

    /**
     * Constructor
     * @param word the word we are looking for
     */
    public Keyword(String word)
    {
        mWord = word;
        mLocations = new HashMap<>();
        mOccurrences = 0;
    }

    /**
     * Adds an occurrence of the word
     * @param loc for the map
     * @param num for the map
     */
    public void addOccurrence(String loc, int num)
    {
        mLocations.put(loc, num);
        mOccurrences += num;
        mObserverKeywords.refresh();
    }

    /**
     * Getter for the word
     * @return keyword
     */
    public String getWord() {return mWord;}

    /**
     * Gets the total number of occurrences of this word
     * @return total
     */
    public int getOccurrences()
    {
        return mOccurrences;
    }

    /**
     * Gets the number of pages this word is found
     * @return location's size
     */
    public int getTotalLocations() {return mLocations.size();}

    /**
     * Sets the observer keywords
     * @param keywords a table
     */
    public void setObserverKeywords(TableView keywords) {mObserverKeywords = keywords;}

    /**
     * Getter for the locations
     * @return a map
     */
    public HashMap<String, Integer> getLocations() {return mLocations;}

    /**
     * Checks a page to see if this keyword is located on it.
     * @param document the document to check
     * @param url the location of the url
     */
    public void checkDocument(Document document, String url)
    {

    }
}
