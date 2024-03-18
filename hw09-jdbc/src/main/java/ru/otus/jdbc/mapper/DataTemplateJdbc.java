package ru.otus.jdbc.mapper;

import ru.otus.core.repository.DataTemplate;
import ru.otus.core.repository.DataTemplateException;
import ru.otus.core.repository.executor.DbExecutor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Сохратяет объект в базу, читает объект из базы
 */
public class DataTemplateJdbc<T> implements DataTemplate<T> {
    private final EntityClassMetaData<T> entityClassMetaData;
    private final DbExecutor dbExecutor;
    private final EntitySQLMetaData entitySQLMetaData;

    public DataTemplateJdbc(EntityClassMetaData<T> entityClassMetaData, DbExecutor dbExecutor, EntitySQLMetaData entitySQLMetaData) {
        this.entityClassMetaData = entityClassMetaData;
        this.dbExecutor = dbExecutor;
        this.entitySQLMetaData = entitySQLMetaData;
    }

    @Override
    public Optional<T> findById(Connection connection, long id) {
        return dbExecutor.executeSelect(connection, entitySQLMetaData.getSelectByIdSql(), List.of(id), rs -> {
            try {
                if (rs.next()) {
                    try {
                        T instance = entityClassMetaData.getConstructor().newInstance();

                        for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                            Field field = instance.getClass().getDeclaredField(rs.getMetaData().getColumnName(i));
                            field.setAccessible(true);
                            field.set(instance, rs.getObject(i));
                        }

                        return instance;
                    } catch (NoSuchFieldException | IllegalAccessException | InstantiationException |
                             InvocationTargetException e) {
                        throw new DataTemplateException(e);
                    }
                }
                return null;
            } catch (SQLException e) {
                throw new DataTemplateException(e);
            }
        });
    }

    @Override
    public List<T> findAll(Connection connection) {
        return dbExecutor.executeSelect(connection, entitySQLMetaData.getSelectAllSql(), Collections.emptyList(), rs -> {
                    Constructor<T> constructor = entityClassMetaData.getConstructor();
                    var instances = new ArrayList<T>();

                    try {
                        while (rs.next()) {
                            T instance = constructor.newInstance();

                            for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                                Field field = instance.getClass().getDeclaredField(rs.getMetaData().getColumnName(i));
                                field.setAccessible(true);
                                field.set(instance, rs.getObject(i));
                            }

                            instances.add(instance);
                        }
                        return instances;
                    } catch (SQLException | IllegalAccessException | InstantiationException |
                             InvocationTargetException | NoSuchFieldException e) {
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

    private List<Object> getFields(T client) {
        return entityClassMetaData
                .getFieldsWithoutId()
                .stream()
                .map(field -> {
                    field.setAccessible(true);
                    try {
                        return field.get(client);
                    } catch (IllegalAccessException e) {
                        throw new DataTemplateException(e);
                    }
                }).toList();
    }
}
