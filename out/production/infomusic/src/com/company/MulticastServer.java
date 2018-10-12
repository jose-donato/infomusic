package com.company;

import java.net.MulticastSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

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
        Connection c = null;
        try {
            c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/", "postgres", "postgres");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Statement statement = null;
        try {
            statement = c.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            statement.executeUpdate("CREATE DATABASE infomusic");
        } catch (SQLException e) {
            if (e.getErrorCode() == 0) {
                // Database already exists error
                try {
                    c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/infomusic", "postgres", "postgres");
                    statement = c.createStatement();
                    String sql = "CREATE TABLE USERS " +
                            "(USERNAME TEXT PRIMARY KEY     NOT NULL," +
                            " PASSWORD           TEXT    NOT NULL)";
                    try {
                        statement.executeUpdate(sql);
                    }
                    catch(SQLException e2) {
                        sql = "INSERT INTO USERS (USERNAME, PASSWORD) "
                                + "VALUES ('user1', 'pass1');";
                        statement.executeUpdate(sql);
                    }
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            } else {
                // Some other problems, e.g. Server down, no permission, etc
                e.printStackTrace();
            }
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
