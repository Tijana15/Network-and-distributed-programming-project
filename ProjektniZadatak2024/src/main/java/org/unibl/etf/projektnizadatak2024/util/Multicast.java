package org.unibl.etf.projektnizadatak2024.util;

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
        try (MulticastSocket ms = new MulticastSocket(Integer.parseInt(ConfigLoader.getProperty("multicast_port")))) {
            String address = ConfigLoader.getProperty("multicast_address");
            InetAddress group = InetAddress.getByName(address);
            ms.joinGroup(group);
            while (true) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                ms.receive(packet);
                String message = new String(packet.getData(), 0, packet.getLength());
                Platform.runLater(() -> messageList.add(message));
            }
        } catch (Exception e) {
            System.out.println("Greška kod multikasta " + e.getMessage());
        }
    }

}

