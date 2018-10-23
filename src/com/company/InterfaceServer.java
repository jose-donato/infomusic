package com.company;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.Date;


/**
 * InterfaceServer for RMI Server
 * has all the functions that can be called in RMI Server
 */
public interface InterfaceServer extends Remote {
    public boolean loginOrRegister(String username, String password, boolean isRegister) throws RemoteException;
    public boolean checkIfUserIsAdmin(String username) throws RemoteException;
    public boolean grantAdminToUser(String username) throws RemoteException;
    public boolean changeData(String tableName, String columnType, Integer tableID, String newName) throws RemoteException;
    //public int register(String username,String password) throws RemoteException;
    public String getTCPAddress() throws RemoteException;
    public boolean addMusic(String name, String description, Integer duration, Integer albumID, Integer artistID) throws RemoteException;
    public boolean addAlbum(String name, String genre, String date, Integer artistID) throws RemoteException;
    public boolean addArtist(String name, String description) throws RemoteException;

    //for upload text file lyrics and picture to album
    public boolean uploadFileToTable(String table, String column, String fileLocation, Integer id) throws RemoteException;

    public String getTable(String table) throws RemoteException;

    //search details about an album
    public String searchDetailAboutAlbum(int albumToSearch) throws RemoteException;
    public String searchDetailAboutArtist(int artistToSearch) throws RemoteException;

    public void subscribe(InterfaceClient c, String username) throws RemoteException;

    public boolean writeAlbumReview(int albumToReviewID, int albumRating, String albumReview) throws RemoteException;

    //need to be implemented
    public boolean searchByGenre() throws RemoteException;
    public boolean searchByAlbumName() throws RemoteException;
    public boolean searchByArtistName() throws RemoteException;




    //public void subscribe(String name, InterfaceClient client) throws RemoteException;

}
