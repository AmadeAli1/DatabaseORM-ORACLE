package annotations;

import java.util.Map;

public interface DatabaseQuery {

    <T> Map<Object, T> selectQuery(String query, Class<T> clazz);

    <T, ID> T findById(Class<T> clazz, ID id);

    <T> boolean save(T data);

    <T> boolean update(T data);

    <T, ID> boolean deleteById(Class<T> clazz, ID id);

}
