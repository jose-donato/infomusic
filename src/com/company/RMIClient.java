package com.company;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;




import java.util.Scanner;

public class RMIClient {
    public static void main(String[] args) throws MalformedURLException, RemoteException, NotBoundException {
        Interface i = (Interface) Naming.lookup("infoMusicRegistry");
        System.out.println("menu: (type one of the options)");
        System.out.println("1. register/login");
        System.out.println("2. exit");

        Scanner keyboard = new Scanner(System.in);
        int choice = keyboard.nextInt();
        switch(choice) {
            case 1:
                System.out.println("username");
                break;
            case 2:
                System.out.println("exiting...");
                System.exit(0);
                break;
        }

    }
}
