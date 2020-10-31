package cc.w0rm.ghost.entity.resolver;

import cc.w0rm.ghost.enums.CommodityType;
import cc.w0rm.ghost.util.MsgUtil;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author : xuyang
 * @date : 2020/10/31 2:56 上午
 */
@Component("jd-couponUrl")
public class JdCouponUrlResolver extends UrlUnmodifyResolver {

    /**
     * todo 暂时不支持jd优惠卷的url解析, 保持url不变
     */

    @Override
    public List<String> getUrlList(String msg) {
        return MsgUtil.listJdCouponUrls(msg);
    }

    @Override
    public CommodityType getCommodityType() {
        return CommodityType.JD_COUPON_URL;
    }
}
