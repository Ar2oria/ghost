package cc.w0rm.ghost.service;

import cc.w0rm.ghost.api.AccountManager;
import cc.w0rm.ghost.api.Coordinator;
import cc.w0rm.ghost.api.MsgProducer;
import cc.w0rm.ghost.api.MsgResolver;
import cc.w0rm.ghost.common.util.CompletableFutureWithMDC;
import cc.w0rm.ghost.dto.MsgInfoDTO;
import cc.w0rm.ghost.entity.GroupMsgExt;
import cc.w0rm.ghost.enums.ResolveType;
import cn.hutool.core.collection.ConcurrentHashSet;
import com.forte.qqrobot.beans.messages.msgget.GroupMsg;
import com.forte.qqrobot.sender.MsgSender;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author : xuyang
 * @date : 2020/10/15 3:40 下午
 */
@Slf4j
@Service("msgProducer")
public class MsgProducerImpl implements MsgProducer {

    private static final ExecutorService EXECUTOR_SERVICE = new ThreadPoolExecutor(20,
            Integer.MAX_VALUE,
            60,
            TimeUnit.SECONDS,
            new SynchronousQueue<>(),
            new ThreadFactoryBuilder()
                    .setDaemon(true)
                    .setNameFormat("MsgProducer-ThreadPool")
                    .build());

    private static final Cache<String, Set<Integer>> MSG_FILTER_CACHE = CacheBuilder.newBuilder()
            .concurrencyLevel(Integer.MAX_VALUE)
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .softValues()
            .build();

    @Autowired
    private Coordinator coordinator;

    @Autowired
    private MsgResolver msgResolver;

    @Autowired
    private AccountManager accountManager;

    @Override
    public void make(MsgSender msgSender, GroupMsg groupMsg) {
        List<String> msgGroupFlag = accountManager.listAllGroups();
        try {
            Map<String, MsgInfoDTO> msgInfoMap = msgGroupFlag.stream()
                    .collect(Collectors.toMap(Function.identity(),
                            flag -> msgResolver.resolve(groupMsg.getMsg(), flag)));

            msgInfoMap.keySet().forEach(flag -> {
                MsgInfoDTO msgInfoDTO = msgInfoMap.get(flag);
                if (msgInfoDTO.getResolveType() == ResolveType.NONE) {
                    return;
                }

                Set<Integer> filter = MSG_FILTER_CACHE.getIfPresent(flag);
                if (filter == null) {
                    synchronized (this) {
                        filter = MSG_FILTER_CACHE.getIfPresent(flag);
                        if (filter == null) {
                            filter = new ConcurrentHashSet<>();
                            MSG_FILTER_CACHE.put(flag, filter);
                        }
                    }
                }

                if (filter.contains(msgInfoDTO.getReferenceId())) {
                    return;
                } else {
                    filter.add(msgInfoDTO.getReferenceId());
                }

                GroupMsgExt groupMsgExt = new GroupMsgExt(groupMsg, flag);
                groupMsgExt.setModifiedMsg(msgInfoDTO.getModifiedMsg());
                CompletableFutureWithMDC.runAsyncWithMdc(() -> coordinator.forward(flag, groupMsgExt), EXECUTOR_SERVICE);

            });

        } catch (Exception exp) {
            log.error("消息生产者，转发消息失败 msgId[{}]",
                    groupMsg.getId(), exp);
        }

    }
}
