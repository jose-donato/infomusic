package com.company;

import java.net.MulticastSocket;
import java.net.InetAddress;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;

/**
 * multicast server class
 * starts by creating one database and all tables neccessaries to the program to run
 * has one thread that receives udp datagrams with some request and assigns one thread to each request (Threads class)
 * other thread waiting for tcp connection if any user wants to send some files to the server
 */
public class MulticastServer extends Thread {
    private String MULTICAST_ADDRESS = "224.0.224.0";
    private int PORT = 4321;
    private static long number = (long) (Math.random() * 100);

    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
        SQL.initialConfig(number);

        MulticastServer server = new MulticastServer();
        server.start();

        //waiting for tcp connection
        ConnectionFunctions.receiveMusicMulticastServer();
    }

    /**
     * class constructor
     */
    public MulticastServer() {
        super("server ready");
        System.out.println("server "+number+" ready.");
    }

    /**
     * thread to receive datagram packet and assign one thread to treat request
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
                String message = ConnectionFunctions.receiveUdpPacket();
                HashMap<String, String> map = ConnectionFunctions.string2HashMap(message);
                //treat the message type and create one thread per message received
                new Threads(map);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }




}
