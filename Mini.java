import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;

@Retention(RetentionPolicy.RUNTIME)   
@Target(ElementType.METHOD)
@interface Run {
}

class SampleTests {
    @Run
    void additionWorks() {
        check(2 + 2 == 4, "2 + 2 should be 4");
    }

    @Run
    void stringUpperCase() {
        check("abc".toUpperCase().equals("ABC"), "toUpperCase should give ABC");
    }

    @Run
    void brokenOnPurpose() {
        check(1 + 1 == 3, "1 + 1 should be 3");
    }

    @Run
    void divideByZero() {
        int x = 10 / 0;               
    }

    void helper() { }

    void notATest() { }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}

class MiniRunner {
    static void run(Class<?> testClass) throws Exception {
        Method[] methods = testClass.getDeclaredMethods();
        Arrays.sort(methods, Comparator.comparing(Method::getName));   

        int passed = 0, failed = 0, skipped = 0;

        for (Method m : methods) {
            if (!m.isAnnotationPresent(Run.class)) {
                skipped++;
                continue;
            }
            if (m.getParameterCount() != 0) {
                System.out.println("IGNORED " + m.getName() + " -> @Run methods must take no arguments");
                continue;
            }

            Object instance = testClass.getDeclaredConstructor().newInstance();  
            m.setAccessible(true);

            try {
                m.invoke(instance);
                System.out.println("PASS " + m.getName());
                passed++;
            } catch (InvocationTargetException e) {
                Throwable cause = e.getCause();      
                System.out.println("FAIL " + m.getName() + " -> "
                                   + cause.getClass().getSimpleName() + ": " + cause.getMessage());
                failed++;
            }
        }

        System.out.println();
        System.out.println("Ran " + (passed + failed) + " @Run methods: "
                           + passed + " passed, " + failed + " failed");
        System.out.println("Skipped (not marked): " + skipped);
    }
}

public class Mini {
    public static void main(String[] args) throws Exception {
        MiniRunner.run(SampleTests.class);
    }
}