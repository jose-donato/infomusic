//package hello;
import java.rmi.*;

public class HelloClient {

	public static void main(String args[]) {

		/* This might be necessary if you ever need to download classes:
		System.getProperties().put("java.security.policy", "policy.all");
		System.setSecurityManager(new RMISecurityManager());
		*/

		try {

			Hello h = (Hello) Naming.lookup("rmi://localhost:7000/benfica");

			String message = h.sayHello();
			System.out.println("HelloClient: " + message);
		} catch (Exception e) {
			System.out.println("Exception in main: " + e);
			e.printStackTrace();
		}

	}

}