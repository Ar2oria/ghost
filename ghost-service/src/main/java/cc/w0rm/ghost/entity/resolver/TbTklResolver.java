package cc.w0rm.ghost.entity.resolver;

import cc.w0rm.ghost.api.AccountManager;
import cc.w0rm.ghost.common.util.CompletableFutureWithMDC;
import cc.w0rm.ghost.common.util.Strings;
import cc.w0rm.ghost.dto.*;
import cc.w0rm.ghost.entity.platform.GetAble;
import cc.w0rm.ghost.entity.resolver.detect.PreTestText;
import cc.w0rm.ghost.enums.CommodityType;
import cc.w0rm.ghost.enums.TextType;
import cc.w0rm.ghost.rpc.baozou.BaozouService;
import cc.w0rm.ghost.rpc.taokouling.TaokoulingService;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * @author : xuyang
 * @date : 2020/10/30 11:43 下午
 */

@Slf4j
@Component
public class TbTklResolver implements Resolver {
    private static final ExecutorService EXECUTOR_SERVICE = new ThreadPoolExecutor(4,
            Integer.MAX_VALUE,
            60,
            TimeUnit.SECONDS,
            new SynchronousQueue<>(),
            new ThreadFactoryBuilder()
                    .setDaemon(true)
                    .setNameFormat("TbTklResolver-ThreadPool")
                    .build());
    private static final Cache<Integer, CommodityDetailDTO> TKL_CACHE = CacheBuilder.newBuilder()
            .concurrencyLevel(Integer.MAX_VALUE)
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .softValues()
            .build();

    @Resource
    private BaozouService baoZouService;

    @Resource
    private TaokoulingService taokoulingService;

    @Resource
    private AccountManager accountManager;

    @Override
    public CommodityDetailDTO resolve(PreTestText preTestText, String group) {
        if (preTestText.getTextType() != TextType.TAOKOULING) {
            return null;
        }

        if (Strings.isBlank(group)) {
            throw new IllegalArgumentException("解析淘口令失败，消息组为空");
        }

        GetAble account = accountManager.getPlatformConfig("taobao").get(group);
        if (Objects.isNull(account)) {
            throw new IllegalArgumentException("解析淘口令失败，无平台配置文件");
        }

        String tkl = preTestText.getFind();
        CompletableFuture<BaozouResponseDTO<TklInfoDTO>> future = CompletableFutureWithMDC.supplyAsyncWithMdc(()
                -> baoZouService.tklDecrypt(tkl), EXECUTOR_SERVICE);
        try {
            BaozouResponseDTO<TklInfoDTO> responseDTO = future.get(5, TimeUnit.SECONDS);
            if (Objects.nonNull(responseDTO) && Objects.nonNull(responseDTO.getData())) {
                return getCommodityDetail(responseDTO.getData(), preTestText.getSource(), account);
            }
        }catch (Exception exp){
            log.error("暴走工具箱解析异常", exp);
        }

        return tryResolveFromItemInfo(preTestText, account);
    }

    @NotNull
    private CommodityDetailDTO tryResolveFromItemInfo(PreTestText preTestText, GetAble account) {
        String tkl = preTestText.getFind();
        String apikey = account.get("apikey");

        TklJmDTO tklJmDTO = new TklJmDTO(apikey, tkl);
        TklResponseDTO tklResponseDTO = taokoulingService.tklJm(tklJmDTO);
        if (tklResponseDTO == null || tklResponseDTO.getCode() != 1){
            return buildUnresolvedDTO(preTestText);
        }

        CommodityDetailDTO commodityDetailDTO = new CommodityDetailDTO();
        commodityDetailDTO.setCommodityType(CommodityType.TAOBAO_TAOKOULING);
        commodityDetailDTO.setSource(preTestText.getSource());
        commodityDetailDTO.setModified(preTestText.getSource());
        commodityDetailDTO.setCommodityTitle(tklResponseDTO.getContent());
        commodityDetailDTO.setAttachments(tklResponseDTO.getPicUrl());
        commodityDetailDTO.setCommodityDesc(tklResponseDTO.getUrl());

        return commodityDetailDTO;
    }


    private CommodityDetailDTO buildUnresolvedDTO(PreTestText preTestText) {
        CommodityDetailDTO commodityDetailDTO = new CommodityDetailDTO();
        commodityDetailDTO.setCommodityType(getCommodityType());
        commodityDetailDTO.setSource(preTestText.getSource());
        commodityDetailDTO.setModified(preTestText.getSource());
        return commodityDetailDTO;
    }

    private CommodityDetailDTO getCommodityDetail(TklInfoDTO tklInfoDTO, String source, GetAble account) {
        String goodsId = tklInfoDTO.getGoodsId();
        String pid = account.get("pid");
        Integer hashCode = (goodsId + pid).hashCode();
        CommodityDetailDTO cache = TKL_CACHE.getIfPresent(hashCode);
        if (cache != null) {
            cache.setSource(source);
            return cache;
        }

        TklConvertDTO tklConvertDTO = TklConvertDTO.builder()
                .goodsId(tklInfoDTO.getGoodsId())
                .action("tkl")
                .title(tklInfoDTO.getTitle())
                .activityId(tklInfoDTO.getActivityId())
                .picUrl(tklInfoDTO.getPirUrl())
                .pid(pid)
                .build();
        BaozouResponseDTO<?> baozouResponseDTO = baoZouService.convertMiddle(tklConvertDTO);

        CommodityDetailDTO commodityDetailDTO = new CommodityDetailDTO();
        commodityDetailDTO.setCommodityType(CommodityType.TAOBAO_TAOKOULING);
        commodityDetailDTO.setSource(source);
        commodityDetailDTO.setChannel("baozou");
        commodityDetailDTO.setCommodityId(tklInfoDTO.getGoodsId());
        commodityDetailDTO.setActivityId(tklInfoDTO.getActivityId());
        commodityDetailDTO.setCommodityTitle(tklInfoDTO.getTitle());
        commodityDetailDTO.setCommodityDesc(tklInfoDTO.getDesc());
        commodityDetailDTO.setPrice(tklInfoDTO.getPrice());
        commodityDetailDTO.setEndPrice(tklInfoDTO.getEndPrice());
        commodityDetailDTO.setCommission(tklInfoDTO.getMoney());
        commodityDetailDTO.setCouponMoney(tklInfoDTO.getCouponMoney());
        commodityDetailDTO.setAttachments(tklInfoDTO.getPirUrl());
        commodityDetailDTO.setModified(source);

        if (baozouResponseDTO != null && baozouResponseDTO.getCode() == 0) {
            commodityDetailDTO.setModified(baozouResponseDTO.getErrmsg());
            TKL_CACHE.put(hashCode, commodityDetailDTO);
        }

        return commodityDetailDTO;
    }

    @Override
    public CommodityType getCommodityType() {
        return CommodityType.TAOBAO_TAOKOULING;
    }
}
