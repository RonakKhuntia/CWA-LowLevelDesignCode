import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        runCase("Semaphore + Butler", new DiningPhilosophers());
        runCase("Ordered Fork Locks", new DiningPhilosophersOrderedLocks());
        runCase("Monitor (state + wait/notifyAll)", new DiningPhilosophersMonitor());
    }

    private static void runCase(String title, DiningPhilosophersSolver solver) {
        System.out.println("\n=== " + title + " ===");
        ExecutorService executorService = Executors.newFixedThreadPool(5);

        for (int philosopher = 0; philosopher < 5; philosopher++) {
            final int id = philosopher;
            executorService.submit(() -> {
                Thread.currentThread().setName(title.replace(' ', '-') + "-P" + id);
                try {
                    solver.wantsToEat(
                            id,
                            () -> System.out.println("Philosopher " + id + " picked up left fork (" + Thread.currentThread().getName() + ")"),
                            () -> System.out.println("Philosopher " + id + " picked up right fork (" + Thread.currentThread().getName() + ")"),
                            () -> {
                                System.out.println("Philosopher " + id + " is eating (" + Thread.currentThread().getName() + ")");
                                try {
                                    Thread.sleep(300);
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                }
                            },
                            () -> System.out.println("Philosopher " + id + " put down left fork (" + Thread.currentThread().getName() + ")"),
                            () -> System.out.println("Philosopher " + id + " put down right fork (" + Thread.currentThread().getName() + ")")
                    );
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(1, TimeUnit.MINUTES)) {
                executorService.shutdownNow();
                throw new IllegalStateException("Timed out waiting for philosophers to finish");
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
