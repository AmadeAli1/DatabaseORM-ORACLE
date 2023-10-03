package database.query;

import database.ColumnType;

public interface SqlCompareOperator {
    public LogicOperator equal(String column, Object value);

    public LogicOperator notEqual(String column, Object value);

    public LogicOperator moreThan(String column, Object value);

    public LogicOperator lessThan(String column,  Object value);

    public LogicOperator moreThanOrEqual(String column, Object value);

    public LogicOperator lessThanOrEquals(String column,  Object value);
}
