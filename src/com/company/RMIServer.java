package com.company;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class RMIServer extends UnicastRemoteObject implements Interface {
    private static final long serialVersionUID = 1L;

    protected RMIServer() throws RemoteException {
        super();
    }


    public int login(String username, String password) {
        if(username.equals("ola") && password.equals("adeus")) {
            return 1;
        }
        else {
            return 0;
        }
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

