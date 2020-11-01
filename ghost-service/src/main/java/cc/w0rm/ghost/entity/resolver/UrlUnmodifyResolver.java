package cc.w0rm.ghost.entity.resolver;

import cc.w0rm.ghost.dto.CommodityDetailDTO;

/**
 * @author : xuyang
 * @date : 2020/10/31 2:46 上午
 */
public abstract class UrlUnmodifyResolver extends UrlResolver {

    @Override
    public CommodityDetailDTO resolve(String url, String group) {
        CommodityDetailDTO commodityDetailDTO = new CommodityDetailDTO();
        commodityDetailDTO.setCommodityType(getCommodityType());
        commodityDetailDTO.setSource(url);
        commodityDetailDTO.setModified(url);
        return commodityDetailDTO;
    }
}
