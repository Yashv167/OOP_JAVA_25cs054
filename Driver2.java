import java.util.Scanner;

public class Driver2 {

    public static void main(String[] args) {
        // (a) log lines, including one malformed line (only 2 parts)
        String[] logs = {
            "10:05 alice Hello there",
            "10:07 bob Meeting at noon",
            "10:09 carol"
        };

        // (b) read keyword
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter keyword: ");
        String keyword = sc.nextLine().trim();

        // (c)-(d) filter
        chatFilter filter = new chatFilter();
        filter.filter(logs, keyword);

        // (e) print results
        System.out.println("Matches: " + filter.getMatchCount());
        System.out.print(filter.getReport());

        sc.close();
    }
}