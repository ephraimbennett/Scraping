import com.scrape.ephraim.Crawler;
import com.scrape.ephraim.Fetcher;
import com.scrape.ephraim.Link;
import com.scrape.ephraim.Scraper;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;

public class TestRun {
    public static void main(String[] args)
    {
        TestCrawler();
        System.out.println("Done!");
    }

    static void TestCrawler()
    {
        //set up a timer for performance
        long beginTime = System.currentTimeMillis();

        //creates a crawler
        Crawler crawler = new Crawler("https://www.coursereport.com/");
        List<Link> urls = new ArrayList<>();
        Link link = new Link("https://www.coursereport.com/", true, false);
        urls.add(link);

        //now create a scraper
        Scraper scraper = new Scraper(crawler.getDomain());
        crawler.setScraper(scraper);


        crawler.crawl(urls);
        System.out.println("total links visited: " + crawler.getVisitedLinks().size());

        long endTime = System.currentTimeMillis();
        System.out.println("Elapsed time: " + (endTime - beginTime) / 1000);
    }

    static void TestFetcher()
    {
        Fetcher fetcher = new Fetcher();
        fetcher.setUrl("https://https://unclejulios.com/");
//        fetcher.fetch();
        Document drD = fetcher.getDocument();

        System.out.println(fetcher.getUrl());
        System.out.println(drD);
    }
}
