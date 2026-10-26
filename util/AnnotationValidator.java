package util;

import model.annotation.Positive;
import model.annotation.MaxLength;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class AnnotationValidator {
    public static String[] validate(Object obj) {
        List<String> errors = new ArrayList<>();
        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(obj);

             
                if (field.isAnnotationPresent(Positive.class)) {
                    Positive positive = field.getAnnotation(Positive.class);
                    if (value instanceof Number) {
                        long num = ((Number) value).longValue();
                        if (num <= 0) {
                            errors.add(field.getName() + " " + positive.message());
                        }
                    }
                }

           
                if (field.isAnnotationPresent(MaxLength.class)) {
                    MaxLength maxLength = field.getAnnotation(MaxLength.class);
                    if (value instanceof String) {
                        String str = (String) value;
                        if (str.length() > maxLength.value()) {
                            errors.add(field.getName() + " exceeds max length of " + maxLength.value());
                        }
                    }
                }
            } catch (IllegalAccessException e) {
                errors.add("Error accessing field: " + field.getName());
            }
        }

        return errors.toArray(new String[0]);
    }
}