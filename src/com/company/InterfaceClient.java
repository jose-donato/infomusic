package com.company;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface InterfaceClient extends Remote {
    public void notifyAdminGranted(String s) throws RemoteException;
    public void notifyAlbumChanges(String s) throws RemoteException;
}
