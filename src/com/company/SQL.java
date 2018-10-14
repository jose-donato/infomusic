package com.company;

import javax.swing.plaf.nimbus.State;
import java.sql.*;
import java.util.HashMap;
import java.util.Iterator;

/**
 *
 */
public class SQL {
    private String serverUrl = "jdbc:postgresql://localhost:5432/";

    /**
     * enter in postgressql database or create if it doesn't exist
     * @param name of the database
     * @return the connection to the database in success, null otherwise
     * @throws SQLException
     */
    public Connection enterDatabase(String name) throws SQLException {
        Connection c = null;
        try {
            String url = serverUrl + name;
            c = DriverManager.getConnection(url, "postgres", "postgres");
            System.out.println("d: connected to database " + name);
        } catch (SQLException e) {
            //e.printStackTrace();
            System.out.println("d: database doesn't exist.. creating");
            c = DriverManager.getConnection(serverUrl, "postgres", "postgres");
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
    public void createTable(Connection c, String name, HashMap<String, String> values) {
        Statement statement = null;
        try {
            statement = c.createStatement();
            //optimize values iterations may be needed hashmap to set primary keys and if its text or number
            String sql = "CREATE TABLE "+ name.toUpperCase() + " (";
            Iterator it = values.entrySet().iterator();
            while(it.hasNext()) {
                HashMap.Entry pair = (HashMap.Entry) it.next();
                sql += pair.getKey().toString().toUpperCase() + " " + pair.getValue().toString().toUpperCase() +" NOT NULL, ";
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

    /**
     * add one value to one table
     * @param c connection to the table
     * @param table name of the table
     * @param keysValues
     */
    public void addValuesToTable(Connection c, String table, String[] keysValues) {
        Statement statement = null;
        try {
            statement = c.createStatement();
            //tbf
            String sql = "INSERT INTO "+ table.toUpperCase()+ " (" + keysValues[0].toUpperCase()+") "
                    + "VALUES (" + keysValues[1] + ");";
            System.out.println(sql);
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
    public String selectUser(Connection c, String table, String username) throws SQLException {
        Statement s = c.createStatement();
        ResultSet rs = s.executeQuery("SELECT * FROM USERS WHERE user1='"+username+"'");
        System.out.println(rs);
        String lastName = null;
        while (rs.next()) {
            lastName = rs.getString("user1");
        }
        return lastName;
    }
}



/*Connection c = null;
        try {
            c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/", "postgres", "postgres");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Statement statement = null;
        try {
            statement = c.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            statement.executeUpdate("CREATE DATABASE infomusic");
        } catch (SQLException e) {
            if (e.getErrorCode() == 0) {
                // Database already exists error
                try {
                    c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/infomusic", "postgres", "postgres");
                    statement = c.createStatement();
                    String sql = "CREATE TABLE USERS " +
                            "(USERNAME TEXT PRIMARY KEY     NOT NULL," +
                            " PASSWORD           TEXT    NOT NULL)";
                    try {
                        statement.executeUpdate(sql);
                    }
                    catch(SQLException e2) {
                        sql = "INSERT INTO USERS (USERNAME, PASSWORD) "
                                + "VALUES ('user1', 'pass1');";
                        statement.executeUpdate(sql);
                    }
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            } else {
                // Some other problems, e.g. Server down, no permission, etc
                e.printStackTrace();
            }
        }
        */