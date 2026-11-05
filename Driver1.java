public class Driver1 {

    public static void main(String[] args) {

        Point[] points = {
                new Point(1, 2),
                new Point(3, 4),
                new Point(1, 2), // Repeat
                new Point(5, 6),
                new Point(3, 4)  // Repeat
        };

        int distinct = 0;

        for (int i = 0; i < points.length; i++) {

            boolean found = false;

            for (int j = 0; j < i; j++) {
                if (points[i].equals(points[j])) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                distinct++;
            }
        }

        System.out.println("Distinct: " + distinct);
    }
}