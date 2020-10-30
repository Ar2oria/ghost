package cc.w0rm.ghost.common.json;

import cc.w0rm.ghost.common.json.adapter.JsonAdapter;
import cc.w0rm.ghost.common.json.adapter.JsonAdapterFactory;
import cc.w0rm.ghost.common.util.Strings;
import com.google.common.base.Preconditions;

import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Optional;

public class JsonUtil {

    /** json 类库适配器 */
    private static JsonAdapter jsonAdapter;

    /**
     * 转化 java 对象为 json 字符串
     *
     * @param obj java 对象
     * @return json 字符串
     */
    public static String writeValueAsString(Object obj) {
        return writeValueAsString(obj, getJsonAdapter());
    }

    /**
     * 转化 java 对象为 json 字符串
     *
     * @param obj java 对象
     * @param jsonAdapter json 类库适配器
     * @return json 字符串
     */
    public static String writeValueAsString(Object obj, JsonAdapter jsonAdapter) {
        Preconditions.checkArgument(!Objects.isNull(obj), "obj can not be null");
        Preconditions.checkArgument(!Objects.isNull(jsonAdapter), "jsonAdapter can not be null");
        return jsonAdapter.writeValueAsString(obj);
    }

    /**
     * 转化 json 字符串为 java 对象
     *
     * @param json json 字符串
     * @param type java 对象的类型
     * @param <T> 输出 java 对象的范性
     * @return java 对象
     */
    public static <T> T readValue(String json, TypeReference<T> type) {
        return readValue(json, type.getType(), getJsonAdapter());
    }

    /**
     * 转化 json 字符串为 java 对象
     *
     * @param json json 字符串
     * @param type java 对象的类型
     * @param <T> 输出 java 对象的范性
     * @return java 对象
     */
    public static <T> T readValue(String json, TypeReference<T> type, JsonAdapter jsonAdapter) {
        return readValue(json, type.getType(), jsonAdapter);
    }

    /**
     * 转化 json 字符串为 java 对象
     *
     * @param json json 字符串
     * @param type java 对象的类型
     * @param <T> 输出 java 对象的范性
     * @return java 对象
     */
    public static <T> T readValue(String json, Type type) {
        return readValue(json, type, getJsonAdapter());
    }

    /**
     * 转化 json 字符串为 java 对象
     *
     * @param json json 字符串
     * @param type java 对象的类型
     * @param <T> 输出 java 对象的范性
     * @return java 对象
     */
    public static <T> T readValue(String json, Type type, JsonAdapter jsonAdapter) {
        Preconditions.checkArgument(Strings.isNotBlank(json), "json can not be blank");
        Preconditions.checkArgument(!Objects.isNull(type), "type can not be null");
        Preconditions.checkArgument(!Objects.isNull(jsonAdapter), "jsonAdapter can not be null");
        return jsonAdapter.readValue(json, type);
    }
    
    /**
     * 获取 json 适配器
     * 
     * @return JsonAdapter
     */
    public static JsonAdapter getJsonAdapter() {
        if (Objects.isNull(jsonAdapter)) {
            Optional<JsonAdapter> jsonAdapter = JsonAdapterFactory.create();
            if (jsonAdapter.isPresent()) {
                setJsonAdapter(jsonAdapter.get());
                return jsonAdapter.get();
            } else {
                throw new IllegalStateException("please set JsonAdapter before use JsonUtil !");
            }
        }
        return jsonAdapter;
    }
    
    /**
     * 设置 JsonAdapter
     * 
     * @param jsonAdapter json 适配器
     */
    public static void setJsonAdapter(JsonAdapter jsonAdapter) {
        JsonUtil.jsonAdapter = jsonAdapter;
    }
}
