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
        try {
            Connection c = new SQL().enterDatabase("infomusic");
            /*HashMap<String, String> arr = new HashMap<String, String>();
            arr.put("user1", "TEXT PRIMARY KEY");
            arr.put("pass1", "TEXT");

            new SQL().createTable(c, "users", arr);*/

            //String[] a = {"user1,pass1", "'josedonato','chupemmaostomates'"};
            //new SQL().addValuesToTable(c, "users", a);
            Statement s = c.createStatement();
            ResultSet rs = s.executeQuery("SELECT * FROM USERS WHERE user1='hugobrink'");
            while (rs.next()) {
                String lastName = rs.getString("user1");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
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
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }
}
