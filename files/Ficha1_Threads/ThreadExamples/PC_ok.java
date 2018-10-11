class Q_ok {
int n;
boolean valueSet = false;
synchronized int get() {
    while(!valueSet)
      try {
        wait();
      } catch(InterruptedException e) {
        System.out.println("interruptedException caught");
      }
      System.out.println("Got: " + n);
      valueSet = false;
      notify();
      return n;
  }
synchronized void put(int n) {
    while(valueSet)
      try {
        wait();
      } catch(InterruptedException e) {
        System.out.println("interruptedException caught");
      }
      this.n = n;
      valueSet = true;
      System.out.println("Put: " + n);
      notify();
  }
}
class Producer_ok implements Runnable {
Q_ok q;
Producer_ok(Q_ok q) {
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
class Consumer_ok implements Runnable {
Q_ok q;
Consumer_ok(Q_ok q) {
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
class PC_ok{
  public static void main(String args[]) {
    Q_ok q = new Q_ok();
    new Producer_ok(q);
    new Consumer_ok(q);
  }
}
