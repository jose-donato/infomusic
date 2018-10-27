package com.company;

import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * RMI Server class
 * has all methods that can be called by the client
 * makes requests to multicast with udp datagram packets
 */
public class RMIServer extends UnicastRemoteObject implements InterfaceServer {
    private static final long serialVersionUID = 1L;

    //array with all online rmi clients to send notifications when needed
    public static CopyOnWriteArrayList<User> onlineRmiClients = new CopyOnWriteArrayList<User>();

    //address changed when receives udp packet from server, used for the tcp connection between rmi client and multicast server
    public static  String MulticastTCPAddress = null;

    protected RMIServer() throws RemoteException {
        super();
    }

    public static void main(String[] args) throws RemoteException, InterruptedException, MalformedURLException, NotBoundException {

        boolean serverBackup;
        //to see if server is the primary or backup when class runs
        try {
            InterfaceServer i = (InterfaceServer) Naming.lookup("infoMusicRegistry");
            serverBackup = true;

        } catch (RemoteException e) {
            serverBackup = false;
        }

        if(serverBackup) {
            //tries to connect the server in case it's the backup server to see if the primary server fails, in case it fails becomes primary
            int attempt = 0;
            while (attempt < 5) {
                System.out.println("Trying to connect...");
                try {
                    InterfaceServer i = (InterfaceServer) Naming.lookup("infoMusicRegistry");
                    System.out.println("Connect!");
                    Thread.sleep(5000);
                    attempt = 0;

                } catch (RemoteException e) {
                    attempt += 1;
                }
            }
        }
        InterfaceServer i = new RMIServer();
        LocateRegistry.createRegistry(1099).rebind("infoMusicRegistry", i);

        System.out.println("Server ready...");
    }

    /**
     * logins a user in the program
     * @param username of the user
     * @param password of the user
     * @return 1 in case of success (in case of register returns 1 if user doesn't exist and does the regist), 0 otherwise
     */
    @Override
    public boolean loginOrRegister(String username, String password, boolean isRegister) {
        //Vai enviar a informação do cliente ao multicast para saber se este é
        int verify;
        HashMap<String, String> hmap = new HashMap<String, String>();
        hmap.put("username", username);
        hmap.put("password", password);
        if(isRegister) {
            hmap.put("type", "register");
            verify = ConnectionFunctions.sendUdpPacket(hmap);
        }
        else {
            hmap.put("type", "login");
            verify = ConnectionFunctions.sendUdpPacket(hmap);
        }
        //send the message to multicast server without problems
        if(verify == 1) {
            String message = ConnectionFunctions.receiveUdpPacket();
            //receives server response
            HashMap<String, String> map = ConnectionFunctions.string2HashMap(message);
            if(map.get("condition").equals("true")) {
                return true;
            }
            return false;
        }
        //problems when sending the message
        System.out.println("d: problems when sending message to multicast server");
        return false;
    }

    /**
     * checks if one user is admin
     * @param username to check if is admin
     * @return true in case it is admin, false otherwise
     */
    @Override
    public boolean checkIfUserIsAdmin(String username){
        HashMap<String, String> hmap = new HashMap<String, String>();
        hmap.put("type", "verifyAdmin");
        hmap.put("username", username);
        int verify = ConnectionFunctions.sendUdpPacket(hmap);
        if(verify == 1) {
            //receives server response to see if user is admin or not
            String message = ConnectionFunctions.receiveUdpPacket();
            HashMap<String, String> map = ConnectionFunctions.string2HashMap(message);
            if(map.get("condition").equals("true")) {
                return true;
            }
            return false;
        }
        //problems when sending the message
        System.out.println("d: problems when sending message to multicast server");
        return false;
    }

    /**
     * grant admin to one user
     * @param username to grant admin
     * @return true in case of success, false otherwise
     */
    @Override
    public boolean grantAdminToUser(String username) {
        HashMap<String, String> hmap = new HashMap<String, String>();
        hmap.put("type", "grantAdmin");
        hmap.put("username", username);
        int verify = ConnectionFunctions.sendUdpPacket(hmap);
        if(verify == 1) {
            //receives server response to check check if it admin was granted or don't
            String message = ConnectionFunctions.receiveUdpPacket();
            HashMap<String, String> map = ConnectionFunctions.string2HashMap(message);
            if(map.get("condition").equals("true")) {
                return true;
            }
            return false;
        }
        //problems when sending the message
        System.out.println("d: problems when sending message to multicast server");
        return false;
    }

