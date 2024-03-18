package ru.otus.jdbc.mapper;

import java.lang.reflect.Field;
import java.util.stream.Collectors;

public class EntitySQLMetaDataImpl implements EntitySQLMetaData {
    private final EntityClassMetaData<?> entityClassMetaData;

    public EntitySQLMetaDataImpl(EntityClassMetaData<?> entityClassMetaData) {
        this.entityClassMetaData = entityClassMetaData;
    }

    @Override
    public String getSelectAllSql() {
        return String.format("SELECT * FROM %s", entityClassMetaData.getName());
    }

    @Override
    public String getSelectByIdSql() {
        return String.format("SELECT * FROM %s WHERE %s = ?", entityClassMetaData.getName(), entityClassMetaData.getIdField().getName());
    }

    @Override
    public String getInsertSql() {
        int fieldsCount = entityClassMetaData.getFieldsWithoutId().size();
        StringBuilder values = new StringBuilder();
        values.repeat("?,", fieldsCount);
        values.setLength(values.length() - 1);

        return String.format("INSERT INTO %s (%s) VALUES (%s)", entityClassMetaData.getName(), getFieldNames(), values);
    }

    @Override
    public String getUpdateSql() {
        return String.format("UPDATE %s SET %s WHERE id = ?", entityClassMetaData.getName(), getFieldNames());
    }

    private String getFieldNames() {
        return entityClassMetaData.getFieldsWithoutId().stream()
                .map(Field::getName)
                .collect(Collectors.joining(","));
    }
}
