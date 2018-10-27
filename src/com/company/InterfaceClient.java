package com.company;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface for RMI Client
 * used to send notifications by server
 */
public interface InterfaceClient extends Remote {
    public void notifyAdminGranted() throws RemoteException;
    public void notifyAlbumChanges() throws RemoteException;
}
