import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class templateFiller {

    private static final Pattern PLACEHOLDER = Pattern.compile("\\{(\\w+)\\}");

    private static String lookup(String name, String[] names, String[] values) {
        for (int i = 0; i < names.length; i++) {
            if (names[i].equals(name)) {
                return values[i];
            }
        }
        return "[?]";
    }

    public static String fill(String template, String[] names, String[] values) {
        Matcher m = PLACEHOLDER.matcher(template);
        StringBuilder sb = new StringBuilder();
        int last = 0;

        while (m.find()) {
            sb.append(template, last, m.start());           
            sb.append(lookup(m.group(1), names, values));   
            last = m.end();
        }
        sb.append(template.substring(last));                

        return sb.toString();
    }
}