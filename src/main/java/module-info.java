module Scraping
{
    requires javafx.controls;
    requires javafx.fxml;
    opens com.scrape.ephraim.ui to javafx.fxml;
    requires org.jsoup;
    exports com.scrape.ephraim.ui;
}