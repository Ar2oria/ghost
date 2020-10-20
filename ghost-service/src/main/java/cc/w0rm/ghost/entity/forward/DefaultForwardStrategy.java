package cc.w0rm.ghost.entity.forward;

import cc.w0rm.ghost.api.AccountManager;
import cc.w0rm.ghost.api.MsgConsumer;
import cc.w0rm.ghost.config.role.Consumer;
import cc.w0rm.ghost.config.role.MsgGroup;
import cn.hutool.core.collection.CollUtil;
import com.forte.qqrobot.beans.messages.msgget.MsgGet;
import com.forte.qqrobot.beans.messages.result.GroupList;
import com.forte.qqrobot.beans.messages.result.inner.Group;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author : xuyang
 * @date : 2020/10/16 11:51 下午
 */
@Slf4j
@Component
public class DefaultForwardStrategy implements ForwardStrategy {

    private static final ExecutorService EXECUTOR_SERVICE = new ThreadPoolExecutor(4,
            Integer.MAX_VALUE,
            60,
            TimeUnit.SECONDS,
            new SynchronousQueue<>(),
            new ThreadFactoryBuilder()
                    .setDaemon(true)
                    .setNameFormat("DefaultForwardStrategy-ThreadPool")
                    .build());

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
        if (msgGet == null || !accountManager.isProducer(msgGet)) {
            return;
        }

        List<MsgGroup> msgGroups;
        if (msgGet instanceof MsgGetExt) {
            MsgGetExt msgGetExt = (MsgGetExt) msgGet;
            msgGet = msgGetExt.getMsgGet();
            String msgGroupName = msgGetExt.getMsgGroup();
            MsgGroup msgGroup = accountManager.getMsgGroup(msgGroupName);
            if (msgGroup == null){
                return;
            }
            msgGroups = Lists.newArrayList(msgGroup);
        } else {
            msgGroups = accountManager.listMsgGroup(msgGet);
        }

        Object[] taskArray = getFutureTaskArray(msgGet, msgGroups);
        if (taskArray == null) {
            return;
        }

        try {
            CompletableFuture<Void> future = CompletableFuture.allOf((CompletableFuture<?>[]) taskArray);
            future.join();
        } catch (Exception exp) {
            log.error("消息: [{}] 转发失败", msgGet.getMsg(), exp);
        }
    }

    @Nullable
    private Object[] getFutureTaskArray(MsgGet msgGet, List<MsgGroup> msgGroups) {
        if (msgGet == null || CollUtil.isEmpty(msgGroups)) {
            return null;
        }

        return msgGroups.stream()
                .flatMap(msgGroup -> {
                    String msgGroupName = msgGroup.getName();
                    MsgConsumer msgConsumer = msgConsumerMap.get(msgGroupName);
                    if (msgConsumer == null) {
                        return null;
                    }

                    Set<Consumer> consumerSet = msgGroup.getConsumer();
                    if (CollUtil.isEmpty(consumerSet)) {
                        return null;
                    }

                    return consumerSet.stream()
                            .flatMap(consumer -> {
                                Set<String> groupCodes = groupCodeCache.getIfPresent(consumer.getBotCode());
                                if (CollUtil.isEmpty(groupCodes)) {
                                    GroupList groupList = consumer.getSender().GETTER.getGroupList();
                                    groupCodes = groupList.stream()
                                            .map(Group::getCode).collect(Collectors.toSet());
                                    groupCodeCache.put(consumer.getBotCode(), groupCodes);
                                }

                                return groupCodes.stream()
                                        .map(groupCode -> CompletableFuture.runAsync(() -> {
                                            try {
                                                msgConsumer.consume(consumer, groupCode, msgGet);
                                                log.debug("send msg to group[{}] success, msgId={}", groupCode, msgGet.getId());
                                            } catch (Exception exp) {
                                                log.error("发送群消息给群[{}]失败， 账号:{}, 消息:{}}", groupCode, consumer.getBotCode(), msgGet.getMsg(), exp);
                                            }
                                        }, EXECUTOR_SERVICE));
                            });

                }).filter(Objects::nonNull).toArray();
    }
}
