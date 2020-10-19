package cc.w0rm.ghost.entity.forward;

import cc.w0rm.ghost.api.AccountManager;
import cc.w0rm.ghost.api.MsgConsumer;
import cn.hutool.core.collection.CollUtil;
import com.forte.qqrobot.beans.messages.msgget.MsgGet;
import com.forte.qqrobot.beans.messages.result.GroupList;
import com.forte.qqrobot.beans.messages.result.inner.Group;
import com.forte.qqrobot.bot.BotInfo;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author : xuyang
 * @date : 2020/10/16 11:51 下午
 */
@Component
public class DefaultForwardStrategy implements ForwardStrategy {

    @Autowired
    private AccountManager accountManager;

    @Autowired
    private Map<String, MsgConsumer> msgConsumerMap;

    private final Cache<String, Set<String>> groupCodeCache = CacheBuilder.newBuilder()
            .expireAfterAccess(1, TimeUnit.HOURS)
            .initialCapacity(10).build();

    /**
     * 转发消息
     *
     * @param msgGet
     */
    @Override
    public void forward(MsgGet msgGet) {
        Set<BotInfo> botInfoSet = accountManager.listMsgGroupMember(msgGet);
        String msgGroupFlag = accountManager.getMsgGroupFlag(msgGet);

        MsgConsumer msgConsumer = msgConsumerMap.get(msgGroupFlag);

        Object[] taskArray = botInfoSet.stream()
                .flatMap(botInfo -> {
                    Set<String> groupCode = groupCodeCache.getIfPresent(botInfo.getBotCode());
                    if (CollUtil.isEmpty(groupCode)) {
                        GroupList groupList = botInfo.getSender().GETTER.getGroupList();
                        groupCode = groupList.stream()
                                .map(Group::getCode).collect(Collectors.toSet());
                        groupCodeCache.put(botInfo.getBotCode(), groupCode);
                    }
                    return groupCode.stream()
                            .map(group -> CompletableFuture.runAsync(() -> {
                                msgConsumer.consume(botInfo, group, msgGet);
                            }));
                }).toArray();

        CompletableFuture.allOf((CompletableFuture<?>[]) taskArray);
    }
}
