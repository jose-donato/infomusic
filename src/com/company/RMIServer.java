package com.company;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class RMIServer extends UnicastRemoteObject implements Interface {
    private static final long serialVersionUID = 1L;

    protected RMIServer() throws RemoteException {
        super();
    }


    public int login(String username, String password) {
        byte[] buffer = auxLogin(username, password).toString().getBytes();
        if(username.equals("ola") && password.equals("adeus")) {
            return 1;
        }
        else {
            return 0;
        }
    }

    public HashMap<String, String> auxLogin(String username, String password) {
        HashMap<String, String> hmap = new HashMap<String, String>();
        hmap.put("type", "login");
        hmap.put("username", username);
        hmap.put("password", password);
        return hmap;
    }

    public int register(String username, String password) {
        return 1;
    }


    /**
     * @param args
     * @throws RemoteException
     */
    public static void main(String[] args) throws RemoteException {
        Interface i = new RMIServer();
        LocateRegistry.createRegistry(1099).rebind("infoMusicRegistry", i);
        System.out.println("Server ready...");
    }

}

