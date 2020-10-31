package cc.w0rm.ghost.entity.resolver;

import cc.w0rm.ghost.enums.CommodityType;
import cc.w0rm.ghost.util.MsgUtil;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author : xuyang
 * @date : 2020/10/31 2:23 上午
 */

@Component("jd-shortUrl")
public class JdShortUrlResolver extends UrlUnmodifyResolver {

    /**
     * todo 暂时不支持jd商品的url解析, 保持url不变
     */

    @Override
    public List<String> getUrlList(String msg) {
        return MsgUtil.listJdShortUrls(msg);
    }

    @Override
    public CommodityType getCommodityType() {
        return CommodityType.JD_SHORT_URL;
    }

}
