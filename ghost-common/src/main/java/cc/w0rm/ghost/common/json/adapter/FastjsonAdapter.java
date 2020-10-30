package cc.w0rm.ghost.common.json.adapter;

import cc.w0rm.ghost.common.json.JsonParseException;
import com.alibaba.fastjson.JSON;

import java.lang.reflect.Type;


public class FastjsonAdapter implements JsonAdapter {

    @Override
    public String writeValueAsString(Object obj) {
        try {
            return JSON.toJSONString(obj);
        } catch (Exception e) {
            throw new JsonParseException(e);
        }
    }

    @Override
    public <T> T readValue(String json, Type type) {
        try {
            return JSON.parseObject(json, type);
        } catch (Exception e) {
            throw new JsonParseException(e);
        }
    }
}
