class NewThread implements Runnable {
  String name;
  Thread t;
  NewThread(String threadname) {
    name = threadname;
    t = new Thread(this, name);
    System.out.println("New thread: " + t);
    t.start(); // Start the thread
  }
  public void run() {      // entry point
    try {
      for(int i = 5; i > 0; i--) {
        System.out.println(name + ": " + i);
        Thread.sleep(1000);
      }
    } catch (InterruptedException e) {
      System.out.println(name + "Interrupted");
    }
    System.out.println(name + " exiting.");
  }
}
class MultiThreadDemo {
  public static void main(String args[]) {
    new NewThread("Sporting"); // create threads
    new NewThread("Benfica");
    new NewThread("Porto");
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      System.out.println("Main thread Interrupted");
    }
    System.out.println("Main thread exiting...");
  }
}