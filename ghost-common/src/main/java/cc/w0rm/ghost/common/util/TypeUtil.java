package cc.w0rm.ghost.common.util;

/**
 * @author : xuyang
 * @date : 2020/10/13 3:02 下午
 */
public class TypeUtil {

    @SuppressWarnings("unchecked")
    public static<T> T convert(Object o){
        return (T)o;
    }
}
