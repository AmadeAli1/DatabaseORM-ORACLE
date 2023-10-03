package database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public enum ColumnType {
    String {
        @Override
        public Object getValue(String column, ResultSet resultSet) {
            try {
                return resultSet.getString(column);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void setValue(int index, Object value, PreparedStatement preparedStatement) {
            try {
                preparedStatement.setString(index, (String) value);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    },
    Boolean {
        @Override
        public Object getValue(String column, ResultSet resultSet) {
            try {
                return resultSet.getBoolean(column);
            } catch (SQLException e) {
                //System.err.printf("The columnLabel %s is not valid\n, this field must be null", column);
                throw new RuntimeException(e);
            }
        }

        @Override
        public void setValue(int index, Object value, PreparedStatement preparedStatement) {
            try {
                preparedStatement.setBoolean(index, (Boolean) value);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    },
    Long {
        @Override
        public Object getValue(String column, ResultSet resultSet) {
            try {
                return resultSet.getLong(column);
            } catch (SQLException e) {
                throw new RuntimeException(e);

            }
        }

        @Override
        public void setValue(int index, Object value, PreparedStatement preparedStatement) {
            try {
                preparedStatement.setLong(index, (Long) value);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    },
    Integer {
        @Override
        public Object getValue(String column, ResultSet resultSet) {
            try {
                return resultSet.getInt(column);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }

        @Override
        public void setValue(int index, Object value, PreparedStatement preparedStatement) {
            try {
                preparedStatement.setInt(index, (Integer) value);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    },
    Float {
        @Override
        public Object getValue(String column, ResultSet resultSet) {
            try {
                return resultSet.getFloat(column);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void setValue(int index, Object value, PreparedStatement preparedStatement) {
            try {
                preparedStatement.setFloat(index, (Float) value);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    },
    Byte {
        @Override
        public Object getValue(String column, ResultSet resultSet) {
            try {
                return resultSet.getByte(column);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void setValue(int index, Object value, PreparedStatement preparedStatement) {
            try {
                preparedStatement.setByte(index, (java.lang.Byte) value);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    },
    Double {
        @Override
        public Object getValue(String column, ResultSet resultSet) {
            try {
                return resultSet.getDouble(column);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void setValue(int index, Object value, PreparedStatement preparedStatement) {
            try {
                preparedStatement.setDouble(index, (Float) value);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    },
    Date {
        @Override
        public Object getValue(String column, ResultSet resultSet) {
            try {
                return resultSet.getDate(column);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void setValue(int index, Object value, PreparedStatement preparedStatement) {
            try {
                preparedStatement.setDate(index, (java.sql.Date) value);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    };


    public abstract Object getValue(String column, ResultSet resultSet);

    public abstract void setValue(int index, Object value, PreparedStatement preparedStatement);
}

