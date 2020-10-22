package cc.w0rm.ghost.entity.forward;

import cc.w0rm.ghost.api.AccountManager;
import cc.w0rm.ghost.api.MsgConsumer;
import cc.w0rm.ghost.config.role.Consumer;
import cc.w0rm.ghost.config.role.MsgGroup;
import cc.w0rm.ghost.common.util.CompletableFutureWithMDC;
import cn.hutool.core.collection.CollUtil;
import com.forte.qqrobot.beans.messages.msgget.GroupMsg;
import com.forte.qqrobot.beans.messages.msgget.MsgGet;
import com.forte.qqrobot.beans.messages.result.GroupList;
import com.forte.qqrobot.beans.messages.result.inner.Group;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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


    private static final Cache<String, Set<String>> GROUP_CODE_CACHE = CacheBuilder.newBuilder()
            .concurrencyLevel(Integer.MAX_VALUE)
            .expireAfterAccess(1, TimeUnit.HOURS)
            .initialCapacity(10).build();

    @Autowired
    private AccountManager accountManager;

    @Autowired
    private Map<String, MsgConsumer> msgConsumerMap;

    /**
     * 转发消息
     *
     * @param msgGet
     */
    @Override
    public void forward(MsgGet msgGet) {
        if (msgGet == null) {
            return;
        }

        if (!accountManager.isProducer(msgGet)) {
            return;
        }

        List<MsgGroup> msgGroups;
        if (msgGet instanceof MsgGetExt) {
            MsgGetExt msgGetExt = (MsgGetExt) msgGet;
            msgGet = msgGetExt.getGroupMsg();
            String msgGroupName = msgGetExt.getMsgGroup();
            MsgGroup msgGroup = accountManager.getMsgGroup(msgGroupName);
            if (msgGroup == null) {
                return;
            }
            msgGroups = Lists.newArrayList(msgGroup);
        } else {
            msgGroups = accountManager.listMsgGroup(msgGet);
        }

        CompletableFuture<?>[] taskArray = getFutureTaskArray(msgGet, msgGroups);
        if (taskArray.length == 0) {
            return;
        }

        CompletableFuture<Void> future = CompletableFuture.allOf(taskArray);
        future.join();
    }


    private CompletableFuture<?>[] getFutureTaskArray(MsgGet msgGet, List<MsgGroup> msgGroups) {
        if (msgGet == null || CollUtil.isEmpty(msgGroups)) {
            return new CompletableFuture[0];
        }

        return msgGroups.stream()
                .flatMap(msgGroup -> {
                    String msgGroupName = msgGroup.getName();
                    MsgConsumer msgConsumer = msgConsumerMap.get(msgGroupName);
                    if (msgConsumer == null) {
                        log.error("未找到对应消息组[{}]的消费者策略，请检查配置文件！", msgGroupName);
                        return null;
                    }

                    Set<Consumer> consumerSet = msgGroup.getConsumer();
                    if (CollUtil.isEmpty(consumerSet)) {
                        log.error("消息组[{}]没有qq账号对信息进行消费", msgGroupName);
                        return null;
                    }

                    return getCompletableFutureStream(msgGet, msgConsumer, consumerSet);
                }).filter(Objects::nonNull).toArray(CompletableFuture[]::new);
    }

    @NotNull
    private Stream<CompletableFuture<Void>> getCompletableFutureStream(MsgGet msgGet, MsgConsumer msgConsumer, Set<Consumer> consumerSet) {
        return consumerSet.stream()
                .flatMap(consumer -> {
                    Set<String> groupCodes = getGroupCodeFromCache(consumer);

                    return groupCodes.parallelStream()
                            .map(groupCode -> CompletableFutureWithMDC.runAsyncWithMdc(() -> {
                                if (msgGet instanceof GroupMsg) {
                                    GroupMsg groupMsg = (GroupMsg) msgGet;
                                    String sourceCode = groupMsg.getGroup();
                                    if (sourceCode.equals(groupCode)) {
                                        log.debug("msg forward to self, skip.");
                                        return;
                                    }
                                }

                                try {
                                    msgConsumer.consume(consumer, groupCode, msgGet);
                                } catch (Exception exp) {
                                    log.error("消息处理异常，消费者[{}], 群[{}], 消息[{}]", consumer.getQQCode(), groupCode, msgGet.getId(), exp);
                                }
                            }, EXECUTOR_SERVICE));
                });
    }

    @NotNull
    private Set<String> getGroupCodeFromCache(Consumer consumer) {
        Set<String> groupCodes = GROUP_CODE_CACHE.getIfPresent(consumer.getBotCode());
        if (CollUtil.isEmpty(groupCodes)) {
            GroupList groupList = consumer.getSender().GETTER.getGroupList();
            groupCodes = groupList.stream()
                    .map(Group::getCode).collect(Collectors.toSet());
            GROUP_CODE_CACHE.put(consumer.getBotCode(), groupCodes);
        }
        return groupCodes;
    }
}
