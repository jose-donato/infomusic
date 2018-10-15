package com.company;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.HashMap;

/**
 *
 */
public class ConnectionFunctions {
    private String MULTICAST_ADDRESS = "224.0.224.0";
    private int PORT = 4321;
    private MulticastSocket socket = null;

    /**
     * send udp datagrampacket
     * @param map hashmap that we want to send, will be converted in string to be possible to go to the packet
     * @return 1 in case of success, 0 otherwise
     */
    public int sendUdpPacket(HashMap<String, String> map) {
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
    public String receiveUdpPacket() {
        String message = null;
        try {
            socket = new MulticastSocket(PORT);  // create socket and bind it
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            socket.joinGroup(group);
            byte[] buffer = new byte[256];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
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
     * Convert string (received by udp datagrampacket) to hashmap
     * @param string to convert to hashmap
     * @return the hashmap converted
     */
    public HashMap<String, String> string2HashMap(String string) {
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

