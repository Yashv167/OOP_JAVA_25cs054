abstract class Media {
    private final String title;

    Media(String title) {
        this.title = title;
    }

    String getTitle() { return title; }

    String type() {
        return getClass().getSimpleName();
    }

    final double lateFee(int daysLate) {
        if (daysLate <= 0) return 0;
        return computeFee(daysLate);
    }

    protected abstract double computeFee(int daysLate);
}

class Book extends Media {
    Book(String title) { super(title); }

    @Override
    protected double computeFee(int daysLate) {
        return Math.min(daysLate * 2.0, 100);
    }
}

class DVD extends Media {
    DVD(String title) { super(title); }

    @Override
    protected double computeFee(int daysLate) {
        int chargeableDays = Math.max(0, daysLate - 1);
        return chargeableDays * 10.0;
    }
}

class Magazine extends Media {
    Magazine(String title) { super(title); }

    @Override
    protected double computeFee(int daysLate) {
        int firstWeek = Math.min(daysLate, 7);
        int afterWeek = Math.max(0, daysLate - 7);
        return firstWeek * 1.0 + afterWeek * 3.0;
    }
}

class Game extends Media {
    private final double replacementCost;

    Game(String title, double replacementCost) {
        super(title);
        this.replacementCost = replacementCost;
    }

    @Override
    protected double computeFee(int daysLate) {
        return Math.min(30 + daysLate * 15.0, replacementCost);
    }
}

class Loan {
    final Media item;
    final int daysLate;

    Loan(Media item, int daysLate) {
        this.item = item;
        this.daysLate = daysLate;
    }
}

public class Fees {
    public static void main(String[] args) {
        Loan[] returned = {
            new Loan(new Book("Clean Code"), 5),
            new Loan(new DVD("Inception"), 4),
            new Loan(new Magazine("Nat Geo"), 10),
            new Loan(new Game("FIFA 24", 400), 30),
            new Loan(new Book("Atomic Habits"), 80),
            new Loan(new DVD("Interstellar"), 1)
        };

        double total = 0;

        System.out.printf("%-16s %-10s %4s %9s%n", "Title", "Type", "Days", "Fee");

        for (Loan loan : returned) {
            double fee = loan.item.lateFee(loan.daysLate);  
            total += fee;
            System.out.printf("%-16s %-10s %4d %9.2f%n",
                              loan.item.getTitle(), loan.item.type(), loan.daysLate, fee);
        }

        System.out.printf("%nTotal late fees: %.2f%n", total);
    }
}