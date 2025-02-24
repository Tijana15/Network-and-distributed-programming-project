package org.unibl.etf.bibliotekaklijent.communication;

import javafx.application.Platform;
import javafx.collections.ObservableList;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Multicast extends Thread {
    private ObservableList<String> messageList;

    public Multicast(ObservableList<String> messageList) {
        this.messageList = messageList;
        setDaemon(true);
    }

    @Override
    public void run() {
        byte[] buf = new byte[1024];
        try (MulticastSocket ms = new MulticastSocket(20000)) {
            InetAddress group = InetAddress.getByName("225.0.0.11");
            ms.joinGroup(group);
            while (true) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                ms.receive(packet);
                String message = new String(packet.getData(), 0, packet.getLength());
                Platform.runLater(() -> messageList.add(message));
                System.out.println("Received: " + message);
            }

        } catch (Exception e) {
            System.out.println("Greškaa " + e.getMessage());
        }
    }
}

