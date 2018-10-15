package com.company;

import java.net.MulticastSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;

/**
 *
 */
public class MulticastServer extends Thread {
    private String MULTICAST_ADDRESS = "224.0.224.0";
    private int PORT = 4321;
    private long SLEEP_TIME = 5000;


    public static void main(String[] args) {
        MulticastServer server = new MulticastServer();
        server.start();
    }

    /**
     *
     */
    public MulticastServer() {
        super("Server " + (long) (Math.random() * 1000));
    }

    /**
     *
     */
    public void run() {
        //use if no table is created in the system
        //new SQL().initialConfig();
        MulticastSocket socket = null;
        try {
            socket = new MulticastSocket(PORT);  // create socket and bind it
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            socket.joinGroup(group);
            while (true) {
                String message = new ConnectionFunctions().receiveUdpPacket();
                HashMap<String, String> map = new ConnectionFunctions().string2HashMap(message);
                //treatLogin(map);
                treatRegister(map);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            socket.close();
        }
    }


    /**
     * create the hashmap the response by multicast if the user exists or dont
     * @param username
     * @param exists
     * @return
     */
    //aux para converter em hashmap a resposta do multicast se existe o utilziador ou nao
    private HashMap<String, String> aux(String username, String exists) {
        HashMap<String, String> hmap = new HashMap<String, String>();
        hmap.put("type", "checkIfExists");
        hmap.put("username", username);
        hmap.put("condition", exists);
        return hmap;
    }

    //aux para converter em hashmap a resposta do multicast se existe o utilziador ou nao
    private HashMap<String, String> auxForArray(String username, String password, String exists) {
        HashMap<String, String> hmap = new HashMap<String, String>();
        hmap.put("type", "checkIfExists");
        hmap.put("username", username);
        hmap.put("password", password);
        hmap.put("condition", exists);
        return hmap;
    }

    private void treatLogin(HashMap<String, String> map) throws SQLException {
        String username = map.get("username");
        String password = map.get("password");
        Connection c = new SQL().enterDatabase("infomusic");
        String [] user = new SQL().selectUserAndGetPassword(c, "USERS", username);
        String return_username = user[0];
        String return_pass = user[1];
        if(return_username != null && return_pass.equals(password)) {
            new ConnectionFunctions().sendUdpPacket(auxForArray(return_username, return_pass, "true"));
        }
        else {
            new ConnectionFunctions().sendUdpPacket(auxForArray(return_username,return_pass, "false"));
        }
    }

    private void treatRegister(HashMap<String, String> map) throws SQLException {
        String username = map.get("username");
        Connection c = new SQL().enterDatabase("infomusic");
        String user = new SQL().selectUser(c, "USERS", username);
        if(user == null) {
            new ConnectionFunctions().sendUdpPacket(aux(username, "false"));
            String[] arr = {"user1,pass1", "'"+username+"','"+map.get("password")+"'"};
            new SQL().addValuesToTable(c, "USERS", arr);
        }
        else {
            new ConnectionFunctions().sendUdpPacket(aux(username, "true"));
        }
    }
}
