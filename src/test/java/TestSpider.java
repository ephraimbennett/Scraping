import com.scrape.ephraim.crawler.Configuration;
import com.scrape.ephraim.crawler.Scraper;
import com.scrape.ephraim.crawler.Spider;
import com.scrape.ephraim.data.Keyword;
import com.scrape.ephraim.ui.ExporterCSV;

import java.io.File;
import java.util.ArrayList;

public class TestSpider
{
    public static void main(String[] args)
    {
        //set up a timer for performance
        long beginTime = System.currentTimeMillis();

        Scraper scraper = new Scraper("https://jsoup.org/");
        ArrayList<Keyword> keywords = new ArrayList<>();
        keywords.add(new Keyword("Tree"));
        scraper.setKeywords(keywords);
//        Scraper scraper = new Scraper("https://unclejulios.com/");
        Spider spider = new Spider(scraper, new Configuration(20, 15, true, true, true));
        spider.crawl(scraper.getDomain());

        //performance stuff
        System.out.println("total links visited: " + spider.getVisitedLinks().size());

        long endTime = System.currentTimeMillis();
        System.out.println("Elapsed time: " + (endTime - beginTime) / 1000);

        ExporterCSV exporterCSV = new ExporterCSV(new File("C:/Users/Patri/Documents/shoo.csv/"));
        exporterCSV.exportPage(scraper);
    }

}
