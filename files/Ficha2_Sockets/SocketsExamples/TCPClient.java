import java.net.*;
import java.io.*;

public class TCPClient {
    public static void main(String args[]) {
	// args[0] <- hostname of destination
	if (args.length == 0) {
	    System.out.println("java TCPClient hostname");
	    System.exit(0);
	}

	Socket s = null;
	int serversocket = 7000;
	try {
	    // 1o passo
	    s = new Socket(args[0], serversocket);

	    System.out.println("SOCKET=" + s);
	    // 2o passo
	    DataInputStream in = new DataInputStream(s.getInputStream());
	    DataOutputStream out = new DataOutputStream(s.getOutputStream());

	    String texto = "";
	    InputStreamReader input = new InputStreamReader(System.in);
	    BufferedReader reader = new BufferedReader(input);
	    System.out.println("Introduza texto:");

	    // 3o passo
	    while (true) {
		// READ STRING FROM KEYBOARD
		try {
		    texto = reader.readLine();
		} catch (Exception e) {
		}

		// WRITE INTO THE SOCKET
		out.writeUTF(texto);

		// READ FROM SOCKET
		String data = in.readUTF();

		// DISPLAY WHAT WAS READ
		System.out.println("Received: " + data);
	    }

	} catch (UnknownHostException e) {
	    System.out.println("Sock:" + e.getMessage());
	} catch (EOFException e) {
	    System.out.println("EOF:" + e.getMessage());
	} catch (IOException e) {
	    System.out.println("IO:" + e.getMessage());
	} finally {
	    if (s != null)
		try {
		    s.close();
		} catch (IOException e) {
		    System.out.println("close:" + e.getMessage());
		}
	}
    }
}