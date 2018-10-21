package com.company;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
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
            case "grantAdmin":
                try {
                    treatGrantAdmin(this.map);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            case "changeData":
                try {
                    treatChangeData(this.map);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            case "reviewAlbum":
                try {
                    treatWriteReview(this.map);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            case "albumDetail":
                try {
                    treatAlbumDetail(this.map);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            case "artistDetail":
                try {
                    treatArtistDetail(this.map);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            case "addSong":
                try {
                    treatAddMusic(this.map);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            case "addAlbum":
                try {
                    treatAddAlbum(this.map);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            case "addArtist":
                try {
                    treatAddArtist(this.map);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            case "uploadFileToTable":
                try {
                    treatUploadFile(this.map);
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



    private void treatUploadFile(HashMap<String, String> map) throws SQLException {
        String table = map.get("table");
        String column = map.get("column");
        String fileLocation = map.get("fileLocation");
        int id = Integer.parseInt(map.get("id"));
        Connection c = SQL.enterDatabase("infomusic");
        SQL.enterFileInTable(c, table, column, fileLocation, id);
    }

    private void treatAddArtist(HashMap<String, String> map) throws SQLException {
        String name = map.get("name");
        String description = map.get("description");
        Connection c = SQL.enterDatabase("infomusic");
        String a[] = {"name, description", "'"+name+"', '"+description+"'"};
        SQL.addValuesToTable(c, "musics", a);
    }

    private void treatAddAlbum(HashMap<String, String> map) throws SQLException {
        String name = map.get("name");
        Date date  = Date.valueOf(map.get("date"));
        int artistID = Integer.parseInt(map.get("artistID"));
        Connection c = SQL.enterDatabase("infomusic");
        String a[] = {"name, date, artistid", "'"+name+"', "+date+", "+artistID};
        SQL.addValuesToTable(c, "musics", a);
    }

    private void treatAddMusic(HashMap<String, String> map) throws SQLException {
        String name = map.get("name");
        String description = map.get("description");
        int duration = Integer.parseInt(map.get("duration"));
        int albumID = Integer.parseInt(map.get("albumID"));
        int artistID = Integer.parseInt(map.get("artistID"));
        Connection c = SQL.enterDatabase("infomusic");
        String a[] = {"name, description, duration, albumid, artistid", "'"+name+"', '"+description+"', "+duration+", "+albumID+", "+artistID};
        SQL.addValuesToTable(c, "musics", a);
    }

    private void treatArtistDetail(HashMap<String, String> map) throws SQLException {
        int artistToSearch = Integer.parseInt(map.get("artistToSearch"));
        Connection c = SQL.enterDatabase("infomusic");
        String result = SQL.artistData(c, artistToSearch);
        HashMap<String, String> mapResult = new HashMap<>();
        mapResult.put("type", "artistDetailResponse");
        mapResult.put("resultString", result);
        ConnectionFunctions.sendUdpPacket(mapResult);
    }

    private void treatAlbumDetail(HashMap<String, String> map) throws SQLException {
        int albumToSearch = Integer.parseInt(map.get("albumToSearch"));
        Connection c = SQL.enterDatabase("infomusic");
        String result = SQL.albumData(c, albumToSearch);
        HashMap<String, String> mapResult = new HashMap<>();
        mapResult.put("type", "albumDetailResponse");
        mapResult.put("resultString", result);
        ConnectionFunctions.sendUdpPacket(mapResult);
    }

    private void treatWriteReview(HashMap<String, String> map) throws SQLException {
        int albumToReviewID = Integer.parseInt(map.get("albumToReviewID"));
        int albumRating = Integer.parseInt(map.get("albumRating"));
        System.out.println(albumRating);
        System.out.println(albumToReviewID);
        String albumReview = map.get("albumReview");
        Connection c = SQL.enterDatabase("infomusic");
        SQL.reviewToAlbum(c, albumReview, albumRating, albumToReviewID);
    }

    private void treatChangeData(HashMap<String, String> map) throws SQLException {
        String tableName = map.get("tableName");
        String columnType = map.get("columnType");
        String newName = map.get("newName");
        Integer tableID = Integer.parseInt(map.get("tableID"));

        Connection c = SQL.enterDatabase("infomusic");
        SQL.changeName(c, tableName, newName, tableID, columnType);
    }

    private void treatGrantAdmin(HashMap<String, String> map) throws SQLException {
        String username = map.get("username");
        Connection c = SQL.enterDatabase("infomusic");
        if(SQL.grantAdminToUser(c, username)) {
            ConnectionFunctions.sendUdpPacket(auxGrantAdmin(username, "true"));
        }
        else {
            ConnectionFunctions.sendUdpPacket(auxGrantAdmin(username, "false"));
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
    private HashMap<String, String> auxGrantAdmin(String username, String exists) {
        HashMap<String, String> hmap = new HashMap<String, String>();
        hmap.put("type", "checkGrantAdmin");
        hmap.put("username", username);
        hmap.put("condition", exists);
        return hmap;
    }
}
