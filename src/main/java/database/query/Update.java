package database.query;

import database.ColumnType;

public interface Update {

    Update set(String column, Object value);

    SqlCompareOperator where();
}
