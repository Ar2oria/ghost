package cc.w0rm.ghost.common.json.adapter;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class JsonAdapterFactory {
    
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(JsonAdapterFactory.class);

    /** JsonAdapter及其依赖的对应 */
    private static Map<String, Class<? extends JsonAdapter>> classAdapterRelationMap = new LinkedHashMap<>();

    static {
        classAdapterRelationMap.put("com.fasterxml.jackson.databind.ObjectMapper", JacksonAdapter.class);
        classAdapterRelationMap.put("com.alibaba.fastjson.JSON", FastjsonAdapter.class);
    }

    /**
     * 添加JsonAdapter及其依赖的对应关系
     * 
     * @param className 依赖的类
     * @param jsonAdapterClass JsonAdapter
     */
    public static void addClassAdapterRelation(String className, Class<? extends JsonAdapter> jsonAdapterClass) {
        classAdapterRelationMap.put(className, jsonAdapterClass);
    }
    
    /**
     * 获取JsonAdapter及其依赖的对应
     *
     * @return JsonAdapter及其依赖的对应
     */
    public static Map<String, Class<? extends JsonAdapter>> getClassAdapterRelationMap() {
        return classAdapterRelationMap;
    }
    
    /**
     * 自动创建 JsonAdapter
     * 匹配 classAdapterRelationMap 的 className，创建对应 JsonAdapter
     *
     * @return JsonAdapter
     */
    public static Optional<JsonAdapter> create() {
        for (Map.Entry<String, Class<? extends JsonAdapter>> entry : classAdapterRelationMap.entrySet()) {
            if(!findByClassName(entry.getKey()).isPresent()){
                LOGGER.info("can not find class with className=[{}]", entry.getKey());
                continue;
            }
            try {
                LOGGER.info("try to create JsonAdapter with classAdapterRelation=[{}]", entry);
                return Optional.of(entry.getValue().newInstance());
            } catch (Exception e) {
                LOGGER.info("can not create [" + entry.getValue().getName() + "]", e);
            }
        }
        LOGGER.info("can not create JsonAdapter with [{}]", classAdapterRelationMap);
        return Optional.empty();
    }
    
    /**
     * 尝试查询 ClassName 对应的类
     *
     * @param className 类名
     * @return 类
     */
    private static Optional<Class> findByClassName(String className) {
        try {
            return Optional.of(Class.forName(className));
        } catch (ClassNotFoundException ige) {
            return Optional.empty();
        }
    }
}