    /**
     * change data in database
     * @param tableName type of data you want to change, can be artists, albums or musics
     * @param columnType type of data in each table, can be name or description
     * @param tableID id of each data, can be artistID, albumID, musicID
     * @param newName new data to add
     * @return always false, no verification yet
     * @throws RemoteException
     */
    @Override
    public boolean changeData(String tableName, String columnType, Integer tableID, String newName) throws RemoteException {
        HashMap<String, String> hmap = new HashMap<>();
        hmap.put("type", "changeData");
        hmap.put("tableName", tableName);
        hmap.put("columnType", columnType);
        hmap.put("tableID", tableID+"");
        hmap.put("newName", newName);
        ConnectionFunctions.sendUdpPacket(hmap);
        return false;
    }


    /**
     * adds one music to database
     * @param name of the music
     * @param description of the music
     * @param duration of the music
     * @param albumID of the music
     * @param artistID of the music
     * @return always false, no verification yet
     * @throws RemoteException
     */
    @Override
    public boolean addMusic(String name, String description, Integer duration, Integer albumID, Integer artistID) throws RemoteException {
        HashMap<String, String> hmap = new HashMap<>();
        hmap.put("type", "addMusic");
        hmap.put("name", name);
        hmap.put("description", description);
        hmap.put("duration", duration+"");
        hmap.put("albumID", albumID+"");
        hmap.put("artistID", artistID+"");
        ConnectionFunctions.sendUdpPacket(hmap);
        return false;
    }

    /**
     * adds one album to database
     * @param name of the album
     * @param genre of the album
     * @param description of the album
     * @param date of the album
     * @param artistID of the album
     * @return always false, no verification yet
     * @throws RemoteException
     */
    @Override
    public boolean addAlbum(String name, String genre, String description, String date, Integer artistID) throws RemoteException {
        HashMap<String, String> hmap = new HashMap<>();
        hmap.put("type", "addAlbum");
        hmap.put("name", name);
        hmap.put("description", description);
        hmap.put("genre", genre);
        hmap.put("date", date);
        hmap.put("artistID", artistID+"");
        ConnectionFunctions.sendUdpPacket(hmap);
        return false;
    }

    /**
     * adds one artist to database
     * @param name of the artist
     * @param description of the artist
     * @return always false, no verification yet
     * @throws RemoteException
     */
    @Override
    public boolean addArtist(String name, String description) throws RemoteException {
        HashMap<String, String> hmap = new HashMap<>();
        hmap.put("type", "addArtist");
        hmap.put("name", name);
        hmap.put("description", description);
        ConnectionFunctions.sendUdpPacket(hmap);
        return false;
    }


    /**
     * upload file to database (in case of lyrics and album's picture)
     * @param table to add file (albums in case of picture or musics in case of lyrics)
     * @param column of the table in database, picture or
     * @param fileLocation location of the file in local computer
     * @param id in the database
     * @return always false, no verification yet
     * @throws RemoteException
     */
    @Override
    public boolean uploadFileToTable(String table, String column, String fileLocation, Integer id) throws RemoteException {
        HashMap<String, String> hmap = new HashMap<>();
        hmap.put("type", "uploadFileToTable");
        hmap.put("table", table);
        hmap.put("column", column);
        hmap.put("fileLocation", fileLocation);
        hmap.put("id", id+"");
        ConnectionFunctions.sendUdpPacket(hmap);
        return false;
    }


    /**
     * grab table from database
     * @param table name, can be users, albums, musics, artists, cloudmusics
     * @param username in case is cloudmusics to grab all musics from one username
     * @return always false, no verification yet
     * @throws RemoteException
     */
    @Override
    public String getTable(String table, String username) throws RemoteException {
        HashMap<String, String> hmap = new HashMap<>();
        hmap.put("type", "getTable");
        hmap.put("table", table);
        hmap.put("username", username);
        ConnectionFunctions.sendUdpPacket(hmap);
        String message = ConnectionFunctions.receiveUdpPacket();
        HashMap<String, String> map = ConnectionFunctions.string2HashMap(message);
        return map.get("result");
    }


    /**
     * share music with one user
     * @param username user you want to share with
     * @param musicIDToShare id of the music to share with certain user
     * @return always false, no verification yet
     */
    @Override
    public boolean shareMusicInCloud(String username, int musicIDToShare) {
        HashMap<String, String> hmap = new HashMap<>();
        hmap.put("type", "shareMusicInCloud");
        hmap.put("username", username);
        hmap.put("musicID", musicIDToShare +"");
        ConnectionFunctions.sendUdpPacket(hmap);
        return false;
    }

