package com.company;

import java.io.File;
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
            //PARA APAGAR TBM -> So para criar a Base de Dados de Musica
            case "CreateDataBaseforsong":
                try {
                    createDataBaseForSongs();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            //PARA APAGAR TBM -> So para criar a Base de Dados de Musica
            // PARA APAGAR ESTE TAMBEM -> So para testar inserir uma musica na Base de Dados
            case "uploadbrink":
                File thefile = new File("H:\\OneDrive 1Tera\\OneDrive - dei.uc.pt\\Universidade - onedrive\\1ºsemestre - OneDrive\\SD\\PROJETO\\musicas\\james-tw-when-you-love-someone-official-video.mp3");

                try {
                    insertASongInDatabase(thefile);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            //PARA APAGAR TBM -> So para criar a Base de Dados de Musica

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

    private void createDataBaseForSongs() throws SQLException {
        // ISTO E SO PARA CRIAR A TABELA DE MUSICA NA BASE DE DADOS
        Connection c = new SQL().enterDatabase("infomusic");
        HashMap<String, String> arr = new HashMap<String, String>();
        arr.put("name_song", "VARCHAR(20) PRIMARY KEY"); // alterei aqui
        arr.put("file", "bytea"); // alterei aqui

        new SQL().createTable(c, "musica", arr);
        //ACABA AQUI !!!!!!!!!!!!!!!!!!!!!

    }

    private void insertASongInDatabase(File thefile) throws SQLException {
        Connection c = new SQL().enterDatabase("infomusic");
        new SQL().EnterFileInDatabase(c,"musica","When",thefile);
        //String[] a = {"name_song,file", "'when you love Someone',"+thefile};
        //new SQL().addValuesToTable(c, "users", a);


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
