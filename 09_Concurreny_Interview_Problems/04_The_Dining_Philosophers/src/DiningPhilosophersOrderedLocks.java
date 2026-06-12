import java.util.concurrent.locks.ReentrantLock;

class DiningPhilosophersOrderedLocks implements DiningPhilosophersSolver {
    private final ReentrantLock[] forks = new ReentrantLock[5];

    public DiningPhilosophersOrderedLocks() {
        for (int i = 0; i < forks.length; i++) {
            forks[i] = new ReentrantLock();
        }
    }

    @Override
    public void wantsToEat(int philosopher, Runnable pickLeftFork, Runnable pickRightFork,
                           Runnable eat, Runnable putLeftFork, Runnable putRightFork) {
        int left = philosopher;
        int right = (philosopher + 1) % forks.length;

        int firstFork = Math.min(left, right);
        int secondFork = Math.max(left, right);

        forks[firstFork].lock();
        forks[secondFork].lock();
        try {
            pickLeftFork.run();
            pickRightFork.run();
            eat.run();
            putLeftFork.run();
            putRightFork.run();
        } finally {
            forks[secondFork].unlock();
            forks[firstFork].unlock();
        }
    }
}

