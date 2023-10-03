package database;

import annotations.Entity;
import database.query.ExecuteSqlQuery;
import database.query.SqlCompare;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DeleteBuilder extends SqlCompare implements ExecuteSqlQuery {

    @Override
    public <T> boolean execute(
            Class<T> clazz
    ) {
        String queryBuilderString = queryBuilder.toString();
        Connection connection = DatabaseConnection.getInstance().getCONNECTION();

        if (clazz.isAnnotationPresent(Entity.class)) {
            var entity = clazz.getAnnotation(Entity.class).name();
            String query = "delete from %s where %s".formatted(entity, queryBuilderString);

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
