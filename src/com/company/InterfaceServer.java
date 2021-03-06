package com.company;

import java.rmi.Remote;
import java.rmi.RemoteException;


/**
 * InterfaceServer for RMI Server
 * has all the functions that can be called in RMI Client
 */
public interface InterfaceServer extends Remote {
    public boolean loginOrRegister(String username, String password, boolean isRegister) throws RemoteException;
    public boolean checkIfUserIsAdmin(String username) throws RemoteException;
    public boolean grantAdminToUser(String username) throws RemoteException;
    public void logout(String username) throws RemoteException;


    public String getTCPAddress() throws RemoteException;

    public boolean addMusic(String name, String description, Integer duration, Integer albumID, Integer artistID) throws RemoteException;
    public boolean addAlbum(String name, String genre, String description, String date, Integer artistID) throws RemoteException;
    public boolean addArtist(String name, String description) throws RemoteException;

    public boolean changeData(String tableName, String columnType, Integer tableID, String newName) throws RemoteException;
    public boolean writeAlbumReview(int albumToReviewID, int albumRating, String albumReview) throws RemoteException;

    public String searchDetailAboutAlbum(int albumToSearch) throws RemoteException;
    public String searchDetailAboutArtist(int artistToSearch) throws RemoteException;

    public String getTable(String table, String username) throws RemoteException;

    public boolean setMusicIDToDownload(String username, int musicID) throws RemoteException;
    public boolean shareMusicInCloud(String username, int musicIDToShare) throws RemoteException;

    public void subscribe(InterfaceClient c, String username) throws RemoteException;
    public boolean notifyUsersAboutAlbumDescriptionEdit(String username, int albumID) throws RemoteException;
    public boolean notifyUserAboutAdminGranted(String username) throws RemoteException;
    public String checkNotifications(String username) throws RemoteException;
    public boolean clearNotifications(String username) throws RemoteException;
    public boolean userEditAlbum(String username, int albumID) throws RemoteException;
    public String printOnlineUsers() throws RemoteException;


    public boolean uploadFileToTable(String table, String column, String fileLocation, Integer id) throws RemoteException;

}

