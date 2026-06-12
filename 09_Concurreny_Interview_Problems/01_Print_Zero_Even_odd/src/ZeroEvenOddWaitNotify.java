import java.util.function.IntConsumer;

public class ZeroEvenOddWaitNotify {
    private final int n;
    private final Object lock = new Object();
    private int turn = 0; // 0 -> zero, 1 -> odd, 2 -> even

    public ZeroEvenOddWaitNotify(int n) {
        this.n = n;
    }

    public void zero(IntConsumer printNumber) throws InterruptedException {
        for (int i = 1; i <= n; i++) {
            synchronized (lock) {
                while (turn != 0) {
                    lock.wait();
                }

                printNumber.accept(0);
                turn = (i % 2 == 1) ? 1 : 2;
                lock.notifyAll();
            }
        }
    }

    public void even(IntConsumer printNumber) throws InterruptedException {
        for (int i = 2; i <= n; i += 2) {
            synchronized (lock) {
                while (turn != 2) {
                    lock.wait();
                }

                printNumber.accept(i);
                turn = 0;
                lock.notifyAll();
            }
        }
    }

    public void odd(IntConsumer printNumber) throws InterruptedException {
        for (int i = 1; i <= n; i += 2) {
            synchronized (lock) {
                while (turn != 1) {
                    lock.wait();
                }

                printNumber.accept(i);
                turn = 0;
                lock.notifyAll();
            }
        }
    }
}

