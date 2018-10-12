//package hello;
import java.rmi.*;
import java.rmi.server.*;
import java.net.*;

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
			Naming.rebind("rmi://localhost:7000/benfica", h);
			System.out.println("Hello Server ready.");
		} catch (RemoteException re) {
			System.out.println("Exception in HelloImpl.main: " + re);
		} catch (MalformedURLException e) {
			System.out.println("MalformedURLException in HelloImpl.main: " + e);
		}

	}

}