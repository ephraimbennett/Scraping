package com.scrape.ephraim.crawler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

///class to store regular expression patterns
public class Patterns {
    ///for the slashes
    public static Pattern slashPattern = Pattern.compile("^/");

    ///grabs the domain name from the url
    public static Pattern domainPattern = Pattern.compile("^https?://([\\w.]+)");

    ///determines if the link is an on page jump
    public static Pattern onPagePattern = Pattern.compile("^(.*)(#[^/]*)$");


    ///grabs the path from the url
    public static Pattern pathPattern = Pattern.compile("^https?://([\\w.]+)(.*)$");
}
