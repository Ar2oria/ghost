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
    TAOKOULING(1, "淘口令"),
    ELEME_URL(2,"饿了么红包"),
    MEITUAN_URL(3, "美团红包"),
    JD_URL(4, "京东url"),
    SUNING_URL(5,"苏宁链接"),
    TAOBAO_SHORT_URL(6, "淘宝click短链接"),
    TAOBAO_ULAND_URL(7, "淘宝商品落地页链接"),
    TAOBAO_URL(8, "淘宝商品详情页"),
    TIANMAO_URL(9, "天猫商品详情页"),
    GUIDE(10, "购物操作指导"),


    ;
    private Integer code;
    private String desc;
}
