package com.scrape.ephraim.ui;

import com.scrape.ephraim.data.Keyword;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class KeywordsController implements Initializable
{
    @FXML
    TextArea inputArea;

    @FXML
    ListView<String> keywordsList;

    @FXML
    Button addButton;

    @FXML
    Button removeButton;

    @FXML
    Button applyButton;

    ///the keywords list
    List<Keyword> keywords;

    /**
     * adds a keyword
     */
    public void addKeyword()
    {
        String word = inputArea.getText();
        keywordsList.getItems().add(word);
    }

    /**
     * Removes an item
     */
    public void removeKeyword()
    {
        String selected = keywordsList.getSelectionModel().getSelectedItem();
        if (selected != null)
        {
            keywordsList.getItems().remove(selected);
        } else {
            keywordsList.getItems().remove(keywordsList.getItems().size() - 1);
        }
    }

    /**
     * Updates the actual list
     */
    public void applyChanges()
    {
        keywords.clear();
        for (String word : keywordsList.getItems())
        {
            keywords.add(new Keyword(word));
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        Platform.runLater(() -> {
            //grab the keywords from one of the nodes
            keywords = (List<Keyword>) keywordsList.getScene().getUserData();

            //make a list of the keywords as strings
            List<String> keyStrings = new ArrayList<>();
            for (var keyword : keywords)
            {
                keyStrings.add(keyword.getWord());
            }

            //fill the table with the keywords
            keywordsList.setItems(FXCollections.observableArrayList(keyStrings));
        });
    }
}
