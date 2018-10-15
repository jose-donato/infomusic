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
        /*try {
            Connection c = new SQL().enterDatabase("infomusic");
            /*HashMap<String, String> arr = new HashMap<String, String>();
            arr.put("user1", "TEXT PRIMARY KEY");
            arr.put("pass1", "TEXT");

            new SQL().createTable(c, "users", arr);*/

            //String[] a = {"user1,pass1", "'josedonato','chupemmaostomates'"};
            //new SQL().addValuesToTable(c, "users", a);
        /*  Statement s = c.createStatement();
            ResultSet rs = s.executeQuery("SELECT * FROM USERS WHERE user1='hugobrink'");
            while (rs.next()) {
                String lastName = rs.getString("user1");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }*/
        MulticastSocket socket = null;
        try {
            socket = new MulticastSocket(PORT);  // create socket and bind it
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            socket.joinGroup(group);
            while (true) {
                /*
                //begining of treatment for login
                //Recebe mensagens
                String message = new ConnectionFunctions().receiveUdpPacket();
                HashMap<String, String> map = new ConnectionFunctions().string2HashMap(message);

                String username = map.get("username");
                String password = map.get("password");
                Connection c = new SQL().enterDatabase("infomusic");

                //String user = new SQL().selectUser(c, "USERS", username);
                //Usar para a password
                String [] user = new SQL().selectUserAndGetPassword(c, "USERS", username);
                String return_username = user[0];
                String return_pass = user[1];
                if(return_username != null && return_pass.equals(password)) {
                    new ConnectionFunctions().sendUdpPacket(auxForArray(return_username, return_pass, "true"));

                }
                else {
                    new ConnectionFunctions().sendUdpPacket(auxForArray(return_username,return_pass, "false"));
                }
                //end of treatment for login
                */
                //begining of treatment for register
                String message = new ConnectionFunctions().receiveUdpPacket();
                HashMap<String, String> map = new ConnectionFunctions().string2HashMap(message);
                String username = map.get("username");
                Connection c = new SQL().enterDatabase("infomusic");
                String user = new SQL().selectUser(c, "USERS", username);
                if(user != null) {
                    new ConnectionFunctions().sendUdpPacket(aux(username, "true"));
                    String[] arr = {"user1,pass1", username+','+map.get("password")};
                    new SQL().addValuesToTable(c, "USERS", arr);
                }
                else {
                    new ConnectionFunctions().sendUdpPacket(aux(username, "false"));
                }
                //end of treatment for register

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //vem da linha a seguir ao sout username, (Connection c = new SQL().enterDatabase("infomusic");)
        catch (SQLException e) {
            e.printStackTrace();
        } //acaba aqui
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
    public HashMap<String, String> aux(String username, String exists) {
        HashMap<String, String> hmap = new HashMap<String, String>();
        hmap.put("type", "checkIfExists");
        hmap.put("username", username);
        hmap.put("condition", exists);
        return hmap;
    }

    //aux para converter em hashmap a resposta do multicast se existe o utilziador ou nao
    public HashMap<String, String> auxForArray(String username, String password, String exists) {
        HashMap<String, String> hmap = new HashMap<String, String>();
        hmap.put("type", "checkIfExists");
        hmap.put("username", username);
        hmap.put("password", password);
        hmap.put("condition", exists);
        return hmap;
    }

}
