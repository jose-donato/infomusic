//package calculator;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class Calculator extends UnicastRemoteObject implements CalculatorInterface {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected Calculator() throws RemoteException {
		super();
	}

	public double add(double a, double b) {
		return a + b;
	}

	public double div(double a, double b) throws DivByZeroException {
		if (b == 0)
			throw new DivByZeroException();
		return a / b;
	}

	public double mul(double a, double b) {
		return a * b;
	}

	public double sub(double a, double b) {
		return a - b;
	}
	
	/**
	 * @param args
	 * @throws RemoteException 
	 */
	public static void main(String[] args) throws RemoteException {
		CalculatorInterface ci = new Calculator();
		LocateRegistry.createRegistry(1099).rebind("calc", ci);
		System.out.println("Calculator ready...");
	}

}
