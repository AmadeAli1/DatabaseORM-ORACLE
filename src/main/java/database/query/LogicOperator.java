package database.query;

public interface LogicOperator extends ExecuteSqlQuery{
    public SqlCompareOperator and();

    public SqlCompareOperator or();
}
