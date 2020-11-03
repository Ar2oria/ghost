package cc.w0rm.ghost.service;

import cc.w0rm.ghost.api.MsgResolver;
import cc.w0rm.ghost.common.util.CompletableFutureWithMDC;
import cc.w0rm.ghost.common.util.Strings;
import cc.w0rm.ghost.dto.CommodityDetailDTO;
import cc.w0rm.ghost.dto.MsgInfoDTO;
import cc.w0rm.ghost.entity.resolver.Resolver;
import cc.w0rm.ghost.entity.resolver.detect.Detector;
import cc.w0rm.ghost.entity.resolver.detect.PreTestText;
import cc.w0rm.ghost.enums.MsgHashMode;
import cc.w0rm.ghost.enums.ResolveType;
import cc.w0rm.ghost.enums.TextType;
import cc.w0rm.ghost.util.MsgUtil;
import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author : xuyang
 * @date : 2020/10/30 5:53 下午
 */

@Slf4j
@Service
public class MsgResolverImpl implements MsgResolver {
    private static final ExecutorService EXECUTOR_SERVICE = new ThreadPoolExecutor(20,
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

    private static final Integer MSG_LENGTH_THRESHOLD = 13;
    @Resource
    private List<Detector> detectorList;

    @Resource
    private List<Resolver> resolverList;

    @Override
    public MsgInfoDTO resolve(String msg, String group) {
        Preconditions.checkArgument(Strings.isNotBlank(group), "消息组为空");
        Preconditions.checkArgument(Strings.isNotBlank(msg), "消息内容为空");

        Integer hashCode = (msg + group).hashCode();
        MsgInfoDTO msgInfoDTO = MSG_INFO_CACHE.getIfPresent(hashCode);
        if (Objects.nonNull(msgInfoDTO)) {
            return msgInfoDTO;
        }

        ArrayList<PreTestText> preTestTexts = Lists.newArrayList(new PreTestText(TextType.SOURCE_TEXT, msg, msg));
        List<PreTestText> texts = detectorList.parallelStream()
                .flatMap(detector -> {
                    try {
                        return detector.detect(msg).stream();
                    } catch (Exception exp) {
                        log.error("文本预处理器出现异常", exp);
                    }
                    return Stream.empty();
                }).collect(Collectors.toList());

        preTestTexts.addAll(texts);

        LinkedBlockingQueue<CommodityDetailDTO> result = new LinkedBlockingQueue<>();
        CompletableFuture<?>[] completableFutures = resolverList.stream()
                .flatMap(resolver -> preTestTexts.stream().map(preTestText ->
                        CompletableFutureWithMDC.supplyAsyncWithMdc(() ->
                                resolver.resolve(preTestText, group), EXECUTOR_SERVICE)
                                .whenComplete((t, e) -> {
                                    if (e != null) {
                                        log.error("解析消息失败", e);
                                    } else if (Objects.nonNull(t)) {
                                        result.add(t);
                                    }
                                }))
                ).toArray(CompletableFuture[]::new);
        try {
            CompletableFuture.allOf(completableFutures).get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("消息解析超时", e);
        }

        int referenceId = 0;
        String modifiedMsg = msg;
        for (CommodityDetailDTO commodityDetailDTO : result) {
            if (!commodityDetailDTO.getSource().equals(commodityDetailDTO.getModified())) {
                modifiedMsg = modifiedMsg.replace(commodityDetailDTO.getSource(), commodityDetailDTO.getModified());
            }

            if (Strings.isNotBlank(commodityDetailDTO.getCommodityId())) {
                referenceId |= commodityDetailDTO.getCommodityId().hashCode();
            }
        }

        if (referenceId == 0) {
            String replace = MsgUtil.replace(msg);
            int len = replace.length() > MSG_LENGTH_THRESHOLD? MSG_LENGTH_THRESHOLD : replace.length();
            referenceId = replace.substring(0, len).hashCode();
        }


        ResolveType resolveType = ResolveType.SUCCESS;
        if (CollectionUtils.isEmpty(result)) {
            resolveType = msg.length() > MSG_LENGTH_THRESHOLD ? ResolveType.UNSUPPORT_URL : ResolveType.NONE;
        }

        msgInfoDTO = new MsgInfoDTO();
        msgInfoDTO.setModifiedMsg(modifiedMsg);
        msgInfoDTO.setResolveType(resolveType);
        msgInfoDTO.setReferenceId(referenceId);
        msgInfoDTO.setResolveList(new ArrayList<>(result));

        MSG_INFO_CACHE.put(hashCode, msgInfoDTO);

        return msgInfoDTO;
    }
}
