import com.scrape.ephraim.Crawler;
import com.scrape.ephraim.Fetcher;
import com.scrape.ephraim.Link;
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
        Crawler crawler = new Crawler("https://developer.mozilla.org/en-US/");
        List<Link> urls = new ArrayList<>();
        Link link = new Link("https://developer.mozilla.org/en-US/", true, false);
        urls.add(link);
        crawler.crawl(urls);
        System.out.println("total links visited: " + crawler.getVisitedLinks().size());
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
