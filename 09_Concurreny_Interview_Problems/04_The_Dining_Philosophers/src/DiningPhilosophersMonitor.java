class DiningPhilosophersMonitor implements DiningPhilosophersSolver {
    private static final int THINKING = 0;
    private static final int HUNGRY = 1;
    private static final int EATING = 2;

    private final Object lock = new Object();
    private final int[] state = new int[5];

    @Override
    public void wantsToEat(int philosopher, Runnable pickLeftFork, Runnable pickRightFork,
                           Runnable eat, Runnable putLeftFork, Runnable putRightFork) throws InterruptedException {
        synchronized (lock) {
            state[philosopher] = HUNGRY;
            test(philosopher);
            while (state[philosopher] != EATING) {
                lock.wait();
            }
        }

        pickLeftFork.run();
        pickRightFork.run();
        eat.run();
        putLeftFork.run();
        putRightFork.run();

        synchronized (lock) {
            state[philosopher] = THINKING;
            test(left(philosopher));
            test(right(philosopher));
            lock.notifyAll();
        }
    }

    private void test(int philosopher) {
        if (state[philosopher] == HUNGRY
                && state[left(philosopher)] != EATING
                && state[right(philosopher)] != EATING) {
            state[philosopher] = EATING;
        }
    }

    private int left(int philosopher) {
        return (philosopher + 4) % 5;
    }

    private int right(int philosopher) {
        return (philosopher + 1) % 5;
    }
}

