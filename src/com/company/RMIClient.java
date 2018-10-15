package com.company;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;




import java.util.Scanner;

/**
 *
 */
public class RMIClient {
    public static void main(String[] args) throws MalformedURLException, RemoteException, NotBoundException {
        Interface i = (Interface) Naming.lookup("infoMusicRegistry");
        boolean loginSucess = false;
        while(!loginSucess) {
            System.out.println("menu: (type one of the options)");
            System.out.println("1. login");
            System.out.println("2. register");
            System.out.println("3. exit");

            Scanner keyboard = new Scanner(System.in);
            int choice = keyboard.nextInt();

            switch(choice) {
                case 1:
                    System.out.println("type your username:");
                    keyboard = new Scanner(System.in);
                    String username = keyboard.nextLine();
                    System.out.println("type your password:");
                    keyboard = new Scanner(System.in);
                    String password = keyboard.nextLine();

                    //if login succeeds
                    if (i.loginOrRegister(username, password) == 1) {
                        //tbc
                        System.out.println("login successful");
                        loginSucess = true;
                    } else {
                        //tbc
                        System.out.println("wrong credentials, make sure to regist first!");
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
                    if(i.loginOrRegister(username, password) == 1) {
                        System.out.println(username+ " registed.");
                    }
                    else {
                        System.out.println("someone already has that username!");
                    }
                    break;
                case 3:
                    System.out.println("exiting...");
                    System.exit(0);
                    break;
            }
        }

       System.out.println("welcome username! what you want to do?");
    }
}
