package com.company;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;


import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;

/**
 *
 */
public class RMIClient {
    protected RMIClient() throws RemoteException {
        super();
    }

    /*public void printOnClient(String s) throws RemoteException {
        System.out.println("> " + s);
    }
    */


    public static void main(String[] args) throws IOException, NotBoundException, SQLException {
        InterfaceServer i = (InterfaceServer) Naming.lookup("infoMusicRegistry");
       // RMIClient c = new RMIClient();
        //i.subscribe("cliente1", (InterfaceClient) c);

        boolean enter = true;
        while(enter){
            String user = enterTheProgram(i);
            if (user != null){
                enter = menu(i, user);
            }
            else{
                System.out.println("thank you. we hope to see you again!");
                return;
            }
        }
        System.out.println("thank you. we hope to see you again!");
    }

    public static String enterTheProgram(InterfaceServer i) throws RemoteException, MalformedURLException, NotBoundException {
        while (true) {
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
                        return username;
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
                default:
                    System.out.println("please enter valid option");
            }
        }
    }

    public static boolean menu(InterfaceServer i, String username) throws IOException, SQLException {
        Connection c = SQL.enterDatabase("infomusic");
        System.out.println("welcome username! what you want to do?");

        while (true) {
            System.out.println("menu: (type one of the options)");
            System.out.println("1. search for songs");
            System.out.println("2. search for some detail information about an artist or a specific album ");
            System.out.println("3. write a review to an album");
            System.out.println("4. upload/download a song");
            System.out.println("5. manage operations (admins only)"); //Só pode aparecer ao admin
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
                    System.out.println("what operation you want to do? (type one of the options)");
                    System.out.println("1. search about an album");
                    System.out.println("2. search about an artist");
                    keyboard = new Scanner(System.in);
                    int choice4 = keyboard.nextInt();

                    switch (choice4) {
                        case 1:
                            SQL.printAllTable(c, "albums");
                            System.out.println("type the album id u want to know more about");
                            keyboard = new Scanner(System.in);
                            int albumToSearch = keyboard.nextInt();
                            String albumDetail = i.searchDetailAboutAlbum(albumToSearch);
                            if(albumDetail != null) {
                                System.out.println(albumDetail);
                            }
                            break;
                        case 2:
                            SQL.printAllTable(c, "artists");
                            System.out.println("type the album id u want to know more about");
                            keyboard = new Scanner(System.in);
                            int artistToSearch = keyboard.nextInt();
                            String artistDetail = i.searchDetailAboutArtist(artistToSearch);
                            if(artistDetail != null) {
                                System.out.println(artistDetail);
                            }
                            break;

                        default:
                            System.out.println("please enter valid option");
                    }
                    // a specific album
                    break;
                case 3:
                    //write a review to an album
                    SQL.printAllTable(c, "albums");
                    System.out.println("select the ID of the album you want to review");
                    keyboard = new Scanner(System.in);
                    int albumToReviewID = keyboard.nextInt();
                    System.out.println("rating: (0 to 10)");
                    keyboard = new Scanner(System.in);
                    int albumRating = keyboard.nextInt();
                    System.out.println("review: (max 300 char)");
                    keyboard = new Scanner(System.in);
                    String albumReview = keyboard.nextLine();
                    if(albumRating < 11 && albumRating >= 0 && albumReview.length() < 301) {
                        i.writeAlbumReview(albumToReviewID, albumRating, albumReview);
                    }
                    else {
                        System.out.println("rating must be between 0 and 10 and review have 300 char limit");
                    }
                    break;
                case 4:
                    System.out.println("which operation you want to do?");
                    System.out.println("1. upload");
                    System.out.println("2. download");
                    keyboard = new Scanner(System.in);
                    int choice5 = keyboard.nextInt();
                    switch(choice5){
                        case 1:
                            SQL.printAllTable(c,"musics");



                            ConnectionFunctions.uploadMusicTCP("C:\\Users\\zmcdo\\Documents\\music.mp3", false, 1, "rita");


                            System.out.println("select the music's ID u want to upload");
                            keyboard = new Scanner(System.in);
                            int musicID = keyboard.nextInt();
                            System.out.println("type the music location: ");
                            keyboard = new Scanner(System.in);
                            String location = keyboard.nextLine();
                            break;
                        case 2:
                            SQL.printAllTable(c,"albums");
                            System.out.println("select the ID you want to change the name");
                            keyboard = new Scanner(System.in);
                            int albumID = keyboard.nextInt();
                            System.out.println("type the new name: ");
                            keyboard = new Scanner(System.in);
                            String albumNewName = keyboard.nextLine();

                            break;
                        default:
                            System.out.println("please enter valid option");

                    }
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
                    if(i.checkIfUserIsAdmin(username)) {//check if user is admin
                        System.out.println("what operation you want to do? (type one of the options)");
                        System.out.println("1. add music/album/artist");
                        System.out.println("2. change artist, album or music name");
                        System.out.println("3. grant admin to user");
                        System.out.println("4. add picture to an album");
                        System.out.println("5. upload song lyrics");
                        keyboard = new Scanner(System.in);
                        int choice2 = keyboard.nextInt();
                        switch(choice2) {
                            case 1:
                                System.out.println("which data you want to add? (type one of the options)");
                                System.out.println("1. music");
                                System.out.println("2. album");
                                System.out.println("3. artist");
                                keyboard = new Scanner(System.in);
                                int choice6 = keyboard.nextInt();
                                switch(choice6) {
                                    case 1:
                                        System.out.println("music's name: ");
                                        keyboard = new Scanner(System.in);
                                        String musicName = keyboard.nextLine();
                                        System.out.println("description: ");
                                        keyboard = new Scanner(System.in);
                                        String description = keyboard.nextLine();
                                        if(description.length() == 0) {
                                            description = "no description";
                                        }
                                        System.out.println("what is the duration: (in seconds)");
                                        keyboard = new Scanner(System.in);
                                        int duration = keyboard.nextInt();
                                        SQL.printAllTable(c, "albums");
                                        System.out.println("type the album's ID: ");
                                        keyboard = new Scanner(System.in);
                                        int albumID = keyboard.nextInt();
                                        SQL.printAllTable(c, "artists");
                                        System.out.println("type the artist's ID: ");
                                        keyboard = new Scanner(System.in);
                                        int artistID = keyboard.nextInt();
                                        i.addMusic(musicName, description, duration, albumID, artistID);
                                        break;

                                    case 2:
                                        System.out.println("album name: ");
                                        keyboard = new Scanner(System.in);
                                        String albumName = keyboard.nextLine();
                                        System.out.println("album genre: ");
                                        keyboard = new Scanner(System.in);
                                        String albumGenre = keyboard.nextLine();
                                        System.out.println("date of the album: (in 'yyyy-mm-dd')");
                                        keyboard = new Scanner(System.in);
                                        String albumDate = keyboard.nextLine();
                                        SQL.printAllTable(c, "artists");
                                        System.out.println("type the artist's ID: ");
                                        keyboard = new Scanner(System.in);
                                        int artistID2 = keyboard.nextInt();
                                        i.addAlbum(albumName, albumGenre, albumDate, artistID2);
                                        break;

                                    case 3:
                                        System.out.println("artist's name: ");
                                        keyboard = new Scanner(System.in);
                                        String artistName = keyboard.nextLine();
                                        System.out.println("description: ");
                                        keyboard = new Scanner(System.in);
                                        String descriptionArtist = keyboard.nextLine();
                                        if(descriptionArtist.length() == 0) {
                                            descriptionArtist = "no description";
                                        }
                                        i.addArtist(artistName, descriptionArtist);
                                        break;
                                    default:
                                        System.out.println("please enter valid option");
                                }
                                break;
                            case 2:
                                System.out.println("which you want to change the name?");
                                System.out.println("1. artist");
                                System.out.println("2. album");
                                System.out.println("3. music");
                                keyboard = new Scanner(System.in);
                                int choice3 = keyboard.nextInt();
                                switch(choice3){
                                    case 1:
                                        SQL.printAllTable(c,"artists");
                                        System.out.println("select the ID you want to change the name");
                                        keyboard = new Scanner(System.in);
                                        int artistID = keyboard.nextInt();
                                        System.out.println("type the new name: ");
                                        keyboard = new Scanner(System.in);
                                        String artistNewName = keyboard.nextLine();
                                        i.changeData("artists","name", artistID, artistNewName);
                                        break;
                                    case 2:
                                        SQL.printAllTable(c,"albums");
                                        System.out.println("select the ID you want to change the name");
                                        keyboard = new Scanner(System.in);
                                        int albumID = keyboard.nextInt();
                                        System.out.println("type the new name: ");
                                        keyboard = new Scanner(System.in);
                                        String albumNewName = keyboard.nextLine();
                                        i.changeData("albums","name", albumID, albumNewName);
                                        break;
                                    case 3:
                                        SQL.printAllTable(c,"musics");
                                        System.out.println("select the ID you want to change the name");
                                        keyboard = new Scanner(System.in);
                                        int musicID = keyboard.nextInt();
                                        System.out.println("type the new name: ");
                                        keyboard = new Scanner(System.in);
                                        String musicNewName = keyboard.nextLine();
                                        i.changeData("musics","name", musicID, musicNewName);
                                        break;
                                    default:
                                        System.out.println("please enter valid option");

                                }
                                break;
                            case 3:
                                System.out.println("type the username you want to make admin: ");
                                keyboard = new Scanner(System.in);
                                username = keyboard.nextLine();
                                if(i.grantAdminToUser(username)) {
                                    System.out.println(username + " admin granted");
                                }
                                else {
                                    System.out.println("already admin / username doesn't exist");
                                }
                                break;
                        }
                    }
                    else{
                        System.out.println("you are not an admin");
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
