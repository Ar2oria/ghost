package cc.w0rm.ghost.common.json;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class TypeReference<T> implements Comparable<TypeReference<T>> {
    
    protected final Type type;

    protected TypeReference() {
        Type superClass = getClass().getGenericSuperclass();
        if (superClass instanceof Class<?>) {
            throw new IllegalArgumentException(
                "Internal error: TypeReference constructed without actual type information");
        }
        type = ((ParameterizedType)superClass).getActualTypeArguments()[0];
    }

    public Type getType() {
        return type;
    }

    /**
     * The only reason we define this method (and require implementation of <code>Comparable</code>) is to prevent
     * constructing a reference without type information.
     */
    @Override
    public int compareTo(TypeReference<T> o) {
        return 0;
    }
}
