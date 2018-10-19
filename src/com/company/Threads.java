package com.company;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

public class Threads extends Thread {
    private HashMap<String, String> map;
    public Threads(HashMap<String, String> map) {
        this.map = map;
        this.start();
    }

    public void run() {
        switch(this.map.get("type")) {
            case "login":
                try {
                    treatLogin(this.map);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            case "register":
                try {
                    treatRegister(this.map);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            case "verifyAdmin":
                try {
                    treatVerifyAdmin(this.map);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            case "upload":
                String musicLocation = "C:\\Users\\JoséMariaCamposDonat\\Desktop\\macmiller.mp3";
                try {
                    ConnectionFunctions.uploadMusicTCP(musicLocation);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "download":
                String musicLocation2 = "C:\\Users\\JoséMariaCamposDonat\\Desktop\\macmiller.mp3";
                try {
                    ConnectionFunctions.downloadMusicTCP(musicLocation2);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void treatLogin(HashMap<String, String> map) throws SQLException {
        String username = map.get("username");
        String password = map.get("password");
        Connection c = SQL.enterDatabase("infomusic");
        String [] user = SQL.selectUserAndGetPassword(c, username);
        String return_username = user[0];
        String return_pass = user[1];
        if(return_username != null && return_pass.equals(password)) {
            ConnectionFunctions.sendUdpPacket(auxForArray(return_username, return_pass, "true"));
        }
        else {
            ConnectionFunctions.sendUdpPacket(auxForArray(return_username,return_pass, "false"));
        }
    }

    private void treatRegister(HashMap<String, String> map) throws SQLException {
        String username = map.get("username");
        Connection c = SQL.enterDatabase("infomusic");
        String user = SQL.selectUser(c, "USERS", username);
        System.out.println("\n\n\n"+user+"\n\n\n");
        if(user == null) {
            ConnectionFunctions.sendUdpPacket(aux(username, "true"));
            String[] arr;
            if(SQL.checkIftableIsEmpty(c, "users")) {
                arr = new String[]{"username,password,isAdmin", "'" + username + "','" + map.get("password") + "',true"};
            }
            else {
                arr = new String[]{"username,password,isAdmin", "'" + username + "','" + map.get("password") + "',false"};
            }

            SQL.addValuesToTable(c, "USERS", arr);
        }
        else {
            //corrigir
            ConnectionFunctions.sendUdpPacket(aux(username, "false"));
        }
    }

    private void treatVerifyAdmin(HashMap<String, String> map) throws SQLException {
        String username = map.get("username");
        Connection c = SQL.enterDatabase("infomusic");
        if(SQL.checkIfUserIsAdmin(c, username)) {
            ConnectionFunctions.sendUdpPacket(auxIsAdmin(username, "true"));
        }
        else{
            ConnectionFunctions.sendUdpPacket(auxIsAdmin(username, "false"));
        }
    }
    /**
     * create the hashmap the response by multicast if the user exists or dont
     * @param username
     * @param exists
     * @return
     */
    //aux para converter em hashmap a resposta do multicast se existe o utilziador ou nao
    private HashMap<String, String> aux(String username, String exists) {
        HashMap<String, String> hmap = new HashMap<String, String>();
        hmap.put("type", "checkIfExists");
        hmap.put("username", username);
        hmap.put("condition", exists);
        return hmap;
    }

    //aux para converter em hashmap a resposta do multicast se existe o utilziador ou nao
    private HashMap<String, String> auxForArray(String username, String password, String exists) {
        HashMap<String, String> hmap = new HashMap<String, String>();
        hmap.put("type", "checkIfExists");
        hmap.put("username", username);
        hmap.put("password", password);
        hmap.put("condition", exists);
        return hmap;
    }
    private HashMap<String, String> auxIsAdmin(String username, String exists) {
        HashMap<String, String> hmap = new HashMap<String, String>();
        hmap.put("type", "checkIfAdminExists");
        hmap.put("username", username);
        hmap.put("condition", exists);
        return hmap;
    }
}
