import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class PoolRunner {
    public static void main(String[] args) throws InterruptedException {
        final int TASKS = 10;
        final int WORK_MS = 200;

        ExecutorService pool = Executors.newFixedThreadPool(3);
        Map<String, AtomicInteger> perThread = new ConcurrentHashMap<>();   
        long begin = System.nanoTime();

        for (int i = 1; i <= TASKS; i++) {
            final int id = i;
            final long submittedAt = System.nanoTime();

            pool.submit(() -> {
                String thread = Thread.currentThread().getName();
                perThread.computeIfAbsent(thread, k -> new AtomicInteger()).incrementAndGet();

                long waitedMs = (System.nanoTime() - submittedAt) / 1_000_000;
                System.out.printf("task %2d started on %s after waiting %3d ms%n", id, thread, waitedMs);

                try {
                    Thread.sleep(WORK_MS);                   
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();      
                }
            });
        }

        System.out.println("All " + TASKS + " tasks submitted; main did not block");

        pool.shutdown();                                     
        if (!pool.awaitTermination(10, TimeUnit.SECONDS)) {
            System.out.println("Timed out, forcing stop");
            pool.shutdownNow();                              
        }

        long totalMs = (System.nanoTime() - begin) / 1_000_000;

        System.out.println();
        System.out.println("Distinct threads used: " + perThread.size());
        for (Map.Entry<String, AtomicInteger> e : new TreeMap<>(perThread).entrySet()) {
            System.out.println("  " + e.getKey() + " ran " + e.getValue() + " tasks");
        }
        System.out.println("Pool terminated: " + pool.isTerminated());
        System.out.println("Elapsed: ~" + totalMs + " ms");
    }
}