import java.util.Arrays;

public class SharedTotal {

    interface Chunk { void process(int from, int to); }
    interface Strategy { long run(int[] data, int threads) throws InterruptedException; }

    static void parallel(int n, int threads, Chunk chunk) throws InterruptedException {
        Thread[] ts = new Thread[threads];
        int size = (n + threads - 1) / threads;
        for (int i = 0; i < threads; i++) {
            final int from = Math.min(n, i * size);
            final int to = Math.min(n, from + size);
            ts[i] = new Thread(() -> chunk.process(from, to));
            ts[i].start();
        }
        for (Thread t : ts) t.join();
    }

    static class PlainTotal { long value; }                 

    static class LockedTotal {
        private long value;
        synchronized void add(long x) { value += x; }
        synchronized long get() { return value; }
    }

    static long sequential(int[] d, int threads) {
        long sum = 0;
        for (int x : d) sum += x;
        return sum;
    }

    static long unsafe(int[] d, int threads) throws InterruptedException {
        PlainTotal total = new PlainTotal();
        parallel(d.length, threads, (from, to) -> {
            for (int i = from; i < to; i++) total.value += d[i];
        });
        return total.value;
    }

    static long lockPerElement(int[] d, int threads) throws InterruptedException {
        LockedTotal total = new LockedTotal();
        parallel(d.length, threads, (from, to) -> {
            for (int i = from; i < to; i++) total.add(d[i]);
        });
        return total.get();
    }

    static long localThenMerge(int[] d, int threads) throws InterruptedException {
        LockedTotal total = new LockedTotal();
        parallel(d.length, threads, (from, to) -> {
            long local = 0;                        
            for (int i = from; i < to; i++) local += d[i];
            total.add(local);                      
        });
        return total.get();
    }

    static void bench(String label, Strategy s, int[] d, int threads, long expected)
            throws InterruptedException {
        final int WARMUP = 3, RUNS = 7;
        for (int i = 0; i < WARMUP; i++) s.run(d, threads);

        double[] ms = new double[RUNS];
        for (int i = 0; i < RUNS; i++) {
            long t0 = System.nanoTime();
            long result = s.run(d, threads);
            ms[i] = (System.nanoTime() - t0) / 1_000_000.0;
            if (result != expected) {
                throw new IllegalStateException(label + " gave " + result + ", expected " + expected);
            }
        }
        Arrays.sort(ms);
        System.out.printf("%-22s median %8.2f ms   min %8.2f ms%n", label, ms[RUNS / 2], ms[0]);
    }

    public static void main(String[] args) throws InterruptedException {
        int n = 4_000_000;
        int[] data = new int[n];
        for (int i = 0; i < n; i++) data[i] = (i % 100) + 1; 

        int threads = Runtime.getRuntime().availableProcessors();
        long expected = sequential(data, 1);

        System.out.println("Elements: " + n + ", threads: " + threads);
        System.out.println("Expected total: " + expected + "\n");

        System.out.println("Unsafe shared total (3 runs):");
        for (int i = 0; i < 3; i++) {
            long got = unsafe(data, threads);
            System.out.printf("  got %d  (lost %d)%n", got, expected - got);
        }

        System.out.println("\nTimings (all results verified correct):");
        bench("sequential", SharedTotal::sequential, data, threads, expected);
        bench("lock per element", SharedTotal::lockPerElement, data, threads, expected);
        bench("local sum, then merge", SharedTotal::localThenMerge, data, threads, expected);
    }
}