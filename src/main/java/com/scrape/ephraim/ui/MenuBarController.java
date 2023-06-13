package com.scrape.ephraim.ui;

import javafx.application.Platform;
import javafx.scene.control.MenuBar;

public class MenuBarController
{
    ///the menu bar in question
    MenuBar menuBar;

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
}
