package cc.w0rm.ghost.entity.resolver;

import cc.w0rm.ghost.enums.CommodityType;
import cc.w0rm.ghost.util.MsgUtil;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author : xuyang
 * @date : 2020/10/31 1:58 上午
 */
@Component("taobao-shortUrl")
public class TbShortUrlResolver extends UrlToEmptyResolver {

    /**
     * 淘宝短链接解析为空值
     */

    @Override
    public List<String> getUrlList(String msg) {
        return MsgUtil.listTbShortUrls(msg);
    }

    @Override
    public CommodityType getCommodityType() {
        return CommodityType.TAOBAO_SHORT_URL;
    }
}
