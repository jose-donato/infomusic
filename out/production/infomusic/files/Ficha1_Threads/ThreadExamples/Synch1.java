class Callme1 {
	synchronized void call(String msg) {
		System.out.print("[" + msg);
		try {
			Thread.sleep(1000);
		} catch(InterruptedException e) {
			System.out.println("Interrupted");
		}
		System.out.println("]");
	}
}
class Caller1 implements Runnable {
	String msg;
	Callme1 target;
	Thread t;
	public Caller1(Callme1 targ, String s) {
		target = targ;
		msg = s;
		t = new Thread(this);
		t.start();
	}
	public void run() {
		target.call(msg);
	}
}
class Synch1 {
	public static void main(String args[]) {
		Callme1 target = new Callme1();
		Caller1 ob1 = new Caller1(target, "Hello");
		Caller1 ob2 = new Caller1(target, "Synchronized");
		Caller1 ob3 = new Caller1(target, "World");
		try {
			ob1.t.join();
			ob2.t.join();
			ob3.t.join();
		} catch(InterruptedException e) {
			System.out.println("Interrupted");
		}

	}
}