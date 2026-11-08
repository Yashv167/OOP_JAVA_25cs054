import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

class Account {
    final int id;
    final String name;
    int balance;

    Account(int id, String name, int balance) {
        this.id = id;
        this.name = name;
        this.balance = balance;
    }
}

interface Transfer {
    void go(Account from, Account to, int amount);
}

class Bank {
    private static void pause() {
        try {
            Thread.sleep(100);        // widens the window so the deadlock is near-certain
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void log(String msg) {
        System.out.println("  " + Thread.currentThread().getName() + ": " + msg);
    }

    // BUG: locks "from" then "to", so two opposite transfers lock in opposite order
    static void transferUnsafe(Account from, Account to, int amount) {
        synchronized (from) {
            log("holds " + from.name + ", wants " + to.name);
            pause();
            synchronized (to) {
                from.balance -= amount;
                to.balance += amount;
                log("moved " + amount + " " + from.name + " -> " + to.name);
            }
        }
    }

    // FIX: always lock the lower id first, whatever the direction of the transfer
    static void transferOrdered(Account from, Account to, int amount) {
        Account first = from.id < to.id ? from : to;
        Account second = (first == from) ? to : from;
        synchronized (first) {
            log("holds " + first.name + ", wants " + second.name);
            pause();
            synchronized (second) {
                from.balance -= amount;
                to.balance += amount;
                log("moved " + amount + " " + from.name + " -> " + to.name);
            }
        }
    }
}

public class DeadLock {

    static void run(String label, Transfer transfer) throws InterruptedException {
        Account a = new Account(1, "A", 1000);
        Account b = new Account(2, "B", 1000);
        System.out.println(label);

        Thread t1 = new Thread(() -> transfer.go(a, b, 100), "T1 (A->B)");
        Thread t2 = new Thread(() -> transfer.go(b, a, 50), "T2 (B->A)");
        t1.setDaemon(true);           // so a stuck thread can't keep the JVM alive
        t2.setDaemon(true);
        t1.start();
        t2.start();

        t1.join(1500);                // wait at most 1.5 s each
        t2.join(1500);

        if (t1.isAlive() || t2.isAlive()) {
            System.out.println("  DEADLOCK: threads still stuck after 1.5 s");
            ThreadMXBean mx = ManagementFactory.getThreadMXBean();
            long[] ids = mx.findDeadlockedThreads();
            if (ids != null) {
                for (ThreadInfo info : mx.getThreadInfo(ids)) {
                    System.out.println("  " + info.getThreadName()
                        + " is blocked, waiting for a lock held by " + info.getLockOwnerName());
                }
            }
            System.out.println("  Balances never changed: A=" + a.balance + ", B=" + b.balance);
        } else {
            System.out.println("  Both finished. A=" + a.balance + ", B=" + b.balance
                + ", total=" + (a.balance + b.balance));
        }
        System.out.println();
    }

    public static void main(String[] args) throws InterruptedException {
        run("1) Opposite lock order", Bank::transferUnsafe);
        run("2) Consistent lock order (lower id first)", Bank::transferOrdered);
    }
}