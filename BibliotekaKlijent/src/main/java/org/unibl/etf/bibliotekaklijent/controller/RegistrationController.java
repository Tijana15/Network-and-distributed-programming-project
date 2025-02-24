package org.unibl.etf.bibliotekaklijent.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;
import org.unibl.etf.bibliotekaklijent.model.User;
import org.unibl.etf.bibliotekaklijent.util.ConfigLoader;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RegistrationController {
    private static Logger logger = Logger.getLogger(RegistrationController.class.getName());
    @FXML
    private TextField addressField;
    @FXML
    private TextField emailAddressField;
    @FXML
    private TextField lastnameField;
    @FXML
    private TextField nameField;
    @FXML
    private PasswordField password2Field;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button registerButton;
    @FXML
    private TextField usernameField;
    @FXML
    private Text text;

    @FXML
    void onRegisterButton(MouseEvent event) {
        User user = createUser();
        if (user != null) {
            if (registerUser(user)) {
                System.out.println("User successfully registered.");
                Stage stage = (Stage) registerButton.getScene().getWindow();
                stage.close();
            } else {
                System.out.println("Registration failed");
            }
        } else {
            text.setText("Passwords have to be same!");
        }
    }

    boolean checkPasswords() {
        String p1 = passwordField.textProperty().getValue();
        String p2 = password2Field.textProperty().getValue();
        return p1.equals(p2);
    }

    private User createUser() {
        User user = new User();
        user.setName(nameField.getText());
        user.setLastname(lastnameField.getText());
        user.setUsername(usernameField.getText());
        if (checkPasswords()) {
            user.setPassword(passwordField.getText());
        } else {
            passwordField.clear();
            password2Field.clear();
            return null;
        }
        user.setEmail(emailAddressField.getText());
        user.setAddress(addressField.getText());
        user.setActivated(false);
        return user;
    }

    private boolean registerUser(User user) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String requestBody = objectMapper.writeValueAsString(user);

            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder().uri(new URI(ConfigLoader.getProperty("url_register"))).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(requestBody)).build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error in registrateUser", e);
            return false;
        }
    }
}
