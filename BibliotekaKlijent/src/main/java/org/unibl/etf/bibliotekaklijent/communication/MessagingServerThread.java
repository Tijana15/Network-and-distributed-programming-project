package org.unibl.etf.bibliotekaklijent.communication;

import org.unibl.etf.bibliotekaklijent.model.Message;

import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class MessagingServerThread extends Thread {
    SSLSocket socket;
    ObjectOutputStream out;
    ObjectInputStream in;
    ArrayList<Message> messages;

    public MessagingServerThread(SSLSocket socket, ArrayList<Message> messages) {
        this.socket = socket;
        this.messages = messages;
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.out.println("Greška kod inicijalizacije message socket threada");
        }
    }

    public void run() {
        try {
            if (!socket.isClosed()) {
                String request = (String) in.readObject();
                if (request.startsWith("MESSAGES")) {
                    String[] parts = request.split("#");
                    String from = parts[1];
                    String to = parts[2];
                    ArrayList<Message> messageArrayList = new ArrayList<>();
                    for (Message message : messages) {
                        if (message.getFromUser().equals(from) && message.getToUser().equals(to) || message.getFromUser().equals(to) && message.getToUser().equals(from)) {
                            messageArrayList.add(message);
                        }
                    }
                    out.writeObject(messageArrayList);
                    out.flush();
                } else if (request.startsWith("SEND")) {
                    Message message = (Message) in.readObject();
                    messages.add(message);
                }
                out.close();
                in.close();
                socket.close();
            }
        } catch (IOException e) {
            System.out.println("IOO");
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            System.out.println("NOT FOUND");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
