package com.company;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * connectionfunctions class
 * has static functions that can will be called by multicast or rmi servers to communicate between or functions convert data to be possible to be sent to each other
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

    /**
     * create socket for server to establish tcp connection
     * @param port to bind the socket (multicast server: 53287 for receiving files, 53288 for sending files)
     * @return server socket already created and connected to port supplied
     * @throws IOException
     */
    public static ServerSocket establishConnectionServer(int port) throws IOException {
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.setReuseAddress(true);
        serverSocket.bind(new InetSocketAddress(port));
        return serverSocket;
    }

    /**
     * create a socket for client to establish tcp connection
     * @param TCPAddress address to connect to (multicast address)
     * @param port to bind the socket
     * @return client socket already created with port and tcp address
     * @throws IOException
     */
    public static Socket establishConnectionClient(String TCPAddress, int port) throws IOException {
        Socket clientSocket = new Socket(TCPAddress, port);
        return clientSocket;
    }


    /**
     * send file from rmi client (upload music to multicast server for example)
     * @param filePath file location in local computer
     * @param musicID id from the database to associate a file to one music
     * @param username of the user that sends the music to the server
     * @param TCPAddress used to connect to the server (the multicast address)
     * @throws IOException
     */
    public static void sendMusicFromRMIClient(String filePath, int musicID, String username, String TCPAddress) throws IOException {
        File file = new File(filePath);
        byte[] musicFileByteArray = FileUtils.readFileToByteArray(file);
        HashMap<String, String> hmap = new HashMap<>();
        hmap.put("type", "sendMusicFromRMIClient");
        hmap.put("musicFile", toString(musicFileByteArray));
        hmap.put("musicID", musicID+"");
        hmap.put("username", username);
        byte[] array = hashToByte(hmap);
        sendBytes(array,0, array.length, establishConnectionClient(TCPAddress, 53287));
    }

    /**
     * receive file by multicast server, writes it to the database with its musicID and username (upload from rmi client)
     * @throws IOException
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public static void receiveMusicMulticastServer() throws IOException, SQLException, ClassNotFoundException {
        ServerSocket serverSocket = establishConnectionServer(53287);
        Socket socket = serverSocket.accept();
        byte[] array = readBytes(socket);
        HashMap<String, String> hmap = byteToHash(array);
        String username = hmap.get("username");
        int musicID = Integer.parseInt(hmap.get("musicID"));
        SQL.enterArrayInTable("cloudmusics", array, musicID, username);
    }

    /**
     * send file from multicast server (to rmi client, rmi client downloads a file for example)
     * @param musicID that rmiclient wants to download, to grab from the database
     * @param username of the rmiclient, to grab from the database
     * @throws IOException
     * @throws SQLException
     */
    public static void sendMusicFromMulticastServer(int musicID, String username) throws IOException, SQLException {
        ServerSocket serverSocket = establishConnectionServer(53288);
        Socket socket = serverSocket.accept();
        byte[] array = SQL.getArrayInTable("cloudmusics", musicID, username);
        sendBytes(array, 0, array.length, socket);
    }

    /**
     * receive file by rmi client (download file from server, for example)
     * @param path output file location, where the user wants to save the file
     * @param TCPAddress server address to setup the connection
     * @throws IOException
     */
    public static void receiveMusicRMIClient(String path, String TCPAddress) throws IOException {
        byte[] array = readBytes(establishConnectionClient(TCPAddress, 53288));
        FileOutputStream fos = new FileOutputStream(path);
        fos.write(array);
        fos.close();
    }


    /**
     * send bytes (file converted to bytes to be send by tcp connection) to one socket
     * @param myByteArray file converted in bytes
     * @param start the beggining of the array, normally 0
     * @param len the length of the array, normally myByteArray.length
     * @param socket where you want to send the file
     * @throws IOException
     */
    public static void sendBytes(byte[] myByteArray, int start, int len, Socket socket) throws IOException {
        if (len < 0)
            throw new IllegalArgumentException("negative length not allowed");
        if (start < 0 || start >= myByteArray.length)
            throw new IndexOutOfBoundsException("out of bounds: " + start);

        OutputStream out = socket.getOutputStream();
        DataOutputStream dos = new DataOutputStream(out);

        dos.writeInt(len);
        if (len > 0) {
            dos.write(myByteArray, start, len);
        }
    }

    /**
     * receive bytes from sendBytes function
     * @param socket receives the socket of the connection
     * @return the data received from the socket in bytes array
     * @throws IOException
     */
    public static byte[] readBytes(Socket socket) throws IOException {
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
     * convert array of bytes in string
     * @param bytes array to convert
     * @return string converted
     */
    public static String toString(byte[] bytes) {
        return new String(bytes);
    }


    /**
     * convert hashmap in array of bytes
     * @param map hashmap to convert
     * @return array of bytes converted
     * @throws IOException
     */
    public static byte[] hashToByte(HashMap<String, String> map) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(byteStream));
        oos.writeObject(map);
        oos.close();
        byte[] array = byteStream.toByteArray();
        return array;
    }

    /**
     * convert array of bytes in hashmap
     * @param array bytes array to convert
     * @return hashmap converted
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static HashMap<String, String> byteToHash(byte[] array) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteStream = new ByteArrayInputStream(array);
        ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(byteStream));
        HashMap<String, String> map = (HashMap<String, String>) ois.readObject();
        ois.close();
        return map;
    }
}

