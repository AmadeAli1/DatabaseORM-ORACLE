package database.query;

public interface ExecuteSqlQuery {
    abstract <T> boolean execute(Class<T> clazz);

}
