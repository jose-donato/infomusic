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
            arr.put("name", "VARCHAR(30) NOT NULL");
            arr.put("releaseDate", "DATE NOT NULL");
            arr.put("picture", "bytea");
            SQL.createTable(c, "albums", arr);
            SQL.addForeignKeyToTable(c, "artists", "albums", "artistID");

            arr = new HashMap<>();
            arr.put("musicID", "SERIAL PRIMARY KEY NOT NULL");
            arr.put("albumID", "SERIAL NOT NULL");
            arr.put("artistID", "SERIAL NOT NULL");
            arr.put("name", "VARCHAR(30) NOT NULL");
            arr.put("genre", "VARCHAR(30) NOT NULL");
            arr.put("lyrics", "bytea");
            SQL.createTable(c, "musics", arr);
            SQL.addForeignKeyToTable(c, "albums", "musics", "albumID");
            SQL.addForeignKeyToTable(c, "artists", "musics", "artistID");




            /*arr = new HashMap<String, String>();
            arr.put("name", "VARCHAR(20) PRIMARY KEY");
            arr.put("file", "bytea");
            SQL.createTable(c, "musicsFiles", arr);
            */
            //String[] a = {"user1,pass1", "'josedonato','123123'"};
            //SQL.addValuesToTable(c, "users", a);
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

    public static void reviewToAlbum(Connection c, String review, int rating) throws SQLException {
        String sql = "UPDATE REVIEWS SET review = '"+review+"' AND RATING='"+rating+"' WHERE ..."; //terminar qd base de dados tiver feita
        Statement s = c.createStatement();
        s.executeUpdate(sql);
    }

    public static boolean enterFileInTable(Connection c, String table, String keysValues, String fileLocation) {
        File file = new File(fileLocation);
        PreparedStatement prestatement = null;
        try {

            //tbf
            String sql = "UPDATE albums SET picture = ? WHERE albumid = 1";

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

    public static boolean changeName(Connection c, String table, String newName, Integer albumID) throws SQLException {
        PreparedStatement ps = c.prepareStatement("UPDATE ? SET name = ? WHERE albumid = ?");
        ps.setString(1, table);
        ps.setString(2, newName);
        ps.setInt(3, albumID);
        ps.executeUpdate();
        ps.close();
        return false;
    }
    public static void printAllTable(Connection c, String table) throws SQLException {
        DBTablePrinter.printTable(c, table);
        //return allRows;
    }

}



