package cc.w0rm.ghost.dto;

import cc.w0rm.ghost.enums.CommodityType;
import lombok.Data;

/**
 * @author : xuyang
 * @date : 2020/10/30 4:43 下午
 */

@Data
public class CommodityDetailDTO {
    private CommodityType commodityType;
    /**
     * 商品id
     */
    private String commodityId;
    /**
     * 原始文本
     */
    private String source;
    /**
     * 修改后的文本
     */
    private String modified;
    /**
     * 商品标题
     */
    private String commodityTitle;
    /**
     * 商品描述
     */
    private String commodityDesc;
    /**
     * 商品价格
     */
    private double price;
    /**
     * 折后价格
     */
    private double endPrice;
    /**
     * 优惠卷价格
     */
    private double couponMoney;
    /**
     * 优惠卷描述
     */
    private String couponDesc;
    /**
     * 商品url图片
     */
    private String attachments;
    /**
     * 佣金（预估，不可靠）
     */
    private double commission;
    /**
     * 渠道，暴走工具箱，淘口令网等
     */
    private String channel;
}
