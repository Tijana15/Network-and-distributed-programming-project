package org.unibl.etf.projektnizadatak2024.controllers;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import org.unibl.etf.projektnizadatak2024.logger.LoggerConfig;
import org.unibl.etf.projektnizadatak2024.util.ConfigLoader;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SuggestionsController {
    private static final Logger logger = Logger.getLogger(SuggestionsController.class.getName());
    private ObservableList<String> messageList;
    @FXML
    private ListView<String> chatList;
    @FXML
    private TextField sugesstionField;

    @FXML
    void sendMessage(MouseEvent event) throws UnknownHostException {
        InetAddress inetAddress = InetAddress.getByName(ConfigLoader.getProperty("multicast_address"));
        String message = "Bibliotekar: " + sugesstionField.getText();
        sugesstionField.clear();
        byte[] buf = message.getBytes();
        try (DatagramSocket socket = new DatagramSocket()) {
            DatagramPacket packet = new DatagramPacket(buf, buf.length, inetAddress, 20000);
            socket.send(packet);
            System.out.println("Message send.");
        } catch (IOException ioe) {
            logger.log(Level.SEVERE, "Error sending message", ioe);
        }
    }

    public void setMessageList(ObservableList<String> messageList) {
        this.messageList = messageList;
        chatList.setItems(messageList);

        messageList.addListener((ListChangeListener<String>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    chatList.scrollTo(change.getFrom());
                }
            }
        });
    }

}
