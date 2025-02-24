package org.unibl.etf.dobavljacserver.knjigovodstveniservis;

import com.google.gson.Gson;
import org.unibl.etf.dobavljacserver.model.Bill;
import org.unibl.etf.dobavljacserver.service.BookService;
import org.unibl.etf.dobavljacserver.util.ConfigLoader;

import java.io.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;

public class KnjigovodstveniServis implements ISaveBill {
    public static int PORT = Integer.parseInt(ConfigLoader.getProperty("registry_port"));
    private static Logger logger = Logger.getLogger(KnjigovodstveniServis.class.getName());

    @Override
    public double saveBill(Bill bill) throws RemoteException {
        serializeBill(bill);
        double taxPercentage = 0.17;
        return taxPercentage * bill.getPrice();
    }

    private void serializeBill(Bill bill) {
        String path = "src/main/resources/bill";
        String fileName = path + System.currentTimeMillis() + ".json";
        File file = new File(fileName);
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)), true)) {
            Gson gson = new Gson();
            pw.println(gson.toJson(bill));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        try {
            ISaveBill server = new KnjigovodstveniServis();
            ISaveBill stub = (ISaveBill) UnicastRemoteObject.exportObject(server, 0);
            Registry registry = LocateRegistry.createRegistry(PORT);
            registry.rebind("KnjigovodstveniServis", stub);
        } catch (RemoteException e) {
            logger.log(Level.SEVERE, "Error occupied in main method of Knjigovodstveni servis", e);
        }
    }
}
