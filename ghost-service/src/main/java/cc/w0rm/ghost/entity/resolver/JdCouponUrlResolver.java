package cc.w0rm.ghost.entity.resolver;

import cc.w0rm.ghost.enums.CommodityType;
import org.springframework.stereotype.Component;

/**
 * @author : xuyang
 * @date : 2020/10/31 2:56 上午
 */
@Component
public class JdCouponUrlResolver extends UrlUnmodifyResolver {

    private static final Domain DOMAIN = new Domain("coupon.m.jd.com", null);

    /**
     * todo 暂时不支持jd优惠卷的url解析, 保持url不变
     */

    @Override
    public Domain getResolveDomain() {
        return DOMAIN;
    }

    @Override
    public CommodityType getCommodityType() {
        return CommodityType.JD_COUPON_URL;
    }

}
