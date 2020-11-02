package cc.w0rm.ghost.entity.resolver;

import cc.w0rm.ghost.dto.CommodityDetailDTO;
import cc.w0rm.ghost.entity.resolver.detect.PreTestText;
import cc.w0rm.ghost.enums.CommodityType;

/**
 * @author : xuyang
 * @date : 2020/10/30 10:56 下午
 */

public interface Resolver {

    /**
     * 解析消息 返回商品列表
     * @param preTestText
     * @param group
     * @return
     */
    CommodityDetailDTO resolve(PreTestText preTestText, String group);

    /**
     * 返回解析的分类
     * @return
     */
    CommodityType getCommodityType();

}
