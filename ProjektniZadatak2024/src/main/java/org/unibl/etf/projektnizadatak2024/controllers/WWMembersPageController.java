package org.unibl.etf.projektnizadatak2024.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.unibl.etf.projektnizadatak2024.logger.LoggerConfig;
import org.unibl.etf.projektnizadatak2024.model.User;
import org.unibl.etf.projektnizadatak2024.util.ConfigLoader;

import java.io.IOException;
import java.io.ObjectInputFilter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WWMembersPageController {
    private ObservableList<User> userList = FXCollections.observableArrayList();
    private static final Logger logger = LoggerConfig.getLogger(WWMembersPageController.class);
    @FXML
    private TableView<User> tableView;
    @FXML
    private TableColumn<User, String> emailColumn;
    @FXML
    private TableColumn<User, String> firstnameColumn;
    @FXML
    private TableColumn<User, String> lastnameColumn;
    @FXML
    private TableColumn<User, String> usernameColumn;
    @FXML
    private Button backButton;
    @FXML
    private TextField searchInputField;

    @FXML
    private void initialize() {
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        firstnameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        lastnameColumn.setCellValueFactory(new PropertyValueFactory<>("lastname"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        loadUsers();
    }

    @FXML
    void onBackButton(MouseEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/unibl/etf/projektnizadatak2024/BibliotekaStartPage.fxml"));
        Scene startScene = new Scene(fxmlLoader.load());
        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/org/unibl/etf/projektnizadatak2024/BookIcon.png")));

        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.setScene(startScene);
        stage.getIcons().add(icon);
        stage.show();
    }

    @FXML
    void onRegistrationRequestsButton(MouseEvent event) {
        tableView.setEditable(false);
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(ConfigLoader.getProperty("url_requests")))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                Gson gson = new Gson();
                User[] users = gson.fromJson(response.body(), User[].class);

                tableView.getItems().clear();
                tableView.getItems().addAll(users);

                tableView.setOnMouseClicked(event1 -> {
                    if (event1.getClickCount() == 2) {
                        User selectedUser = tableView.getSelectionModel().getSelectedItem();
                        if (selectedUser != null) {
                            showConfirmationDialog(selectedUser);
                        }
                    }
                });
            } else {
                logger.log(Level.SEVERE, "Error occupied on registration, response: ", response);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error loading requests from server", e);
        }
    }

    @FXML
    void onSearchButton(MouseEvent event) {
        String searchQuery = searchInputField.getText().trim().toLowerCase();

        if (userList != null) {
            ObservableList<User> filteredUsers = FXCollections.observableArrayList();
            for (User user : userList) {
                if (user.getName().toLowerCase().contains(searchQuery)) {
                    filteredUsers.add(user);
                }
            }
            tableView.setItems(filteredUsers);
        } else {
            System.err.println("Lista knjiga nije učitana.");
        }
    }

    @FXML
    void onDeleteUserButton(MouseEvent event) {
        ObservableList<User> selectedUsers = tableView.getSelectionModel().getSelectedItems();

        if (selectedUsers == null || selectedUsers.isEmpty()) {
            showAlert("No users selected", "Please select at least one user to delete.");
            return;
        }

        for (User user : selectedUsers) {
            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI(ConfigLoader.getProperty("url_users") + user.getUsername()))
                        .DELETE()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    showAlert("Success", "User successfully deleted");
                } else {
                    logger.log(Level.SEVERE, "Error occupied while deleting user, response: ", response);
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Unable to send request for deleting user.", e);
            }
        }

        loadUsers();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void activateUser(User user) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            Gson gson = new Gson();
            String json = gson.toJson(user);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(ConfigLoader.getProperty("url_accept")))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                System.out.println("User successfully activated: " + response.body());
            } else {
                logger.log(Level.SEVERE, "Error while activating user, response ", response);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error while activating user ", e);
        }
    }

    private void showConfirmationDialog(User user) {

        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.setTitle("Accept Request");

        dialogStage.initOwner(tableView.getScene().getWindow());

        Label messageLabel = new Label("Do you want to accept registration request for " + user.getUsername() + "?");

        Button yesButton = new Button("Yes");
        Button noButton = new Button("No");

        yesButton.setOnAction(event -> {
            user.setActivated(true);
            activateUser(user);
            tableView.getItems().remove(user);
            dialogStage.close();
        });

        noButton.setOnAction(event -> {
            tableView.getItems().remove(user);
            dialogStage.close();
        });

        HBox buttonsBox = new HBox(10, yesButton, noButton);
        buttonsBox.setAlignment(Pos.CENTER);

        VBox vbox = new VBox(10, messageLabel, buttonsBox);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(20));

        Scene dialogScene = new Scene(vbox);
        dialogStage.setScene(dialogScene);

        dialogStage.show();
    }

    private void loadUsers() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(ConfigLoader.getProperty("url_users")))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                ObjectMapper objectMapper = new ObjectMapper();
                List<User> users = objectMapper.readValue(response.body(), new TypeReference<List<User>>() {
                });
                userList.setAll(users);
                tableView.setItems(userList);
            } else {
                logger.log(Level.SEVERE, "Error while loading users. response: ", response);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unable to load users.", e);
        }
    }

}