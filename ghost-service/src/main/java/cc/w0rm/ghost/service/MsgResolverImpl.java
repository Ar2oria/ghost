package cc.w0rm.ghost.service;

import cc.w0rm.ghost.api.AccountManager;
import cc.w0rm.ghost.api.MsgResolver;
import cc.w0rm.ghost.common.util.CompletableFutureWithMDC;
import cc.w0rm.ghost.common.util.Strings;
import cc.w0rm.ghost.dto.CommodityDetailDTO;
import cc.w0rm.ghost.dto.MsgInfoDTO;
import cc.w0rm.ghost.entity.resolver.Resolver;
import cc.w0rm.ghost.enums.ResolveType;
import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * @author : xuyang
 * @date : 2020/10/30 5:53 下午
 */

@Slf4j
@Service
public class MsgResolverImpl implements MsgResolver {
    private static final ExecutorService EXECUTOR_SERVICE = new ThreadPoolExecutor(4,
            Integer.MAX_VALUE,
            60,
            TimeUnit.SECONDS,
            new SynchronousQueue<>(),
            new ThreadFactoryBuilder()
                    .setDaemon(true)
                    .setNameFormat("MsgResolver-ThreadPool")
                    .build());

    private static final Cache<Integer, MsgInfoDTO> MSG_INFO_CACHE = CacheBuilder.newBuilder()
            .concurrencyLevel(Integer.MAX_VALUE)
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .softValues()
            .build();
    @Resource
    private AccountManager accountManager;

    @Resource
    private Map<String, Resolver> resolverMap;

    @Override
    public MsgInfoDTO resolve(String msg, String group) {
        Preconditions.checkArgument(Strings.isNotBlank(group), "消息组为空");
        Preconditions.checkArgument(Strings.isNotBlank(msg), "消息内容为空");

        Integer hashCode = (msg + group).hashCode();
        MsgInfoDTO msgInfoDTO = MSG_INFO_CACHE.getIfPresent(hashCode);
        if (Objects.nonNull(msgInfoDTO)) {
            return msgInfoDTO;
        }

        LinkedBlockingQueue<CommodityDetailDTO> result = new LinkedBlockingQueue<>(resolverMap.size());
        CompletableFuture<?>[] completableFutures = resolverMap.keySet().stream()
                .map(name -> CompletableFutureWithMDC.supplyAsyncWithMdc(() ->
                        resolverMap.get(name).resolve(msg, accountManager.getPlatformConfig(name.split("-")[0]).get(group)), EXECUTOR_SERVICE)
                        .whenComplete((t, u) -> {
                            if (u != null) {
                                log.error("消息解析失败", u);
                            } else if (!CollectionUtils.isEmpty(t)) {
                                result.addAll(t);
                            }
                        }))
                .toArray(CompletableFuture[]::new);
        try {
            CompletableFuture.allOf(completableFutures).get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("消息解析超时", e);
        }

        String modifiedMsg = msg;
        for (CommodityDetailDTO commodityDetailDTO : result) {
            if (!commodityDetailDTO.getSource().equals(commodityDetailDTO.getModified())) {
                modifiedMsg = modifiedMsg.replace(commodityDetailDTO.getSource(), commodityDetailDTO.getModified());
            }
        }

        ResolveType resolveType = ResolveType.SUCCESS;
        if (modifiedMsg.equals(msg) && CollectionUtils.isEmpty(result)) {
            resolveType = modifiedMsg.length() > 13 ? ResolveType.UNSUPPORT_URL : ResolveType.NONE;
        }
        msgInfoDTO = new MsgInfoDTO();
        msgInfoDTO.setModifiedMsg(modifiedMsg);
        msgInfoDTO.setResolveType(resolveType);
        msgInfoDTO.setResolveList(new ArrayList<>(result));

        MSG_INFO_CACHE.put(hashCode, msgInfoDTO);

        return msgInfoDTO;
    }
}
