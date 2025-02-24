package org.unibl.etf.bibliotekaklijent.communication;

import org.unibl.etf.bibliotekaklijent.model.Message;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.util.ArrayList;

public class MessagingServer {
    public static void main(String[] args) {
        System.setProperty("javax.net.ssl.keyStore", "keystore.jks");
        System.setProperty("javax.net.ssl.keyStorePassword", "sigurnost");
        try {
            SSLServerSocketFactory ssf = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            SSLServerSocket ss = (SSLServerSocket) ssf.createServerSocket(9000);
            ArrayList<Message> messages = new ArrayList<>();
            System.out.println("Server started");
            while (true) {
                SSLSocket socket = (SSLSocket) ss.accept();
                new MessagingServerThread(socket, messages).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
