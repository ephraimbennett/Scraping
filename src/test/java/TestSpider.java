import com.scrape.ephraim.crawler.Configuration;
import com.scrape.ephraim.crawler.Scraper;
import com.scrape.ephraim.crawler.Spider;

public class TestSpider
{
    public static void main(String[] args)
    {
        //set up a timer for performance
        long beginTime = System.currentTimeMillis();

        Scraper scraper = new Scraper("https://jsoup.org/");
//        Scraper scraper = new Scraper("https://unclejulios.com/");
        Spider spider = new Spider(scraper, new Configuration(20, true, true, true));
        spider.crawl(scraper.getDomain());

        //performance stuff
        System.out.println("total links visited: " + spider.getVisitedLinks().size());

        long endTime = System.currentTimeMillis();
        System.out.println("Elapsed time: " + (endTime - beginTime) / 1000);
    }

}
