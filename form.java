import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface NotBlank {
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface MaxLength {
    int value();
}

class SignupForm {
    @NotBlank
    @MaxLength(10)
    private String username;

    @NotBlank
    @MaxLength(30)
    private String email;

    @NotBlank
    @MaxLength(16)
    private String password;

    @MaxLength(20)         
    private String bio;

    SignupForm(String username, String email, String password, String bio) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.bio = bio;
    }
}

class Validator {
    static List<String> validate(Object obj) {
        List<String> errors = new ArrayList<>();

        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);                 // fields are private
            Object value;
            try {
                value = field.get(obj);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Cannot read field " + field.getName(), e);
            }

            if (field.isAnnotationPresent(NotBlank.class)) {
                if (value == null || value.toString().trim().isEmpty()) {
                    errors.add(field.getName() + " must not be blank");
                }
            }

            if (field.isAnnotationPresent(MaxLength.class)) {
                int max = field.getAnnotation(MaxLength.class).value();
                if (value instanceof String) {
                    int len = ((String) value).length();
                    if (len > max) {
                        errors.add(field.getName() + " must be at most " + max
                                   + " characters (was " + len + ")");
                    }
                }
            }
        }
        return errors;
    }
}

public class form {
    public static void main(String[] args) {
        SignupForm bad = new SignupForm(
            "   ",
            "this.email.address@verylongdomain.example.com",
            "abc",
            "This bio is far too long"
        );
        SignupForm good = new SignupForm("asha", "asha@example.com", "secret123", null);

        report("Bad form", bad);
        report("Good form", good);
    }

    private static void report(String label, Object form) {
        List<String> errors = Validator.validate(form);
        System.out.println(label + ":");
        if (errors.isEmpty()) {
            System.out.println("  valid");
        } else {
            for (String e : errors) {
                System.out.println("  - " + e);
            }
        }
    }
}