package com.company;

import java.rmi.Remote;
import java.rmi.RemoteException;


/**
 * Interface for RMI Server
 */
public interface Interface extends Remote {
    public int login(String username, String password) throws RemoteException;
    public int register(String username,String password) throws RemoteException;

}
