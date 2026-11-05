public class PasswordChecker {

    public static boolean hasMinLength(String pw) {
        return pw != null && pw.length() >= 8;
    }

    public static boolean hasUppercase(String pw) {
        return pw != null && pw.matches(".*[A-Z].*");
    }

    public static boolean hasDigit(String pw) {
        return pw != null && pw.matches(".*[0-9].*");
    }

    public static boolean hasSpecialChar(String pw) {
        return pw != null && pw.matches(".*[^A-Za-z0-9].*");
    }

    public static int countPassed(String pw) {
        int count = 0;
        if (hasMinLength(pw))  count++;
        if (hasUppercase(pw))  count++;
        if (hasDigit(pw))      count++;
        if (hasSpecialChar(pw)) count++;
        return count;
    }

    public static String strength(String pw) {
        int score = countPassed(pw);
        if (score <= 1) {
            return "Weak";
        } else if (score <= 3) {
            return "Medium";
        } else {
            return "Strong";
        }
    }
}
