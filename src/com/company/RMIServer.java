package com.company;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Scanner;

public class RMIServer extends UnicastRemoteObject implements Interface {
    private static final long serialVersionUID = 1L;

    protected RMIServer() throws RemoteException {
        super();
    }


    public int login(String username, String password) {
        String MULTICAST_ADDRESS = "224.0.224.0";
        int PORT = 4321;
        MulticastSocket socket = null;

        //Vai enviar a informação do cliente ao multicast para saber se este é
        try {

            socket = new MulticastSocket();  // create socket without binding it (only for sending)
            byte[] buffer = auxLogin(username, password).toString().getBytes();

            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //socket.close();
        }
    // Vai receber informação do Multicast para saber se existe um Username
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



        if(username.equals("ola") && password.equals("adeus")) {
            return 1;
        }
        else {
            return 0;
        }
    }

    public HashMap<String, String> auxLogin(String username, String password) {
        HashMap<String, String> hmap = new HashMap<String, String>();
        hmap.put("type", "login");
        hmap.put("username", username);
        hmap.put("password", password);
        return hmap;
    }

    public int register(String username, String password) {
        return 1;
    }


    /**
     * @param args
     * @throws RemoteException
     */
    public static void main(String[] args) throws RemoteException {
        Interface i = new RMIServer();
        LocateRegistry.createRegistry(1099).rebind("infoMusicRegistry", i);
        System.out.println("Server ready...");
    }

}

