package cc.w0rm.ghost.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author panyupeng
 * @date 2020-10-27 00:03
 */
public class JacksonUtils {
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger LOG = LoggerFactory.getLogger(JacksonUtils.class);
    public static <T> T jsonString2Object(String jsonStr, Class<T> clazz) {
        return jsonString2Object(jsonStr, clazz, false);
    }
    
    public static <T> T jsonString2Object(String jsonStr, Class<T> clazz, boolean ignoreUnknowField) {
        if (null == jsonStr || jsonStr.isEmpty()) {
            return null;
        }
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, !ignoreUnknowField);
        
        try {
            return objectMapper.readValue(jsonStr, clazz);
        } catch (Exception e) {
            LOG.error("Json parse error! {}", e);
            return null;
        }
    }
    
    public static <T> T jsonString2Object(String jsonStr, TypeReference valueTypeRef) {
        if (null == jsonStr || jsonStr.isEmpty()) {
            return null;
        }
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        try {
            return objectMapper.readValue(jsonStr, valueTypeRef);
        } catch (Exception e) {
            LOG.error("Json parse error!", e);
            return null;
        }
    }
    
    
    public static <T> String object2jsonString(T object) {
        if (null == object) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            LOG.error("Json parse error!", e);
            return null;
        }
    }
}
