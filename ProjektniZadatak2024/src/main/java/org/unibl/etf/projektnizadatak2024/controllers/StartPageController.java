package org.unibl.etf.projektnizadatak2024.controllers;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class StartPageController {
    private ObservableList<String> messageList;
    @FXML
    private Button orderBooksButton;
    @FXML
    private Button workWithBooksButton;
    @FXML
    private Button workWithMembersButton;
    @FXML
    private Button suggestionsButton;

    @FXML
    void onOrderBooksButton(MouseEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/unibl/etf/projektnizadatak2024/OrderBooksPage.fxml"));
        Scene newScene = new Scene(fxmlLoader.load());

        Stage stage = (Stage) orderBooksButton.getScene().getWindow();
        stage.setScene(newScene);
        stage.show();
    }

    @FXML
    void onWWBooksButton(MouseEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/unibl/etf/projektnizadatak2024/WWBooksPage.fxml"));
        Scene newScene = new Scene(fxmlLoader.load());

        Stage stage = (Stage) workWithBooksButton.getScene().getWindow();
        stage.setScene(newScene);
        stage.show();
    }

    @FXML
    void onWWMembersButton(MouseEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/unibl/etf/projektnizadatak2024/WWMembersPage.fxml"));
        Scene newScene = new Scene(fxmlLoader.load());

        Stage stage = (Stage) workWithMembersButton.getScene().getWindow();
        stage.setScene(newScene);
        stage.show();
    }

    @FXML
    void onSuggestions(MouseEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/unibl/etf/projektnizadatak2024/Suggestions.fxml"));
        Scene newScene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        SuggestionsController suggestionsController = fxmlLoader.getController();
        suggestionsController.setMessageList(messageList);
        stage.setScene(newScene);
        stage.show();
    }

    public void setMessageList(ObservableList<String> messageList) {
        this.messageList = messageList;
    }
}