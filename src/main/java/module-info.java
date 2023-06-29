module Scraping
{
    requires javafx.controls;
    requires javafx.fxml;
    opens com.scrape.ephraim.ui to javafx.fxml;
    requires org.jsoup;
    requires opencsv;
    requires com.google.gson;
    opens com.scrape.ephraim.data to com.google.gson;
    exports com.scrape.ephraim.ui;
}