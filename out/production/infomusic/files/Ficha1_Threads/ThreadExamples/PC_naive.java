class Q_naive {
int n;
boolean valueSet = false;
int get() {
while (!valueSet)
	    ;
System.out.println("Got: " + n);
valueSet = false;
return n;
}
void put(int n) {
while (valueSet)
	    ;
this.n = n;
valueSet = true;
System.out.println("Put: " + n);
}
}
class Producer_naive implements Runnable {
Q_naive q;
Producer_naive(Q_naive q) {
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
class Consumer_naive implements Runnable {
Q_naive q;
Consumer_naive(Q_naive q) {
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
class PC_naive {
    public static void main(String args[]) {
	Q_naive q = new Q_naive();
	new Producer_naive(q);
	new Consumer_naive(q);
    }
}