package club.minion.practice.util;

import com.conaxgames.util.finalutil.CC;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class CCUtil {

    public static String getValue(String fieldName) {
        try {
            Field field = CC.class.getDeclaredField(fieldName);

            // Make sure the field is static and of type String
            if (Modifier.isStatic(field.getModifiers()) && field.getType() == String.class) {
                field.setAccessible(true); // Allow access to private fields
                return (String) field.get(null); // Get the value of the field
            } else {
                // Handle cases where the field is not found or not of the expected type
                return "Field not found or is not a String";
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            // Handle exceptions, e.g., if the field doesn't exist or access is denied
            return "Error: " + e.getMessage();
        }
    }

}
