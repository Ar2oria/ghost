package cc.w0rm.ghost.entity.platform;

import cn.hutool.core.collection.CollUtil;
import org.apache.logging.log4j.util.Strings;

import java.util.Map;

/**
 * @author : xuyang
 * @date : 2020/10/27 5:16 下午
 */

public class Account implements GetAble {

    private final Map<String, Object> account;

    public Account(Map<String, Object> config){
        this.account = config;
    }

    @Override
    public String get(String key) {
        if (CollUtil.isEmpty(account)){
            return Strings.EMPTY;
        }

        return (String)account.get(key);
    }
}
