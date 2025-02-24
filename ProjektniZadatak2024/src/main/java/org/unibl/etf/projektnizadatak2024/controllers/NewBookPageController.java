package org.unibl.etf.projektnizadatak2024.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.unibl.etf.projektnizadatak2024.logger.LoggerConfig;
import org.unibl.etf.projektnizadatak2024.model.Book;
import org.unibl.etf.projektnizadatak2024.util.ConfigLoader;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NewBookPageController {
    private static final Logger logger = LoggerConfig.getLogger(NewBookPageController.class);
    private ObservableList<Book> bookList;
    @FXML
    private TextField authorField;

    @FXML
    private TextField languageField;

    @FXML
    private TextField naslovnaStranaField;

    @FXML
    private TextField publicationDateField;

    @FXML
    private TextField sadrzajField;

    @FXML
    private Button saveButton;

    @FXML
    private TextField titleField;

    @FXML
    public void initialize() {
    }

    @FXML
    void onSaveButton(MouseEvent event) {
        saveNewBook();
    }

    public void setBookList(ObservableList<Book> bookList) {
        this.bookList = bookList;
    }

    private void saveNewBook() {
        String title = titleField.getText();
        String author = authorField.getText();
        String language = languageField.getText();
        String publicationDate = publicationDateField.getText();
        String pathToUrl = naslovnaStranaField.getText();
        String content = sadrzajField.getText();

        if (title.isEmpty() || author.isEmpty() || language.isEmpty() || publicationDate.isEmpty()) {
            showAlert("Error", "All fields has to be filled.");
            return;
        }
        Book newBook = new Book(title, author, language, publicationDate, pathToUrl, content);
        bookList.add(newBook);
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();

        saveNewBookOnServer(newBook);
    }

    private void saveNewBookOnServer(Book book) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            ObjectMapper objectMapper = new ObjectMapper();
            String bookJson = objectMapper.writeValueAsString(book);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(ConfigLoader.getProperty("url_books")))
                    .POST(HttpRequest.BodyPublishers.ofString(bookJson))
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                System.out.println("Successful");
            } else {
                System.out.println("Unsuccessful");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error occupied while saving books.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
