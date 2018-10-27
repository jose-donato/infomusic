package com.company;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.*;
import java.nio.file.Path;
import java.sql.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
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
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.setReuseAddress(true);
        serverSocket.bind(new InetSocketAddress(53257));
        return serverSocket;
    }
    public static Socket establishConnectionClient(String TCPAddress) throws IOException {
        Socket clientSocket = new Socket(TCPAddress, 53257);
        return clientSocket;
    }

    public static void sendMusicFromRMIClient(String filePath, int musicID, String username, String TCPAddress) throws IOException {
        File file = new File(filePath);
        byte[] musicFileByteArray = FileUtils.readFileToByteArray(file);
        HashMap<String, String> hmap = new HashMap<>();
        hmap.put("type", "sendMusicFromRMIClient");
        hmap.put("musicFile", toString(musicFileByteArray));
        hmap.put("musicID", musicID+"");
        hmap.put("username", username);
        byte[] array = hashToByte(hmap);
        sendBytes(array,0, array.length, establishConnectionClient(TCPAddress));
    }

    public static void receiveMusicMulticastServer() throws IOException, SQLException, ClassNotFoundException {
        ServerSocket serverSocket = establishConnectionServer();
        Socket socket = serverSocket.accept();
        byte[] array = readBytes(socket);
        HashMap<String, String> hmap = byteToHash(array);
        String username = hmap.get("username");
        int musicID = Integer.parseInt(hmap.get("musicID"));
        SQL.enterArrayInTable("cloudmusics", array, musicID, username);
    }

    public static void sendMusicFromMulticastServer(int musicID, String username) throws IOException, SQLException {
        ServerSocket serverSocket = establishConnectionServer();
        Socket socket = serverSocket.accept();
        byte[] array = SQL.getArrayInTable("cloudmusics", musicID, username);
        sendBytes(array, 0, array.length, socket);
    }

    public static void receiveMusicRMIClient(String path, String TCPAddress) throws IOException {
        byte[] array = readBytes(establishConnectionClient(TCPAddress));
        FileOutputStream fos = new FileOutputStream(path);
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

    public static byte[] intToByteArray(int value) {
        return new byte[] {
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)value};
    }
    public static int fromByteArray(byte[] bytes) {
        return bytes[0] << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
    }
    public static byte[] concatenateByteArrays(byte[] a, byte[] b) {
        byte[] result = new byte[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    public static String toString(byte[] bytes) {
        return new String(bytes);
    }

    public static byte[] hashToByte(HashMap<String, String> map) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(byteStream));
        oos.writeObject(map);
        oos.close();
        byte[] array = byteStream.toByteArray();
        return array;
    }
    public static HashMap<String, String> byteToHash(byte[] array) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteStream = new ByteArrayInputStream(array);
        ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(byteStream));
        HashMap<String, String> map = (HashMap<String, String>) ois.readObject();
        ois.close();
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

