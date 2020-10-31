package cc.w0rm.ghost.entity.resolver;

import cc.w0rm.ghost.enums.CommodityType;
import cc.w0rm.ghost.util.MsgUtil;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author : xuyang
 * @date : 2020/10/31 2:53 上午
 */

@Component("taobao-clickUrl")
public class TbClickUrlResolver extends UrlToEmptyResolver {
    /**
     * 淘宝click链接解析为空值
     */

    @Override
    public List<String> getUrlList(String msg) {
        return MsgUtil.listTbClickUrls(msg);
    }

    @Override
    public CommodityType getCommodityType() {
        return CommodityType.TAOBAO_CLICK_URL;
    }
}
