package com.company;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface InterfaceClient extends Remote {
    public void printOnClient(String s) throws RemoteException;
}
