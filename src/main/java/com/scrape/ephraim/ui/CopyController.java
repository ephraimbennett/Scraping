package com.scrape.ephraim.ui;

import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.input.*;

import java.util.ArrayList;
import java.util.List;

public class CopyController
{
    ///the copy button from the menu
    private MenuItem copyItem;

    //the table views
    private List<TableView> tables;

    ///the list views
    private List<ListView> lists;

    ///the tree view for the site map
    private TreeView<String> tree;

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
            copyTreeToClipBoard(tree);
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
     * Adds the site map
     * @param tree view
     */
    public void addTree(TreeView<String> tree) {
        this.tree = tree;
        final KeyCodeCombination keyCodeCopy = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_ANY);
        tree.setOnKeyPressed((event) -> {
            if (keyCodeCopy.match(event))
                copyTreeToClipBoard(tree);
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

    /**
     * Copies an item selected from the tree view
     * @param treeView view
     */
    public void copyTreeToClipBoard(TreeView<String> treeView)
    {
        //set up shop
        StringBuilder urlBuilder = new StringBuilder();
        TreeItem<String> currentItem = treeView.getSelectionModel().getSelectedItem();

        //if no item is selected, just stop!
        if (currentItem == null) return;

        //keep adding the item's value until we get to the root
        while (currentItem.getParent() != null)
        {
            urlBuilder.insert(0, currentItem.getValue()).insert(0, "/");
            currentItem = currentItem.getParent();
        }

        //add the https://
        //since we are always adding a trailing "/" we only need to add http:/
        urlBuilder.insert(0, "https:/");

        final ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(urlBuilder.toString());
        Clipboard.getSystemClipboard().setContent(clipboardContent);
    }
}
