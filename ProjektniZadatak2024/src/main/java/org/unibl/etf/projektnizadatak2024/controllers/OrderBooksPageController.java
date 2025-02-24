package org.unibl.etf.projektnizadatak2024.controllers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.unibl.etf.projektnizadatak2024.logger.LoggerConfig;
import org.unibl.etf.projektnizadatak2024.model.Book;
import org.unibl.etf.projektnizadatak2024.model.Supplier;
import org.unibl.etf.projektnizadatak2024.util.ConfigLoader;
import org.unibl.etf.projektnizadatak2024.util.ConnectionFactoryUtil;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OrderBooksPageController {
    private ObservableList<Book> booksOrder = FXCollections.observableArrayList();
    private static final Logger logger = LoggerConfig.getLogger(OrderBooksPageController.class);
    public static String supplier;
    @FXML
    private Button backButton;
    @FXML
    private VBox titledPaneCointainer;
    @FXML
    private TableColumn<Book, String> bookTitleColumnCart;
    @FXML
    private TableView<Book> cart;

    @FXML
    public void initialize() {
        try {
            loadSuppliersAndBooks();
            bookTitleColumnCart.setCellValueFactory(new PropertyValueFactory<>("title"));
            cart.setItems(booksOrder);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error in initialize method.", e);
        }
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
    void onAddToCartButton(MouseEvent event) {
        TitledPane selectedPane = (TitledPane) titledPaneCointainer.getChildren()
                .stream()
                .filter(node -> node instanceof TitledPane && ((TitledPane) node).isExpanded())
                .findFirst()
                .orElse(null);

        if (selectedPane == null) return;

        AnchorPane anchorPane = (AnchorPane) selectedPane.getContent();
        TableView<Book> tableView = (TableView<Book>) anchorPane.getChildren().get(0);
        Book selectedBook = tableView.getSelectionModel().getSelectedItem();

        if (selectedBook != null) {
            booksOrder.add(selectedBook);
        }
        supplier = selectedPane.getText();
    }

    @FXML
    void onOrderBooks(MouseEvent event) throws Exception {
        if (!booksOrder.isEmpty()) {
            sendOrder();
        } else {
            logger.log(Level.WARNING, "Cart is empty!");
        }
        cart.getItems().clear();
    }

    private void loadSuppliersAndBooks() throws IOException, ClassNotFoundException {
        List<Supplier> suppliers = loadSuppliersFromServer();
        if (suppliers != null) {
            for (Supplier supplier : suppliers) {
                TitledPane titledPane = createSupplierPane(supplier);
                titledPaneCointainer.getChildren().add(titledPane);
                titledPaneCointainer.setLayoutX(-7.0);
            }
        } else {
            System.out.println("No suppliers found while loading.");
        }
    }

    private TitledPane createSupplierPane(Supplier supplier) {
        TitledPane titledPane = new TitledPane();
        titledPane.setText(supplier.getName());
        titledPane.setMinWidth(660);

        AnchorPane anchorPane = new AnchorPane();
        TableView<Book> tableView = createTableView();

        try {
            List<Book> books = supplier.getBooks();
            if (books != null) {
                ObservableList<Book> bookList = FXCollections.observableArrayList(books);
                tableView.setItems(bookList);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error in creating supplier pane.", e);
        }

        anchorPane.getChildren().add(tableView);
        AnchorPane.setTopAnchor(tableView, 0.0);
        AnchorPane.setRightAnchor(tableView, 0.0);
        AnchorPane.setBottomAnchor(tableView, 0.0);
        AnchorPane.setLeftAnchor(tableView, 0.0);

        titledPane.setContent(anchorPane);
        return titledPane;
    }

    private TableView<Book> createTableView() {
        TableView<Book> tableView = new TableView<>();

        TableColumn<Book, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));

        TableColumn<Book, String> authorColumn = new TableColumn<>("Author");
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));

        TableColumn<Book, String> publicationColumn = new TableColumn<>("Publication");
        publicationColumn.setCellValueFactory(new PropertyValueFactory<>("publicationDate"));

        tableView.getColumns().addAll(titleColumn, authorColumn, publicationColumn);

        return tableView;
    }

    public void sendOrder() throws Exception {
        Connection connection = ConnectionFactoryUtil.createConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(supplier, false, false, false, null);

        Gson gson = new Gson();
        String message = gson.toJson(booksOrder);

        channel.basicPublish("", supplier, null, message.getBytes());
    }

    private static List<Supplier> loadSuppliersFromServer() throws IOException, ClassNotFoundException {
        List<Supplier> suppliers = new ArrayList<>();
        InetAddress address = InetAddress.getByName("127.0.0.1");
        Socket socket = new Socket(address, Integer.parseInt(ConfigLoader.getProperty("socket_port_suppliers")));
        try {
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

            oos.writeObject("SUPPLIERS");
            oos.flush();
            String jsonResponse = (String) ois.readObject();

            Gson gson = new Gson();
            Type listType = new TypeToken<List<Supplier>>() {
            }.getType();
            suppliers = gson.fromJson(jsonResponse, listType);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error occupied while loading suppliers from server.");
        }
        return suppliers;
    }

}


