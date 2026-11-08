import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class InvalidQuantityException extends Exception {
    InvalidQuantityException(int qty) {
        super("quantity must be positive, got " + qty);
    }
}

class OutOfStockException extends Exception {
    private final String item;
    private final int requested;
    private final int available;

    OutOfStockException(String item, int requested, int available) {
        super("only " + available + " in stock, short by " + (requested - available));
        this.item = item;
        this.requested = requested;
        this.available = available;
    }

    String getItem()     { return item; }
    int getRequested()   { return requested; }
    int getAvailable()   { return available; }
    int getShortfall()   { return requested - available; }
}

class Warehouse {
    private final Map<String, Integer> stock = new LinkedHashMap<>();

    void add(String item, int qty) {
        stock.put(item, qty);
    }

    int issue(String item, int qty) throws InvalidQuantityException, OutOfStockException {
        if (qty <= 0) {
            throw new InvalidQuantityException(qty);        
        }
        int available = stock.getOrDefault(item, 0);        
        if (qty > available) {
            throw new OutOfStockException(item, qty, available);
        }
        int left = available - qty;
        stock.put(item, left);                              
        return left;
    }

    @Override
    public String toString() {
        return stock.toString();
    }
}

class Request {
    final String item;
    final int qty;

    Request(String item, int qty) {
        this.item = item;
        this.qty = qty;
    }
}

public class StockIssue {
    public static void main(String[] args) {
        Warehouse w = new Warehouse();
        w.add("Bolts", 100);
        w.add("Nuts", 50);
        w.add("Washers", 20);
        w.add("Screws", 0);

        List<Request> requests = List.of(
            new Request("Bolts", 30),
            new Request("Nuts", 60),
            new Request("Washers", 0),
            new Request("Washers", -5),
            new Request("Screws", 1),
            new Request("Bolts", 70),
            new Request("Bolts", 1),
            new Request("Gears", 5)
        );

        int issued = 0, outOfStock = 0, invalid = 0;
        int totalShortfall = 0;

        for (Request r : requests) {
            try {
                int left = w.issue(r.item, r.qty);
                System.out.printf("%-10s%s x%d -> %d left%n", "[OK]", r.item, r.qty, left);
                issued++;
            } catch (InvalidQuantityException e) {
                System.out.printf("%-10s%s x%d -> %s%n", "[INVALID]", r.item, r.qty, e.getMessage());
                invalid++;
            } catch (OutOfStockException e) {
                System.out.printf("%-10s%s x%d -> %s%n", "[SHORT]", r.item, r.qty, e.getMessage());
                outOfStock++;
                totalShortfall += e.getShortfall();
            }
        }

        System.out.println();
        System.out.println("Processed " + requests.size() + " requests: "
                           + issued + " issued, " + outOfStock + " out of stock, "
                           + invalid + " invalid");
        System.out.println("Total units short: " + totalShortfall);
        System.out.println("Final stock: " + w);
    }
}