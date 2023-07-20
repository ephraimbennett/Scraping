package com.scrape.ephraim.ui;

import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.input.*;

import java.util.ArrayList;
import java.util.List;

public class CopyController
{
    ///the copy button from the menu
    MenuItem copyItem;

    //the table views
    List<TableView> tables;

    ///the list views
    List<ListView> lists;

    /**
     * Constructor
     * @param copyItem the menu item
     */
    public CopyController(MenuItem copyItem)
    {
        this.copyItem = copyItem;
        tables = new ArrayList<>();
        lists = new ArrayList<>();

        copyItem.setOnAction((event) -> {
            for (var table : tables)
            {
                copyTableToClipBoard(table);
            }
            for (var list : lists)
            {
                copyListToClipBoard(list);
            }
        });
    }

    /**
     * Adds a table that can be copied.
     * Sets up the ctrl+c
     * @param table the table
     */
    public void addTable(TableView table)
    {
        tables.add(table);
        final KeyCodeCombination keyCodeCopy = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_ANY);
        table.setOnKeyPressed((event) -> {
            if (keyCodeCopy.match(event))
                copyTableToClipBoard(table);
        });
    }

    /**
     * Adds a list that can be copied
     * Sets up the ctrl+c
     * @param list the list
     */
    public void addList(ListView list)
    {
        lists.add(list);
        final KeyCodeCombination keyCodeCopy = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_ANY);
        list.setOnKeyPressed((event) -> {
            if (keyCodeCopy.match(event))
                copyListToClipBoard(list);
        });
    }

    /**
     * Copy given a tableview
     * @param table the table
     */
    public void copyTableToClipBoard(TableView table)
    {
        if (table.getSelectionModel().getSelectedCells().size() > 0)
        {
            ObservableList<TablePosition> posList = table.getSelectionModel().getSelectedCells();
            TablePosition p = posList.get(0);
            var x = p.getTableColumn();
            var cell = x.getCellData(p.getRow());
            String result = "";

            if (cell != null)
                result = String.valueOf(cell);

            final ClipboardContent clipboardContent = new ClipboardContent();
            clipboardContent.putString(result);
            Clipboard.getSystemClipboard().setContent(clipboardContent);
        }
    }

    /**
     * Copies a list
     * @param list the list
     */
    public void copyListToClipBoard(ListView list)
    {
        if (list.getSelectionModel().getSelectedItem() != null)
        {
            var selected = list.getSelectionModel().getSelectedItem();
            if (selected != null)
            {
                String result = String.valueOf(selected);
                final ClipboardContent clipboardContent = new ClipboardContent();
                clipboardContent.putString(result);
                Clipboard.getSystemClipboard().setContent(clipboardContent);
            }
        }
    }
}
