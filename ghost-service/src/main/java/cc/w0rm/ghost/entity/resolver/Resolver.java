package cc.w0rm.ghost.entity.resolver;

import cc.w0rm.ghost.dto.CommodityDetailDTO;
import cc.w0rm.ghost.entity.platform.GetAble;

import java.util.List;

/**
 * @author : xuyang
 * @date : 2020/10/30 10:56 下午
 */

/**
 * 解析器名称分为两段，第一段为平台名称，第二段为具体的解析器名称
 */
public interface Resolver {

    /**
     * 解析消息 返回商品列表
     * @param msg
     * @param account
     * @return
     */
    List<CommodityDetailDTO> resolve(String msg, GetAble account);

}
