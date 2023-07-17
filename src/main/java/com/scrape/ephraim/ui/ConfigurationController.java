package com.scrape.ephraim.ui;

import com.scrape.ephraim.crawler.Configuration;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class ConfigurationController implements Initializable
{
    ///the configuration object
    Configuration configuration;

    @FXML
    TextField threadCountField;

    @FXML
    CheckBox testImagesBox;

    @FXML
    CheckBox testExternalsBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        //this is to ensure that the fxml has fully loaded
        Platform.runLater(() -> {
            configuration = (Configuration) threadCountField.getScene().getUserData();
        });
    }

    public void apply()
    {
        configuration.setThreadCount(Integer.valueOf(threadCountField.getText()));
        configuration.setTestExternals(testExternalsBox.isSelected());
        configuration.setTestImages(testImagesBox.isSelected());
    }



}
