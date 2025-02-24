package org.unibl.etf.bibliotekaklijent.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.unibl.etf.bibliotekaklijent.logger.LoggerConfig;
import org.unibl.etf.bibliotekaklijent.model.Book;
import org.unibl.etf.bibliotekaklijent.util.ConfigLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BooksOverviewController {
    private ObservableList<String> messageList;
    private final ObservableList<Book> bookList = FXCollections.observableArrayList();
    private static final Logger logger = LoggerConfig.getLogger(BooksOverviewController.class);
    private List<Book> allBooks;
    @FXML
    private TableColumn<Book, String> authorColumn;
    @FXML
    private TableView<Book> books;
    @FXML
    private TableColumn<Book, Void> detailsColumn;
    @FXML
    private TableColumn<Book, String> languageColumn;
    @FXML
    private TableColumn<Book, String> publicationDateColumn;
    @FXML
    private TextField searchField;
    @FXML
    private TableColumn<Book, String> titleColumn;

    @FXML
    public void initialize() {
        books.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        languageColumn.setCellValueFactory(new PropertyValueFactory<>("language"));
        publicationDateColumn.setCellValueFactory(new PropertyValueFactory<>("publicationDate"));

        detailsColumn.setCellFactory(column -> new TableCell<Book, Void>() {
            private final Button detailsButton = new Button("Details");

            {
                detailsButton.setOnAction(event -> {
                    Book book = getTableView().getItems().get(getIndex());
                    showDetailsPopup(book);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(detailsButton);
                }
            }
        });
        loadBooksFromApi();
    }

    @FXML
    void onChatButton(MouseEvent event) throws IOException {
        Stage stage = new Stage();
        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/org/unibl/etf/bibliotekaklijent/BookIcon.png")));
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/unibl/etf/bibliotekaklijent/Chat.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.getIcons().add(icon);
        stage.setTitle("Chat with others!");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void onDownloadButton(MouseEvent event) {
        ObservableList<Book> selectedBooks = books.getSelectionModel().getSelectedItems();
        if (selectedBooks.isEmpty()) {
            showAlert("No Books Selected", "Please select at least one book to download.");
            return;
        }
        sendBooksToMailAPI(new ArrayList<>(selectedBooks));
    }

    @FXML
    void onWriteSuggestionsButton(MouseEvent event) throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/unibl/etf/bibliotekaklijent/Suggestions.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        SuggestionsController suggestionsController = fxmlLoader.getController();
        suggestionsController.setMessageList(messageList);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void onSearchButton(MouseEvent event) {
        String searchQuery = searchField.getText().trim().toLowerCase();

        if (allBooks != null) {
            ObservableList<Book> filteredBooks = FXCollections.observableArrayList();
            for (Book book : allBooks) {
                if (book.getTitle().toLowerCase().contains(searchQuery)) {
                    filteredBooks.add(book);
                }
            }
            books.setItems(filteredBooks);
        } else {
            System.out.println("Book list is not loaded.");
        }
    }

    private void sendBooksToMailAPI(ArrayList<Book> books) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonBody = objectMapper.writeValueAsString(books);

            String userEmail = SignInController.user.getEmail();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(ConfigLoader.getProperty("url_emailTo") + userEmail))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                showAlert("Success", "Books have been sent to your email!");
            } else {
                showAlert("Error", "Books are not sent. Status code: " + response.statusCode());
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error ocuppied while sending books on email.", e);
        }
    }

    private void loadBooksFromApi() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(ConfigLoader.getProperty("url_books")))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                ObjectMapper objectMapper = new ObjectMapper();
                allBooks = objectMapper.readValue(response.body(), new TypeReference<List<Book>>() {
                });
                bookList.setAll(allBooks);
                books.setItems(bookList);
            } else {
                System.out.println("Error ocupied while loading books.");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error ocuppied while loading books from API.", e);
        }
    }

    private void showDetailsPopup(Book book) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Book Details");


        Image bookImage = loadImage(book.getUrlPath());
        ImageView imageView = new ImageView(bookImage);

        String content = loadContent(book.getContent());
        TextArea contentArea = new TextArea(content);
        contentArea.setEditable(false);
        contentArea.setWrapText(true);
        contentArea.setPrefHeight(200);


        VBox layout = new VBox(10, imageView, contentArea);
        layout.setPadding(new Insets(10));
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 400, 400);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }

    private Image loadImage(String imageUrl) {
        try {
            return new Image(imageUrl);
        } catch (Exception e) {
            System.out.println("Error while loading image.");
            return null;
        }
    }

    private String loadContent(String contentUrl) {
        try {
            URL url = new URL(contentUrl);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder content = new StringBuilder();
            String line;
            int lineCount = 0;

            while ((line = reader.readLine()) != null && lineCount < 100) {
                content.append(line).append("\n");
                lineCount++;
            }
            reader.close();
            return content.toString();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error while loading content", e);
            return "Nije moguće učitati sadržaj.";
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setMessageList(ObservableList<String> messageList) {
        this.messageList = messageList;
    }
}
