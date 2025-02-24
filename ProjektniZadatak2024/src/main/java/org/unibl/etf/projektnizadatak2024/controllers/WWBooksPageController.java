package org.unibl.etf.projektnizadatak2024.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.unibl.etf.projektnizadatak2024.logger.LoggerConfig;
import org.unibl.etf.projektnizadatak2024.model.Book;
import org.unibl.etf.projektnizadatak2024.util.ConfigLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WWBooksPageController {
    private final ObservableList<Book> bookList = FXCollections.observableArrayList();
    private static final Logger logger = LoggerConfig.getLogger(OrderBooksPageController.class);
    private List<Book> allBooks;
    @FXML
    private Button backButton;
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
    private TableColumn<Book, String> titleColumn;

    @FXML
    public void initialize() {
        loadBooksFromApi();
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
    }

    @FXML
    void onBackButton(MouseEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/unibl/etf/projektnizadatak2024/BibliotekaStartPage.fxml"));
        Scene startScene = new Scene(fxmlLoader.load());

        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.setScene(startScene);
        stage.show();
    }

    @FXML
    void onAddNewButton(MouseEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/unibl/etf/projektnizadatak2024/NewBookPage.fxml"));
        Scene newBookScene = new Scene(fxmlLoader.load());

        NewBookPageController newBookPageController = fxmlLoader.getController();
        newBookPageController.setBookList(bookList);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(newBookScene);
        stage.show();
    }

    @FXML
    void onDeleteButton(MouseEvent event) {
        ObservableList<Book> selectedBooks = books.getSelectionModel().getSelectedItems();

        if (selectedBooks.isEmpty()) {
            showAlert("Error", "Select books to delete.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Accept deleting");
        alert.setHeaderText("Are you sure about deleting those books?");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                for (Book book : selectedBooks) {
                    deleteBookFromApi(book);
                }
                bookList.removeAll(selectedBooks);
            }
        });
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
                logger.log(Level.SEVERE, "Error while getting books from API, response: ", response);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error while getting books from API", e);
        }
    }

    private void showDetailsPopup(Book book) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Book Details");


        Image bookImage = loadImage(book.getUrlPath());
        ImageView imageView = new ImageView(bookImage);


        VBox layout = new VBox(10, imageView);
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
            logger.log(Level.SEVERE, "Error while loading image", e);
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
            System.err.println("Greška pri učitavanju sadržaja: " + e.getMessage());
            return "Nije moguće učitati sadržaj.";
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void deleteBookFromApi(Book book) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(ConfigLoader.getProperty("url_books") + book.getId()))
                    .DELETE()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
            } else {
                logger.log(Level.SEVERE, "Error while deleting book from API, response: ", response);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error while deleting book from API", e);
        }
    }

}
