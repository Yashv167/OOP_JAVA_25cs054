import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

interface Booking {
    boolean book();
    int seatsLeft();
}

class Pause {
    static void briefly() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

// No protection: check-then-act is not atomic
class UnsafeBooking implements Booking {
    private int seatsLeft = 5;

    @Override
    public boolean book() {
        if (seatsLeft > 0) {          
            Pause.briefly();          
            seatsLeft--;              
            return true;
        }
        return false;
    }

    @Override
    public int seatsLeft() { return seatsLeft; }
}

class SafeBooking implements Booking {
    private int seatsLeft = 5;

    @Override
    public synchronized boolean book() {
        if (seatsLeft > 0) {
            Pause.briefly();
            seatsLeft--;
            return true;
        }
        return false;
    }

    @Override
    public synchronized int seatsLeft() { return seatsLeft; }
}

public class SeatBookingRace {
    static final int THREADS = 10;

    static void run(String label, Booking booking) throws InterruptedException {
        AtomicInteger succeeded = new AtomicInteger();  
        CountDownLatch start = new CountDownLatch(1);
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < THREADS; i++) {
            Thread t = new Thread(() -> {
                try {
                    start.await();                       
                } catch (InterruptedException e) {
                    return;
                }
                if (booking.book()) {
                    succeeded.incrementAndGet();
                }
            });
            threads.add(t);
            t.start();
        }

        start.countDown();                              
        for (Thread t : threads) {
            t.join();
        }

        int ok = succeeded.get();
        System.out.println(label);
        System.out.println("  Threads:             " + THREADS);
        System.out.println("  Successful bookings: " + ok);
        System.out.println("  Failed bookings:     " + (THREADS - ok));
        System.out.println("  seatsLeft:           " + booking.seatsLeft());
        System.out.println(ok > 5
            ? "  RESULT: OVERSOLD by " + (ok - 5) + " seat(s)"
            : "  RESULT: correct, exactly 5 seats sold");
        System.out.println();
    }

    public static void main(String[] args) throws InterruptedException {
        run("Without synchronization", new UnsafeBooking());
        run("With synchronized book()", new SafeBooking());
    }
}