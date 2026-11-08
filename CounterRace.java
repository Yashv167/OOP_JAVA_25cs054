import java.util.ArrayList;
import java.util.List;

public class CounterRace {

    static final int NUM_THREADS = 10;
    static final int NUM_INCREMENTS = 10000;

    static class UnsafeCounter {

        private int count = 0;

        void increment() {
            count++;
        }

        int getCount() {
            return count;
        }
    }

    static class SafeCounter {

        private int count = 0;

        synchronized void increment() {
            count++;
        }

        synchronized int getCount() {
            return count;
        }
    }

    public static void main(String[] args) throws InterruptedException {

        int expectedCount = NUM_THREADS * NUM_INCREMENTS;

        System.out.println("Expected total: " + expectedCount);
        System.out.println();

        UnsafeCounter unsafeCounter = new UnsafeCounter();
        runThreads(unsafeCounter::increment);
        System.out.println("Unsafe counter total: " + unsafeCounter.getCount());

        SafeCounter safeCounter = new SafeCounter();
        runThreads(safeCounter::increment);
        System.out.println("Safe counter total: " + safeCounter.getCount());
    }

    static void runThreads(Runnable incrementMethod)
            throws InterruptedException {

        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < NUM_THREADS; i++) {

            Thread thread = new Thread(() -> {
                for (int j = 0; j < NUM_INCREMENTS; j++) {
                    incrementMethod.run();
                }
            });

            threads.add(thread);
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }
    }
}