import java.net.*;
import java.io.*;
public class UDPClient{
	public static void main(String args[]){ 
		// argumentos da linha de comando: hostname 
		if(args.length == 0){
			System.out.println("java UDPClient hostname");
			System.exit(0);
		}
		DatagramSocket aSocket = null;
		try {
			aSocket = new DatagramSocket();    

		String texto = "";
		InputStreamReader input = new InputStreamReader(System.in);
		BufferedReader reader = new BufferedReader(input);
		
			while(true){
				System.out.print("Mensagem a enviar = ");
				// READ STRING FROM KEYBOARD
	     	  try{
					texto = reader.readLine();

				}
			  catch(Exception e){}
			  
				
				byte [] m = texto.getBytes();
				
				InetAddress aHost = InetAddress.getByName(args[0]);
				int serverPort = 6789;		                                                
				DatagramPacket request = new DatagramPacket(m,m.length,aHost,serverPort);
				aSocket.send(request);			                        
				byte[] buffer = new byte[1000];
				DatagramPacket reply = new DatagramPacket(buffer, buffer.length);	
				aSocket.receive(reply);
				System.out.println("Recebeu: " + new String(reply.getData(), 0, reply.getLength()));	
			} // while
		}catch (SocketException e){System.out.println("Socket: " + e.getMessage());
		}catch (IOException e){System.out.println("IO: " + e.getMessage());
		}finally {if(aSocket != null) aSocket.close();}
	} 
}