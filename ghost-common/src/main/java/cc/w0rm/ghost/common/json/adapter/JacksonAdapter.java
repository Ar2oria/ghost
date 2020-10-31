package cc.w0rm.ghost.common.json.adapter;


import cc.w0rm.ghost.common.json.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.lang.reflect.Type;

/**
 * Jackson 适配器
 *
 * @author sunbufu
 */
public class JacksonAdapter implements JsonAdapter {

    private ObjectMapper objectMapper;
    
    public JacksonAdapter() {
        objectMapper = new ObjectMapper();
    }

    public JacksonAdapter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String writeValueAsString(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new JsonParseException(e);
        }
    }

    @Override
    public <T> T readValue(String json, Type type) {
        try {
            return objectMapper.readValue(json, TypeFactory.defaultInstance().constructType(type));
        } catch (Exception e) {
            throw new JsonParseException(e);
        }
    }
}
