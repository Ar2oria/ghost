package cc.w0rm.ghost.entity.resolver;

import cc.w0rm.ghost.enums.CommodityType;
import org.springframework.stereotype.Component;

/**
 * @author : xuyang
 * @date : 2020/10/31 1:58 上午
 */
@Component
public class TbShortUrlResolver extends UrlToEmptyResolver {

    private static final Domain DOMAIN = new Domain("m.tb.cn", null);

    /**
     * 淘宝短链接解析为空值
     */

    @Override
    public Domain getResolveDomain() {
        return DOMAIN;
    }

    @Override
    public CommodityType getCommodityType() {
        return CommodityType.TAOBAO_SHORT_URL;
    }

}
