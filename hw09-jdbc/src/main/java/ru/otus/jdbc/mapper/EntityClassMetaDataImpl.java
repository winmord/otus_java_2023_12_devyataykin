package ru.otus.jdbc.mapper;

import ru.otus.crm.annotation.Id;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;

public class EntityClassMetaDataImpl<T> implements EntityClassMetaData<T> {
    private final Class<T> clazz;
    private Constructor<T> constructor;
    private List<Field> fields;
    private Field id;

    public EntityClassMetaDataImpl(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public String getName() {
        return clazz.getSimpleName();
    }

    @Override
    public Constructor<T> getConstructor() {
        if (constructor == null) {
            try {
                constructor = clazz.getDeclaredConstructor();
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

        return constructor;
    }

    @Override
    public Field getIdField() {
        if (id == null) {
            for (Field field : fields) {
                if (field.getDeclaredAnnotation(Id.class) != null) {
                    id = field;
                    return id;
                }
            }
        }

        return id;
    }

    @Override
    public List<Field> getAllFields() {
        if (fields == null) {
            fields = List.of(clazz.getDeclaredFields());
        }

        return fields;
    }

    @Override
    public List<Field> getFieldsWithoutId() {
        return getAllFields().stream()
                .filter(field -> field.getDeclaredAnnotation(Id.class) == null)
                .toList();
    }
}
