package org.unibl.etf.dobavljacserver.knjigovodstveniservis;

import org.unibl.etf.dobavljacserver.model.Bill;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ISaveBill extends Remote {
    double saveBill(Bill bill) throws RemoteException;
}
