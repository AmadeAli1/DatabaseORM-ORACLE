package database.query;

import database.ColumnType;
import database.Pair;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public abstract class SqlCompare implements LogicOperator, SqlCompareOperator {
    protected StringBuilder queryBuilder = new StringBuilder();
    protected final List<Pair<ColumnType, Object>> values;

    public SqlCompare() {
        values = new ArrayList<>();
    }

    @Override
    public LogicOperator equal(String column, Object value) {
        var append = column + " = " + "?";
        queryBuilder.append(append);
        values.add(new Pair<>(toColumnType(value), value));
        return this;
    }

    @Override
    public LogicOperator notEqual(String column, Object value) {
        var append = column + " != " + "?";
        queryBuilder.append(append);
        values.add(new Pair<>(toColumnType(value), value));
        return this;
    }

    @Override
    public LogicOperator moreThan(String column, Object value) {
        var append = column + " > " + "?";
        queryBuilder.append(append);
        values.add(new Pair<>(toColumnType(value), value));
        return this;
    }

    @Override

    public LogicOperator lessThan(String column, Object value) {
        var append = column + " < " + "?";
        queryBuilder.append(append);
        values.add(new Pair<>(toColumnType(value), value));
        return this;
    }

    @Override
    public SqlCompare moreThanOrEqual(String column, Object value) {
        var append = column + ">=" + "?";
        queryBuilder.append(append);
        values.add(new Pair<>(toColumnType(value), value));
        return this;
    }

    @Override
    public LogicOperator lessThanOrEquals(String column, Object value) {
        var append = column + " <= " + "?";
        queryBuilder.append(append);
        values.add(new Pair<>(toColumnType(value), value));
        return this;
    }

    @Override
    public SqlCompareOperator and() {
        queryBuilder.append(" and ");
        return this;
    }

    @Override
    public SqlCompareOperator or() {
        queryBuilder.append(" or ");
        return this;
    }

    protected ColumnType toColumnType(Object value) {
        if (value instanceof Integer) {
            return ColumnType.Integer;
        }
        if (value instanceof Long) {
            return ColumnType.Long;

        }
        if (value instanceof Double) {
            return ColumnType.Double;

        }
        if (value instanceof Float) {
            return ColumnType.Float;

        }
        if (value instanceof Byte) {
            return ColumnType.Byte;

        }
        if (value instanceof Boolean) {
            return ColumnType.Boolean;

        }
        if (value instanceof String) {
            return ColumnType.String;
        }
        if (value instanceof Date) {
            return ColumnType.Date;
        }
        throw new RuntimeException("Invalid column type. See ColumnType enum!");
    }

}
