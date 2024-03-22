package ru.otus.jdbc.mapper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import ru.otus.core.repository.DataTemplate;
import ru.otus.core.repository.DataTemplateException;
import ru.otus.core.repository.executor.DbExecutor;

/**
 * Сохратяет объект в базу, читает объект из базы
 */
public class DataTemplateJdbc<T> implements DataTemplate<T> {
    private final EntityClassMetaData<T> entityClassMetaData;
    private final DbExecutor dbExecutor;
    private final EntitySQLMetaData entitySQLMetaData;

    public DataTemplateJdbc(
            EntityClassMetaData<T> entityClassMetaData, DbExecutor dbExecutor, EntitySQLMetaData entitySQLMetaData) {
        this.entityClassMetaData = entityClassMetaData;
        this.dbExecutor = dbExecutor;
        this.entitySQLMetaData = entitySQLMetaData;
    }

    @Override
    public Optional<T> findById(Connection connection, long id) {
        return dbExecutor.executeSelect(connection, entitySQLMetaData.getSelectByIdSql(), List.of(id), rs -> {
            try {
                if (rs.next()) {
                    return parseResultSetRecord(rs);
                }
                return null;
            } catch (SQLException e) {
                throw new DataTemplateException(e);
            }
        });
    }

    @Override
    public List<T> findAll(Connection connection) {
        return dbExecutor
                .executeSelect(connection, entitySQLMetaData.getSelectAllSql(), Collections.emptyList(), rs -> {
                    var instances = new ArrayList<T>();
                    try {
                        while (rs.next()) {
                            instances.add(parseResultSetRecord(rs));
                        }
                        return instances;
                    } catch (SQLException e) {
                        throw new DataTemplateException(e);
                    }
                })
                .orElseThrow(() -> new RuntimeException("Unexpected error"));
    }

    @Override
    public long insert(Connection connection, T client) {
        try {
            return dbExecutor.executeStatement(connection, entitySQLMetaData.getInsertSql(), getFields(client));
        } catch (Exception e) {
            throw new DataTemplateException(e);
        }
    }

    @Override
    public void update(Connection connection, T client) {
        try {
            dbExecutor.executeStatement(connection, entitySQLMetaData.getUpdateSql(), getFields(client));
        } catch (Exception e) {
            throw new DataTemplateException(e);
        }
    }

    private T parseResultSetRecord(ResultSet rs) {
        try {
            T instance = entityClassMetaData.getConstructor().newInstance();
            Map<String, Field> fields = entityClassMetaData.getAllFields().stream()
                    .collect(Collectors.toMap(Field::getName, Function.identity()));

            for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                String columnName = rs.getMetaData().getColumnName(i);
                Field field = fields.get(columnName);

                if (field == null) {
                    throw new NoSuchFieldException();
                }

                field.setAccessible(true);
                field.set(instance, rs.getObject(i));
            }

            return instance;
        } catch (IllegalAccessException
                | InstantiationException
                | InvocationTargetException
                | SQLException
                | NoSuchFieldException e) {
            throw new DataTemplateException(e);
        }
    }

    private List<Object> getFields(T client) {
        return entityClassMetaData.getFieldsWithoutId().stream()
                .map(field -> {
                    field.setAccessible(true);
                    try {
                        return field.get(client);
                    } catch (IllegalAccessException e) {
                        throw new DataTemplateException(e);
                    }
                })
                .toList();
    }
}
