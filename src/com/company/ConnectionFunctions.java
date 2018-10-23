package com.company;

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

    public static boolean uploadMusicTCP(String musicLocation, boolean fromSQL, int musicID, String username) throws IOException {
        int serverPort = 13267;
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        OutputStream os = null;
        ServerSocket servsock = null;
        Socket sock = null;
        boolean musicUploaded = false;
        try {
            servsock = new ServerSocket(serverPort);
            while (!musicUploaded) {
                System.out.println("d: waiting...");
                try {
                    sock = servsock.accept();
                    System.out.println("d: accepted connection : " + sock);
                    // send file
                    if(fromSQL) {
                        byte[] mybytearray = SQL.getArrayInTable(SQL.enterDatabase("infomusic"), "cloudmusics", musicID, username);
                    }
                    else {

                    }
                    File myFile = new File (musicLocation);
                    byte [] mybytearray  = new byte [(int)myFile.length()];
                    fis = new FileInputStream(myFile);
                    bis = new BufferedInputStream(fis);
                    bis.read(mybytearray,0,mybytearray.length);
                    os = sock.getOutputStream();
                    System.out.println("d: sending " + musicLocation + "(" + mybytearray.length + " bytes)");
                    os.write(mybytearray,0,mybytearray.length);
                    os.flush();
                    System.out.println("d: done.");
                    musicUploaded = true;
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    if (bis != null) bis.close();
                    if (os != null) os.close();
                    if (sock!=null) sock.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (servsock != null) servsock.close();
        }

        return false;
    }
    public static boolean downloadMusicTCP(String musicDestination, int musicID, String username, boolean toSQL) throws IOException, InterruptedException {
        int serverPort = 13267;
        int FILE_SIZE = 6000000;
        boolean musicDownloaded = false;
        int bytesRead;
        int current = 0;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        Socket s = null;
        while(!musicDownloaded) {
            if (RMIServer.MulticastTCPAddress != null) {
                try {
                    //sock = new Socket("127.0.0.1", serverSocket);
                    s = new Socket(RMIServer.MulticastTCPAddress, serverPort);
                    System.out.println("d: connecting...");

                    // receive file
                    byte[] mybytearray = new byte[FILE_SIZE];
                    InputStream is = s.getInputStream();
                    if(toSQL) {
                        bytesRead = is.read(mybytearray, 0, mybytearray.length);
                        current = bytesRead;
                        do {
                            bytesRead = is.read(mybytearray, current, (mybytearray.length - current));
                            if (bytesRead >= 0) current += bytesRead;
                        } while (bytesRead > -1);
                        SQL.enterArrayInTable(SQL.enterDatabase("infomusic"), "cloudmusics", mybytearray, musicID, username);
                    }
                    else {
                        fos = new FileOutputStream(musicDestination);
                        bos = new BufferedOutputStream(fos);
                        bytesRead = is.read(mybytearray, 0, mybytearray.length);
                        current = bytesRead;

                        do {
                            bytesRead = is.read(mybytearray, current, (mybytearray.length - current));
                            if (bytesRead >= 0) current += bytesRead;
                        } while (bytesRead > -1);

                        bos.write(mybytearray, 0, current);
                        bos.flush();
                    }
                    System.out.println("d: file " + musicDestination
                            + " downloaded (" + current + " bytes read)");
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    if (fos != null) fos.close();
                    if (bos != null) bos.close();
                    if (s != null) s.close();
                    musicDownloaded = true;
                }
            }
            else {
                System.out.println("d: no rmiserver.tcpaddress yet");
            }
            Thread.sleep(1000);
        }
        return false;
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

