import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.IntConsumer;

public class Main {
    public static void main(String[] args) {
        int n = 10;
        IntConsumer printNumber = number ->
                System.out.println(Thread.currentThread().getName() + " printed: " + number);

        runSemaphoreVersion(n, printNumber);
        runWaitNotifyVersion(n, printNumber);
        runLockConditionVersion(n, printNumber);
    }

    private static void runSemaphoreVersion(int n, IntConsumer printNumber) {
        System.out.println("=== Semaphore version ===");
        ZeroEvenOdd zeroEvenOdd = new ZeroEvenOdd(n);
        ExecutorService executor = Executors.newFixedThreadPool(3);

        submitZero(executor, "Semaphore-Zero", () -> zeroEvenOdd.zero(printNumber));
        submitEven(executor, "Semaphore-Even", () -> zeroEvenOdd.even(printNumber));
        submitOdd(executor, "Semaphore-Odd", () -> zeroEvenOdd.odd(printNumber));

        shutdownAndAwait(executor);
        System.out.println();
    }

    private static void runWaitNotifyVersion(int n, IntConsumer printNumber) {
        System.out.println("=== wait/notify version ===");
        ZeroEvenOddWaitNotify zeroEvenOdd = new ZeroEvenOddWaitNotify(n);
        ExecutorService executor = Executors.newFixedThreadPool(3);

        submitZero(executor, "WaitNotify-Zero", () -> zeroEvenOdd.zero(printNumber));
        submitEven(executor, "WaitNotify-Even", () -> zeroEvenOdd.even(printNumber));
        submitOdd(executor, "WaitNotify-Odd", () -> zeroEvenOdd.odd(printNumber));

        shutdownAndAwait(executor);
        System.out.println();
    }

    private static void runLockConditionVersion(int n, IntConsumer printNumber) {
        System.out.println("=== ReentrantLock/Condition version ===");
        ZeroEvenOddLockCondition zeroEvenOdd = new ZeroEvenOddLockCondition(n);
        ExecutorService executor = Executors.newFixedThreadPool(3);

        submitZero(executor, "LockCondition-Zero", () -> zeroEvenOdd.zero(printNumber));
        submitEven(executor, "LockCondition-Even", () -> zeroEvenOdd.even(printNumber));
        submitOdd(executor, "LockCondition-Odd", () -> zeroEvenOdd.odd(printNumber));

        shutdownAndAwait(executor);
    }

    private static void submitZero(ExecutorService executor, String threadName, ThrowingRunnable task) {
        executor.submit(() -> runWithThreadName(threadName, task));
    }

    private static void submitEven(ExecutorService executor, String threadName, ThrowingRunnable task) {
        executor.submit(() -> runWithThreadName(threadName, task));
    }

    private static void submitOdd(ExecutorService executor, String threadName, ThrowingRunnable task) {
        executor.submit(() -> runWithThreadName(threadName, task));
    }

    private static void runWithThreadName(String threadName, ThrowingRunnable task) {
        Thread.currentThread().setName(threadName);
        try {
            task.run();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void shutdownAndAwait(ExecutorService executor) {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
                executor.shutdownNow();
                throw new IllegalStateException("Timed out waiting for tasks to finish");
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws InterruptedException;
    }
}

/*

Output :

Zero-Thread printed: 0
Odd-Thread printed: 1
Zero-Thread printed: 0
Even-Thread printed: 2
Zero-Thread printed: 0
Odd-Thread printed: 3
Zero-Thread printed: 0
Even-Thread printed: 4
Zero-Thread printed: 0
Odd-Thread printed: 5
Zero-Thread printed: 0
Even-Thread printed: 6
Zero-Thread printed: 0
Odd-Thread printed: 7
Zero-Thread printed: 0
Even-Thread printed: 8
Zero-Thread printed: 0
Odd-Thread printed: 9
Zero-Thread printed: 0
Even-Thread printed: 10

*/