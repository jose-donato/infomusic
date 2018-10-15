package com.company;

import java.rmi.Remote;
import java.rmi.RemoteException;


/**
 * Interface for RMI Server
 * has all the functions that can be called in RMI Server
 */
public interface Interface extends Remote {
    public int loginOrRegister(String username, String password) throws RemoteException;
    //public int register(String username,String password) throws RemoteException;

}
