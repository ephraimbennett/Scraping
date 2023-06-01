package com.scrape.ephraim.ui;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class GUI extends Application
{

    @Override
    public void start(Stage primaryStage) throws IOException
    {
        Parent root = FXMLLoader.load(getClass().getResource("/sample.fxml"));
        primaryStage.setTitle("Ephraim Scraper");
        primaryStage.setScene(new Scene(root, 500, 600));
        primaryStage.show();
    }
    public static void main(String[] args)
    {
        launch(args);
    }
}
