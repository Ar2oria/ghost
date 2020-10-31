package cc.w0rm.ghost.entity.resolver;

import cc.w0rm.ghost.common.util.Strings;
import cc.w0rm.ghost.dto.BaozouResponseDTO;
import cc.w0rm.ghost.dto.CommodityDetailDTO;
import cc.w0rm.ghost.dto.TklConvertDTO;
import cc.w0rm.ghost.dto.TklInfoDTO;
import cc.w0rm.ghost.entity.platform.GetAble;
import cc.w0rm.ghost.enums.CommodityType;
import cc.w0rm.ghost.rpc.baozou.BaoZouService;
import cc.w0rm.ghost.util.MsgUtil;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author : xuyang
 * @date : 2020/10/30 11:43 下午
 */

/**
 * 解析器名称分为两段，第一段为平台名称，第二段为具体的解析器名称
 */
@Slf4j
@Component("taobao-tkl")
public class TbTklResolver implements Resolver {
    private static final Cache<Integer, CommodityDetailDTO> TKL_CACHE = CacheBuilder.newBuilder()
            .concurrencyLevel(Integer.MAX_VALUE)
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .softValues()
            .build();

    @Resource
    private BaoZouService baoZouService;

    /**
     * 使用暴走工具箱解析，后续替换成淘口令网接口
     *
     * @param msg
     * @param account
     * @return
     */
    @Override
    public List<CommodityDetailDTO> resolve(String msg, GetAble account) {
        if (Strings.isBlank(msg) || Objects.isNull(account)) {
            return null;
        }

        Map<String, String> tklMap = MsgUtil.getTaokouling(msg);
        if (CollectionUtils.isEmpty(tklMap)) {
            return null;
        }

        return tklMap.keySet().stream()
                .map(tkl -> {
                    BaozouResponseDTO<TklInfoDTO> responseDTO = baoZouService.tklDecrypt(tkl);
                    if (Objects.isNull(responseDTO) || Objects.isNull(responseDTO.getData())) {
                        return new UrlUnmodifyResolver() {
                            @Override
                            public List<String> getUrlList(String msg) {
                                return Collections.singletonList(tklMap.get(tkl));
                            }

                            @Override
                            public CommodityType getCommodityType() {
                                return CommodityType.TAOBAO_TAOKOULING;
                            }
                        }.resolve(msg, account).get(0);
                    }
                    return getCommodityDetail(responseDTO.getData(), account, tklMap.get(tkl));
                }).collect(Collectors.toList());
    }

    private CommodityDetailDTO getCommodityDetail(TklInfoDTO tklInfoDTO, GetAble account, String source) {
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
}
