package com.company;

import java.rmi.Remote;
import java.rmi.RemoteException;


/**
 * Interface for RMI Server
 * has all the functions that can be called in RMI Server
 */
public interface Interface extends Remote {
    public boolean loginOrRegister(String username, String password, boolean isRegister) throws RemoteException;
    public boolean checkIfUserIsAdmin(String username) throws RemoteException;
    public boolean grantAdminToUser(String username) throws RemoteException;
    public boolean changeData(String tableName, String columnType, Integer tableID, String newName) throws RemoteException;
    //public int register(String username,String password) throws RemoteException;
    public String getTCPAddress() throws RemoteException;
    public boolean addSong(String name, String genre, Integer duration) throws RemoteException;
    public int searchSong() throws RemoteException;
    public int searchDetailAboutArtist() throws RemoteException;
    public int searchDetailAboutAlbum() throws RemoteException;
    public boolean writeAlbumReview(int albumToReviewID, int albumRating, String albumReview) throws RemoteException;
    public int uploadSong() throws RemoteException;
    public int downloadSong() throws RemoteException;

}
