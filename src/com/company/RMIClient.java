package com.company;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;


import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

/**
 * RMI Client class
 * has the menu interface (in command line) which the user uses to communicate with the server
 * calls methods from the server
 */
public class RMIClient extends UnicastRemoteObject implements InterfaceClient {

    protected RMIClient() throws RemoteException {
        super();
    }

    /**
     * send notification to client by rmi server that admin was granted to him
     * @throws RemoteException
     */
    @Override
    public void notifyAdminGranted() throws RemoteException {
        System.out.println("you now have admin permissions!");
    }

    /**
     * send notification to client by rmi server that one album he changed was modified
     * @throws RemoteException
     */
    @Override
    public void notifyAlbumChanges() throws RemoteException {
        System.out.println("one album you edited was changed!");
    }



    public static void main(String[] args) throws IOException, NotBoundException, InterruptedException {
        boolean bounding = false;
        InterfaceServer i = null;

        while (!bounding) {
            //try to connect to server rmi
            //if cant connect keeps trying until hsuccess
            try {
                i = (InterfaceServer) Naming.lookup("//"+ InetAddress.getLocalHost().getHostAddress()+":1099/infoMusicRegistry");
                //i = (InterfaceServer) Naming.lookup("//192.168.1.188:1099/infoMusicRegistry");
                //i = (InterfaceServer) Naming.lookup("//192.168.1.185:1099/infoMusicRegistry");
                bounding = true;
            } catch (RemoteException e) {
                System.out.println("trying...");
            }
        }

        //initialized to do subscribe to rmi server (so callback can be done after for notifications)
        RMIClient client = new RMIClient();

        boolean enter = true;
        while(enter){
            String user = enterTheProgram(i);
            if (user != null){
                //user logged in and connects to the program, now the menu is display
                enter = menu(i, user, client);
            }
            else{
                System.out.println("thank you. we hope to see you again!");
                return;
            }
        }
        System.out.println("thank you. we hope to see you again!");
    }

    /**
     * receives the server interface to call functions from rmi server
     * @param i server interface
     * @return the username string when the user makes the login successful
     * @throws RemoteException
     */
    public static String enterTheProgram(InterfaceServer i) throws RemoteException, InterruptedException {
        while (true) {
            System.out.println("menu: (type one of the options)\n");
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

                    try {
                        //if login succeeds
                        if (i.loginOrRegister(username, password, false)) {
                            //tbc
                            System.out.println("login successful");
                            return username;
                        } else {
                            //tbc
                            System.out.println("wrong credentials, make sure to register first!");
                        }
                    } catch(java.rmi.ConnectException e) {
                        //to connect to backup rmi server if primary goes down
                        System.out.println("Wait...");
                        Thread.sleep(5000);
                        boolean tryBounding = false;
                        while (!tryBounding) {
                            try {
                                i = (InterfaceServer) Naming.lookup("//"+ InetAddress.getLocalHost().getHostAddress()+":1099/infoMusicRegistry");
                                //i = (InterfaceServer) Naming.lookup("//192.168.1.188:1099/infoMusicRegistry");
                                //i = (InterfaceServer) Naming.lookup("//192.168.1.185:1099/infoMusicRegistry");
                                tryBounding = true;
                            } catch (RemoteException y) {
                                System.out.println("trying...");
                            } catch (NotBoundException e1) {
                                e1.printStackTrace();
                            } catch (MalformedURLException e1) {
                                e1.printStackTrace();
                            } catch (UnknownHostException e1) {
                                e1.printStackTrace();
                            }
                        }
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

                    //sends info about the user (username and password) to rmi server to make the regist. if already exists returns false, otherwise it makes the regist and returns true
                    if (i.loginOrRegister(username, password, true)) {
                        System.out.println(username + " registed. please login now");

                    } else {
                        System.out.println("someone already has that username!");
                    }
                    break;
                case 3:
                    System.out.println("exiting...");
                    System.exit(0);
                default:
                    System.out.println("please enter valid option");
            }
        }
    }

