package database;

import annotations.Column;
import annotations.DatabaseQuery;
import annotations.Entity;
import annotations.Id;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Database implements DatabaseQuery {
    private final Connection connection;

    public Database() {
        connection = DatabaseConnection.getInstance().getCONNECTION();
    }

    public Database(String filePath) {
        connection = DatabaseConnection.getInstance(filePath).getCONNECTION();
    }

    @Override
    public <T> Map<Object, T> selectQuery(String query, Class<T> clazz) {
        try {
            if (clazz.isAnnotationPresent(Entity.class)) {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                ResultSet resultSet = preparedStatement.executeQuery();
                Map<Object, T> allData = new HashMap<>();
                while (resultSet.next()) {
                    try {
                        T instance = clazz.getConstructor().newInstance();
                        Object id = null;
                        boolean isIdVerify = false;
                        for (Field declaredField : clazz.getFields()) {
                            if (declaredField.isAnnotationPresent(Column.class)) {
                                declaredField.setAccessible(true);
                                try {
                                    var column = declaredField.getAnnotation(Column.class).name();
                                    var type = declaredField.getAnnotation(Column.class).type();
                                    Object value = type.getValue(column, resultSet);
                                    if (!isIdVerify) {
                                        var isIdColumn = declaredField.getAnnotation(Id.class);
                                        if (isIdColumn != null) {
                                            id = value;
                                            isIdVerify = true;
                                        }
                                    }
                                    declaredField.set(instance, value);
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                        allData.put(id, instance);
                    } catch (NoSuchMethodException e) {
                        throw new RuntimeException("Required empty constructor public %s(){}".formatted(clazz.getSimpleName()));
                    } catch (Exception e) {
                        throw new RuntimeException(e.getMessage());
                    }
                }
                resultSet.close();
                preparedStatement.closeOnCompletion();
                return allData;
            } else {
                throw new RuntimeException("Invalid Class! Your Data model require @Entity annotation");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T, ID> T findById(Class<T> clazz, ID id) {
        try {
            if (clazz.isAnnotationPresent(Entity.class)) {
                String entity = clazz.getAnnotation(Entity.class).name();
                String idColumn = Arrays.stream(clazz.getFields()).filter(field -> field.isAnnotationPresent(Id.class))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Column with annotation @Id not found!"))
                        .getName();
                final var findById = "select * from %s where %s=%s".formatted(entity, idColumn, id);
                PreparedStatement preparedStatement = connection.prepareStatement(findById);
                ResultSet resultSet = preparedStatement.executeQuery();
                try {
                    resultSet.next();
                    if (resultSet.getRow() == 0) return null;
                    T instance = clazz.getConstructor().newInstance();
                    for (Field declaredField : clazz.getFields()) {
                        if (declaredField.isAnnotationPresent(Column.class)) {
                            try {
                                declaredField.setAccessible(true);
                                var column = declaredField.getAnnotation(Column.class).name();
                                var type = declaredField.getAnnotation(Column.class).type();
                                Object value = type.getValue(column, resultSet);
                                declaredField.set(instance, value);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                    resultSet.close();
                    preparedStatement.closeOnCompletion();
                    return instance;
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException("Required empty constructor public %s(){}".formatted(clazz.getSimpleName()));
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage());
                }
            } else {
                throw new RuntimeException("Invalid Class! Your Data model require @Entity annotation");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T, ID> boolean deleteById(Class<T> clazz, ID id) {
        try {
            if (clazz.isAnnotationPresent(Entity.class)) {
                String entity = clazz.getAnnotation(Entity.class).name();
                String idColumn = Arrays.stream(clazz.getFields()).filter(field -> field.isAnnotationPresent(Id.class))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Column with annotation @Id not found!"))
                        .getName();
                final var deleteById = "delete from %s where %s=%s".formatted(entity, idColumn, id);
                PreparedStatement preparedStatement = connection.prepareStatement(deleteById);
                try {
                    int deleted = preparedStatement.executeUpdate();
                    preparedStatement.closeOnCompletion();
                    return deleted == 1;
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage());
                }
            } else {
                throw new RuntimeException("Invalid Class! Your Data model (%s) require @Entity annotation".formatted(clazz.getSimpleName()));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> boolean save(T data) {
        try {
            Class<?> clazz = data.getClass();
            if (clazz.isAnnotationPresent(Entity.class)) {
                var entity = clazz.getAnnotation(Entity.class).name();
                String params = Arrays.stream(clazz.getFields()).filter(field -> field.isAnnotationPresent(Column.class))
                        .map(field -> field.getAnnotation(Column.class).name())
                        .collect(Collectors.joining(","));

                String args = Arrays.stream(params.split(","))
                        .map(name -> "?")
                        .collect(Collectors.joining(","));

                var insertQuery = "insert into %s (%s) values (%s)".formatted(entity, params, args);
                PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
                for (int index = 0; index < clazz.getFields().length; index++) {
                    Field declaredField = clazz.getFields()[index];
                    declaredField.setAccessible(true);
                    if (declaredField.isAnnotationPresent(Column.class)) {
                        declaredField.getAnnotation(Column.class)
                                .type()
                                .setValue(index + 1, declaredField.get(data), preparedStatement);
                    }
                }
                preparedStatement.executeUpdate();
                preparedStatement.close();
                return true;
            } else {
                throw new RuntimeException("Invalid Class! Your Data model require @Entity annotation");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> boolean update(T data) {
        try {
            Class<?> clazz = data.getClass();
            if (clazz.isAnnotationPresent(Entity.class)) {
                var entity = clazz.getAnnotation(Entity.class).name();
                String params = Arrays.stream(clazz.getFields()).filter(field -> field.isAnnotationPresent(Column.class))
                        .map(field -> field.getAnnotation(Column.class).name() + " = ?")
                        .collect(Collectors.joining(","));
                Field idField = Arrays.stream(clazz.getFields()).filter(field -> field.isAnnotationPresent(Id.class))
                        .findFirst()
                        .orElseThrow();
                idField.setAccessible(true);
                var idColumn = idField.getAnnotation(Column.class).name();
                var idValue = idField.get(data);
                var updateQuery = "UPDATE %s SET %s where %s = %s".formatted(entity, params, idColumn, idValue);
                PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
                for (int index = 0; index < clazz.getFields().length; index++) {
                    Field declaredField = clazz.getFields()[index];
                    declaredField.setAccessible(true);
                    if (declaredField.isAnnotationPresent(Column.class)) {
                        declaredField.getAnnotation(Column.class)
                                .type()
                                .setValue(index + 1, declaredField.get(data), preparedStatement);
                    }
                }
                var updated = (preparedStatement.executeUpdate() == 1);
                preparedStatement.close();
                return updated;
            } else {
                throw new RuntimeException("Invalid Class! Your Data model require @Entity annotation");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
