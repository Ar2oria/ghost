package cc.w0rm.ghost.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : xuyang
 * @date : 2020/10/30 4:44 下午
 */

@Getter
@AllArgsConstructor
public enum CommodityType {
    TAOBAO_TAOKOULING(10, "淘口令"),
    TAOBAO_CLICK_URL(11, "淘宝click短链接"),
    TAOBAO_SHORT_URL(12, "淘宝短链接"),
    TAOBAO_ULAND_URL(13, "淘宝商品落地页链接"),
    TAOBAO_URL(14, "淘宝商品详情页"),
    TIANMAO_URL(15, "天猫商品详情页"),

    JD_URL(20, "京东商品url"),
    JD_SHORT_URL(21, "京东短链接"),
    JD_COUPON_URL(22, "京东优惠卷"),

    SUNING_URL(30,"苏宁链接"),

    ELEME_URL(40,"饿了么红包"),

    MEITUAN_URL(50, "美团红包"),

    GUIDE(60, "购物操作指导"),


    ;
    private Integer code;
    private String desc;
}
