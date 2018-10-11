package com.company;

import java.rmi.Remote;
import java.rmi.RemoteException;


/**
 * Interface for RMI Server
 */
public interface Interface extends Remote {
    public void login(String a, double b) throws RemoteException;
    public void register(String username,String password) throws RemoteException;

}
