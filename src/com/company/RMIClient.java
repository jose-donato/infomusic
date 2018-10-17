package com.company;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;


import java.util.HashMap;
import java.util.Scanner;

/**
 *
 */
public class RMIClient {

    public static void main(String[] args) throws IOException, NotBoundException {
        Interface i = (Interface) Naming.lookup("infoMusicRegistry");
        boolean loginSucess = false;
        while (!loginSucess) {
            System.out.println("menu: (type one of the options)");
            System.out.println("1. login");
            System.out.println("2. register");
            System.out.println("3. exit");

            Scanner keyboard = new Scanner(System.in);
            int choice = keyboard.nextInt();

            switch (choice) {
                case 1:
                    System.out.println("type your username:");
                    keyboard = new Scanner(System.in);
                    String username = keyboard.nextLine();
                    System.out.println("type your password:");
                    keyboard = new Scanner(System.in);
                    String password = keyboard.nextLine();

                    //if login succeeds
                    if (i.loginOrRegister(username, password, false)) {
                        //tbc
                        System.out.println("login successful");
                        loginSucess = true;
                    } else {
                        //tbc
                        System.out.println("wrong credentials, make sure to register first!");
                    }
                    break;
                case 2:
                    System.out.println("type your username:");
                    keyboard = new Scanner(System.in);
                    username = keyboard.nextLine();
                    //check if username exists in database
                    System.out.println("type your password:");
                    keyboard = new Scanner(System.in);
                    password = keyboard.nextLine();
                    //check if password is certain by asking two times
                    if (i.loginOrRegister(username, password, true)) {
                        System.out.println(username + " registed. please login now");

                    } else {
                        System.out.println("someone already has that username!");
                    }
                    break;
                case 3:
                    System.out.println("exiting...");
                    System.exit(0);
                    break;
                default:
                    System.out.println("please enter valid option");
            }
        }

        System.out.println("welcome username! what you want to do?");
        boolean running = true;
        while (running) {
            System.out.println("menu: (type one of the options)");
            System.out.println("????. manage artist, album and songs"); //Só pode aparecer ao admin
            System.out.println("????. grant privileges to other user"); //Só para admin
            System.out.println("1. search for songs");
            System.out.println("2. search for some detail information about an artist or a specific album ");
            System.out.println("3. write a review to an album");
            System.out.println("4. upload/download a song");
            System.out.println("5. logout");
            System.out.println("6. exit");
            System.out.println("7. Create a DataBase for Songs"); //para apagar, apenas para testar criar uma base de dados
            System.out.println("8. Enter a song in the DataBase"); //para apagar, apenas para testar inserir uma musica base de dados




            Scanner keyboard = new Scanner(System.in);
            int choice = keyboard.nextInt();

            switch (choice) {
                case 4:
                    HashMap<String, String> map = new HashMap<>();
                    map.put("type", "upload");
                    new Threads(map);
                    break;
                case 7:
                    map = new HashMap<>();
                    map.put("type", "CreateDataBaseforsong");
                    new Threads(map);
                    break;
                case 8:
                    map = new HashMap<>();
                    map.put("type","uploadbrink");
                    new Threads(map);
                    break;
                default:
                    System.out.println("please enter valid option");
            }
        }
    }
}
