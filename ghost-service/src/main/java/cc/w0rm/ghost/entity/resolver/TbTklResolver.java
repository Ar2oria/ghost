package cc.w0rm.ghost.entity.resolver;

import cc.w0rm.ghost.api.AccountManager;
import cc.w0rm.ghost.common.util.Strings;
import cc.w0rm.ghost.dto.BaozouResponseDTO;
import cc.w0rm.ghost.dto.CommodityDetailDTO;
import cc.w0rm.ghost.dto.TklConvertDTO;
import cc.w0rm.ghost.dto.TklInfoDTO;
import cc.w0rm.ghost.entity.platform.GetAble;
import cc.w0rm.ghost.entity.resolver.detect.PreTestText;
import cc.w0rm.ghost.enums.CommodityType;
import cc.w0rm.ghost.enums.TextType;
import cc.w0rm.ghost.rpc.baozou.BaozouService;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author : xuyang
 * @date : 2020/10/30 11:43 下午
 */

@Slf4j
@Component
public class TbTklResolver implements Resolver {
    private static final Cache<Integer, CommodityDetailDTO> TKL_CACHE = CacheBuilder.newBuilder()
            .concurrencyLevel(Integer.MAX_VALUE)
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .softValues()
            .build();

    @Resource
    private BaozouService baoZouService;

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
        BaozouResponseDTO<TklInfoDTO> responseDTO = baoZouService.tklDecrypt(tkl);
        if (Objects.isNull(responseDTO) || Objects.isNull(responseDTO.getData())) {
            return buildUnresolvedDTO(preTestText);
        }

        return getCommodityDetail(responseDTO.getData(), preTestText.getSource(), account);
    }

    @NotNull
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
