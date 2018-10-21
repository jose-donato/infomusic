package com.company;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.Date;


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
    public boolean addMusic(String name, String description, Integer duration, Integer albumID, Integer artistID) throws RemoteException;
    public boolean addAlbum(String name, Date date, Integer artistID) throws RemoteException;
    public boolean addArtist(String name, String description) throws RemoteException;

    public int searchSong() throws RemoteException;
    public int searchDetailAboutArtist() throws RemoteException;
    public String searchDetailAboutAlbum(int albumToSearch) throws RemoteException;
    public boolean writeAlbumReview(int albumToReviewID, int albumRating, String albumReview) throws RemoteException;
    public int uploadSong() throws RemoteException;
    public int downloadSong() throws RemoteException;

}
