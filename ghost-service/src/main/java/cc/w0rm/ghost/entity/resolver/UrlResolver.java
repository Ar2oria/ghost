package cc.w0rm.ghost.entity.resolver;

import cc.w0rm.ghost.enums.CommodityType;

import java.util.List;

/**
 * @author : xuyang
 * @date : 2020/10/31 2:50 上午
 */
public interface UrlResolver extends Resolver {
    List<String> getUrlList(String msg);

    CommodityType getCommodityType();
}
