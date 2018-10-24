package com.company;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
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
 *
 */
public class RMIServer extends UnicastRemoteObject implements InterfaceServer {
    private static final long serialVersionUID = 1L;
    public static CopyOnWriteArrayList<User> onlineRmiClients = new CopyOnWriteArrayList<User>();

    public static  String MulticastTCPAddress = null;
    protected RMIServer() throws RemoteException {
        super();
    }

    /**
     * @param args
     * @throws RemoteException
     */
    public static void main(String[] args) throws RemoteException, InterruptedException, MalformedURLException, NotBoundException {
          /* InterfaceServer i = new RMIServer();
        LocateRegistry.createRegistry(1099).rebind("infoMusicRegistry", i);
        //client.printOnClient("ola do servidor");
        System.out.println("Server ready...");*/
        boolean ServerBackup = true;
        try {
            InterfaceServer i = (InterfaceServer) Naming.lookup("infoMusicRegistry");
            ServerBackup = true;

        } catch (RemoteException e) {
            ServerBackup = false;
        }

        if(ServerBackup) {
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
    public boolean loginOrRegister(String username, String password, boolean isRegister) {
        //Vai enviar a informação do cliente ao multicast para saber se este é
        int verify;
        if(isRegister) {
            verify = ConnectionFunctions.sendUdpPacket(aux(username, password, "register"));
        }
        else {
            verify = ConnectionFunctions.sendUdpPacket(aux(username, password, "login"));
        }
        //send the message to multicast server without problems
        if(verify == 1) {
            // Vai receber informação do Multicast para saber se existe um Username
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

    public boolean checkIfUserIsAdmin(String username){
        int verify = ConnectionFunctions.sendUdpPacket(aux2(username,"verifyAdmin"));
        if(verify == 1) {
            // Vai receber informação do Multicast para saber se o username é admin ou nao
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

    @Override
    public boolean grantAdminToUser(String username) {
        int verify = ConnectionFunctions.sendUdpPacket(aux2(username,"grantAdmin"));
        if(verify == 1) {
            // Vai receber informação do Multicast para saber se o username é admin ou nao
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

    @Override
    public boolean addArtist(String name, String description) throws RemoteException {
        HashMap<String, String> hmap = new HashMap<>();
        hmap.put("type", "addArtist");
        hmap.put("name", name);
        hmap.put("description", description);
        ConnectionFunctions.sendUdpPacket(hmap);
        return false;
    }

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
     * aux to create the login hashmap to convert to string to send to multicast server
     * @param username
     * @param password
     * @param type can be login, register, etc
     * @return the hashmap
     */
    public HashMap<String, String> aux(String username, String password, String type) {
        HashMap<String, String> hmap = new HashMap<String, String>();
        hmap.put("type", type);
        hmap.put("username", username);
        hmap.put("password", password);
        return hmap;
    }

    public HashMap<String, String> aux2(String username, String type) {
        HashMap<String, String> hmap = new HashMap<String, String>();
        hmap.put("type", type);
        hmap.put("username", username);
        return hmap;
    }



    @Override
    public boolean searchByGenre() throws RemoteException {
        return false;
    }

    @Override
    public boolean searchByAlbumName() throws RemoteException {
        return false;
    }

    @Override
    public boolean searchByArtistName() throws RemoteException {
        return false;
    }

    @Override
    public boolean shareMusicInCloud(String username, int musicIDToShare) {
        HashMap<String, String> hmap = new HashMap<>();
        hmap.put("type", "shareMusicInCloud");
        hmap.put("username", username);
        hmap.put("musicID", musicIDToShare +"");
        ConnectionFunctions.sendUdpPacket(hmap);
        //verify if it worked
        return false;
    }

    @Override
    public boolean userEditAlbum(String username, int albumID) throws RemoteException {
        HashMap<String, String> hmap = new HashMap<>();
        hmap.put("type", "userEditAlbum");
        hmap.put("username", username);
        hmap.put("albumID", albumID+"");
        ConnectionFunctions.sendUdpPacket(hmap);
        return false;
    }

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

    @Override
    public void subscribe(InterfaceClient c, String username) throws RemoteException {
        User u = new User(username, c);
        onlineRmiClients.add(u);
    }


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

    @Override
    public boolean setMusicIDToDownload(String username, int musicID) throws RemoteException {
        HashMap<String, String> hmap = new HashMap<>();
        hmap.put("type", "setMusicIDToDownload");
        hmap.put("username", username);

        ConnectionFunctions.sendUdpPacket(hmap);
        return false;
    }

    @Override
    public boolean notifyUsersAboutAlbumDescriptionEdit(String username, int albumID) throws RemoteException {
        HashMap<String, String> hmap = new HashMap<>();
        hmap.put("type", "notifyUsersAboutAlbumDescriptionEdit");
        hmap.put("albumID", albumID +"");
        ConnectionFunctions.sendUdpPacket(hmap);
        String message = ConnectionFunctions.receiveUdpPacket();
        HashMap<String, String> map = ConnectionFunctions.string2HashMap(message);
        String users = map.get("result");
        if(!users.equals("no users")) {
            ArrayList<String> usersThatEditedAlbum = new ArrayList<String>(Arrays.asList(users.split(";")));
            ArrayList<String> usersToReceiveNotificationOffline = new ArrayList<>();
            ArrayList<String> usersOnline = new ArrayList<>();
            for (User u : onlineRmiClients) {
                usersOnline.add(u.username);
            }
            for (String u : usersThatEditedAlbum) {
                if (usersOnline.contains(u)) {
                    for (User user : onlineRmiClients) {
                        if (user.username.equals(u) && !username.equals(u)) {
                            user.client.notifyAlbumChanges();
                        }
                    }
                } else {
                    usersToReceiveNotificationOffline.add(u);
                }
            }
            hmap = new HashMap<>();
            hmap.put("type", "addUsersToAlbumEditedNotificationTable");

            String arrayNames = "";
            for (String s : usersToReceiveNotificationOffline) {
                arrayNames += s + ";";
            }
            arrayNames = arrayNames.substring(0, arrayNames.length() - 1);
            hmap.put("users", arrayNames);
            ConnectionFunctions.sendUdpPacket(hmap);
        }
        return false;
    }

    @Override
    public boolean notifyUserAboutAdminGranted(String username) throws RemoteException {
        boolean isOnline = false;
        for(User u : onlineRmiClients) {
            if(u.username.equals(username)) {
                u.client.notifyAdminGranted();
                isOnline = true;
            }
        }
        if(!isOnline) {
            HashMap<String, String> hmap = new HashMap<>();
            hmap.put("type", "addUsersToAdminGrantedNotificationTable");
            hmap.put("user", username);
            ConnectionFunctions.sendUdpPacket(hmap);
        }
        return false;
    }

    @Override
    public String checkNotifications(String username) throws RemoteException {
        HashMap<String, String> hmap = new HashMap<>();
        hmap.put("type", "checkNotifications");
        hmap.put("user", username);
        ConnectionFunctions.sendUdpPacket(hmap);
        String message = ConnectionFunctions.receiveUdpPacket();
        HashMap<String, String> map = ConnectionFunctions.string2HashMap(message);
        return map.get("result");
    }

}

