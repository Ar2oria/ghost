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

    private static final Cache<String, Set<Integer>> ACCOUNT_MSG_FILTER = CacheBuilder.newBuilder()
            .concurrencyLevel(Integer.MAX_VALUE)
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .softValues()
            .build();

    private static final String GLOBAL_CODE = "#";
    private static final List<Pattern> PATTERN_LIST = Lists.newArrayList(MsgUtil.ELEME_PATTERN, MsgUtil.MEITUAN_PATTERN,
            MsgUtil.C88_10_PATTERN);


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

        Set<Integer> msgHash = ACCOUNT_MSG_FILTER.getIfPresent(GLOBAL_CODE);
        if (msgHash == null) {
            synchronized (this) {
                msgHash = ACCOUNT_MSG_FILTER.getIfPresent(GLOBAL_CODE);
                if (msgHash == null) {
                    msgHash = new ConcurrentHashSet<>();
                    ACCOUNT_MSG_FILTER.put(GLOBAL_CODE, msgHash);
                }
            }
        }
        int hash = MsgUtil.hashCode(msgGet.getMsg(), MsgHashMode.STRICT);
        if (msgHash.contains(hash)) {
            return true;
        }
        msgHash.add(hash);

        for (Pattern p : PATTERN_LIST) {
            boolean match = match(msgGet.getMsg(), p, msgHash);
            if (match) {
                return true;
            }
        }

        return false;
    }


    private boolean match(String msg, Pattern pattern, Set<Integer> hashSet) {
        Matcher matcher = pattern.matcher(msg);
        if (matcher.find()) {
            int hash = pattern.pattern().hashCode();
            if (hashSet.contains(hash)) {
                return true;
            } else {
                hashSet.add(hash);
            }
        }

        return false;
    }
}
