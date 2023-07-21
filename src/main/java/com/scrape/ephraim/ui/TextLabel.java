package com.scrape.ephraim.ui;

import javafx.scene.control.TextArea;

public class TextLabel extends TextArea
{
    /**
     * Constructor
     * @param text the text for this text area
     */
    public TextLabel(String text)
    {
        super(text);
        setEditable(false);
        setWrapText(true);
    }
}
