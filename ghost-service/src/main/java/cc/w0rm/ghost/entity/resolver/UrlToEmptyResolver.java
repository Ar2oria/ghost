package cc.w0rm.ghost.entity.resolver;

import cc.w0rm.ghost.common.util.Strings;
import cc.w0rm.ghost.dto.CommodityDetailDTO;
import cc.w0rm.ghost.entity.platform.GetAble;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : xuyang
 * @date : 2020/10/31 2:46 上午
 */
public abstract class UrlToEmptyResolver implements UrlResolver {

    @Override
    public List<CommodityDetailDTO> resolve(String msg, GetAble account) {
        if (Strings.isBlank(msg)) {
            return null;
        }

        List<String> urlList = getUrlList(msg);
        if (CollectionUtils.isEmpty(urlList)){
            return null;
        }

        return urlList.stream()
                .map(url->{
                    CommodityDetailDTO commodityDetailDTO = new CommodityDetailDTO();
                    commodityDetailDTO.setCommodityType(getCommodityType());
                    commodityDetailDTO.setSource(url);
                    commodityDetailDTO.setModified(Strings.EMPTY);
                    return commodityDetailDTO;
                }).collect(Collectors.toList());
    }
}
