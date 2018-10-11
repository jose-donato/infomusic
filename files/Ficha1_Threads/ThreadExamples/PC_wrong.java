class Q {
  int n;
  synchronized int get() {
    System.out.println("Got: " + n);
    return n;
  }
  synchronized void put(int n) {
    this.n = n;
    System.out.println("Put: " + n);
  }
}
class Producer implements Runnable {
  Q q;
  Producer(Q q) {
    this.q = q;
    new Thread(this, "Producer").start();
  }
  public void run() {
    int i = 0;
    while(i<100) {
      q.put(i++);
    }
  }
}
class Consumer implements Runnable {
  Q q;
  Consumer(Q q) {
    this.q = q;
    new Thread(this, "Consumer").start();
  }
  public void run() {
    int i=0;
    while(i<100) {
      q.get();
       i++;
    }
  }
}
class PC_wrong {
  public static void main(String args[]) {
    Q q = new Q();
    new Producer(q);
    new Consumer(q);
  }
}