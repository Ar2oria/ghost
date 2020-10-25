package cc.w0rm.ghost.mysql.dao;

import cc.w0rm.ghost.mysql.mapper.CommodityMapper;
import cc.w0rm.ghost.mysql.mapper.MsgGroupMapper;
import cc.w0rm.ghost.mysql.po.Commodity;
import cc.w0rm.ghost.mysql.po.MsgGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
    
    public Set<String> getTargetCommodityPushedGroups(String commodityId) {
        try {
            MsgGroup msgGroup = msgGroupMapper.selectByCommdityId(commodityId);
            String groups = msgGroup.getGroups();
            if (!StringUtils.isEmpty(groups)) {
                return new HashSet<>(Arrays.asList(groups.split(",")));
            }
        } catch (Exception exp) {
            log.error("商品分发群组查询失败 商品id:{}", commodityId);
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
            log.error("商品信息添加失败 商品信息:{}", commodity.toString());
        }
        if (StringUtils.isEmpty(groups)) {
            return;
        }
        try {
           MsgGroup msgGroup = new MsgGroup();
           msgGroup.setGroups(groups);
           msgGroup.setCommodityId(commodityId);
           msgGroupMapper.insertOrUpdate(msgGroup);
        } catch (Exception exp) {
            log.error("商品信息添加失败 商品信息:{}", commodity.toString());
        }
    }
}
