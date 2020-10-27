package cc.w0rm.ghost.entity.platform;

import cc.w0rm.ghost.entity.Strategy;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author : xuyang
 * @date : 2020/10/27 5:15 下午
 */
public interface Parser extends Strategy {
    void parse(LinkedHashMap<String, Object> config);

    Map<String,GetAble> getMsgGroupConfig();
}
