public class chatFilter {

    private int matchCount = 0;
    private StringBuilder report = new StringBuilder();

    public void filter(String[] lines, String keyword) {
        matchCount = 0;
        report.setLength(0);

        String key = keyword.toLowerCase();

        for (String line : lines) {
            String[] parts = line.split(" ", 3);

            if (parts.length != 3) {
                continue;
            }

            String time = parts[0];
            String user = parts[1];
            String message = parts[2];

            if (message.toLowerCase().contains(key)) {
                matchCount++;
                report.append(time).append(" ")
                      .append(user).append(": ")
                      .append(message).append("\n");
            }
        }
    }

    public int getMatchCount() {
        return matchCount;
    }

    public String getReport() {
        return report.toString();
    }
}