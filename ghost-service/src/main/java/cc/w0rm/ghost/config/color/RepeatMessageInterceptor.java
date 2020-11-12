package cc.w0rm.ghost.config.color;

import cc.w0rm.ghost.config.role.ConfigRole;
import cc.w0rm.ghost.enums.MsgHashMode;
import cc.w0rm.ghost.util.MsgUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import com.forte.qqrobot.beans.messages.msgget.MsgGet;
import com.forte.qqrobot.intercept.Context;
import com.forte.qqrobot.listener.MsgGetContext;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author : xuyang
 * @date : 2020/10/22 5:27 下午
 */
@Slf4j
@Component
public class RepeatMessageInterceptor implements ProducerInterceptor {

    private static final Cache<String, Set<Integer>> GLOBAL_MSG_FILTER = CacheBuilder.newBuilder()
            .concurrencyLevel(Integer.MAX_VALUE)
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .softValues()
            .build();

    private static final String GLOBAL_CODE = "#";
    private static final List<Pattern> PATTERN_LIST = Lists.newArrayList(MsgUtil.ELEME_PATTERN, MsgUtil.MEITUAN_PATTERN,
            MsgUtil.C88_10_PATTERN);

    private static final int REPEAT_COUNT = 2;

    @Override
    public boolean intercept(Context context, ConfigRole configRole) {
        if (context instanceof MsgGetContext) {
            MsgGetContext msgGetContext = (MsgGetContext) context;
            if (isForward(msgGetContext.getMsgGet())) {
                log.debug("msg[{}] is already forward to this account, skip", msgGetContext.getMsgGet().getId());
                return false;
            }
        }

        return true;
    }

    private boolean isForward(MsgGet msgGet) {
        if (msgGet.getMsg() == null) {
            return false;
        }

        Set<Integer> msgHash = newConcurrentHashSet(GLOBAL_CODE);
        int hash = MsgUtil.hashCode(msgGet.getMsg(), MsgHashMode.STRICT);
        if (msgHash.contains(hash)) {
            return true;
        }
        msgHash.add(hash);

        for (Pattern p : PATTERN_LIST) {
            boolean match = match(msgGet.getMsg(), p);
            if (match) {
                return true;
            }
        }

        return false;
    }

    private boolean match(String msg, Pattern pattern) {
        Matcher matcher = pattern.matcher(msg);
        if (matcher.find()) {
            String key = pattern.pattern();
            Set<Integer> countSet = newConcurrentHashSet(key);
            if (countSet.size() >= REPEAT_COUNT) {
                return true;
            } else {
                countSet.add(msg.hashCode());
            }
        }

        return false;
    }

    private Set<Integer> newConcurrentHashSet(String key) {
        Set<Integer> set = GLOBAL_MSG_FILTER.getIfPresent(key);
        if (set == null) {
            synchronized (this) {
                set = GLOBAL_MSG_FILTER.getIfPresent(key);
                if (set == null) {
                    set = new ConcurrentHashSet<>();
                    GLOBAL_MSG_FILTER.put(key, set);
                }
            }
        }
        return set;
    }
}
