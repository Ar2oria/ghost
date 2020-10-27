package cc.w0rm.ghost.entity.platform;

import cn.hutool.core.collection.CollUtil;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author : xuyang
 * @date : 2020/10/27 5:19 下午
 */

public abstract class DefaultParser implements Parser {

    private final Map<String, Map<String, GetAble>> platformAccount;

    public abstract String getPlatform();

    public DefaultParser() {
        platformAccount = new HashMap<>();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void parse(LinkedHashMap<String, Object> config) {
        if (CollUtil.isEmpty(config)) {
            throw new IllegalArgumentException("商户平台配置解析器，传入参数为空");
        }
        Object o = config.get(getPlatform());
        if (o == null) {
            throw new IllegalStateException("商户平台配置解析器，无相应的平台配置");
        }

        LinkedHashMap<String, Object> groups = (LinkedHashMap<String, Object>) o;
        if (CollUtil.isEmpty(groups)) {
            throw new IllegalStateException("商户平台配置解析器，无法获取消息组配置");
        }

        if (platformAccount.containsKey(getPlatform())) {
            return;
        }

        Map<String, GetAble> account = new HashMap<>();
        for (String key : groups.keySet()) {
            account.put(key, new Account((Map<String, Object>) groups.get(key)));
        }

        platformAccount.putIfAbsent(getPlatform(), account);
    }

    @Override
    public Map<String, GetAble> getMsgGroupConfig() {
        return platformAccount.get(getPlatform());
    }
}
