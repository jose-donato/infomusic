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
     * @return 1 in case of success, 0 otherwise
     */
    public int login(String username, String password) {
        String MULTICAST_ADDRESS = "224.0.224.0";
        int PORT = 4321;
        MulticastSocket socket = null;

        //Vai enviar a informação do cliente ao multicast para saber se este é
        int verify = new ConnectionFunctions().sendUdpPacket(auxLogin(username, password));
        //send the message to multicast server without problems
        if(verify == 1) {
            // Vai receber informação do Multicast para saber se existe um Username
            String message = new ConnectionFunctions().receiveUdpPacket();
            if(username.equals("ola") && password.equals("adeus")) {
                return 1;
            }
            else {
                return 0;
            }
        }
        //problems when sending the messaeg
        else {
            System.out.println("d: problems when sending message to multicast server");
        }
        return 0;

    }

    /**
     * aux to create the login hashmap to convert to string to send to multicast server
     * @param username
     * @param password
     * @return the hashmap
     */
    public HashMap<String, String> auxLogin(String username, String password) {
        HashMap<String, String> hmap = new HashMap<String, String>();
        hmap.put("type", "login");
        hmap.put("username", username);
        hmap.put("password", password);
        return hmap;
    }

    /**
     * regist a user in the program
     * @param username of the user
     * @param password of the user
     * @return 1 in success, 0 otherwise
     */
    public int register(String username, String password) {
        return 1;
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

}

