package cc.w0rm.ghost.common.json.adapter;


import cc.w0rm.ghost.common.json.JsonParseException;

import java.lang.reflect.Type;

/**
 * Json 类库适配器
 *
 * @author sunbufu
 */
public interface JsonAdapter {


    String writeValueAsString(Object obj) throws JsonParseException;

    <T> T readValue(String json, Type type) throws JsonParseException;
}