    /**
     * adds info to albumsedits table in database that one user has edited one album
     * @param username of the user that edited one album
     * @param albumID that user edited
     * @return always false, no verification yet
     * @throws RemoteException
     */
    @Override
    public boolean userEditAlbum(String username, int albumID) throws RemoteException {
        HashMap<String, String> hmap = new HashMap<>();
        hmap.put("type", "userEditAlbum");
        hmap.put("username", username);
        hmap.put("albumID", albumID+"");
        ConnectionFunctions.sendUdpPacket(hmap);
        return false;
    }

    /**
     * search info about one specific album
     * @param albumToSearch albumID of the album user wants to search
     * @return always false, no verification yet
     * @throws RemoteException
     */
    @Override
    public String searchDetailAboutAlbum(int albumToSearch) throws RemoteException {
        HashMap<String, String> hmap = new HashMap<>();
        hmap.put("type", "albumDetail");
        hmap.put("albumToSearch", albumToSearch+"");
        int verify = ConnectionFunctions.sendUdpPacket(hmap);
        if(verify == 1) {
            String message = ConnectionFunctions.receiveUdpPacket();
            HashMap<String, String> map = ConnectionFunctions.string2HashMap(message);
            return map.get("resultString");
        }
        return null;
    }

    /**
     * search info about one specific album
     * @param artistToSearch artistID of the artist user wants to search
     * @return always false, no verification yet
     * @throws RemoteException
     */
    @Override
    public String searchDetailAboutArtist(int artistToSearch) throws RemoteException {
        HashMap<String, String> hmap = new HashMap<>();
        hmap.put("type", "artistDetail");
        hmap.put("artistToSearch", artistToSearch+"");
        int verify = ConnectionFunctions.sendUdpPacket(hmap);
        if(verify == 1) {
            String message = ConnectionFunctions.receiveUdpPacket();
            HashMap<String, String> map = ConnectionFunctions.string2HashMap(message);
            return map.get("resultString");
        }
        return null;
    }

    /**
     * add user to array of online rmi clients for notifications
     * @param c client interface to add to User class (contains the client interface and its username)
     * @param username of the client to add to User class
     * @throws RemoteException
     */
    @Override
    public void subscribe(InterfaceClient c, String username) throws RemoteException {
        User u = new User(username, c);
        onlineRmiClients.add(u);
    }


    /**
     * write review to an album
     * @param albumToReviewID id of the album to review
     * @param albumRating album rating (0 to 10)
     * @param albumReview album review (300 char max)
     * @return always false, no verification yet
     * @throws RemoteException
     */
    @Override
    public boolean writeAlbumReview(int albumToReviewID, int albumRating, String albumReview) throws RemoteException {
        HashMap<String, String> hmap = new HashMap<>();
        hmap.put("type", "reviewAlbum");
        hmap.put("albumToReviewID", albumToReviewID+"");
        hmap.put("albumRating", albumRating +"");
        hmap.put("albumReview", albumReview);
        ConnectionFunctions.sendUdpPacket(hmap);
        return false;
    }

    /**
     * sends to multicast information about the music one user (rmi client) wants to download
     * @param username user that wants to download
     * @param musicID id of the music user wants to download
     * @return always false, no verification yet
     * @throws RemoteException
     */
    @Override
    public boolean setMusicIDToDownload(String username, int musicID) throws RemoteException {
        HashMap<String, String> hmap = new HashMap<>();
        hmap.put("type", "setMusicIDToDownload");
        hmap.put("username", username);
        hmap.put("musicID", musicID+"");
        ConnectionFunctions.sendUdpPacket(hmap);
        return false;
    }

