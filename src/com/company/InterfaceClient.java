package com.company;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface InterfaceClient extends Remote {
    public void notifyAdminGranted() throws RemoteException;
    public void notifyAlbumChanges() throws RemoteException;
}
