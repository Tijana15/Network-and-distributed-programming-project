package org.unibl.etf.projektnizadatak2024;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.unibl.etf.projektnizadatak2024.controllers.StartPageController;
import org.unibl.etf.projektnizadatak2024.util.Multicast;

import java.io.IOException;
import java.util.Objects;

public class HelloApplication extends Application {
    public static Scene primaryStage;
    private ObservableList<String> messageList = FXCollections.observableArrayList();

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("BibliotekaStartPage.fxml"));
        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("BookIcon.png")));
        primaryStage = new Scene(fxmlLoader.load());
        stage.getIcons().add(icon);
        stage.setResizable(false);
        stage.setTitle("Welcome to online library!");
        stage.setScene(primaryStage);

        Multicast multicastClient = new Multicast(messageList);
        multicastClient.start();
        StartPageController startPageController = fxmlLoader.getController();
        startPageController.setMessageList(messageList);

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}