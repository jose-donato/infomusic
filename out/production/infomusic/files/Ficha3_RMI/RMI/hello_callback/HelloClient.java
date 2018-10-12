import java.rmi.*;
import java.rmi.server.*;
import java.net.*;
import java.io.*;

public class HelloClient extends UnicastRemoteObject implements Hello_C_I {

	HelloClient() throws RemoteException {
		super();
	}

	public void print_on_client(String s) throws RemoteException {
		System.out.println("> " + s);
	}

	public static void main(String args[]) {
		String a;
		// usage: java HelloClient username
		System.getProperties().put("java.security.policy", "policy.all");
		System.setSecurityManager(new RMISecurityManager());
 
		InputStreamReader input = new InputStreamReader(System.in);
		BufferedReader reader = new BufferedReader(input);
		try {
			//User user = new User();
			Hello_S_I h = (Hello_S_I) Naming.lookup("XPTO");
			HelloClient c = new HelloClient();
			h.subscribe(args[0], (Hello_C_I) c);
			System.out.println("Client sent subscription to server");
			while (true) {
				System.out.print("> ");
				a = reader.readLine();
				h.print_on_server(a);
			}

		} catch (Exception e) {
			System.out.println("Exception in main: " + e);
		}

	}

}
