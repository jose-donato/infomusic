package com.company;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class RMIServer extends UnicastRemoteObject implements Interface {


        private static final long serialVersionUID = 1L;

        protected RMIServer() throws RemoteException {
            super();
        }


        public double add(double a, double b) {
            return a + b;
        }


        /**
         * @param args
         * @throws RemoteException
         */
        public static void main(String[] args) throws RemoteException {
            CalculatorInterface ci = new Calculator();
            LocateRegistry.createRegistry(1099).rebind("calc", ci);
            System.out.println("Calculator ready...");
        }

    }

}
