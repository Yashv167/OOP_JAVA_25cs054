interface Switchable {
    void on();
    void off();

    default void toggle() {
        System.out.println("Device toggled");
    }
}

class Fan implements Switchable {
    public void on() {
        System.out.println("Fan is ON");
    }

    public void off() {
        System.out.println("Fan is OFF");
    }
}

class Light implements Switchable {
    public void on() {
        System.out.println("Light is ON");
    }

    public void off() {
        System.out.println("Light is OFF");
    }
}

@FunctionalInterface
interface SwitchPermission {
    boolean maySwitchOn(Switchable device, int hour);
}

public class RemoteControl {
    public static void main(String[] args) {

        Fan fan = new Fan();
        Light light = new Light();

        // Array of Switchable objects
        Switchable[] devices = {fan, light};

        // Toggle every device
        for (Switchable device : devices) {
            device.toggle();
        }

        // Anonymous class
        SwitchPermission anonymousPermission = new SwitchPermission() {
            public boolean maySwitchOn(Switchable device, int hour) {
                return hour >= 8 && hour <= 22;
            }
        };

        // Lambda expression
        SwitchPermission lambdaPermission =
                (device, hour) -> hour >= 6 && hour <= 23;

        System.out.println(
            "Fan allowed at 10? " +
            anonymousPermission.maySwitchOn(fan, 10)
        );

        System.out.println(
            "Light allowed at 11? " +
            lambdaPermission.maySwitchOn(light, 11)
        );
    }
}