    /**
     * notify users about album changes (callback), prints immediatly on all users online and saves in database for users offline
     * @param username username that changed last time to not received the message
     * @param albumID album that was edited
     * @return always false, no verification yet
     * @throws RemoteException
     */
    @Override
    public boolean notifyUsersAboutAlbumDescriptionEdit(String username, int albumID) throws RemoteException {
        HashMap<String, String> hmap = new HashMap<>();
        hmap.put("type", "notifyUsersAboutAlbumDescriptionEdit");
        hmap.put("albumID", albumID +"");
        ConnectionFunctions.sendUdpPacket(hmap);
        String message = ConnectionFunctions.receiveUdpPacket();
        HashMap<String, String> map = ConnectionFunctions.string2HashMap(message);
        String users = map.get("result");

        if(!users.equals("no users")) { //if one or more users already changed this album, will send notification for them

            ArrayList<String> usersThatEditedAlbum = new ArrayList<String>(Arrays.asList(users.split(";")));
            ArrayList<String> usersToReceiveNotificationOffline = new ArrayList<>();
            ArrayList<String> usersOnline = new ArrayList<>();

            for (User u : onlineRmiClients) {
                usersOnline.add(u.username);
            }

            for (String u : usersThatEditedAlbum) {
                if (usersOnline.contains(u)) {
                    for (User user : onlineRmiClients) {
                        if (user.username.equals(u) && !username.equals(u)) { //if user is online and isn't the last one that edited, receives instant notification
                            user.client.notifyAlbumChanges();
                        }
                    }
                } else {
                    //if user isn't online, receives notification when loggs in
                    usersToReceiveNotificationOffline.add(u);
                }
            }

            //tell multicast to add notifications for users offline to database
            hmap = new HashMap<>();
            hmap.put("type", "addUsersToAlbumEditedNotificationTable");
            if(usersToReceiveNotificationOffline.size() > 0) {
                String arrayNames = "";
                for (String s : usersToReceiveNotificationOffline) {
                    arrayNames += s + ";";
                }
                arrayNames = arrayNames.substring(0, arrayNames.length() - 1);
                hmap.put("users", arrayNames);
                ConnectionFunctions.sendUdpPacket(hmap);
            }
        }
        return false;
    }

    /**
     * notify users if admin was granted
     * @param username of the user that admin was granted
     * @return always false, no verification yet
     * @throws RemoteException
     */
    @Override
    public boolean notifyUserAboutAdminGranted(String username) throws RemoteException {
        boolean isOnline = false;
        for(User u : onlineRmiClients) {
            if(u.username.equals(username)) {
                //if user is online notifies immediatly
                u.client.notifyAdminGranted();
                isOnline = true;
            }
        }
        if(!isOnline) {
            //if user isn't online, tells multicast to add notification to database
            HashMap<String, String> hmap = new HashMap<>();
            hmap.put("type", "notifyUserAboutAdminGranted");
            hmap.put("user", username);
            ConnectionFunctions.sendUdpPacket(hmap);
        }
        return false;
    }

    /**
     * check if one user has new notifications while he was offline
     * @param username of the user to check
     * @return string with notifications while he was offline (if none, returns "no notifications while you were offline")
     * @throws RemoteException
     */
    @Override
    public String checkNotifications(String username) throws RemoteException {
        HashMap<String, String> hmap = new HashMap<>();
        hmap.put("type", "checkNotifications");
        hmap.put("user", username);
        ConnectionFunctions.sendUdpPacket(hmap);
        String message = ConnectionFunctions.receiveUdpPacket();
        HashMap<String, String> map = ConnectionFunctions.string2HashMap(message);
        hmap = new HashMap<>();
        hmap.put("type", "clearNotifications");
        hmap.put("user", username);
        ConnectionFunctions.sendUdpPacket(hmap);
        return map.get("result");
    }

    /**
     * tells multicast server to clear notifications of one user in the database
     * @param username of the user to clear notifications
     * @return always false, no verification yet
     * @throws RemoteException
     */
    @Override
    public boolean clearNotifications(String username) throws RemoteException {
        HashMap<String, String> hmap = new HashMap<>();
            hmap = new HashMap<>();
            hmap.put("type", "clearNotifications");
            hmap.put("user", username);
            ConnectionFunctions.sendUdpPacket(hmap);
        return false;
    }

    /**
     * removes user from online rmi clients array
     * @param username of the user to remove
     * @throws RemoteException
     */
    @Override
    public void logout(String username) throws RemoteException {
        for(User u : onlineRmiClients) {
            if(u.username.equals(username)) {
                onlineRmiClients.remove(u);
            }
        }
    }

    /**
     * prints all online users in the program
     * @return string with all online users
     * @throws RemoteException
     */
    @Override
    public String printOnlineUsers() throws RemoteException {
        String result = "";
        for(User u : onlineRmiClients) {
            result += u + " \n ";
        }
        return result;
    }

    /**
     * grabs tcp address for rmi client to communicate with multicast for music transfers
     * @return multicast address if it's available, null otherwise
     * @throws RemoteException
     */
    @Override
    public String getTCPAddress() throws RemoteException {
        if(MulticastTCPAddress != null) {
            return MulticastTCPAddress;
        }
        else {
            System.out.println("no multicast available");
            return null;
        }
    }

}

