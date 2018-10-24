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
 *
 */
public final class SQL {
    private static String serverUrl = "jdbc:postgresql://localhost:5432/";

    /**
     * test to create initial values for user
     * @return
     */
    public static Connection initialConfig() {
        try {
            Connection c = enterDatabase("infomusic");

            HashMap<String, String> arr = new HashMap<String, String>();
            arr.put("username", "VARCHAR(20) PRIMARY KEY NOT NULL");
            arr.put("password", "VARCHAR(20) NOT NULL");
            arr.put("isAdmin", "Boolean NOT NULL");

            SQL.createTable(c, "users", arr);

            arr = new HashMap<>();
            arr.put("artistID", "SERIAL PRIMARY KEY NOT NULL");
            arr.put("name", "VARCHAR(30) NOT NULL");
            arr.put("description", "VARCHAR(200) NOT NULL");
            SQL.createTable(c, "artists", arr);

            arr = new HashMap<>();
            arr.put("albumID", "SERIAL PRIMARY KEY NOT NULL");
            arr.put("artistID", "SERIAL NOT NULL");
            arr.put("genre", "VARCHAR(30) NOT NULL");
            arr.put("name", "VARCHAR(30) NOT NULL");
            arr.put("description", "VARCHAR(200) NOT NULL");
            arr.put("releaseDate", "DATE NOT NULL");
            arr.put("picture", "bytea");
            SQL.createTable(c, "albums", arr);
            SQL.addForeignKeyToTable(c, "artists", "albums", "artistID");

            arr = new HashMap<>();
            arr.put("musicID", "SERIAL PRIMARY KEY NOT NULL");
            arr.put("albumID", "SERIAL NOT NULL");
            arr.put("artistID", "SERIAL NOT NULL");
            arr.put("name", "VARCHAR(30) NOT NULL");
            arr.put("description", "VARCHAR(200)");
            arr.put("duration", "INTEGER NOT NULL");
            arr.put("lyrics", "bytea");
            SQL.createTable(c, "musics", arr);
            SQL.addForeignKeyToTable(c, "albums", "musics", "albumID");
            SQL.addForeignKeyToTable(c, "artists", "musics", "artistID");



            arr = new HashMap<>();
            arr.put("reviewID", "SERIAL PRIMARY KEY NOT NULL");
            arr.put("albumID", "SERIAL NOT NULL");
            arr.put("rating", "INTEGER NOT NULL");
            arr.put("review", "VARCHAR(300) NOT NULL");
            SQL.createTable(c, "reviews", arr);
            SQL.addForeignKeyToTable(c, "albums", "reviews", "albumID");


            arr = new HashMap<>();
            arr.put("cloudMusicID", "SERIAL PRIMARY KEY NOT NULL");
            arr.put("username", "VARCHAR(20) NOT NULL");
            arr.put("musicID", "SERIAL NOT NULL");
            arr.put("musicFile", "BYTEA NOT NULL");
            SQL.createTable(c, "cloudMusics", arr);
            SQL.addForeignKeyToTable(c, "musics", "cloudmusics", "musicID");
            SQL.addForeignKeyToTable(c, "users", "cloudmusics", "username");


            arr = new HashMap<>();
            arr.put("notificationID", "SERIAL PRIMARY KEY NOT NULL");
            arr.put("username", "VARCHAR(20) NOT NULL");
            arr.put("notificationType", "VARCHAR(100) NOT NULL");
            SQL.createTable(c, "notifications", arr);
            SQL.addForeignKeyToTable(c, "users", "notifications", "username");


            arr = new HashMap<>();
            arr.put("notificationID", "SERIAL PRIMARY KEY NOT NULL");
            arr.put("username", "VARCHAR(20) NOT NULL");
            arr.put("notification", "VARCHAR(100) NOT NULL");
            SQL.createTable(c, "notifications", arr);
            SQL.addForeignKeyToTable(c, "users", "notifications", "username");

            /*arr = new HashMap<String, String>();
            arr.put("name", "VARCHAR(20) PRIMARY KEY");
            arr.put("file", "bytea");
            SQL.createTable(c, "musicsFiles", arr);

            //String[] a = {"user1,pass1", "'josedonato','123123'"};
            //SQL.addValuesToTable(c, "users", a);
            */

            //add values to table
            String[] a = {"name, description", "'Red Hot Chili Peppers', 'Red Hot Chili Peppers é uma banda de rock dos Estados Unidos formada em Los Angeles, Califórnia, em 13 de fevereiro de 1983, considerada uma das maiores bandas da história do rock.'"};
            SQL.addValuesToTable(c, "artists", a);
            String[] b = {"releasedate, name, genre,description, artistid", "now(),'Californication','Alternative Rock', 'Californication is a album made with love from california', 1"};
            SQL.addValuesToTable(c, "albums", b);
            String[] d = {"name, description, duration, albumid, artistid","'Around The World', 'blabla', 300, 1, 1"};
            SQL.addValuesToTable(c, "musics", d);

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
        try {
            String url = serverUrl + name;
            c = DriverManager.getConnection(url, "postgres", "postgres");
            System.out.println("d: connected to database " + name);
        } catch (SQLException e) {
            //e.printStackTrace();
            System.out.println("d: database doesn't exist.. creating");
            c = DriverManager.getConnection(serverUrl, "postgres", "postgres");
            //c.setAutoCommit(false);
            Statement statement = null;
            try {
                statement = c.createStatement();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            try {
                String sql = "CREATE DATABASE " + name;
                statement.executeUpdate(sql);
                System.out.println("d: database " + name + "created");
            } catch (SQLException e2) {
                System.out.println("d: error creating database "+name);
                e2.printStackTrace();
            }
        }
        return c;
    }

    /**
     * Create table in one database
     * @param c connection to the database
     * @param name of the table
     * @param values /atributes of the table (ex: 'USER TEXT PRIMARY KEY')
     */
    public static void createTable(Connection c, String name, HashMap<String, String> values) {
        Statement statement = null;
        try {
            statement = c.createStatement();
            //optimize values iterations may be needed hashmap to set primary keys and if its text or number
            String sql = "CREATE TABLE "+ name.toUpperCase() + " (";
            Iterator it = values.entrySet().iterator();
            while(it.hasNext()) {
                HashMap.Entry pair = (HashMap.Entry) it.next();
                sql += pair.getKey().toString().toUpperCase() + " " + pair.getValue().toString().toUpperCase() +" , ";
            }
            sql = sql.substring(0, sql.length() - 2);
            sql += ")";
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

    public static void addForeignKeyToTable(Connection c, String parentTable, String childTable, String column) throws SQLException {
        String sql = "ALTER TABLE "+childTable+"\n" +
                "ADD FOREIGN KEY ("+column+") REFERENCES "+parentTable+"("+column+"); ";
        Statement s = c.createStatement();
        s.executeUpdate(sql);
    }

    /**
     * add one value to one table
     * @param c connection to the table
     * @param table name of the table
     * @param keysValues
     */
    public static void addValuesToTable(Connection c, String table, String[] keysValues) {
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
     * select one user from the table
     * @param c
     * @param table
     * @param username
     * @return
     * @throws SQLException
     */
    public static String selectUser(Connection c, String table, String username) throws SQLException {
        Statement s = c.createStatement();
        ResultSet rs = s.executeQuery("SELECT * FROM USERS WHERE username='"+username+"'");
        String name = null;
        while (rs.next()) {
            name = rs.getString("username");
        }
        return name;
    }


    //think better about this function and how relates to selectUser()
    /**
     * get user and password from table
     * @param c database connection
     * @param username username desired
     * @return array with username and password
     * @throws SQLException
     */
    public static String[] selectUserAndGetPassword(Connection c, String username) throws SQLException {
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

    public static boolean checkIftableIsEmpty(Connection c, String table) throws  SQLException {
        String sql = "select true from "+table+" limit 1;";
        Statement s = c.createStatement();
        ResultSet rs = s.executeQuery(sql);
        while(rs.next()) {
            return false;
        }
        return true;
    }

    public static boolean checkIfUserIsAdmin(Connection c, String username) throws SQLException {
        Statement s = c.createStatement();
        ResultSet rs = s.executeQuery("SELECT * FROM USERS WHERE username='"+username+"'");
        Boolean isAdmin = false;
        while (rs.next()) {
            isAdmin = rs.getBoolean("isAdmin");
        }
        return isAdmin;
    }

    public static boolean grantAdminToUser(Connection c, String username) throws SQLException {
        String sql = "UPDATE USERS\n" +
                "SET isAdmin = true\n" +
                "WHERE username like '"+username+"';";
        Statement s = c.createStatement();
        s.executeUpdate(sql);
        if(checkIfUserIsAdmin(c, username)) {
            return true;
        }
        else {
            return false;
        }
        //notificar o utilizador
    }

    public static void reviewToAlbum(Connection c, String review, int rating, int albumID) throws SQLException {
        String sql = "INSERT INTO REVIEWS(albumid, rating, review) values("+albumID+", "+rating+", '"+review+"')";
        Statement s = c.createStatement();
        s.executeUpdate(sql);
    }

    public static boolean enterFileInTable(Connection c, String table, String column, String fileLocation, int id) {
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

    public static void enterArrayInTable(Connection c, String table, byte[] array, int id, String username) throws SQLException {
        String query = "INSERT INTO "+table+" (musicID, username, musicFile) VALUES ("+id+",'"+username+"',?)";
        PreparedStatement pstmt = c.prepareStatement(query);
        pstmt.setBytes(1, array);
        pstmt.execute();
    }

    public static byte[] getArrayInTable(Connection c, String table, int id, String username) throws SQLException {
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

    public static boolean getFileFromTable(Connection c) throws SQLException, IOException {
        PreparedStatement ps = c.prepareStatement("SELECT picture FROM albums WHERE albumid = 1");
        //ps.setString(1, "myimage.gif");
        ResultSet rs = ps.executeQuery();
        byte[] imgBytes = null;
        if (rs != null) {
            while (rs.next()) {
                imgBytes = rs.getBytes(1);
                // use the data in some way here
            }
            rs.close();
        }
        ps.close();
        FileUtils.writeByteArrayToFile(new File("C:\\Users\\zmcdo\\Desktop\\rhc.png"), imgBytes);
        return false;
    }

    public static boolean changeName(Connection c, String table, String newName, Integer ID, String column) throws SQLException {
        String name = table.substring(0, table.length()-1);
        String sql = "UPDATE "+table+" SET name = '"+newName+"' WHERE "+name+"id = "+ID;
        PreparedStatement ps = c.prepareStatement(sql);
        ps.executeUpdate();
        ps.close();
        return false;
    }
    /*public static void printAllTable(Connection c, String table) throws SQLException {
        DBTablePrinter.printTable(c, table);
        //return allRows;
    }*/


    public static String getArtistsTable(Connection c) throws SQLException {
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
            result += artistIDs.get(i) +". "+ artistNames.get(i);
        }
        return result;
    }
    public static String getAlbumsTable(Connection c) throws SQLException {
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
            result += albumIDs.get(i) +". "+ albumNames.get(i);
        }
        return result;
    }

    public static String getMusicsTable(Connection c) throws SQLException {
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

    public static String albumData(Connection c, Integer albumID) throws SQLException {
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


        //show all music data
        rs = s.executeQuery("SELECT * FROM MUSICS WHERE albumID='"+albumID+"'");
        String musicName = null;
        ArrayList<String> musics = new ArrayList<>();


        while (rs.next()) {
            musicName = rs.getString("name");
            musics.add(musicName);
        }

        double averageRating = average(ratings);

        result += name + " was released in " + date+ ". this album has the songs: \n";
        int i = 1;
        for(String str : musics) {
            result += i +". "+str+"\n";
            i++;
        }

        if(ratings.size() != 0) {
            result += "the average rating is " + averageRating + " with " + ratings.size() + " review/s\n";
            result += "album reviews: ";
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

    public static String artistData(Connection c, int artistID) throws SQLException {
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
        //exceeds the limit of the string that can goes in udp
        //result += "description of the artist: " + description + "\n";
        result += "\n";
        return result;
    }

    public static void shareMusicWithUser(Connection c, int cloudMusicID, String userToShare) throws SQLException {
        Statement s = c.createStatement();
        ResultSet rs = s.executeQuery("SELECT * FROM cloudmusics WHERE cloudMusicID='"+cloudMusicID+"'");
        int musicID = 0;
        byte[] array = null;
        while (rs.next()) {
            musicID = rs.getInt("musicID");
            array = rs.getBytes("musicFile");
        }
        SQL.enterArrayInTable(c, "cloudmusics", array, musicID, userToShare);

    }

    private static Double average(ArrayList<Double> array) {
        double sumRating = array.stream()
                .mapToDouble(a -> a)
                .sum();
        return sumRating / array.size();

    }
}



