package com.scrape.ephraim.ui;

import com.scrape.ephraim.crawler.Configuration;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

public class ConfigurationController implements Initializable
{
    ///the configuration object
    Configuration configuration;

    @FXML
    TextField threadCountField;

    @FXML
    TextField timeoutField;

    @FXML
    CheckBox testImagesBox;

    @FXML
    CheckBox testExternalsBox;

    @FXML
    CheckBox crawlSubdomainsBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        //this is to ensure that the fxml has fully loaded
        Platform.runLater(() -> {
            //grab the configuration from one of the nodes
            configuration = (Configuration) threadCountField.getScene().getUserData();

            //set up a listener to ensure that the user is inputting the correct information
            UnaryOperator<TextFormatter.Change> filter = change -> {
                String text = change.getText();

                if (text.matches("[0-9]*")) {
                    return change;
                }

                return null;
            };
            TextFormatter<String> threadFormatter = new TextFormatter<>(filter);
            TextFormatter<String> timeoutFormatter = new TextFormatter<>(filter);
            threadCountField.setTextFormatter(threadFormatter);
            timeoutField.setTextFormatter(timeoutFormatter);

            //reflect the fields with the actual info
            threadCountField.setText(String.valueOf(configuration.getThreadCount()));
            timeoutField.setText(String.valueOf(configuration.getTimeout()));
            testImagesBox.setSelected(configuration.testImages());
            testExternalsBox.setSelected(configuration.testExternals());
            crawlSubdomainsBox.setSelected(configuration.crawlSubdomains());
        });
    }

    public void apply()
    {
        configuration.setThreadCount(Integer.valueOf(threadCountField.getText()));
        configuration.setTimeout(Integer.valueOf(threadCountField.getText()));
        configuration.setTestExternals(testExternalsBox.isSelected());
        configuration.setTestImages(testImagesBox.isSelected());
        configuration.setCrawlSubdomains(crawlSubdomainsBox.isSelected());
    }



}
