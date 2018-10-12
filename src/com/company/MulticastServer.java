package com.company;

import java.net.MulticastSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;

public class MulticastServer extends Thread {
    private String MULTICAST_ADDRESS = "224.0.224.0";
    private int PORT = 4321;
    private long SLEEP_TIME = 5000;


    public static void main(String[] args) {
        MulticastServer server = new MulticastServer();
        server.start();
    }

    public MulticastServer() {
        super("Server " + (long) (Math.random() * 1000));
    }

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
                byte[] buffer = new byte[256];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                System.out.println("Received packet from " + packet.getAddress().getHostAddress() + ":" + packet.getPort() + " with message:");
                String message = new String(packet.getData(), 0, packet.getLength());
                System.out.println(message);
                message = message.substring(1, message.length()-1);           //remove curly brackets
                String[] keyValuePairs = message.split(",");              //split the string to creat key-value pairs
                HashMap<String,String> map = new HashMap<String,String>();

                for(String pair : keyValuePairs)                        //iterate over the pairs
                {
                    String[] entry = pair.split("=");                   //split the pairs to get key and value
                    map.put(entry[0].trim(), entry[1].trim());          //add them to the hashmap and trim whitespaces
                }

                String username = map.get("username");
                System.out.println(username);
                Connection c = new SQL().enterDatabase("infomusic");
                Statement s = c.createStatement();
                ResultSet rs = s.executeQuery("SELECT * FROM USERS WHERE user1='"+username+"'");
                System.out.println(rs);
                String lastName = null;
                while (rs.next()) {
                    lastName = rs.getString("user1");
                }
                System.out.println(lastName);
                try {

                    socket = new MulticastSocket();  // create socket without binding it (only for sending)
                    buffer = aux(lastName, "true").toString().getBytes();

                    group = InetAddress.getByName(MULTICAST_ADDRESS);
                    packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                    socket.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    socket.close();
                }

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


    //aux para converter em hashmap a resposta do multicast se existe o utilziador ou nao
    public HashMap<String, String> aux(String username, String exists) {
        HashMap<String, String> hmap = new HashMap<String, String>();
        hmap.put("type", "checkIfExists");
        hmap.put("username", username);
        hmap.put("condition", exists);
        return hmap;
    }
}
