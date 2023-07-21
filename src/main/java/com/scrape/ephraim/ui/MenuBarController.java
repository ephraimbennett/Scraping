package com.scrape.ephraim.ui;

import com.scrape.ephraim.crawler.Scraper;
import javafx.application.Platform;
import javafx.scene.control.MenuBar;
import javafx.stage.FileChooser;

import java.io.File;

public class MenuBarController
{
    ///the menu bar in question
    MenuBar menuBar;

    /**
     * Constructor
     * @param menuBar le menu
     */
    public MenuBarController(MenuBar menuBar)
    {
        this.menuBar = menuBar;
    }

    /**
     * handler for exit on menu bar
     */
    public void onExit()
    {
        Platform.exit();
        System.out.println("Application ended.");
    }

    /**
     * Exports the pages
     * @param scraper where we get the pages from
     */
    public void exportPages(Scraper scraper)
    {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV File", ".csv"));
        File path = chooser.showSaveDialog(menuBar.getScene().getWindow());
        ExporterCSV exporter = new ExporterCSV(path);
        exporter.exportPage(scraper);
    }


    /**
     * Exports the externals
     * @param scraper the scraper
     */
    public void exportExternals(Scraper scraper)
    {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV File", ".csv"));
        File path = chooser.showSaveDialog(menuBar.getScene().getWindow());
        ExporterCSV exporter = new ExporterCSV(path);
        exporter.exportExternal(scraper);
    }

    /**
     * Exports the issues
     * @param scraper to grab from
     */
    public void exportIssues(Scraper scraper)
    {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV File", ".csv"));
        File path = chooser.showSaveDialog(menuBar.getScene().getWindow());
        ExporterCSV exporter = new ExporterCSV(path);
        exporter.exportIssues(scraper);
    }
}
