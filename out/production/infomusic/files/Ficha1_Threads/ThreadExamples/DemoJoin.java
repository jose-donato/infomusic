class NewThread1 implements Runnable {
  String name; // name of thread
  Thread t;
  NewThread1(String threadname) {
    name = threadname;
    t = new Thread(this, name);
    System.out.println("New thread: " + t);
    t.start(); // Start the thread
  }
  public void run() {
    try {
      for(int i = 3; i > 0; i--) {
        System.out.println(name + ": " + i);
        Thread.sleep(1000);
      }
    } catch (InterruptedException e) {
      System.out.println(name + " interrupted.");
    }
    System.out.println(name + " exiting.");
  }
}
class DemoJoin {
  public static void main(String args[]) {
    NewThread1 ob1 = new NewThread1("T1");
    NewThread1 ob2 = new NewThread1("T2");
    NewThread1 ob3 = new NewThread1("T3");
    System.out.println("T1 is alive: " + ob1.t.isAlive());
    System.out.println("T2 is alive: " + ob2.t.isAlive());
    System.out.println("T3 is alive: " + ob3.t.isAlive());
    // wait for threads to finish
    try {
      System.out.println("Waiting for threads to finish.");
      ob1.t.join();
      ob2.t.join();
      ob3.t.join();
    } catch (InterruptedException e) {
      System.out.println("Main thread Interrupted");
    }
    System.out.println("T1 is alive: " + ob1.t.isAlive());
    System.out.println("T2 is alive: " + ob2.t.isAlive());
    System.out.println("T3 is alive: " + ob3.t.isAlive());
    System.out.println("Main thread exiting.");
  }
}
