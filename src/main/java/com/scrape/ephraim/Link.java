package com.scrape.ephraim;

import java.util.ArrayList;
import java.util.List;

public class Link {
    ///the url for this link
    private String mUrl;

    ///whether or not this link is
    private boolean mIsRelative;

    ///whether or not this link is internal
    private boolean mIsInternal;

    ///this link's in links
    private List<Link> mInLinks;

    ///this link's out links
    private List<Link> mOutLinks;

    /**
     * Constructor
     */
    public Link(String url, boolean isInternal, boolean isRelative)
    {
        mUrl = url;
        mIsInternal = isInternal;
        mIsRelative = isRelative;

        //init the lists
        mInLinks = new ArrayList<Link>();
        mOutLinks = new ArrayList<Link>();
    }

    /**
     * Adds an in link
     * @param inLink
     */
    public void addInLink(Link inLink)
    {
        mInLinks.add(inLink);
    }

    /**
     * Adds an out link
     * @param outLink
     */
    public void addOutLink(Link outLink)
    {
        mOutLinks.add(outLink);
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
     * Getter for internal attribute
     * @return
     */
    public boolean isInternal() {return mIsInternal;}

    /**
     * Getter for relative attribute
     * @return
     */
    public boolean isRelative() {return mIsRelative;}

    /**
     * Deterimines if these links are going to the same page
     * @param otherLink
     * @return
     */
    public boolean equals(Link otherLink)
    {
        //simple exact url check
        if (this.getUrl().equals(otherLink.getUrl()))
        {
            return true;
        }
//
        //need to count for reference
        //first check the smaller link is contained in the other one
//        String containerUrl = "";
//        String baseUrl = "";
//        if (this.getUrl().contains(otherLink.getUrl()))
//        {
//            containerUrl = this.getUrl();
//            baseUrl = otherLink.getUrl();
//        }
//        else if (otherLink.getUrl().contains(this.getUrl()))
//        {
//            containerUrl = otherLink.getUrl();
//            baseUrl = this.getUrl();
//        }
//        //if either of them contains the other one
//        if (containerUrl.length() > 0)
//        {
//            String difference = containerUrl.substring(baseUrl.length(), containerUrl.length());
//            if (difference.charAt(0) == '#')
//            {
//                return true;
//            }
//        }
        return false;
    }
}
