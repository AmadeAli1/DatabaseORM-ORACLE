package database;

import annotations.Entity;
import database.query.SqlCompare;
import database.query.SqlCompareOperator;
import database.query.Update;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateBuilder extends SqlCompare implements Update {

    @Override
    public Update set(String column, Object value) {
        String append;
        if (queryBuilder.isEmpty()) {
            append = column + " = " + "?";
        } else {
            append = " , " + column + " = " + "?";
        }
        queryBuilder.append(append);
        values.add(new Pair<>(toColumnType(value), value));
        return this;
    }

    @Override
    public SqlCompareOperator where() {
        queryBuilder.append(" where ");
        return this;
    }

    @Override
    public <T> boolean execute(
            Class<T> clazz
    ) {
        String updateQuery = queryBuilder.toString();
        Connection connection = DatabaseConnection.getInstance().getCONNECTION();
        if (clazz.isAnnotationPresent(Entity.class)) {
            var entity = clazz.getAnnotation(Entity.class).name();
            String query = "UPDATE %s SET %s".formatted(entity, updateQuery);
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                for (int index = 0; index < values.size(); index++) {
                    Pair<ColumnType, Object> pair = values.get(index);
                    pair.first().setValue(index + 1, pair.second(), preparedStatement);
                }
                int deleted = preparedStatement.executeUpdate();
                preparedStatement.closeOnCompletion();
                return deleted == 1;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new RuntimeException("Invalid Class! Your Data model require @Entity annotation");
        }
    }

}
