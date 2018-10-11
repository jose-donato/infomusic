package com.company;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class RMIClient {
    public static void main(String[] args) throws MalformedURLException, RemoteException, NotBoundException {
        Interface i = (Interface) Naming.lookup("infoMusicRegistry");
        System.out.println(i.add(1,2));
    }
}
