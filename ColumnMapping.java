import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface Column {
    String value();                    
    boolean required() default true;   
}

class Employee {
    @Column("emp_id")
    private int id;

    @Column("full_name")
    private String name;

    @Column("salary")
    private double salary;

    @Column("is_active")
    private boolean active;

    @Column(value = "nickname", required = false)
    private String nickname;

    private String internalNote;      

    @Override
    public String toString() {
        return "Employee{id=" + id + ", name='" + name + "', salary=" + salary
               + ", active=" + active + ", nickname=" + nickname + "}";
    }
}

class MissingColumnException extends RuntimeException {
    MissingColumnException(List<String> columns) {
        super("Missing required column(s): " + String.join(", ", columns));
    }
}

class CsvMapper {
    static <T> T map(Class<T> type, String[] header, String[] row)
            throws ReflectiveOperationException {

        Map<String, Integer> index = new HashMap<>();
        for (int i = 0; i < header.length; i++) {
            index.put(header[i].trim().toLowerCase(), i);
        }

        T obj = type.getDeclaredConstructor().newInstance();
        List<String> missing = new ArrayList<>();

        for (Field field : type.getDeclaredFields()) {
            Column col = field.getAnnotation(Column.class);
            if (col == null) continue;                        

            Integer pos = index.get(col.value().toLowerCase());
            if (pos == null || pos >= row.length) {
                if (col.required()) missing.add(col.value());
                continue;                     
            }

            field.setAccessible(true);
            field.set(obj, convert(row[pos].trim(), field.getType(), col.value()));
        }

        if (!missing.isEmpty()) {
            throw new MissingColumnException(missing);        
        }
        return obj;
    }

    private static Object convert(String text, Class<?> type, String column) {
        try {
            if (type == String.class) return text;
            if (type == int.class || type == Integer.class) return Integer.parseInt(text);
            if (type == long.class || type == Long.class) return Long.parseLong(text);
            if (type == double.class || type == Double.class) return Double.parseDouble(text);
            if (type == boolean.class || type == Boolean.class) return Boolean.parseBoolean(text);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                "Column '" + column + "': cannot convert '" + text + "' to " + type.getSimpleName());
        }
        throw new IllegalArgumentException(
            "Column '" + column + "': unsupported field type " + type.getSimpleName());
    }
}

public class ColumnMapping {
    public static void main(String[] args) {
        demo("1) Normal row, extra column ignored",
             "EMP_ID,Full_Name,Salary,Is_Active,Nickname,Dept",
             "7,Asha Mehta,45000.5,true,Ash,Sales");

        demo("2) Columns in a different order, optional column absent",
             "Salary,Full_Name,EMP_ID,Is_Active",
             "52000,Karan Joshi,8,false");

        demo("3) Required columns missing",
             "emp_id,full_name",
             "9,Ravi Shah");

        demo("4) Value that can't be converted",
             "emp_id,full_name,salary,is_active",
             "x9,Neha Patel,50000,true");
    }

    private static void demo(String label, String headerLine, String dataLine) {
        System.out.println(label);
        try {
            Employee e = CsvMapper.map(Employee.class, headerLine.split(","), dataLine.split(","));
            System.out.println("  " + e);
        } catch (MissingColumnException | IllegalArgumentException e) {
            System.out.println("  ERROR: " + e.getMessage());
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}