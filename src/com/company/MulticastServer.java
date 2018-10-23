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


    public static void main(String[] args) throws IOException, InterruptedException, SQLException, ClassNotFoundException {
        //SQL.initialConfig();
        //Connection c = SQL.enterDatabase("infomusic");
        //SQL.shareMusicWithUser(c, 1, "hugo");
        //System.out.println(SQL.albumData(c, 1));
        //System.out.println(SQL.artistData(c, 1));

        //SQL.printAllTable(c, "albums");
        //SQL.enterFileInTable(c, "", "","C:\\Users\\zmcdo\\Downloads\\rhc.png");
        //SQL.getFileFromTable(c);
        //String[] a = {"username,password,isAdmin", "'josedonato','123123', true"};
        //SQL.addValuesToTable(c, "users", a);
        //SQL.grantAdminToUser(c, "hugobrinkaaa");
        MulticastServer server = new MulticastServer();
        server.start();
        ConnectionFunctions.receiveMusicMulticastServer();

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
                String message = ConnectionFunctions.receiveUdpPacket();
                HashMap<String, String> map = ConnectionFunctions.string2HashMap(message);
                //treat the message type and create one thread per message received
                new Threads(map);
                //treatLogin(map);
                //treatRegister(map);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }




}
