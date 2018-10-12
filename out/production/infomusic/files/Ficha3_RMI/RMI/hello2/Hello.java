//package hello2;
import java.rmi.*;

public interface Hello extends Remote {
	public String sayHello() throws java.rmi.RemoteException;
}