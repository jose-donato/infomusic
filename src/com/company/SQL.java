package com.company;

import javax.swing.plaf.nimbus.State;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQL {
    private String serverUrl = "jdbc:postgresql://localhost:5432/";

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

    public void createTable(Connection c, String name, String[] values) {
        Statement statement = null;
        try {
            statement = c.createStatement();
            //optimize values iterations may be needed hashmap to set primary keys and if its text or number
            String sql = "CREATE TABLE "+ name.toUpperCase() +
                    "(" + values[0].toUpperCase() +" TEXT PRIMARY KEY NOT NULL, " +
                    values[1].toUpperCase() + " TEXT NOT NULL)";
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

    public void addValuesToTable(Connection c, String table, String[] values) {
        Statement statement = null;
        try {
            statement = c.createStatement();
            //tbf
            String sql = "INSERT INTO "+ table.toUpperCase()+ " (" + values[0].toUpperCase()+", "+values[1].toUpperCase() +") "
                    + "VALUES ('user1', 'pass1');";
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
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