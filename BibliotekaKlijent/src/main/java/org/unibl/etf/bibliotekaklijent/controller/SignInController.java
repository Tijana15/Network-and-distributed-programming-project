package org.unibl.etf.bibliotekaklijent.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.unibl.etf.bibliotekaklijent.HelloApplication;
import org.unibl.etf.bibliotekaklijent.model.User;
import org.unibl.etf.bibliotekaklijent.communication.Multicast;
import org.unibl.etf.bibliotekaklijent.util.ConfigLoader;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SignInController {
    private static Logger logger = Logger.getLogger(SignInController.class.getName());
    private ObservableList<String> messageList = FXCollections.observableArrayList();
    public static User user;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button signInButton;
    @FXML
    private TextField usernameField;

    @FXML
    void onRegisterButton(MouseEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Register.fxml"));
        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/org/unibl/etf/bibliotekaklijent/BookIcon.png")));
        Stage stage = new Stage();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);

        stage.getIcons().add(icon);
        stage.setResizable(false);
        stage.setTitle("Registration");
        stage.show();
    }

    @FXML
    void onSignInButton(MouseEvent event) {
        try {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (signIn(username, password)) {
                Stage stage = (Stage) signInButton.getScene().getWindow();
                FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("BooksOverview.fxml"));
                Scene scene = new Scene(fxmlLoader.load());
                BooksOverviewController controller = fxmlLoader.getController();
                controller.setMessageList(messageList);
                Multicast multicastClient = new Multicast(messageList);
                multicastClient.start();
                stage.setScene(scene);
                stage.setTitle("BooksOverview");
                stage.show();
            } else {
                System.out.println("Sign in failed. Check username and password or try later.");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error in SignInController", e);
        }
    }

    private boolean signIn(String username, String password) {
        try {
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);

            ObjectMapper objectMapper = new ObjectMapper();
            String requestBody = objectMapper.writeValueAsString(user);

            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(ConfigLoader.getProperty("url_login")))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                SignInController.user = objectMapper.readValue(response.body(), User.class);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error while signing in in api", e);
            return false;
        }
    }
}