import java.rmi.*;

public interface Hello extends Remote {
	public String sayHello() throws java.rmi.RemoteException;

	public void remote_print(String s) throws java.rmi.RemoteException;

	public void remote_print(Message m) throws java.rmi.RemoteException;

	public Message ping_pong(Message m) throws RemoteException;
}