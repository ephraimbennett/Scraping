package com.scrape.ephraim.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class GUI extends Application
{

    @Override
    public void start(Stage primaryStage)
    {
        Scene scene = new Scene(new StackPane(), 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public static void main(String[] args)
    {
        launch(args);
    }
}
