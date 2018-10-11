import java.rmi.*;

public class HelloClient {

	public static void main(String args[]) {

		System.getProperties().put("java.security.policy", "policy.all");
		System.setSecurityManager(new RMISecurityManager());

		try {

			Hello h = (Hello) Naming.lookup("hello");

			String a = h.sayHello();
			System.out.println(a);
			h.remote_print("print do client para o servidor...");
			Message m = new Message("Ola Bom Dia...");
			h.remote_print(m);
			Message m1 = h.ping_pong(m);
			System.out.println(m1);
			//m.change_text("Ola Boa Noite");
			//System.out.println(m);

		} catch (Exception e) {
			System.out.println("Exception in main: " + e);
		}

	}

}