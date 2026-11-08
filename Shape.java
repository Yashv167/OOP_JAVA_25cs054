abstract class AbstractShape {
    abstract double area();

    String name() {
        return getClass().getSimpleName();
    }
}

class Circle extends AbstractShape {
    private final double radius;

    Circle(double radius) {
        this.radius = radius;
    }

    @Override
    double area() {
        return Math.PI * radius * radius;
    }
}

class Rectangle extends AbstractShape {
    private final double width, height;

    Rectangle(double width, double height) {
        this.width = width;
        this.height = height;
    }

    @Override
    double area() {
        return width * height;
    }
}

class Triangle extends AbstractShape {
    private final double base, height;

    Triangle(double base, double height) {
        this.base = base;
        this.height = height;
    }

    @Override
    double area() {
        return 0.5 * base * height;
    }
}

public class Shape {
    public static void main(String[] args) {
        AbstractShape[] shapes = {
            new Circle(3),
            new Rectangle(4, 5),
            new Triangle(6, 2.5),
            new Circle(1.5),
            new Rectangle(2, 8),
            new Triangle(10, 4)
        };

        double total = 0;
        AbstractShape largest = null;
        double largestArea = Double.NEGATIVE_INFINITY;

        for (AbstractShape s : shapes) {
            double a = s.area();               
            total += a;
            System.out.printf("%-10s area = %8.2f   running total = %8.2f%n",
                              s.name(), a, total);

            if (a > largestArea) {
                largestArea = a;
                largest = s;
            }
        }

        System.out.printf("%nTotal area: %.2f%n", total);
        System.out.printf("Largest: %s with area %.2f%n", largest.name(), largestArea);
    }
}