package com.company;

import org.apache.commons.io.FileUtils;

import javax.swing.plaf.nimbus.State;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * SQL Class
 * has all functions to communicate with database
 */
public final class SQL {
    //url for database
    private static String serverUrl = "jdbc:postgresql://localhost:5432/";
    //name of the database created
    private static String dbname;
    //boolean to see if database already exists or dont
    private static boolean dbExists;


    /**
     * creates database and adds all the tables neccessaries for the program to run
     * @param serverNumber identifier of the multicast server
     * @return the connection to database in case of success, false otherwise
     */
    public static Connection initialConfig(long serverNumber) {
        try {

            dbname = "infomusic"+serverNumber;
            Connection c = enterDatabase(dbname);
            if(!dbExists) {
                HashMap<String, String> arr = new HashMap<String, String>();
                arr.put("username", "VARCHAR(20) PRIMARY KEY NOT NULL");
                arr.put("password", "VARCHAR(20) NOT NULL");
                arr.put("isAdmin", "Boolean NOT NULL");
                SQL.createTable(dbname, "users", arr);

                arr = new HashMap<>();
                arr.put("artistID", "SERIAL PRIMARY KEY NOT NULL");
                arr.put("name", "VARCHAR(30) NOT NULL");
                arr.put("description", "VARCHAR(200) NOT NULL");
                SQL.createTable(dbname, "artists", arr);

                arr = new HashMap<>();
                arr.put("albumID", "SERIAL PRIMARY KEY NOT NULL");
                arr.put("artistID", "SERIAL NOT NULL");
                arr.put("genre", "VARCHAR(30) NOT NULL");
                arr.put("name", "VARCHAR(30) NOT NULL");
                arr.put("description", "VARCHAR(200) NOT NULL");
                arr.put("releaseDate", "DATE NOT NULL");
                arr.put("picture", "bytea");
                SQL.createTable(dbname, "albums", arr);
                SQL.addForeignKeyToTable("artists", "albums", "artistID");

                arr = new HashMap<>();
                arr.put("musicID", "SERIAL PRIMARY KEY NOT NULL");
                arr.put("albumID", "SERIAL NOT NULL");
                arr.put("artistID", "SERIAL NOT NULL");
                arr.put("name", "VARCHAR(30) NOT NULL");
                arr.put("description", "VARCHAR(200)");
                arr.put("duration", "INTEGER NOT NULL");
                arr.put("lyrics", "bytea");
                SQL.createTable(dbname, "musics", arr);
                SQL.addForeignKeyToTable("albums", "musics", "albumID");
                SQL.addForeignKeyToTable("artists", "musics", "artistID");


                arr = new HashMap<>();
                arr.put("reviewID", "SERIAL PRIMARY KEY NOT NULL");
                arr.put("albumID", "SERIAL NOT NULL");
                arr.put("rating", "INTEGER NOT NULL");
                arr.put("review", "VARCHAR(300) NOT NULL");
                SQL.createTable(dbname, "reviews", arr);
                SQL.addForeignKeyToTable("albums", "reviews", "albumID");


                arr = new HashMap<>();
                arr.put("cloudMusicID", "SERIAL PRIMARY KEY NOT NULL");
                arr.put("username", "VARCHAR(20) NOT NULL");
                arr.put("musicID", "SERIAL NOT NULL");
                arr.put("musicFile", "BYTEA NOT NULL");
                SQL.createTable(dbname, "cloudMusics", arr);
                SQL.addForeignKeyToTable("musics", "cloudmusics", "musicID");
                SQL.addForeignKeyToTable("users", "cloudmusics", "username");


                arr = new HashMap<>();
                arr.put("notificationID", "SERIAL PRIMARY KEY NOT NULL");
                arr.put("username", "VARCHAR(20) NOT NULL");
                arr.put("notificationType", "VARCHAR(100) NOT NULL");
                SQL.createTable(dbname, "notifications", arr);
                SQL.addForeignKeyToTable("users", "notifications", "username");

                arr = new HashMap<>();
                arr.put("editionID", "SERIAL PRIMARY KEY NOT NULL");
                arr.put("username", "VARCHAR(20) NOT NULL");
                arr.put("albumID", "SERIAL NOT NULL");
                SQL.createTable(dbname, "albumEdits", arr);
                SQL.addForeignKeyToTable("users", "albumEdits", "username");
                SQL.addForeignKeyToTable("albums", "albumEdits", "albumID");

                //add values to table
                String[] a1 = {"name, description", "'RHC', 'banda de rock dos EUA'"};
                String[] a2 = {"name, description", "'Coldplay', 'banda britânica fundada em 1996 na Inglaterra'"};
                String[] a3 = {"name, description", "'U2', 'banda irlandesa de rock formada no ano de 1976.'"};
                SQL.addValuesToTable("artists", a1);
                SQL.addValuesToTable("artists", a2);
                SQL.addValuesToTable("artists", a3);
                String[] b1 = {"releasedate, name, genre,description, artistid", "now(),'Californication','Alternative Rock', 'album made with love from california', 1"};
                String[] b2 = {"releasedate, name, genre,description, artistid", "now(),'Parachutes','Alternative Rock', 'álbum de estreia da banda inglesa', 2"};
                String[] b3 = {"releasedate, name, genre,description, artistid", "now(),'Achtung Baby','Alternative Rock', 'sétimo álbum de estúdio da banda de rock irlandesa', 3"};
                SQL.addValuesToTable("albums", b1);
                SQL.addValuesToTable("albums", b2);
                SQL.addValuesToTable("albums", b3);

                String[] d1 = {"name, description, duration, albumid, artistid", "'Around The World', 'blabla', 300, 1, 1"};
                String[] d2 = {"name, description, duration, albumid, artistid", "'Shiver', 'blabla', 300, 2, 2"};
                String[] d3 = {"name, description, duration, albumid, artistid", "'Zoo Station', 'blabla', 300, 3, 3"};
                SQL.addValuesToTable("musics", d1);
                SQL.addValuesToTable("musics", d2);
                SQL.addValuesToTable("musics", d3);

                String[] e1 = {"username, password, isAdmin", "'jose', '123', true"};
                String[] e2 = {"username, password, isAdmin", "'hugo', '1234', false"};
                String[] e3 = {"username, password, isAdmin", "'helder', '12345', false"};
                SQL.addValuesToTable("users", e1);
                SQL.addValuesToTable("users", e2);
                SQL.addValuesToTable("users", e3);
            }
            return c;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * enter in postgressql database or create if it doesn't exist
     * @param name of the database
     * @return the connection to the database in success, null otherwise
     * @throws SQLException
     */
    public static Connection enterDatabase(String name) throws SQLException {
        Connection c = null;
        String url = serverUrl + name;
        try {
            c = DriverManager.getConnection(url, "postgres", "postgres");
            System.out.println("d: connected to database " + name);
            dbExists = true;
        } catch (SQLException e) {
            //e.printStackTrace();
            System.out.println("d: database doesn't exist.. creating");
            c = DriverManager.getConnection(serverUrl, "postgres", "postgres");
            dbExists = false;
            //c.setAutoCommit(false);
            Statement statement = null;
            try {
                statement = c.createStatement();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            try {
                String sql = "CREATE DATABASE " + name+";";
                statement.executeUpdate(sql);
                c = DriverManager.getConnection(url, "postgres", "postgres");
                System.out.println("d: database " + name + " created");
            } catch (SQLException e2) {
                System.out.println("d: error creating database "+name);
                e2.printStackTrace();
            }
        }
        return c;
    }

    /**
     * Create table in one database
     * @param name of the table
     * @param values /atributes of the table (ex: 'USER TEXT PRIMARY KEY')
     */
    public static void createTable(String dbname, String name, HashMap<String, String> values) throws SQLException {
        Connection c = enterDatabase(dbname);
        Statement statement = null;
        try {
            statement = c.createStatement();
            String sql = "DROP TABLE IF EXISTS "+name+"; CREATE TABLE "+name + " (";
            Iterator it = values.entrySet().iterator();
            while(it.hasNext()) {
                HashMap.Entry pair = (HashMap.Entry) it.next();
                sql += pair.getKey().toString() + " " + pair.getValue().toString() +" , ";
            }
            sql = sql.substring(0, sql.length() - 2);
            sql += ");";
            try {
                statement.executeUpdate(sql);
            }
            catch(SQLException e1) {
                e1.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * add foreign key to one table
     * @param parentTable table that has the key
     * @param childTable child table to add foreign key
     * @param column name of the column u want to set as foreign key
     * @throws SQLException
     */
    public static void addForeignKeyToTable(String parentTable, String childTable, String column) throws SQLException {
        Connection c = enterDatabase(dbname);
        String sql = "ALTER TABLE "+childTable+"\n" +
                "ADD FOREIGN KEY ("+column+") REFERENCES "+parentTable+"("+column+"); ";
        Statement s = c.createStatement();
        s.executeUpdate(sql);
    }

    /**
     * add one row to one table
     * @param table name of the table, can be any table of the database
     * @param keysValues has the names and values of the table to insert
     */
    public static void addValuesToTable(String table, String[] keysValues) throws SQLException {
        Connection c = enterDatabase(dbname);
        Statement statement = null;
        try {
            statement = c.createStatement();
            //tbf
            String sql = "INSERT INTO "+ table.toUpperCase()+ " (" + keysValues[0].toUpperCase()+") "
                    + "VALUES (" + keysValues[1] + ");";
            System.out.println("d: query in adding values to table: "+sql);
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * select one user from the table users
     * @param username of the user to select
     * @return the name of the user
     * @throws SQLException
     */
    public static String selectUser(String table, String username) throws SQLException {
        Connection c = enterDatabase(dbname);
        Statement s = c.createStatement();
        ResultSet rs = s.executeQuery("SELECT * FROM USERS WHERE username='"+username+"'");
        String name = null;
        while (rs.next()) {
            name = rs.getString("username");
        }
        return name;
    }

    /**
     * get user and password from table
     * @param username name of the user
     * @return array with username and password
     * @throws SQLException
     */
    public static String[] selectUserAndGetPassword(String username) throws SQLException {
        Connection c = enterDatabase(dbname);
        Statement s = c.createStatement();
        ResultSet rs = s.executeQuery("SELECT * FROM USERS WHERE username='"+username+"'");
        String name = null;
        String password = null;
        while (rs.next()) {
            name = rs.getString("username");
            password = rs.getString("password");

        }
        String[] array = {name, password};
        System.out.println();
        return array;
    }

    /**
     * check if one table in database is empty
     * @param table name you want to check
     * @return true in case it's empty, false otherwise
     * @throws SQLException
     */
    public static boolean checkIftableIsEmpty(String table) throws  SQLException {
        Connection c = enterDatabase(dbname);
        String sql = "select true from "+table+" limit 1;";
        Statement s = c.createStatement();
        ResultSet rs = s.executeQuery(sql);
        while(rs.next()) {
            return false;
        }
        return true;
    }

    /**
     * check if one user is admin or dont
     * @param username of the user you want to check
     * @return true in case it's admin, false otherwise
     * @throws SQLException
     */
    public static boolean checkIfUserIsAdmin(String username) throws SQLException {
        Connection c = enterDatabase(dbname);
        Statement s = c.createStatement();
        ResultSet rs = s.executeQuery("SELECT * FROM USERS WHERE username='"+username+"'");
        Boolean isAdmin = false;
        while (rs.next()) {
            isAdmin = rs.getBoolean("isAdmin");
        }
        return isAdmin;
    }

    /**
     * grant admin to an user
     * @param username of the user you want to grant admin
     * @return true in case of success, false otherwise
     * @throws SQLException
     */
    public static boolean grantAdminToUser(String username) throws SQLException {
        Connection c = enterDatabase(dbname);
        String sql = "UPDATE USERS\n" +
                "SET isAdmin = true\n" +
                "WHERE username like '"+username+"';";
        Statement s = c.createStatement();
        s.executeUpdate(sql);
        if(checkIfUserIsAdmin(username)) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * add one review about an album to database with rating and justification
     * @param review text has 300 char limit
     * @param rating 0 to 10
     * @param albumID of the album you want to review
     * @throws SQLException
     */
    public static void reviewToAlbum(String review, int rating, int albumID) throws SQLException {
        Connection c = enterDatabase(dbname);
        String sql = "INSERT INTO REVIEWS(albumid, rating, review) values("+albumID+", "+rating+", '"+review+"')";
        Statement s = c.createStatement();
        s.executeUpdate(sql);
    }

    /**
     * add one file to one table (can be albums for picture, cloudmusics for music file, musics for lyrics)
     * @param table can be cloudmusics, albums, musics
     * @param column can be musicfile, picture, lyrics
     * @param fileLocation where the file is located
     * @param id albumID, musicID
     * @return true in success, false otherwise
     * @throws SQLException
     */
    public static boolean enterFileInTable(String table, String column, String fileLocation, int id) throws SQLException {
        Connection c = enterDatabase(dbname);
        File file = new File(fileLocation);
        try {
            String tableID = table.substring(0, table.length()-1)+id;
            //tbf
            String sql = "UPDATE "+table+" SET "+column+" = ? WHERE "+tableID+" = "+id;

            FileInputStream fis = new FileInputStream(file);
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setBinaryStream(1, fis, file.length());
            ps.executeUpdate();
            ps.close();
            fis.close();
            System.out.println("d: query in adding values to table: "+sql);
            return true;

        } catch (SQLException e) {
            e.printStackTrace();

        } catch (FileNotFoundException e) {
            System.out.println("d: file not found");
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * add array of bytes, for upload music to database and other files
     * @param table of the table to store the array, cloudmusics, for example in case of musics
     * @param array the array of bytes that has the data
     * @param id musicID in case of cloudmusics
     * @param username to associate the file ot username
     * @throws SQLException
     */
    public static void enterArrayInTable(String table, byte[] array, int id, String username) throws SQLException {
        Connection c = enterDatabase(dbname);
        String query = "INSERT INTO "+table+" (musicID, username, musicFile) VALUES ("+id+",'"+username+"',?)";
        PreparedStatement pstmt = c.prepareStatement(query);
        pstmt.setBytes(1, array);
        pstmt.execute();
    }

    /**
     * get array data from table to download file from database (multicast server)
     * @param table where the array with data is cloudmusics
     * @param id musicID associated with the file
     * @param username of the user that has the file
     * @return the array of bytes with file's data
     * @throws SQLException
     */
    public static byte[] getArrayInTable(String table, int id, String username) throws SQLException {
        Connection c = enterDatabase(dbname);
        PreparedStatement ps = c.prepareStatement("SELECT musicFile FROM "+table+" WHERE musicID = "+id+" and username='"+username+"'");
        ResultSet rs = ps.executeQuery();
        byte[] array = null;
        if (rs != null) {
            while (rs.next()) {
                array = rs.getBytes(1);
                // use the data in some way here
            }
            rs.close();
        }
        ps.close();
        return array;
    }


    /**
     * change name ( or description in case of albums table) of one table column
     * @param table can be albums, musics, artists
     * @param newName the new name for the column
     * @param ID of the data you want to change the name, artistID, albumID, musicID
     * @param column name (or description in case of album)
     * @return false, no verification yet
     * @throws SQLException
     */
    public static boolean changeName(String table, String newName, Integer ID, String column) throws SQLException {
        Connection c = enterDatabase(dbname);
        String name = table.substring(0, table.length()-1);
        String sql = "UPDATE "+table+" SET "+column+" = '"+newName+"' WHERE "+name+"id = "+ID;
        PreparedStatement ps = c.prepareStatement(sql);
        ps.executeUpdate();
        ps.close();
        return false;
    }


    /**
     * gets artist table for user to see, with artistID and artistName
     * @return string with artist table
     * @throws SQLException
     */
    public static String getArtistsTable() throws SQLException {
        Connection c = enterDatabase(dbname);
        ArrayList<Integer> artistIDs = new ArrayList<>();
        ArrayList<String> artistNames = new ArrayList<>();
        Statement s = c.createStatement();
        ResultSet rs = s.executeQuery("SELECT * FROM ARTISTS");
        while (rs.next()) {
            artistIDs.add(rs.getInt("artistID"));
            artistNames.add(rs.getString("name"));
        }
        String result = "example: <artistID>. <artistName>\n";
        for(int i=0; i <  artistIDs.size(); i++) {
            result += artistIDs.get(i) +". "+ artistNames.get(i)+"\n";
        }
        return result;
    }

    /**
     * gets album table for user to see, with albumID and albumName
     * @return string with album table
     * @throws SQLException
     */
    public static String getAlbumsTable() throws SQLException {
        Connection c = enterDatabase(dbname);
        ArrayList<Integer> albumIDs = new ArrayList<>();
        ArrayList<String> albumNames = new ArrayList<>();
        Statement s = c.createStatement();
        ResultSet rs = s.executeQuery("SELECT * FROM ALBUMS");
        while (rs.next()) {
            albumIDs.add(rs.getInt("albumID"));
            albumNames.add(rs.getString("name"));
        }
        String result = "example: <albumID>. <albumName>\n";
        for(int i=0; i <  albumIDs.size(); i++) {
            result += albumIDs.get(i) +". "+ albumNames.get(i)+"\n";
        }
        return result;
    }

    /**
     * gets music table for user to see, with musicID and musicName
     * @return string with music table
     * @throws SQLException
     */
    public static String getMusicsTable() throws SQLException {
        Connection c = enterDatabase(dbname);
        ArrayList<Integer> musicIDs = new ArrayList<>();
        ArrayList<String> musicNames = new ArrayList<>();
        Statement s = c.createStatement();
        ResultSet rs = s.executeQuery("SELECT * FROM MUSICS");
        while (rs.next()) {
            musicIDs.add(rs.getInt("musicID"));
            musicNames.add(rs.getString("name"));
        }
        String result = "example: <musicID>. <musicName>\n";
        for(int i=0; i <  musicIDs.size(); i++) {
            result += musicIDs.get(i) +". "+ musicNames.get(i)+"\n";
        }
        return result;
    }

    /**
     * gets users table for user to see, with username
     * @return string with usernames table
     * @throws SQLException
     */
    public static String getUsersTable() throws SQLException {
        Connection c = enterDatabase(dbname);
        ArrayList<String> userNames = new ArrayList<>();
        Statement s = c.createStatement();
        ResultSet rs = s.executeQuery("SELECT * FROM USERS");
        while (rs.next()) {
            userNames.add(rs.getString("username"));
        }
        String result = "example: <username>\n";
        for(int i=0; i <  userNames.size(); i++) {
            result += userNames.get(i)+"\n";
        }
        return result;
    }

    /**
     * gets the musics that one user has in the cloud (server)
     * @param username of the user that wants to check which musics has in the cloud
     * @return cloud musics table for that user
     * @throws SQLException
     */
    public static String getMusicsCloudTable(String username) throws SQLException {
        Connection c = enterDatabase(dbname);
        ArrayList<Integer> musicIDs = new ArrayList<>();
        String musicName = null;
        Statement s = c.createStatement();
        ResultSet rs = s.executeQuery("SELECT musicid FROM CLOUDMUSICS WHERE USERNAME='"+username+"'");
        while (rs.next()) {
            musicIDs.add(rs.getInt("musicid"));
        }
        String result = "";
        if(musicIDs.size() > 0) {
            result = "example: <musicID>. <musicName>\n";
            for (int i = 0; i < musicIDs.size(); i++) {
                rs = s.executeQuery("SELECT name FROM musics WHERE musicID='" + musicIDs.get(i) + "'");
                while (rs.next()) {
                    musicName = rs.getString("name");
                }
                result += musicIDs.get(i) + ". " + musicName + "\n";
            }
        }
        else {
            result = "no results";
        }
        return result;
    }

    /**
     * gets album data from one specific album
     * @param albumID id of the album that user wants the data
     * @return string with album data
     * @throws SQLException
     */
    public static String albumData(Integer albumID) throws SQLException {
        Connection c = enterDatabase(dbname);
        String result = "";
        ArrayList<Double> ratings = new ArrayList<>();
        ArrayList<String> reviews = new ArrayList<>();
        Statement s = c.createStatement();
        ResultSet rs = s.executeQuery("SELECT * FROM REVIEWS WHERE albumID='"+albumID+"'");
        while (rs.next()) {
            ratings.add(Double.parseDouble(rs.getInt("rating")+""));
            reviews.add(rs.getString("review"));
        }
        rs = s.executeQuery("SELECT * FROM ALBUMS WHERE albumID='"+albumID+"'");
        String name = null;
        Date date = null;
        while (rs.next()) {
            name = rs.getString("name");
            date = rs.getDate("releasedate");
        }

        String sql = "SELECT NAME FROM ARTISTS WHERE ARTISTID = (SELECT ARTISTID FROM ALBUMS WHERE ALBUMID = "+albumID+")";
        System.out.println(sql);
        rs = s.executeQuery(sql);
        String artistName = "";
        while(rs.next()) {
            artistName = rs.getString("name");
        }
        //show all music data
        rs = s.executeQuery("SELECT * FROM MUSICS WHERE albumID='"+albumID+"'");
        String musicName = null;
        ArrayList<String> musics = new ArrayList<>();


        while (rs.next()) {
            musicName = rs.getString("name");
            musics.add(musicName);
        }

        double averageRating = average(ratings);

        result += name + " was made by "+artistName+" and was released in " + date + ". this album has the songs: \n";
        int i = 1;
        for(String str : musics) {
            result += i +". "+str+"\n";
            i++;
        }

        if(ratings.size() != 0) {
            result += "the average rating is " + averageRating + " with " + ratings.size() + " review/s\n";
            result += "album reviews: \n";
            i = 1;
            for(String str : reviews) {
                result += i +". "+str+"\n";
                i++;
            }
        }else {
            result += "the album has no reviews yet.";
        }
        return result;
    }

    /**
     * gets artist data from one specific artist
     * @param artistID id of the artist that user wants the data
     * @return string with artist data
     * @throws SQLException
     */
    public static String artistData(int artistID) throws SQLException {
        Connection c = enterDatabase(dbname);
        String result = "";
        Statement s = c.createStatement();
        ResultSet rs = s.executeQuery("SELECT * FROM ARTISTS WHERE artistID='"+artistID+"'");
        String name = null;
        String description = null;
        while (rs.next()) {
            name = rs.getString("name");
            description = rs.getString("description");
        }
        rs = s.executeQuery("SELECT * FROM ALBUMS WHERE artistID='"+artistID+"'");
        ArrayList<String> albumsNames = new ArrayList<>();
        ArrayList<Double> albumsRating = new ArrayList<>();
        ArrayList<Integer> albumsIDs = new ArrayList<>();
        while (rs.next()) {
            albumsNames.add(rs.getString("name"));
            albumsIDs.add(rs.getInt("albumID"));
        }

        for(int i : albumsIDs) {
            ArrayList<Double> ratings = new ArrayList<>();
            rs = s.executeQuery("SELECT * FROM REVIEWS WHERE albumID='"+i+"'");
            while (rs.next()) {
                ratings.add(Double.parseDouble(rs.getInt("rating")+""));
            }
            if(ratings.size() != 0) {
                albumsRating.add(average(ratings));
            }
        }

        rs = s.executeQuery("SELECT * FROM MUSICS WHERE artistID='"+artistID+"'");
        ArrayList<String> musicsNames = new ArrayList<>();
        while (rs.next()) {
            musicsNames.add(rs.getString("name"));
        }
        if(albumsRating.size() == 0) {
            result += "the artist " + name + " has " + albumsNames.size() + " album/s with no reviews and " + musicsNames.size()+ " music/s.\n";
        }
        else {
            result += "the artist " + name + " has " + albumsNames.size() + " album/s with an average of " + average(albumsRating) + " and " + musicsNames.size() + " musics.\n";
        }
        result += "artist description: " + description +"\n";
        if(albumsNames.size() > 0) {
            result += "artist albums: \n";
            int i = 1;
            for(String str : albumsNames) {
                result += i+". " +str+ "\n";
                i++;
            }
        }
        return result;
    }

    /**
     * share one music with one user (adds one row to database with user, musicid and cloudmusicid)
     * @param cloudMusicID id of the music in the cloud
     * @param userToShare user that will have access to one music
     * @throws SQLException
     */
    public static void shareMusicWithUser(int cloudMusicID, String userToShare) throws SQLException {
        Connection c = enterDatabase(dbname);
        Statement s = c.createStatement();
        ResultSet rs = s.executeQuery("SELECT * FROM cloudmusics WHERE cloudMusicID='"+cloudMusicID+"'");
        int musicID = 0;
        byte[] array = null;
        while (rs.next()) {
            musicID = rs.getInt("musicID");
            array = rs.getBytes("musicFile");
        }
        SQL.enterArrayInTable("cloudmusics", array, musicID, userToShare);

    }

    /**
     * adds info to database albumedits about the edits that one user has done to one album
     * @param username of the user that edited the album
     * @param albumID id of the album edited
     * @throws SQLException
     */
    public static void userEditedAlbum(String username, int albumID) throws SQLException {
        Connection c = enterDatabase(dbname);
        Statement s = c.createStatement();
        ResultSet rs = s.executeQuery("SELECT username FROM albumEdits WHERE albumID='"+albumID+"'");
        boolean alreadyInTable = false;
        while(rs.next()) {
            if(username.equals(rs.getString("username"))) {
                alreadyInTable = true;
            }
        }
        if(!alreadyInTable) {
            String sql = "INSERT INTO albumEdits(username, albumid) values('" + username + "'," + albumID + ")";
            s = c.createStatement();
            s.executeUpdate(sql);
        }
    }

    /**
     * get array of users that have edited one certain album
     * @param albumID of the album edited
     * @return array of strings containing the users
     * @throws SQLException
     */
    public static ArrayList<String> getUsersThatEditAlbum(int albumID) throws SQLException {
        Connection c = enterDatabase(dbname);
        ArrayList<String> array = new ArrayList<>();
        Statement s = c.createStatement();
        ResultSet rs = s.executeQuery("SELECT username FROM albumEdits WHERE albumID='"+albumID+"'");
        while (rs.next()) {
            array.add(rs.getString("username"));
        }
        return array;
    }

    /**
     * get notifications in the database notifications of one user (while he was offline)
     * @param username of the user you want to search the notifications he has
     * @return string containing the notifications or "no notifications while you were offline" if user hasn't notifications
     * @throws SQLException
     */
    public static String getUserNotifications(String username) throws SQLException {
        Connection c = enterDatabase(dbname);
        ArrayList<String> arr = new ArrayList<>();
        Statement s = c.createStatement();
        String sql = "SELECT notificationtype FROM notifications WHERE username='"+username+"'";
        ResultSet rs = s.executeQuery(sql);
        while (rs.next()) {
            arr.add(rs.getString("notificationtype"));
        }
        String result = "";
        if(arr.size() > 0) {
            result = "NOTIFICATIONS WHEN YOU WERE OFFLINE: \n";
            for (String a : arr) {
                result += a + "\n";
            }
        }
        else {
            result = "\nno notifications when you were offline\n";
        }
        return result;
    }


    /**
     * remove one row from table in database (in this case notifications), remove the notifications already shown to user
     * @param table name which you want to remove the row
     * @param username of the user that already had notification shown
     * @throws SQLException
     */
    public static void removeRowFromTable(String table, String username) throws SQLException {
        Connection c = enterDatabase(dbname);
        String SQL = "DELETE FROM "+table+" WHERE username = '"+username+"'";
        PreparedStatement pstmt = c.prepareStatement(SQL);
        pstmt.executeUpdate();
    }

    /**
     * calculates the average in one array to calculate the average rating in one album
     * @param array to calculate the average in
     * @return double with array average
     */
    private static Double average(ArrayList<Double> array) {
        double sumRating = array.stream()
                .mapToDouble(a -> a)
                .sum();
        return sumRating / array.size();

    }



}



