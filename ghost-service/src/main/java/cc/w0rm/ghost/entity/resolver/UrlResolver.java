package cc.w0rm.ghost.entity.resolver;

import cc.w0rm.ghost.dto.CommodityDetailDTO;
import cc.w0rm.ghost.entity.resolver.detect.PreTestText;
import cc.w0rm.ghost.enums.TextType;

/**
 * @author : xuyang
 * @date : 2020/11/2 12:43 上午
 */
public abstract class UrlResolver implements Resolver {

    public abstract CommodityDetailDTO resolve(String url, String group);

    public abstract Domain getResolveDomain();

    @Override
    public CommodityDetailDTO resolve(PreTestText preTestText, String group) {
        if (preTestText.getTextType() != TextType.URL) {
            return null;
        }
        if (!getResolveDomain().equals(preTestText.getFind())) {
            return null;
        }

        return resolve(preTestText.getSource(), group);
    }
}
