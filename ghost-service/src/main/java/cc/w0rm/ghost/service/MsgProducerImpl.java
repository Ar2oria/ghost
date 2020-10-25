package cc.w0rm.ghost.service;

import cc.w0rm.ghost.api.Coordinator;
import cc.w0rm.ghost.api.MsgProducer;
import cc.w0rm.ghost.mysql.dao.CommodityDALImpl;
import cc.w0rm.ghost.mysql.mapper.CommodityMapper;
import cc.w0rm.ghost.mysql.po.Commodity;
import com.forte.qqrobot.beans.messages.msgget.GroupMsg;
import com.forte.qqrobot.sender.MsgSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Set;

/**
 * @author : xuyang
 * @date : 2020/10/15 3:40 下午
 */
@Slf4j
@Service("msgProducer")
public class MsgProducerImpl implements MsgProducer {
    
    @Autowired
    private Coordinator coordinator;
    
    @Resource
    private CommodityDALImpl commodityDAL;
    
    @Override
    public void make(MsgSender msgSender, GroupMsg groupMsg) {
        
        // 1. 解析消息
        String msg = groupMsg.getMsg();
        // 2. 判断解析结果 TODO
        String commoditysku = "";
        String commodityId = "";
        
        // 3. 存储数据库
        Commodity commodity = new Commodity();
        commodity.setCommodityId(commodityId);
        commodity.setName(commoditysku);
        commodity.setSku(commoditysku);
        try {
            Set<String> targetCommodityPushedGroups = commodityDAL.getTargetCommodityPushedGroups(commodityId);
            if (targetCommodityPushedGroups.contains(groupMsg.getGroup())) {
                return;
            }
            targetCommodityPushedGroups.add(groupMsg.getGroup());
            commodityDAL.addCommodity(commodity, String.join(",", targetCommodityPushedGroups));
        } catch (Exception exp) {
            log.error("消息生产者，商品记录失败 msgId[{}]", groupMsg.getId(), exp);
        }
        // 4. 使用协调者转发消息 coordinator.forward()
        try {
            coordinator.forward(groupMsg);
        } catch (Exception exp) {
            log.error("消息生产者，转发消息失败 msgId[{}]", groupMsg.getId(), exp);
        }
    }
}


    
