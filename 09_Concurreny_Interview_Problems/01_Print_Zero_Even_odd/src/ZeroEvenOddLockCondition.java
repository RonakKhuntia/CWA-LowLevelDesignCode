import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.IntConsumer;

public class ZeroEvenOddLockCondition {
	private final int n;
	private final ReentrantLock lock = new ReentrantLock();
	private final Condition zeroCondition = lock.newCondition();
	private final Condition oddCondition = lock.newCondition();
	private final Condition evenCondition = lock.newCondition();
	private int turn = 0; // 0 -> zero, 1 -> odd, 2 -> even

	public ZeroEvenOddLockCondition(int n) {
		this.n = n;
	}

	public void zero(IntConsumer printNumber) throws InterruptedException {
		for (int i = 1; i <= n; i++) {
			lock.lock();
			try {
				while (turn != 0) {
					zeroCondition.await();
				}

				printNumber.accept(0);
				turn = (i % 2 == 1) ? 1 : 2;
				if (turn == 1) {
					oddCondition.signal();
				} else {
					evenCondition.signal();
				}
			} finally {
				lock.unlock();
			}
		}
	}

	public void even(IntConsumer printNumber) throws InterruptedException {
		for (int i = 2; i <= n; i += 2) {
			lock.lock();
			try {
				while (turn != 2) {
					evenCondition.await();
				}

				printNumber.accept(i);
				turn = 0;
				zeroCondition.signal();
			} finally {
				lock.unlock();
			}
		}
	}

	public void odd(IntConsumer printNumber) throws InterruptedException {
		for (int i = 1; i <= n; i += 2) {
			lock.lock();
			try {
				while (turn != 1) {
					oddCondition.await();
				}

				printNumber.accept(i);
				turn = 0;
				zeroCondition.signal();
			} finally {
				lock.unlock();
			}
		}
	}
}

