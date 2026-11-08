@FunctionalInterface
interface Notifier {
    void send(String message);
}

interface Urgent {
}

class EmailNotifier implements Notifier, Urgent {
    public void send(String message) {
        System.out.println("Email: " + message);
    }
}

public class Main {
    public static void main(String[] args) {

        Notifier smsNotifier = message ->
                System.out.println("SMS: " + message);

        Notifier emailNotifier = new EmailNotifier();

        Notifier[] notifiers = {
                emailNotifier,
                smsNotifier
        };

        String message = "Meeting starts at 10 AM";

        for (Notifier notifier : notifiers) {
            notifier.send(message);

            if (notifier instanceof Urgent) {
                notifier.send(message);
            }
        }
    }
}