package com.company;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.util.HashMap;

/**
 *
 */
public final class ConnectionFunctions {
    private static String MULTICAST_ADDRESS = "224.0.224.0";
    private static int PORT = 4321;
    private static MulticastSocket socket = null;

    /**
     * send udp datagrampacket
     * @param map hashmap that we want to send, will be converted in string to be possible to go to the packet
     * @return 1 in case of success, 0 otherwise
     */
    public static int sendUdpPacket(HashMap<String, String> map) {
        try {
            socket = new MulticastSocket();  // create socket without binding it (only for sending)
            byte[] buffer = map.toString().getBytes();
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
            socket.send(packet);
            System.out.println("d: sent udp with message: "+map.toString());
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        } finally {
            socket.close();
            return 1;
        }
    }

    /**
     * receive udp datagrampacket
     * @return the message received (string) in success, null otherwise
     */
    public static String receiveUdpPacket() {
        String message = null;
        try {
            socket = new MulticastSocket(PORT);  // create socket and bind it
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            socket.joinGroup(group);
            byte[] buffer = new byte[256];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            RMIServer.MulticastTCPAddress = packet.getAddress().getHostAddress();
            System.out.println("d: received packet from " + packet.getAddress().getHostAddress() + ":" + packet.getPort() + " with message:");
            message = new String(packet.getData(), 0, packet.getLength());
            System.out.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
        return message;
    }

    public static ServerSocket establishConnectionServer() throws IOException {
        ServerSocket serverSocket = new ServerSocket(6789);
        return serverSocket;
    }
    public static Socket establishConnectionClient() throws IOException {
        Socket clientSocket = new Socket("localhost", 6789);
        return clientSocket;
    }

    public static void sendMusicFromRMIClient() throws IOException {
        File file = new File("D:\\Downloads\\mac.mp3");
        byte[] array = FileUtils.readFileToByteArray(file);
        sendBytes(array,0, array.length, establishConnectionClient());
    }

    public static void receiveMusicMulticastServer() throws IOException, SQLException {
        ServerSocket serverSocket = establishConnectionServer();
        Socket socket = serverSocket.accept();
        byte[] array = readBytes(socket);
        SQL.enterArrayInTable(SQL.enterDatabase("infomusic"), "cloudmusics", array, 1, "jose");
    }

    public static void sendMusicFromMulticastServer() throws IOException, SQLException {
        ServerSocket serverSocket = establishConnectionServer();
        Socket socket = serverSocket.accept();
        byte[] array = SQL.getArrayInTable(SQL.enterDatabase("infomusic"), "cloudmusics", 1, "jose");
        sendBytes(array, 0, array.length, socket);
    }

    public static void receiveMusicRMIClient() throws IOException {
        byte[] array = readBytes(establishConnectionClient());
        FileOutputStream fos = new FileOutputStream("D:\\Downloads\\mac2.mp3");
        fos.write(array);
        fos.close();
    }


    public static void sendBytes(byte[] myByteArray, int start, int len, Socket socket) throws IOException {
        if (len < 0)
            throw new IllegalArgumentException("Negative length not allowed");
        if (start < 0 || start >= myByteArray.length)
            throw new IndexOutOfBoundsException("Out of bounds: " + start);
        // Other checks if needed.

        // May be better to save the streams in the support class;
        // just like the socket variable.
        OutputStream out = socket.getOutputStream();
        DataOutputStream dos = new DataOutputStream(out);

        dos.writeInt(len);
        if (len > 0) {
            dos.write(myByteArray, start, len);
        }
    }
    public static byte[] readBytes(Socket socket) throws IOException {
        // Again, probably better to store these objects references in the support class
        InputStream in = socket.getInputStream();
        DataInputStream dis = new DataInputStream(in);

        int len = dis.readInt();
        byte[] data = new byte[len];
        if (len > 0) {
            dis.readFully(data);
        }
        return data;
    }

    /**
     * Convert string (received by udp datagrampacket) to hashmap
     * @param string to convert to hashmap
     * @return the hashmap converted
     */
    public static HashMap<String, String> string2HashMap(String string) {
        string = string.substring(1, string.length()-1);           //remove curly brackets
        String[] keyValuePairs = string.split(",");              //split the string to creat key-value pairs
        HashMap<String,String> map = new HashMap<String,String>();

        for(String pair : keyValuePairs)                        //iterate over the pairs
        {
            String[] entry = pair.split("=");                   //split the pairs to get key and value
            map.put(entry[0].trim(), entry[1].trim());          //add them to the hashmap and trim whitespaces
        }
        return map;
    }

    /**
     *
     * @param username
     * @param password
     * @param exists
     * @return
     */
    //aux para converter em hashmap com o Username e a Password
    public HashMap<String, String> AuxForArray(String username,String password, String exists) {
        HashMap<String, String> hmap = new HashMap<String, String>();
        hmap.put("type", "checkIfExists");
        hmap.put("username", username);
        hmap.put("password", password);
        hmap.put("condition", exists);
        return hmap;
    }

}

