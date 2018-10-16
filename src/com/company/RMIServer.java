package com.company;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Scanner;

/**
 *
 */
public class RMIServer extends UnicastRemoteObject implements Interface {
    private static final long serialVersionUID = 1L;

    protected RMIServer() throws RemoteException {
        super();
    }

    /**
     * logins a user in the program
     * @param username of the user
     * @param password of the user
     * @return 1 in case of success (in case of register returns 1 if user doesn't exist and does the regist), 0 otherwise
     */
    public boolean loginOrRegister(String username, String password, boolean isRegister) {
        new ConnectionFunctions();
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

    /**
     * @param args
     * @throws RemoteException
     */
    public static void main(String[] args) throws RemoteException {
        Interface i = new RMIServer();
        LocateRegistry.createRegistry(1099).rebind("infoMusicRegistry", i);
        System.out.println("Server ready...");
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


    @Override
    public int searchSong() throws RemoteException {
        return 0;
    }

    @Override
    public int searchDetailAboutArtist() throws RemoteException {
        return 0;
    }

    @Override
    public int searchDetailAboutAlbum() throws RemoteException {
        return 0;
    }

    @Override
    public int writeAlbumReview() throws RemoteException {
        return 0;
    }

    @Override
    public int uploadSong() throws RemoteException {
        return 0;
    }

    @Override
    public int downloadSong() throws RemoteException {
        return 0;
    }
}

