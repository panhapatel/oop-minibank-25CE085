package util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.annotation.Id;

public class Repository<T> {
    private final Map<String, T> storage = new HashMap<>();

    public T save(T entity) {
        if (entity == null) return null;
        String id = extractId(entity);
        if (id != null) {
            storage.put(id, entity);
        }
        return entity;
    }

    @SafeVarargs
    public final void saveAll(T... items) {
        for (T item : items) {
            save(item);
        }
    }

    public T findById(String id) {
        return storage.get(id);
    }

    public ArrayList<T> findAll() {
        return new ArrayList<>(storage.values());
    }

    public boolean delete(String id) {
        return storage.remove(id) != null;
    }

    private String extractId(T entity) {
        Class<?> clazz = entity.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) {
                field.setAccessible(true);
                try {
                    Object value = field.get(entity);
                    return value != null ? value.toString() : null;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        throw new RuntimeException("Entity does not have a field marked with @Id annotation.");
    }

    public static <N extends Number> double average(N[] values) {
        if (values == null || values.length == 0) return 0.0;
        double sum = 0.0;
        for (N val : values) {
            sum += val.doubleValue();
        }
        return sum / values.length;
    }
}