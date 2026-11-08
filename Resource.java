class Connection implements AutoCloseable {
    private static int openCount = 0;
    private final String name;
    private final boolean failOnClose;

    Connection(String name, boolean failOnClose) {
        if (name.equals("BAD")) {
            throw new IllegalStateException("cannot open " + name);
        }
        this.name = name;
        this.failOnClose = failOnClose;
        openCount++;
        System.out.println("  open  " + name);
    }

    void query(String sql) {
        if (sql.contains("DROP")) {
            throw new IllegalStateException("query rejected: " + sql);
        }
        System.out.println("  " + name + " ran: " + sql);
    }

    @Override
    public void close() {                    
        openCount--;                          
        System.out.println("  close " + name);
        if (failOnClose) {
            throw new IllegalStateException("close failed on " + name);
        }
    }

    static int openCount() { return openCount; }
}

public class Resource {
    public static void main(String[] args) {

        System.out.println("\n1) Normal run");
        try (Connection c = new Connection("A", false)) {
            c.query("SELECT 1");
        }

        System.out.println("\n2) Exception inside the block");
        try (Connection c = new Connection("B", false)) {
            c.query("DROP TABLE users");
        } catch (IllegalStateException e) {
            System.out.println("  caught: " + e.getMessage());
        }

        System.out.println("\n3) Body and close() both throw");
        try (Connection c = new Connection("C", true)) {
            c.query("DROP TABLE users");
        } catch (IllegalStateException e) {
            System.out.println("  caught: " + e.getMessage());
            for (Throwable s : e.getSuppressed()) {
                System.out.println("  suppressed: " + s.getMessage());
            }
        }

        System.out.println("\n4) Old-style try/finally with the same two failures");
        try {
            Connection d = new Connection("D", true);
            try {
                d.query("DROP TABLE users");
            } finally {
                d.close();                    
            }
        } catch (IllegalStateException e) {
            System.out.println("  caught: " + e.getMessage());
            System.out.println("  suppressed: " + e.getSuppressed().length + " (original error lost)");
        }

        System.out.println("\n5) Two resources, exception in body");
        try (Connection x = new Connection("X", false);
             Connection y = new Connection("Y", false)) {
            x.query("SELECT 1");
            y.query("DROP TABLE users");
        } catch (IllegalStateException e) {
            System.out.println("  caught: " + e.getMessage());
        }

        System.out.println("\n6) Second resource fails to open");
        try (Connection x = new Connection("X", false);
             Connection bad = new Connection("BAD", false)) {
            System.out.println("  (never reached)");
        } catch (IllegalStateException e) {
            System.out.println("  caught: " + e.getMessage());
        }

        System.out.println("\nConnections still open: " + Connection.openCount());
    }
}