    /**
     * menu with all options that a user can do in the program
     * @param i server interface to call functions from rmi server
     * @param username of the user logged in
     * @param iClient client interface to subscribe to the server for further notifications
     * @return true in case user loggs out, false in case user wants to exit the program
     * @throws IOException
     * @throws InterruptedException
     */
    public static boolean menu(InterfaceServer i, String username, InterfaceClient iClient) throws IOException, InterruptedException {
        System.out.println("\nwelcome " + username + "!\n");

        //subscribe client to server for further notifications
        i.subscribe((InterfaceClient) iClient, username);

        //checks if user has notifications while he was offline in case he is admin
        if (i.checkIfUserIsAdmin(username)) {
            System.out.println(i.checkNotifications(username));
            //clear notifications after being displayed
            i.clearNotifications(username);
        }


        boolean reconnected = true;
        while(reconnected) {
            try {
            while (true) {
                System.out.println("\nmenu: (type one of the options)\n");
                System.out.println("1. list musics");
                System.out.println("2. search for some detail information about an artist or a specific album ");
                System.out.println("3. write a review to an album");
                System.out.println("4. upload/download/share a song");
                System.out.println("5. manage operations (admins only)"); //Só pode aparecer ao admin
                System.out.println("6. logout");
                System.out.println("7. exit");

                Scanner keyboard = new Scanner(System.in);
                int choice = keyboard.nextInt();

                switch (choice) {
                    case 1:
                        //list all musics
                        System.out.println(i.getTable("musics", username));
                        break;
                    case 2:
                        //Search for some detail information about an artist
                        System.out.println("what operation you want to do? (type one of the options)");
                        System.out.println("1. search about an album");
                        System.out.println("2. search about an artist");
                        keyboard = new Scanner(System.in);
                        int choice4 = keyboard.nextInt();
                        switch (choice4) {
                            case 1:
                                //search album
                                System.out.println(i.getTable("albums", username));
                                System.out.println("type the album id u want to know more about");
                                keyboard = new Scanner(System.in);
                                int albumToSearch = keyboard.nextInt();
                                String albumDetail = i.searchDetailAboutAlbum(albumToSearch);
                                if (albumDetail != null) {
                                    System.out.println(albumDetail);
                                }
                                break;
                            case 2:
                                //search artist
                                System.out.println(i.getTable("artists", username));
                                System.out.println("type the artist id u want to know more about");
                                keyboard = new Scanner(System.in);
                                int artistToSearch = keyboard.nextInt();
                                String artistDetail = i.searchDetailAboutArtist(artistToSearch);
                                if (artistDetail != null) {
                                    System.out.println(artistDetail);
                                }
                                break;
                            default:
                                System.out.println("please enter valid option");
                        }
                        break;
                    case 3:
                        //write a review to an album
                        System.out.println(i.getTable("albums", username));
                        System.out.println("select the ID of the album you want to review");
                        keyboard = new Scanner(System.in);
                        int albumToReviewID = keyboard.nextInt();
                        System.out.println("rating: (0 to 10)");
                        keyboard = new Scanner(System.in);
                        int albumRating = keyboard.nextInt();
                        System.out.println("review: (max 300 char)");
                        keyboard = new Scanner(System.in);
                        String albumReview = keyboard.nextLine();
                        if (albumRating < 11 && albumRating >= 0 && albumReview.length() < 301) {
                            i.writeAlbumReview(albumToReviewID, albumRating, albumReview);
                        } else {
                            System.out.println("rating must be between 0 and 10 and review have 300 char limit");
                        }
                        break;
                    case 4:
                        //upload/download/share a song
                        System.out.println("which operation you want to do?");
                        System.out.println("1. upload");
                        System.out.println("2. download");
                        System.out.println("3. share music with user");
                        keyboard = new Scanner(System.in);
                        int choice5 = keyboard.nextInt();
                        switch (choice5) {
                            case 1:
                                //upload
                                //print table with musics to associate one id to the file
                                System.out.println(i.getTable("musics", username));
                                System.out.println("select the music's ID u want to upload");
                                keyboard = new Scanner(System.in);
                                int musicID = keyboard.nextInt();
                                System.out.println("type the music location: (example: C:\\Users\\user\\Desktop\\music.mp3 with two bars)");
                                keyboard = new Scanner(System.in);
                                String location = keyboard.nextLine();
                                ConnectionFunctions.sendMusicFromRMIClient(location, musicID, username, i.getTCPAddress());
                                break;
                            case 2:
                                //download
                                //print table with all musics in one user's cloud
                                System.out.println(i.getTable("cloudmusics", username));
                                System.out.println("select the music's ID u want to download");
                                keyboard = new Scanner(System.in);
                                int musicIDDownload = keyboard.nextInt();
                                System.out.println("type the path where u want to save the music: (example: C:\\Users\\user\\Desktop\\music.mp3 with two bars)");
                                keyboard = new Scanner(System.in);
                                String path = keyboard.nextLine();
                                i.setMusicIDToDownload(username, musicIDDownload);
                                ConnectionFunctions.receiveMusicRMIClient(path, i.getTCPAddress());
                                break;
                            case 3:
                                //share with other user
                                //print table with all musics in one user's cloud
                                System.out.println(i.getTable("cloudmusics", username));
                                System.out.println("select the music's ID u want to share");
                                keyboard = new Scanner(System.in);
                                int musicIDToShare = keyboard.nextInt();
                                //print table users with only usernames
                                i.getTable("users", username);
                                System.out.println("type the username with who you want to share");
                                keyboard = new Scanner(System.in);
                                String usernameToShare = keyboard.nextLine();
                                i.shareMusicInCloud(usernameToShare, musicIDToShare);
                                break;
                            default:
                                System.out.println("please enter valid option");
                        }

                        break;
                    case 5:
                        //manage data
                        if (i.checkIfUserIsAdmin(username)) {//check if user is admin
                            System.out.println("what operation you want to do? (type one of the options)");
                            System.out.println("1. add music/album/artist");
                            System.out.println("2. change artist, album or music data");
                            System.out.println("3. grant admin to user");
                            System.out.println("4. add picture to an album"); // falta fazer
                            System.out.println("5. upload song lyrics"); // falta fazer
                            System.out.println("6. see all of the users in the system");
                            keyboard = new Scanner(System.in);
                            int choice2 = keyboard.nextInt();
                            switch (choice2) {
                                case 1:
                                    System.out.println("which data you want to add? (type one of the options)");
                                    System.out.println("1. music");
                                    System.out.println("2. album");
                                    System.out.println("3. artist");
                                    keyboard = new Scanner(System.in);
                                    int choice6 = keyboard.nextInt();
                                    switch (choice6) {
                                        case 1:
                                            //add music
                                            System.out.println("music's name: ");
                                            keyboard = new Scanner(System.in);
                                            String musicName = keyboard.nextLine();
                                            System.out.println("description: ");
                                            keyboard = new Scanner(System.in);
                                            String description = keyboard.nextLine();
                                            if (description.length() == 0) {
                                                description = "no description";
                                            }
                                            System.out.println("what is the duration: (in seconds)");
                                            keyboard = new Scanner(System.in);
                                            int duration = keyboard.nextInt();
                                            System.out.println(i.getTable("albums", username));
                                            System.out.println("type the album's ID: ");
                                            keyboard = new Scanner(System.in);
                                            int albumID = keyboard.nextInt();
                                            System.out.println(i.getTable("artists", username));
                                            System.out.println("type the artist's ID: ");
                                            keyboard = new Scanner(System.in);
                                            int artistID = keyboard.nextInt();
                                            i.addMusic(musicName, description, duration, albumID, artistID);
                                            break;

                                        case 2:
                                            //add album
                                            System.out.println("album name: ");
                                            keyboard = new Scanner(System.in);
                                            String albumName = keyboard.nextLine();
                                            System.out.println("album genre: ");
                                            keyboard = new Scanner(System.in);
                                            String albumGenre = keyboard.nextLine();
                                            System.out.println("album description: ");
                                            keyboard = new Scanner(System.in);
                                            String descriptionAlbum = keyboard.nextLine();
                                            System.out.println("date of the album: (in 'yyyy-mm-dd')");
                                            keyboard = new Scanner(System.in);
                                            String albumDate = keyboard.nextLine();
                                            System.out.println(i.getTable("artists", username));
                                            System.out.println("type the artist's ID: ");
                                            keyboard = new Scanner(System.in);
                                            int artistID2 = keyboard.nextInt();
                                            i.addAlbum(albumName, albumGenre, descriptionAlbum, albumDate, artistID2);
                                            break;

                                        case 3:
                                            //add artist
                                            System.out.println("artist's name: ");
                                            keyboard = new Scanner(System.in);
                                            String artistName = keyboard.nextLine();
                                            System.out.println("description: ");
                                            keyboard = new Scanner(System.in);
                                            String descriptionArtist = keyboard.nextLine();
                                            if (descriptionArtist.length() == 0) {
                                                descriptionArtist = "no description";
                                            }
                                            i.addArtist(artistName, descriptionArtist);
                                            break;
                                        default:
                                            System.out.println("please enter valid option");
                                    }
                                    break;
                                case 2:
                                    //change data
                                    System.out.println("which you want to change data?");
                                    System.out.println("1. artist name");
                                    System.out.println("2. album data");
                                    System.out.println("3. music name");
                                    keyboard = new Scanner(System.in);
                                    int choice3 = keyboard.nextInt();
                                    switch (choice3) {
                                        case 1:
                                            //change artist name
                                            System.out.println(i.getTable("artists", username));
                                            System.out.println("select the ID you want to change the name");
                                            keyboard = new Scanner(System.in);
                                            int artistID = keyboard.nextInt();
                                            System.out.println("type the new name: ");
                                            keyboard = new Scanner(System.in);
                                            String artistNewName = keyboard.nextLine();
                                            i.changeData("artists", "name", artistID, artistNewName);
                                            break;
                                        case 2:
                                            //change album data
                                            System.out.println(i.getTable("albums", username));
                                            System.out.println("select the ID you want to change");
                                            keyboard = new Scanner(System.in);
                                            int albumID = keyboard.nextInt();
                                            System.out.println("what operation you want to do to an album?");
                                            System.out.println("1. change name");
                                            System.out.println("2. change description");
                                            keyboard = new Scanner(System.in);
                                            int choice7 = keyboard.nextInt();
                                            switch (choice7) {
                                                case 1:
                                                    //change album name
                                                    System.out.println("type the new name: ");
                                                    keyboard = new Scanner(System.in);
                                                    String albumNewName = keyboard.nextLine();
                                                    i.changeData("albums", "name", albumID, albumNewName);
                                                    break;
                                                case 2:
                                                    //change album description
                                                    System.out.println("type the new description: ");
                                                    keyboard = new Scanner(System.in);
                                                    String newDescription = keyboard.nextLine();
                                                    i.changeData("albums", "description", albumID, newDescription);
                                                    i.userEditAlbum(username, albumID);
                                                    i.notifyUsersAboutAlbumDescriptionEdit(username, albumID);
                                                    break;
                                                default:
                                                    System.out.println("please enter valid option");
                                            }
                                            break;
                                        case 3:
                                            //change music name
                                            System.out.println(i.getTable("musics", username));
                                            System.out.println("select the ID you want to change the name");
                                            keyboard = new Scanner(System.in);
                                            int musicID = keyboard.nextInt();
                                            System.out.println("type the new name: ");
                                            keyboard = new Scanner(System.in);
                                            String musicNewName = keyboard.nextLine();
                                            i.changeData("musics", "name", musicID, musicNewName);
                                            break;
                                        default:
                                            System.out.println("please enter valid option");

                                    }
                                    break;
                                case 3:
                                    //grant admin to user
                                    System.out.println(i.getTable("users", username));
                                    System.out.println("type the username you want to make admin: ");
                                    keyboard = new Scanner(System.in);
                                    String newAdminUsername = keyboard.nextLine();
                                    if (i.grantAdminToUser(newAdminUsername)) {
                                        System.out.println(newAdminUsername + " admin granted");
                                        i.notifyUserAboutAdminGranted(newAdminUsername);
                                    } else {
                                        System.out.println("already admin / username doesn't exist");
                                    }
                                    break;
                                case 4:
                                    //4. add picture to an album
                                case 5:
                                    //5. upload song lyrics"
                                case 6:
                                    //see users in the program
                                    System.out.println("do u want to see online users or all users?");
                                    System.out.println("1. online users");
                                    System.out.println("2. all users");
                                    keyboard = new Scanner(System.in);
                                    int choice8 = keyboard.nextInt();
                                    switch (choice8) {
                                        case 1:
                                            //online users
                                            System.out.println("online users: \n");
                                            System.out.println(i.printOnlineUsers());
                                            break;
                                        case 2:
                                            //all users
                                            System.out.println("all users: \n");
                                            System.out.println(i.getTable("users", username));
                                            break;
                                        default:
                                            System.out.println("please enter valid option");
                                    }
                                    break;
                            }
                        } else {
                            System.out.println("you are not an admin");
                        }
                        break;
                    case 6:
                        //logout
                        i.logout(username);
                        return true;
                    case 7:
                        //exit
                        i.logout(username);
                        return false;
                    default:
                        System.out.println("please enter valid option");
                }
            }
        }
        catch (java.rmi.ConnectException e) {
                //to connect to backup rmi server if primary goes down
                System.out.println("Wait...");
                Thread.sleep(5000);
                boolean tryBounding = false;
                while (!tryBounding) {
                    try {
                        i = (InterfaceServer) Naming.lookup("//"+ InetAddress.getLocalHost().getHostAddress()+":1099/infoMusicRegistry");
                        //i = (InterfaceServer) Naming.lookup("//192.168.1.188:1099/infoMusicRegistry");
                        //i = (InterfaceServer) Naming.lookup("//192.168.1.185:1099/infoMusicRegistry");
                        tryBounding = true;
                    } catch (RemoteException y) {
                        System.out.println("trying...");
                    } catch (NotBoundException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
    return false;
    }
}
