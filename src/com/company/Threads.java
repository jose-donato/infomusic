package com.company;

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
        }
    }

    private void treatLogin(HashMap<String, String> map) throws SQLException {
        String username = map.get("username");
        String password = map.get("password");
        Connection c = new SQL().enterDatabase("infomusic");
        String [] user = new SQL().selectUserAndGetPassword(c, "USERS", username);
        String return_username = user[0];
        String return_pass = user[1];
        if(return_username != null && return_pass.equals(password)) {
            new ConnectionFunctions().sendUdpPacket(auxForArray(return_username, return_pass, "true"));
        }
        else {
            new ConnectionFunctions().sendUdpPacket(auxForArray(return_username,return_pass, "false"));
        }
    }

    private void treatRegister(HashMap<String, String> map) throws SQLException {
        String username = map.get("username");
        Connection c = new SQL().enterDatabase("infomusic");
        String user = new SQL().selectUser(c, "USERS", username);
        System.out.println("\n\n\n"+user+"\n\n\n");
        if(user == null) {
            new ConnectionFunctions().sendUdpPacket(aux(username, "true"));
            String[] arr = {"user1,pass1", "'"+username+"','"+map.get("password")+"'"};
            new SQL().addValuesToTable(c, "USERS", arr);
        }
        else {
            //corrigir
            new ConnectionFunctions().sendUdpPacket(aux(username, "false"));
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
}
