abstract class Employee {
    private final String name;
    private final int id;

    Employee(String name, int id) {
        this.name = name;
        this.id = id;
    }

    String getName() { return name; }
    int getId() { return id; }

    abstract double monthlySalary();

    String type() {
        return getClass().getSimpleName();
    }
}

class FullTime extends Employee {
    private final double fixedSalary;

    FullTime(String name, int id, double fixedSalary) {
        super(name, id);
        this.fixedSalary = fixedSalary;
    }

    @Override
    double monthlySalary() {
        return fixedSalary;
    }
}

class PartTime extends Employee {
    private final double hours;
    private final double rate;

    PartTime(String name, int id, double hours, double rate) {
        super(name, id);
        this.hours = hours;
        this.rate = rate;
    }

    @Override
    double monthlySalary() {
        return hours * rate;
    }
}

class Intern extends Employee {
    private final double stipend;

    Intern(String name, int id, double stipend) {
        super(name, id);
        this.stipend = stipend;
    }

    @Override
    double monthlySalary() {
        return stipend;
    }
}

public class Payroll {
    public static void main(String[] args) {
        Employee[] staff = {
            new FullTime("Asha Mehta", 101, 45000),
            new PartTime("Ravi Shah", 102, 80, 250),
            new Intern("Neha Patel", 103, 12000),
            new FullTime("Karan Joshi", 104, 52000),
            new PartTime("Dev Desai", 105, 60, 300),
            new Intern("Mira Shah", 106, 10000)
        };

        double total = 0;

        System.out.printf("%-5s %-14s %-10s %10s%n", "ID", "Name", "Type", "Salary");

        for (Employee e : staff) {
            double pay = e.monthlySalary();
            total += pay;
            System.out.printf("%-5d %-14s %-10s %10.2f%n",
                              e.getId(), e.getName(), e.type(), pay);

            if (e instanceof Intern) {
                System.out.println("      -> Note: stipend only, not a salary");
            }
        }

        System.out.printf("%nTotal monthly payroll: %.2f%n", total);
    }
}