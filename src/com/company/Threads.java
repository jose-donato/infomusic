package com.company;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Threads class
 * treats all requests to multicast server
 */
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
                break;
            case "changeData":
                try {
                    treatChangeData(this.map);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
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
            case "addMusic":
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
                } catch (ParseException e) {
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
            case "getTable":
                try {
                    treatGetTable(this.map);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            case "setMusicIDToDownload":
                try {
                    treatGetMusicIDFromCloud(this.map);
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "shareMusicInCloud":
                try {
                    treatShareMusicInCloud(this.map);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            case "userEditAlbum":
                try {
                    treatUserEditAlbum(this.map);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            case "notifyUsersAboutAlbumDescriptionEdit":
                try {
                    treatNotifyUsersAboutAlbumDescriptionEdit(this.map);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            case "addUsersToAlbumEditedNotificationTable":
                try {
                    treatAddUsersToAlbumEditedNotificationTable(this.map);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            case "notifyUserAboutAdminGranted":
                try {
                    treatNotifyUserAboutAdminGranted(this.map);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            case "checkNotifications":
                try {
                    treatCheckNotifications(this.map);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            case "clearNotifications":
                try {
                    treatClearNotifications(this.map);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    /**
     * treat request to clear table notifications from one user
     * @param map
     * @throws SQLException
     */
    private void treatClearNotifications(HashMap<String, String> map) throws SQLException {
        String username = map.get("user");
        SQL.removeRowFromTable("notifications", username);
    }


    /**
     * treat request to get notifications from one user that had been offline
     * @param map
     * @throws SQLException
     */
    private void treatCheckNotifications(HashMap<String, String> map) throws SQLException {
        String username = map.get("user");
        String result = SQL.getUserNotifications(username);
        HashMap<String, String> hmap = new HashMap<>();
        hmap.put("type", "treatCheckNotificationsResponse");
        hmap.put("result", result);
        ConnectionFunctions.sendUdpPacket(hmap);
    }

    /**
     * treat request to add to notifications table that one user was granted with admin
     * @param map
     * @throws SQLException
     */
    private void treatNotifyUserAboutAdminGranted(HashMap<String, String> map) throws SQLException {
        String user = map.get("user");
        String[] a = {"username, notificationType", "'"+user+"', 'you now have admin permissions!'"};
        SQL.addValuesToTable("notifications", a);
    }

    /**
     * treat request to add to notifications table that one user had changed one album description
     * @param map
     * @throws SQLException
     */
    private void treatAddUsersToAlbumEditedNotificationTable(HashMap<String, String> map) throws SQLException {
        String users = map.get("users");
        ArrayList<String> usersThatEditedAlbum = new ArrayList<String>(Arrays.asList(users.split(";")));
        for(String u : usersThatEditedAlbum) {
            String[] a = {"username, notificationType", "'"+u+"', 'one album you edited was changed!'"};
            SQL.addValuesToTable("notifications", a);
        }
    }

    /**
     * treat request to get all users that changed one certain album
     * @param map
     * @throws SQLException
     */
    private void treatNotifyUsersAboutAlbumDescriptionEdit(HashMap<String, String> map) throws SQLException {
        int albumID = Integer.parseInt(map.get("albumID"));
        ArrayList<String> names = SQL.getUsersThatEditAlbum(albumID);
        String arrayNames = "";
        HashMap<String, String> hmap = new HashMap<>();
        if(names.size() > 0) {
            for (String s : names) {
                arrayNames += s + ";";
            }
            arrayNames = arrayNames.substring(0, arrayNames.length() - 1);
        }
        else {
            arrayNames = "no users";
        }
        hmap.put("type", "treatNotifyUsersAboutAlbumDescriptionEditResponse");
        hmap.put("result", arrayNames);
        ConnectionFunctions.sendUdpPacket(hmap);
    }

    /**
     * treat request to add to database albumedits that one user changed one specific album
     * @param map
     * @throws SQLException
     */
    private void treatUserEditAlbum(HashMap<String, String> map) throws SQLException {
        String username = map.get("username");
        int albumID = Integer.parseInt(map.get("albumID"));
        SQL.userEditedAlbum(username, albumID);
    }

    /**
     * treat request to share one music with one user
     * @param map
     * @throws SQLException
     */
    private void treatShareMusicInCloud(HashMap<String, String> map) throws SQLException {
        String username = map.get("username");
        int musicID = Integer.parseInt(map.get("musicID"));
        SQL.shareMusicWithUser(musicID, username);
    }

    /**
     * treat request to get one music id from cloud
     * @param map
     * @throws SQLException
     * @throws IOException
     */
    private void treatGetMusicIDFromCloud(HashMap<String, String> map) throws SQLException, IOException {
        String username = map.get("username");
        int musicID = Integer.parseInt(map.get("musicID"));
        ConnectionFunctions.sendMusicFromMulticastServer(musicID, username);
    }

    /**
     * treat request to get one table from database, can be artists, musics, albums, cloudmusics or users
     * @param map
     * @throws SQLException
     */
    private void treatGetTable(HashMap<String, String> map) throws SQLException {
        String table = map.get("table");
        String result = "";
        if(table.toLowerCase().equals("artists")) {
            result = SQL.getArtistsTable();
        }
        else if(table.toLowerCase().equals("musics")) {
            result = SQL.getMusicsTable();
        }
        else if(table.toLowerCase().equals("albums")) {
            result = SQL.getAlbumsTable();
        }
        else if(table.toLowerCase().equals("cloudmusics")) {
            result = SQL.getMusicsCloudTable(map.get("username"));
        }
        else if(table.toLowerCase().equals("users")) {
            result = SQL.getUsersTable();
        }
        HashMap<String, String> hmap = new HashMap<>();
        hmap.put("type", "getTablesResponse");
        hmap.put("table", table);
        hmap.put("result", result);
        ConnectionFunctions.sendUdpPacket(hmap);
    }


    /**
     * treat request to upload one file to database
     * @param map
     * @throws SQLException
     */
    private void treatUploadFile(HashMap<String, String> map) throws SQLException {
        String table = map.get("table");
        String column = map.get("column");
        String fileLocation = map.get("fileLocation");
        int id = Integer.parseInt(map.get("id"));
        SQL.enterFileInTable(table, column, fileLocation, id);
    }

    /**
     * treat request to add one artist to database
     * @param map
     * @throws SQLException
     */
    private void treatAddArtist(HashMap<String, String> map) throws SQLException {
        String name = map.get("name");
        String description = map.get("description");
        if(description == "no description") {
            description = null;
        }
        String a[] = {"name, description", "'"+name+"', '"+description+"'"};
        SQL.addValuesToTable("artists", a);
    }

    /**
     * treat request to add one album to database
     * @param map
     * @throws SQLException
     */
    private void treatAddAlbum(HashMap<String, String> map) throws SQLException, ParseException {
        String name = map.get("name");
        String genre = map.get("genre");
        String dateString  = map.get("date");
        String description = map.get("description");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd");
        java.util.Date parsed = format.parse(dateString);
        java.sql.Date sqlDate = new java.sql.Date(parsed.getTime());
        int artistID = Integer.parseInt(map.get("artistID"));
        String a[] = {"name, genre, releasedate, artistid, description", "'"+name+"', '"+genre+"','"+sqlDate+"', "+artistID+",'"+description+"'"};
        SQL.addValuesToTable("albums", a);
    }

    /**
     * treat request to add one music to database
     * @param map
     * @throws SQLException
     */
    private void treatAddMusic(HashMap<String, String> map) throws SQLException {
        String name = map.get("name");
        String description = map.get("description");
        if(description == "no description") {
            description = null;
        }
        int duration = Integer.parseInt(map.get("duration"));
        int albumID = Integer.parseInt(map.get("albumID"));
        int artistID = Integer.parseInt(map.get("artistID"));
        String a[] = {"name, description, duration, albumid, artistid", "'"+name+"', '"+description+"', "+duration+", "+albumID+", "+artistID};
        SQL.addValuesToTable("musics", a);
    }

    /**
     * treat request to get data about one certain artist
     * @param map
     * @throws SQLException
     */
    private void treatArtistDetail(HashMap<String, String> map) throws SQLException {
        int artistToSearch = Integer.parseInt(map.get("artistToSearch"));
        String result = SQL.artistData(artistToSearch);
        HashMap<String, String> mapResult = new HashMap<>();
        mapResult.put("type", "artistDetailResponse");
        mapResult.put("resultString", result);
        ConnectionFunctions.sendUdpPacket(mapResult);
    }

    /**
     * treat request to get data about one certain album
     * @param map
     * @throws SQLException
     */
    private void treatAlbumDetail(HashMap<String, String> map) throws SQLException {
        int albumToSearch = Integer.parseInt(map.get("albumToSearch"));
        String result = SQL.albumData(albumToSearch);
        HashMap<String, String> mapResult = new HashMap<>();
        mapResult.put("type", "albumDetailResponse");
        mapResult.put("resultString", result);
        ConnectionFunctions.sendUdpPacket(mapResult);
    }

    /**
     * treat request to add one review (with rating) to database
     * @param map
     * @throws SQLException
     */
    private void treatWriteReview(HashMap<String, String> map) throws SQLException {
        int albumToReviewID = Integer.parseInt(map.get("albumToReviewID"));
        int albumRating = Integer.parseInt(map.get("albumRating"));
        System.out.println(albumRating);
        System.out.println(albumToReviewID);
        String albumReview = map.get("albumReview");
        SQL.reviewToAlbum(albumReview, albumRating, albumToReviewID);
    }

    /**
     * treat request to change data of one table in the database
     * @param map
     * @throws SQLException
     */
    private void treatChangeData(HashMap<String, String> map) throws SQLException {
        String tableName = map.get("tableName");
        String columnType = map.get("columnType");
        String newName = map.get("newName");
        Integer tableID = Integer.parseInt(map.get("tableID"));
        SQL.changeName(tableName, newName, tableID, columnType);
    }

    /**
     * treat request to grant one user admin permissions
     * @param map
     * @throws SQLException
     */
    private void treatGrantAdmin(HashMap<String, String> map) throws SQLException {
        String username = map.get("username");
        HashMap<String, String> hmap = new HashMap<String, String>();
        hmap.put("type", "checkGrantAdmin");
        hmap.put("username", username);
        if(SQL.grantAdminToUser(username)) {
            hmap.put("condition", "true");
            ConnectionFunctions.sendUdpPacket(hmap);
        }
        else {
            hmap.put("condition", "false");
            ConnectionFunctions.sendUdpPacket(hmap);
        }
    }

    /**
     * treat request to verify the login for one user
     * @param map
     * @throws SQLException
     */
    private void treatLogin(HashMap<String, String> map) throws SQLException {
        String username = map.get("username");
        String password = map.get("password");
        String [] user = SQL.selectUserAndGetPassword(username);
        String return_username = user[0];
        String return_pass = user[1];
        HashMap<String, String> hmap = new HashMap<String, String>();
        hmap.put("type", "checkIfExists");
        hmap.put("username", return_username);
        hmap.put("password", return_pass);

        if(return_username != null && return_pass.equals(password)) {
            hmap.put("condition", "true");
            ConnectionFunctions.sendUdpPacket(hmap);
        }
        else {
            hmap.put("condition", "false");
            ConnectionFunctions.sendUdpPacket(hmap);
        }
    }

    /**
     * treat request to make the regist of one user
     * @param map
     * @throws SQLException
     */
    private void treatRegister(HashMap<String, String> map) throws SQLException {
        String username = map.get("username");
        String user = SQL.selectUser("USERS", username);
        HashMap<String, String> hmap = new HashMap<String, String>();
        hmap.put("type", "checkIfExists");
        hmap.put("username", username);
        if(user == null) {
            hmap.put("condition", "true");
            ConnectionFunctions.sendUdpPacket(hmap);
            String[] arr;
            if(SQL.checkIftableIsEmpty("users")) {
                arr = new String[]{"username,password,isAdmin", "'" + username + "','" + map.get("password") + "',true"};
            }
            else {
                arr = new String[]{"username,password,isAdmin", "'" + username + "','" + map.get("password") + "',false"};
            }

            SQL.addValuesToTable("USERS", arr);
        }
        else {
            hmap.put("condition", "false");
            ConnectionFunctions.sendUdpPacket(hmap);
        }
    }

    /**
     * treat request to check if one user is admin or not
     * @param map
     * @throws SQLException
     */
    private void treatVerifyAdmin(HashMap<String, String> map) throws SQLException {
        String username = map.get("username");
        HashMap<String, String> hmap = new HashMap<String, String>();
        hmap.put("type", "checkIfAdminExists");
        hmap.put("username", username);
        if(SQL.checkIfUserIsAdmin(username)) {
            hmap.put("condition", "true");
            ConnectionFunctions.sendUdpPacket(hmap);
        }
        else{
            hmap.put("condition", "false");
            ConnectionFunctions.sendUdpPacket(hmap);
        }
    }

}
