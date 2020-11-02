package cc.w0rm.ghost.mysql.dao;

import cc.w0rm.ghost.mysql.mapper.CommodityMapper;
import cc.w0rm.ghost.mysql.mapper.MsgGroupMapper;
import cc.w0rm.ghost.mysql.po.Commodity;
import cc.w0rm.ghost.mysql.po.MsgGroup;
import com.alibaba.druid.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author panyupeng
 * @date 2020-10-25 16:10
 */
@Service
@Slf4j
public class CommodityDALImpl {
    
    @Resource
    CommodityMapper commodityMapper;
    
    @Resource
    MsgGroupMapper msgGroupMapper;
    
    private static final long HOURS = 60 * 60 * 1000L;
    
    public Set<String> getTargetCommodityPushedGroups(String commodityId) {
        
        try {
            List<MsgGroup> msgGroups = msgGroupMapper.selectByCommdityId(commodityId);
            if (CollectionUtils.isEmpty(msgGroups)) {
                return new HashSet<>();
            }
            return msgGroups.stream().filter(item -> !(item != null && null != item.getInsertTime() && (StringUtils
                .isNumber(item.getInsertTime()) && System.currentTimeMillis() - Long
                .valueOf(item.getInsertTime()) > 6 * HOURS))).map(item -> String.valueOf(item.getGroup()))
                .collect(Collectors.toSet());
        } catch (Exception exp) {
            log.error("商品分发群组查询失败 商品id:{}", commodityId, exp);
        }
        return new HashSet<>();
    }
    
    public void addCommodity(Commodity commodity, String groups) {
        
        String commodityId = commodity.getCommodityId();
        if (StringUtils.isEmpty(commodityId)) {
            return;
        }
        try {
            commodityMapper.insert(commodity);
        } catch (Exception exp) {
            log.error("商品信息添加失败 商品信息:{}", commodity.toString(), exp);
        }
        if (StringUtils.isEmpty(groups)) {
            return;
        }
        try {
            MsgGroup msgGroup = new MsgGroup();
            msgGroup.setGroup(Long.parseLong(groups));
            msgGroup.setCommodityId(commodityId);
            msgGroup.setInsertTime(String.valueOf(System.currentTimeMillis()));
            msgGroupMapper.insertOrUpdate(msgGroup);
        } catch (Exception exp) {
            log.error("商品群组信息添加失败 商品信息:{}", commodity.toString(), exp);
        }
    }
}
