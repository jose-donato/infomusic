import java.rmi.*;
import java.rmi.server.*;
import java.net.*;
import java.io.*;

public class HelloServer extends UnicastRemoteObject implements Hello_S_I {
	static Hello_C_I client;

	public HelloServer() throws RemoteException {
		super();
	}

	public void print_on_server(String s) throws RemoteException {
		System.out.println("> " + s);
	}

	public void subscribe(String name, Hello_C_I c) throws RemoteException {
		System.out.println("Subscribing " + name);
		System.out.print("> ");
		client = c;
	}

	// =======================================================

	public static void main(String args[]) {
		String a;

		System.getProperties().put("java.security.policy", "policy.all");
		System.setSecurityManager(new RMISecurityManager());

		InputStreamReader input = new InputStreamReader(System.in);
		BufferedReader reader = new BufferedReader(input);

		try {
			//User user = new User();
			HelloServer h = new HelloServer();
			Naming.rebind("XPTO", h);
			System.out.println("Hello Server ready.");
			while (true) {
				System.out.print("> ");
				a = reader.readLine();
				client.print_on_client(a);
				}
		} catch (Exception re) {
			System.out.println("Exception in HelloImpl.main: " + re);
		} 
	}
}
