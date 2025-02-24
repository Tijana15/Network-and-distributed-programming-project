package org.unibl.etf.bibliotekaklijent.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import org.unibl.etf.bibliotekaklijent.communication.ChatMessage;
import org.unibl.etf.bibliotekaklijent.model.Message;
import org.unibl.etf.bibliotekaklijent.model.User;
import org.unibl.etf.bibliotekaklijent.util.ConfigLoader;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static org.unibl.etf.bibliotekaklijent.communication.ChatMessage.createSSLContext;
import static org.unibl.etf.bibliotekaklijent.controller.SignInController.user;

public class ChatController {
    private static Logger logger = Logger.getLogger(ChatController.class.getName());
    @FXML
    private ListView<String> listView;
    @FXML
    private ListView<String> listViewMessages;
    @FXML
    private TextField textField;
    private final User currentUser = user;
    private String toUser;

    @FXML
    public void initialize() {
        loadUsersFromApi();
    }

    @FXML
    void onSendButton(MouseEvent event) throws Exception {
        String content = textField.getText();
        textField.clear();
        Message message = new Message(user.getUsername(), toUser, content, new Date());
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        listViewMessages.getItems().add("You: " + message.getMessage() + "     " + formatter.format(message.getDate()));
        SSLContext sslContext = createSSLContext();
        SSLSocketFactory sf = sslContext.getSocketFactory();
        InetAddress inetAddress = InetAddress.getByName("localhost");
        try (SSLSocket s = (SSLSocket) sf.createSocket(inetAddress, 9000);
             ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
             ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream())) {
            oos.writeObject("SEND");
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    void onUserListView(MouseEvent event) throws Exception {
        if (event.getClickCount() == 2) {
            String selectedUser = listView.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                this.toUser = selectedUser;
                ArrayList<Message> messages = getMessages(currentUser.getUsername(), selectedUser);
                if (messages != null) {
                    listViewMessages.getItems().clear();
                    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
                    for (Message m :
                            messages) {
                        if (m.getFromUser().equals(user.getUsername())) {
                            listViewMessages.getItems().add("You: " + m.getMessage() + "     " + formatter.format(m.getDate()));
                        } else {
                            listViewMessages.getItems().add(toUser + ": " + m.getMessage() + "      " + formatter.format(m.getDate()));
                        }
                    }
                }

            }
        }
    }

    private void loadUsersFromApi() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(ConfigLoader.getProperty("url_users")))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                ObjectMapper objectMapper = new ObjectMapper();
                List<User> allUsers = objectMapper.readValue(response.body(), new TypeReference<List<User>>() {
                });

                List<String> filteredUsernames = allUsers.stream()
                        .filter(user -> !user.getUsername().equals(currentUser.getUsername()))
                        .map(User::getUsername)
                        .collect(Collectors.toList());

                listView.getItems().setAll(filteredUsernames);

            } else {
                System.out.println("Error occupied while loading users");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error occupied while loading users", e);
        }
    }

    private ArrayList<Message> getMessages(String firstUser, String secondUser) throws Exception {
        SSLContext sslContext = ChatMessage.createSSLContext();
        SSLSocketFactory socketFactory = sslContext.getSocketFactory();
        InetAddress inetAddress = InetAddress.getByName("localhost");
        try (SSLSocket socket = (SSLSocket) socketFactory.createSocket(inetAddress, 9000);
             ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())) {
            oos.writeObject("MESSAGES#" + firstUser + "#" + secondUser);
            ArrayList<Message> messages = (ArrayList<Message>) ois.readObject();
            return messages;
        } catch (IOException | ClassNotFoundException e) {
            logger.log(Level.SEVERE, "Error while getting messages", e);
        }
        return null;
    }
}
