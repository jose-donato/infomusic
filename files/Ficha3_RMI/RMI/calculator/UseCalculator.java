//package calculator;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class UseCalculator {

	/**
	 * @param args
	 * @throws NotBoundException 
	 * @throws RemoteException 
	 * @throws MalformedURLException 
	 * @throws DivByZeroException 
	 */
	public static void main(String[] args) throws MalformedURLException, RemoteException, NotBoundException, DivByZeroException {
		System.out.println("Simle example of use of a remote calculator");
		CalculatorInterface ci = (CalculatorInterface) Naming.lookup("calc");
		System.out.println("8 + 3 = " + ci.add(8, 3));
		System.out.println("8 - 3 = " + ci.sub(8, 3));
		System.out.println("8 * 3 = " + ci.mul(8, 3));
		System.out.println("8 / 3 = " + ci.div(8, 3));
		System.out.println("What about 8 / 0?");
		System.out.println(ci.div(8, 0));
	}

}
