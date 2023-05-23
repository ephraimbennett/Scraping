import com.scrape.ephraim.crawler.Crawler;
import com.scrape.ephraim.crawler.Fetcher;
import com.scrape.ephraim.crawler.Scraper;
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
        Crawler crawler = new Crawler("https://www.zenrows.com/");
        List<String> urls = new ArrayList<>();
        urls.add(crawler.getUrl());

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
