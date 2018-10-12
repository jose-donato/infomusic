//package hello2;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.*;

public class HelloServer extends UnicastRemoteObject implements Hello {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public HelloServer() throws RemoteException {
		super();
	}

	public String sayHello() throws RemoteException {
		System.out.println("print do lado do servidor...!.");

		return "Hello, World!";
	}

	// =========================================================
	public static void main(String args[]) {

		try {
			HelloServer h = new HelloServer();
			Registry r = LocateRegistry.createRegistry(7000);
			r.rebind("benfica", h);
			System.out.println("Hello Server ready.");
		} catch (RemoteException re) {
			System.out.println("Exception in HelloImpl.main: " + re);
		}
	}

}