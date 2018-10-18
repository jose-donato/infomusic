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
        boolean enter = true;
        while(enter){
            if (enterTheProgram(i)){
                enter = menu();
            }
            else{
                System.out.println("thank you. we hope to see you again!");
                return;
            }
        }
        System.out.println("thank you. we hope to see you again!");
    }

    public static boolean enterTheProgram(Interface i) throws RemoteException, MalformedURLException, NotBoundException {
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
                        return true;
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
                    return false;
                default:
                    System.out.println("please enter valid option");
            }
        }
        return false;
    }
    public static boolean menu(){
        System.out.println("welcome username! what you want to do?");

        while (true) {
            System.out.println("menu: (type one of the options)");

            System.out.println("????. grant privileges to other user"); //Só para admin
            System.out.println("1. search for songs");
            System.out.println("2. search for some detail information about an artist or a specific album ");
            System.out.println("3. write a review to an album");
            System.out.println("4. upload/download a song");
            System.out.println("5. manage artist, album and songs"); //Só pode aparecer ao admin
            System.out.println("6. logout");
            System.out.println("7. exit");
            //System.out.println("7. Create a DataBase for Songs"); //para apagar, apenas para testar criar uma base de dados
            //System.out.println("8. Enter a song in the DataBase"); //para apagar, apenas para testar inserir uma musica base de dados


            Scanner keyboard = new Scanner(System.in);
            int choice = keyboard.nextInt();

            switch (choice) {
                case 1:
                    // Search for Songs;
                    break;
                case 2:
                    //Search for some detail information about an artist
                    // a specific album
                    break;
                case 3:
                    //write a review to an album
                    break;
                case 4:
                    /*HashMap<String, String> map = new HashMap<>();
                    map.put("type", "upload");
                    String s;
                    Scanner sc = new Scanner(System.in);
                    System.out.println("enter the path of the music you would like upload");
                    s = sc.nextLine();
                    // O user tem de meter da maneira correta (com \\)
                    map.put("filePath", s);
                    new Threads(map);*/
                    break;
                case 5:
                    //manage data
                    if(true) {//check if user is admin
                        System.out.println("what operation you want to do? (type one of the options)");
                        System.out.println("1. add song");
                        System.out.println("2. add album");
                        System.out.println("3. add artist");
                        System.out.println("4. edit song");
                        System.out.println("5. edit album");
                        System.out.println("6. edit artist");
                        System.out.println("7. remove song");
                        System.out.println("8. remove album");
                        System.out.println("9. remove artist");
                        keyboard = new Scanner(System.in);
                        int choice2 = keyboard.nextInt();
                        switch(choice2) {
                            case 1:
                                System.out.println("music's name: ");
                                keyboard = new Scanner(System.in);
                                String musicName = keyboard.nextLine();
                                System.out.println("music's name: ");
                                keyboard = new Scanner(System.in);
                                String genre = keyboard.nextLine();
                                /*System.out.println("music's lyrics: ");
                                keyboard = new Scanner(System.in);
                                String lyrics = keyboard.nextLine();*/
                                System.out.println("music's duration: (in seconds) ");
                                keyboard = new Scanner(System.in);
                                int duration = keyboard.nextInt();

                                break;
                            case 2:

                                break;
                            case 3:

                                break;
                        }
                    }
                case 6:
                    //logout
                    return true;
                case 7:
                    //exit
                    return false;
                /*case 7:
                    map = new HashMap<>();
                    map.put("type", "CreateDataBaseforsong");
                    new Threads(map);
                    break;
                case 8:
                    map = new HashMap<>();
                    map.put("type","uploadbrink");
                    new Threads(map);
                    break;*/
                default:
                    System.out.println("please enter valid option");
            }


        }

    }
}
