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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;

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
        } else {
            msgHash.add(hash);

            Matcher elem = MsgUtil.ELEME_PATTERN.matcher(msgGet.getMsg());
            if (elem.find()) {
                hash = MsgUtil.ELEME_REGEX.hashCode();
                if (msgHash.contains(hash)){
                    return true;
                }else {
                    msgHash.add(hash);
                }
            }

            Matcher meituan = MsgUtil.MEITUAN_PATTERN.matcher(msgGet.getMsg());
            if (meituan.find()) {
                hash = MsgUtil.MEITUAN_REGEX.hashCode();
                if (msgHash.contains(hash)){
                    return true;
                }else {
                    msgHash.add(hash);
                }
            }

            return false;
        }
    }
}
