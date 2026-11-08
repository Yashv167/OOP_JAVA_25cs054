import java.util.NoSuchElementException;
import java.util.Scanner;

class DivideByZeroException extends Exception {
    DivideByZeroException(String message) {
        super(message);
    }
}

public class GuardedCalculator {

    static double calculate(double a, double b, String op) throws DivideByZeroException {
        switch (op) {
            case "+": return a + b;
            case "-": return a - b;
            case "*": return a * b;
            case "/":
                if (b == 0) throw new DivideByZeroException("Cannot divide " + fmt(a) + " by zero");
                return a / b;
            case "%":
                if (b == 0) throw new DivideByZeroException("Cannot take " + fmt(a) + " modulo zero");
                return a % b;
            default:
                throw new IllegalArgumentException("Unknown operator '" + op + "' (use + - * / %)");
        }
    }

    // 10.0 -> "10", 2.5 -> "2.5"
    static String fmt(double d) {
        return d == (long) d ? String.valueOf((long) d) : String.valueOf(d);
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int attempt = 0;
        boolean done = false;

        while (!done) {
            attempt++;
            String outcome = "unknown";      

            try {
                System.out.print("First number: ");
                double a = Double.parseDouble(in.nextLine().trim());

                System.out.print("Second number: ");
                double b = Double.parseDouble(in.nextLine().trim());

                System.out.print("Operator (+ - * / %): ");
                String op = in.nextLine().trim();

                double result = calculate(a, b, op);
                System.out.println("Result: " + fmt(a) + " " + op + " " + fmt(b) + " = " + fmt(result));
                outcome = "SUCCESS";
                done = true;                 

            } catch (NumberFormatException e) {
                outcome = "FAILED (invalid number)";
                System.out.println("Error: not a valid number (" + e.getMessage() + "). Please start again.");

            } catch (DivideByZeroException e) {
                outcome = "FAILED (divide by zero)";
                System.out.println("Error: " + e.getMessage() + ". Please try again.");

            } catch (IllegalArgumentException e) {
                outcome = "FAILED (invalid operator)";
                System.out.println("Error: " + e.getMessage() + ". Please try again.");

            } catch (NoSuchElementException e) {
                outcome = "INPUT ENDED";
                System.out.println("\nNo more input, giving up.");
                return;                      

            } finally {
                System.out.println("[LOG] attempt " + attempt + ": " + outcome + "\n");
            }
        }
    }
}