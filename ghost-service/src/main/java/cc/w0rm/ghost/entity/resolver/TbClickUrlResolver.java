package cc.w0rm.ghost.entity.resolver;

import cc.w0rm.ghost.enums.CommodityType;
import org.springframework.stereotype.Component;

/**
 * @author : xuyang
 * @date : 2020/10/31 2:53 上午
 */

@Component
public class TbClickUrlResolver extends UrlToEmptyResolver {

    private static final Domain DOMAIN = new Domain("s.click.taobao.com", null);

    /**
     * 淘宝click链接解析为空值
     */


    @Override
    public CommodityType getCommodityType() {
        return CommodityType.TAOBAO_CLICK_URL;
    }

    @Override
    public Domain getResolveDomain() {
        return DOMAIN;
    }
}
