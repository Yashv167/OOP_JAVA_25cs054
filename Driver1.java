public class Driver1 {

    public static void main(String[] args) {
        String[] passwords = {
            "abc",
            "abcdefgh",
            "Abcdefgh",
            "abcd1234",
            "Abcd1234",
            "abc@123",
            "Abcd1234!",
            ""
        };

        for (String pw : passwords) {
            System.out.println("Password: \"" + pw + "\"");
            System.out.println("  Length >= 8       : " + (PasswordChecker.hasMinLength(pw)   ? "PASS" : "FAIL"));
            System.out.println("  Has uppercase     : " + (PasswordChecker.hasUppercase(pw)   ? "PASS" : "FAIL"));
            System.out.println("  Has digit         : " + (PasswordChecker.hasDigit(pw)       ? "PASS" : "FAIL"));
            System.out.println("  Has special char  : " + (PasswordChecker.hasSpecialChar(pw) ? "PASS" : "FAIL"));
            System.out.println("  Rules passed      : " + PasswordChecker.countPassed(pw) + "/4");
            System.out.println("  Strength          : " + PasswordChecker.strength(pw));
            System.out.println();
        }
    }
}