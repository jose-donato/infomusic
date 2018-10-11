import java.rmi.*;
import java.rmi.server.*;
import java.net.*;

public class HelloImpl extends UnicastRemoteObject implements Hello {

	public HelloImpl() throws RemoteException {
		super();
	}

	public String sayHello() throws RemoteException {
		System.out.println("Printing on server...");
		return "ACK";
	}

	public void remote_print(String s) throws RemoteException {
		System.out.println("Server:" + s);
	}

	public void remote_print(Message m) throws RemoteException {
		System.out.println("Server:" + m);
	}

	public Message ping_pong(Message m) throws RemoteException {
		Message m1 = new Message("");
		m1.text = m.text + "....";
		return m1;
	}

	// =======================================================

	public static void main(String args[]) {

		try {
			HelloImpl h = new HelloImpl();
			Naming.rebind("hello", h);
			System.out.println("Hello Server ready.");
		} catch (RemoteException re) {
			System.out.println("Exception in HelloImpl.main: " + re);
		} catch (MalformedURLException e) {
			System.out.println("MalformedURLException in HelloImpl.main: " + e);
		}

	}

}