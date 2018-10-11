package com.company;

import java.rmi.Remote;
import java.rmi.RemoteException;


/**
 * Interface for RMI Server
 */
public interface Interface extends Remote {
    public double add(double a, double b) throws RemoteException;
}
