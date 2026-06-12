interface DiningPhilosophersSolver {
    void wantsToEat(int philosopher, Runnable pickLeftFork, Runnable pickRightFork,
                    Runnable eat, Runnable putLeftFork, Runnable putRightFork) throws InterruptedException;
}

