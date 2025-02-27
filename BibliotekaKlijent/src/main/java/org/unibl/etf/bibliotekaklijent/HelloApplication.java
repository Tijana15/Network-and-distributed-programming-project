package org.unibl.etf.bibliotekaklijent;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("SignIn.fxml"));
        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("BookIcon.png")));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Welcome to online library!");
        stage.getIcons().add(icon);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        Application.launch(HelloApplication.class, args);
    }
}